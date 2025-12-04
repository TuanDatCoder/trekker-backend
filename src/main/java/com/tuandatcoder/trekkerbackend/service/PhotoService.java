package com.tuandatcoder.trekkerbackend.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.photo.request.UploadPhotoRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.photo.response.PhotoResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PhotoMapper;
import com.tuandatcoder.trekkerbackend.repository.*;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired private PhotoRepository photoRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private AlbumRepository albumRepository;
    @Autowired private PhotoMapper photoMapper;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Transactional
    public ApiResponse<PhotoResponseDTO> uploadPhoto(UploadPhotoRequestDTO dto) {
        Account current = accountUtils.getCurrentAccount();
        if (current == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);

        MultipartFile file = dto.getFile();
        if (file == null || file.isEmpty()) {
            throw new ApiException("File is required", ErrorCode.PHOTO_UPLOAD_FAILED);
        }

        // Kiểm tra trip nếu có
        Trip trip = null;
        if (dto.getTripId() != null) {
            trip = tripRepository.findActiveById(dto.getTripId())
                    .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));
            if (!trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only upload to your own trip", ErrorCode.TRIP_FORBIDDEN);
            }
        }

        // Upload + tạo thumbnail
        String originalUrl = saveFile(file, "original");
        String thumbnailUrl = generateThumbnail(file);

        // Đọc EXIF
        LocalDateTime takenAt = extractTakenAt(file);
        String exifJson = extractExifData(file);

        Photo photo = Photo.builder()
                .account(current)
                .trip(trip)
                .tripDay(null) // sẽ set sau
                .tripPlanItem(null)
                .location(null)
                .place(null)
                .url(originalUrl)
                .thumbnailUrl(thumbnailUrl)
                .caption(dto.getCaption())
                .mediaType(determineMediaType(file))
                .fileSizeMb(file.getSize() / (1024.0 * 1024.0))
                .width(getImageWidth(file))
                .height(getImageHeight(file))
                .isRealtime(dto.getIsRealtime() != null && dto.getIsRealtime())
                .isPublicOnPlace(dto.getIsPublicOnPlace() != null && dto.getIsPublicOnPlace())
                .exifData(exifJson)
                .takenAt(takenAt)
                .build();

        photo = photoRepository.save(photo);

        // Cập nhật totalPhotos của Trip (nếu có)
        if (trip != null) {
            trip.setTotalPhotos(trip.getTotalPhotos() + 1);
            tripRepository.save(trip);
        }

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.UPLOAD_PHOTO,
                "Uploaded " + (photo.getMediaType() == com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum.VIDEO ? "video" : "photo") +
                        " - " + file.getOriginalFilename() + " (ID: " + photo.getId() + ")",
                current
        );

        return new ApiResponse<>(201, "Photo uploaded successfully", photoMapper.toDto(photo));
    }

    public ApiResponse<List<PhotoResponseDTO>> getMyPhotos() {
        Account current = accountUtils.getCurrentAccount();
        List<Photo> photos = photoRepository.findByAccountId(current.getId());
        return new ApiResponse<>(200, "Your photos", photos.stream().map(photoMapper::toDto).collect(Collectors.toList()));
    }

    public ApiResponse<List<PhotoResponseDTO>> getTripPhotos(Long tripId) {
        Trip trip = tripRepository.findActiveById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        Account current = accountUtils.getCurrentAccount();
        if (trip.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum.PRIVATE) {
            if (current == null || !trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("Forbidden", ErrorCode.TRIP_FORBIDDEN);
            }
        }

        List<Photo> photos = photoRepository.findByTripId(tripId);
        return new ApiResponse<>(200, "Trip photos", photos.stream().map(photoMapper::toDto).collect(Collectors.toList()));
    }

    @Transactional
    public ApiResponse<String> deletePhoto(Long id) {
        Photo photo = photoRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Photo not found", ErrorCode.PHOTO_NOT_FOUND));

        Account current = accountUtils.getCurrentAccount();
        if (!photo.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own photo", ErrorCode.PHOTO_FORBIDDEN);
        }

        photo.setDeletedAt(LocalDateTime.now());
        photoRepository.save(photo);

        // Giảm totalPhotos của Trip
        if (photo.getTrip() != null) {
            Trip trip = photo.getTrip();
            trip.setTotalPhotos(Math.max(0, trip.getTotalPhotos() - 1));
            tripRepository.save(trip);
        }

        return new ApiResponse<>(200, "Photo deleted successfully", null);
    }

    // Helper methods
    private String saveFile(MultipartFile file, String prefix) {
        try {
            String fileName = prefix + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new ApiException("Upload failed", ErrorCode.PHOTO_UPLOAD_FAILED);
        }
    }

    private String generateThumbnail(MultipartFile file) {
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) return null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(original)
                    .size(400, 400)
                    .keepAspectRatio(true)
                    .toOutputStream(baos);

            String thumbName = "thumb_" + UUID.randomUUID() + ".jpg";
            Path thumbPath = Paths.get(uploadDir, thumbName);
            Files.write(thumbPath, baos.toByteArray());

            return "/uploads/" + thumbName;
        } catch (Exception e) {
            return null;
        }
    }

    private com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum determineMediaType(MultipartFile file) {
        String type = file.getContentType();
        return (type != null && type.startsWith("video")) ?
                com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum.VIDEO :
                com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum.IMAGE;
    }

    private Integer getImageWidth(MultipartFile file) { /* đọc từ BufferedImage */ return null; }
    private Integer getImageHeight(MultipartFile file) { /* đọc từ BufferedImage */ return null; }

    private LocalDateTime extractTakenAt(MultipartFile file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            Directory dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (dir != null && dir.containsTag(ExifIFD0Directory.TAG_DATETIME)) {
                Date date = dir.getDate(ExifIFD0Directory.TAG_DATETIME);
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String extractExifData(MultipartFile file) {
        // trả về JSON string (có thể dùng ObjectMapper)
        return "{}";
    }
}