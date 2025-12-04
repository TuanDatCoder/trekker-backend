package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.album.request.CreateAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.album.request.UpdateAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.album.response.AlbumResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Album;
import com.tuandatcoder.trekkerbackend.entity.Trip;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.AlbumMapper;
import com.tuandatcoder.trekkerbackend.repository.AlbumRepository;
import com.tuandatcoder.trekkerbackend.repository.TripRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired private AlbumRepository albumRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private AlbumMapper albumMapper;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // CREATE
    public ApiResponse<AlbumResponseDTO> createAlbum(CreateAlbumRequestDTO dto) {
        Account current = getCurrentAccount();

        Album.AlbumBuilder builder = Album.builder()
                .account(current)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .privacy(dto.getPrivacy())
                .totalPhotos(0);

        // Nếu có tripId → kiểm tra quyền sở hữu trip đó
        if (dto.getTripId() != null) {
            Trip trip = tripRepository.findActiveById(dto.getTripId())
                    .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

            if (!trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only create album for your own trip", ErrorCode.TRIP_FORBIDDEN);
            }
            builder.trip(trip);
        }

        Album album = albumRepository.save(builder.build());

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_ALBUM,
                "Created album: " + album.getTitle() + " (ID: " + album.getId() + ")",
                current
        );

        return new ApiResponse<>(201, "Album created successfully", albumMapper.toDto(album));
    }

    // READ - My albums
    public ApiResponse<List<AlbumResponseDTO>> getMyAlbums() {
        Account current = getCurrentAccount();
        List<Album> albums = albumRepository.findByAccountId(current.getId());
        List<AlbumResponseDTO> dtos = albums.stream().map(albumMapper::toDto).collect(Collectors.toList());
        return new ApiResponse<>(200, "Your albums retrieved successfully", dtos);
    }

    // READ - Albums của 1 trip
    public ApiResponse<List<AlbumResponseDTO>> getAlbumsByTripId(Long tripId) {
        Trip trip = tripRepository.findActiveById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        Account current = accountUtils.getCurrentAccount(); // Có thể null nếu chưa login

        // Nếu trip là PRIVATE → phải là owner mới được xem album
        if (trip.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum.PRIVATE) {
            if (current == null || !trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You don't have permission to view albums of this trip", ErrorCode.FORBIDDEN);
            }
        }

        List<Album> albums = albumRepository.findByTripId(tripId);
        List<AlbumResponseDTO> dtos = albums.stream().map(albumMapper::toDto).collect(Collectors.toList());
        return new ApiResponse<>(200, "Albums retrieved successfully", dtos);
    }

    // READ - One album
    public ApiResponse<AlbumResponseDTO> getAlbumById(Long id) {
        Album album = albumRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Album not found or deleted", ErrorCode.NOT_FOUND));

        Account current = accountUtils.getCurrentAccount();

        // Kiểm tra quyền xem
        if (album.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.AlbumPrivacyEnum.PRIVATE) {
            if (current == null || !album.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You don't have permission to view this album", ErrorCode.FORBIDDEN);
            }
        }

        return new ApiResponse<>(200, "Album retrieved successfully", albumMapper.toDto(album));
    }

    // UPDATE
    public ApiResponse<AlbumResponseDTO> updateAlbum(Long id, UpdateAlbumRequestDTO dto) {
        Album album = albumRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Album not found", ErrorCode.NOT_FOUND));

        Account current = getCurrentAccount();
        if (!album.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only edit your own album", ErrorCode.FORBIDDEN);
        }

        albumMapper.updateEntityFromDto(dto, album);
        album = albumRepository.save(album);

        return new ApiResponse<>(200, "Album updated successfully", albumMapper.toDto(album));
    }

    // DELETE (soft delete)
    public ApiResponse<String> deleteAlbum(Long id) {
        Album album = albumRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Album not found", ErrorCode.NOT_FOUND));

        Account current = getCurrentAccount();
        if (!album.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own album", ErrorCode.FORBIDDEN);
        }

        album.setDeletedAt(LocalDateTime.now());
        albumRepository.save(album);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_ALBUM,
                "Deleted album: " + album.getTitle() + " (ID: " + album.getId() + ")",
                current
        );

        return new ApiResponse<>(200, "Album deleted successfully", null);
    }
}