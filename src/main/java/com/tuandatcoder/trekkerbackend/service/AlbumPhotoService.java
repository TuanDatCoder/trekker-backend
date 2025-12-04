package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.albumphoto.request.AddPhotosToAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.albumphoto.request.ReorderAlbumPhotosRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.albumphoto.response.AlbumPhotoResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Album;
import com.tuandatcoder.trekkerbackend.entity.AlbumPhoto;
import com.tuandatcoder.trekkerbackend.entity.Photo;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.AlbumPhotoMapper;
import com.tuandatcoder.trekkerbackend.repository.AlbumPhotoRepository;
import com.tuandatcoder.trekkerbackend.repository.AlbumRepository;
import com.tuandatcoder.trekkerbackend.repository.PhotoRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumPhotoService {

    @Autowired private AlbumRepository albumRepository;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private AlbumPhotoRepository albumPhotoRepository;
    @Autowired private AlbumPhotoMapper albumPhotoMapper;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;

    private Album getAlbumAndCheckOwnership(Long albumId) {
        Album album = albumRepository.findActiveById(albumId)
                .orElseThrow(() -> new ApiException("Album not found or deleted", ErrorCode.ALBUM_NOT_FOUND));

        var current = accountUtils.getCurrentAccount();
        if (current == null || !album.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only manage photos in your own album", ErrorCode.ALBUM_FORBIDDEN);
        }
        return album;
    }

    // LẤY DANH SÁCH ẢNH TRONG ALBUM
    public ApiResponse<List<AlbumPhotoResponseDTO>> getPhotosInAlbum(Long albumId) {
        Album album = albumRepository.findActiveById(albumId)
                .orElseThrow(() -> new ApiException("Album not found", ErrorCode.ALBUM_NOT_FOUND));

        // Kiểm tra quyền xem album (PRIVATE thì phải là owner)
        var current = accountUtils.getCurrentAccount();
        if (album.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.AlbumPrivacyEnum.PRIVATE) {
            if (current == null || !album.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You don't have permission to view this album", ErrorCode.ALBUM_FORBIDDEN);
            }
        }

        List<AlbumPhoto> albumPhotos = albumPhotoRepository.findByAlbumIdActive(albumId);
        List<AlbumPhotoResponseDTO> dtos = albumPhotos.stream()
                .map(albumPhotoMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Photos retrieved successfully", dtos);
    }

    // THÊM ẢNH VÀO ALBUM
    @Transactional
    public ApiResponse<String> addPhotosToAlbum(Long albumId, AddPhotosToAlbumRequestDTO dto) {
        Album album = getAlbumAndCheckOwnership(albumId);
        var current = accountUtils.getCurrentAccount();

        int nextOrder = albumPhotoRepository.findByAlbumIdActive(albumId).size();

        for (Long photoId : dto.getPhotoIds()) {
            Photo photo = photoRepository.findById(photoId)
                    .orElseThrow(() -> new ApiException("Photo not found: " + photoId, ErrorCode.PHOTO_NOT_FOUND));

            // Kiểm tra ảnh có thuộc về user không (hoặc thuộc trip của user)
            if (!photo.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only add your own photos", ErrorCode.PHOTO_FORBIDDEN);
            }

            // Kiểm tra chưa tồn tại trong album
            if (albumPhotoRepository.findByAlbumIdAndPhotoId(albumId, photoId).isPresent()) {
                continue; // Bỏ qua nếu đã có
            }

            AlbumPhoto albumPhoto = AlbumPhoto.builder()
                    .album(album)
                    .photo(photo)
                    .orderIndex(nextOrder++)
                    .build();

            albumPhotoRepository.save(albumPhoto);
        }

        // Cập nhật totalPhotos
        album.setTotalPhotos(albumPhotoRepository.findByAlbumIdActive(albumId).size());
        albumRepository.save(album);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.ADD_PHOTO_TO_ALBUM,
                "Added " + dto.getPhotoIds().size() + " photo(s) to album: " + album.getTitle(),
                current
        );

        return new ApiResponse<>(200, "Photos added to album successfully", null);
    }

    // XÓA ẢNH KHỎI ALBUM (soft delete)
    @Transactional
    public ApiResponse<String> removePhotoFromAlbum(Long albumId, Long albumPhotoId) {
        Album album = getAlbumAndCheckOwnership(albumId);
        var current = accountUtils.getCurrentAccount();

        AlbumPhoto albumPhoto = albumPhotoRepository.findActiveById(albumPhotoId)
                .orElseThrow(() -> new ApiException("Photo not found in this album", ErrorCode.PHOTO_NOT_FOUND));

        if (!albumPhoto.getAlbum().getId().equals(albumId)) {
            throw new ApiException("Photo does not belong to this album", ErrorCode.PHOTO_FORBIDDEN);
        }

        albumPhoto.setDeletedAt(LocalDateTime.now());
        albumPhotoRepository.save(albumPhoto);

        // Cập nhật lại totalPhotos
        album.setTotalPhotos(albumPhotoRepository.findByAlbumIdActive(albumId).size());
        albumRepository.save(album);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.REMOVE_PHOTO_FROM_ALBUM,
                "Removed photo ID " + albumPhoto.getPhoto().getId() + " from album: " + album.getTitle(),
                current
        );

        return new ApiResponse<>(200, "Photo removed from album successfully", null);
    }

    // SẮP XẾP LẠI ẢNH
    @Transactional
    public ApiResponse<String> reorderPhotos(Long albumId, ReorderAlbumPhotosRequestDTO dto) {
        Album album = getAlbumAndCheckOwnership(albumId);

        dto.getPhotoIdToOrder().forEach((photoId, order) -> {
            AlbumPhoto albumPhoto = albumPhotoRepository.findByAlbumIdAndPhotoId(albumId, photoId)
                    .orElseThrow(() -> new ApiException("Photo not found in album: " + photoId, ErrorCode.PHOTO_NOT_FOUND));

            if (albumPhoto.getDeletedAt() != null) {
                throw new ApiException("Cannot reorder deleted photo", ErrorCode.PHOTO_NOT_FOUND);
            }

            albumPhoto.setOrderIndex(order);
            albumPhotoRepository.save(albumPhoto);
        });

        return new ApiResponse<>(200, "Photos reordered successfully", null);
    }
}