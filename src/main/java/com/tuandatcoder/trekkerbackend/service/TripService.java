package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.trip.request.CreateTripRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.trip.request.UpdateTripRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.trip.response.TripResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Trip;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.mapper.TripMapper;
import com.tuandatcoder.trekkerbackend.repository.TripRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired private TripRepository tripRepository;
    @Autowired private TripMapper tripMapper;
    @Autowired private AccountUtils accountUtils;

    // CREATE
    public ApiResponse<TripResponseDTO> createTrip(CreateTripRequestDTO dto) {
        Account currentAccount = getCurrentAccount();

        Trip trip = Trip.builder()
                .account(currentAccount)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .destination(dto.getDestination())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .privacy(dto.getPrivacy())
                .status(com.tuandatcoder.trekkerbackend.enums.TripStatusEnum.PLANNING)
                .isCollaborative(dto.isCollaborative())
                .totalDays((int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1)
                .build();

        trip = tripRepository.save(trip);

        return new ApiResponse<>(201, "Trip created successfully", tripMapper.toDto(trip));
    }

    // READ - My trips
    public ApiResponse<List<TripResponseDTO>> getMyTrips() {
        Account currentAccount = getCurrentAccount();
        List<Trip> trips = tripRepository.findByAccountId(currentAccount.getId());

        List<TripResponseDTO> dtos = trips.stream()
                .map(tripMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "My trips retrieved successfully", dtos);
    }

    // READ - Public trips (không cần login cũng xem được → không check account)
    public ApiResponse<List<TripResponseDTO>> getPublicTrips() {
        List<Trip> trips = tripRepository.findAllPublic();
        List<TripResponseDTO> dtos = trips.stream()
                .map(tripMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Public trips retrieved successfully", dtos);
    }


    public ApiResponse<TripResponseDTO> getTripById(Long id) {
        Trip trip = tripRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Trip not found or has been deleted", ErrorCode.NOT_FOUND));

        Account currentAccount = getCurrentAccount(); // Có thể null nếu chưa login (public trip)

        // Nếu là PRIVATE → phải là chủ hoặc admin mới được xem
        if (trip.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum.PRIVATE) {
            if (currentAccount == null || !trip.getAccount().getId().equals(currentAccount.getId())) {
                throw new ApiException("You don't have permission to view this trip", ErrorCode.FORBIDDEN);
            }
        }

        return new ApiResponse<>(200, "Trip retrieved successfully", tripMapper.toDto(trip));
    }

    // UPDATE
    public ApiResponse<TripResponseDTO> updateTrip(Long id, UpdateTripRequestDTO dto) {
        Trip trip = tripRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.NOT_FOUND));

        Account currentAccount = getCurrentAccount();

        if (!trip.getAccount().getId().equals(currentAccount.getId())) {
            throw new ApiException("You can only edit your own trip", ErrorCode.FORBIDDEN);
        }

        tripMapper.updateEntityFromDto(dto, trip);

        // Cập nhật lại totalDays nếu có thay đổi ngày
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            trip.setTotalDays((int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1);
        }

        trip = tripRepository.save(trip);
        return new ApiResponse<>(200, "Trip updated successfully", tripMapper.toDto(trip));
    }

    // DELETE (soft delete)
    public ApiResponse<String> deleteTrip(Long id) {
        Trip trip = tripRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.NOT_FOUND));

        Account currentAccount = getCurrentAccount();

        if (!trip.getAccount().getId().equals(currentAccount.getId())) {
            throw new ApiException("You can only delete your own trip", ErrorCode.FORBIDDEN);
        }

        trip.setDeletedAt(LocalDateTime.now());
        trip.setStatus(com.tuandatcoder.trekkerbackend.enums.TripStatusEnum.DELETED);
        tripRepository.save(trip);

        return new ApiResponse<>(200, "Trip deleted successfully", null);
    }

    // Helper method để gọn code
    private Account getCurrentAccount() {
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new ApiException("You must be logged in to perform this action", ErrorCode.UNAUTHENTICATED);
        }
        return account;
    }
}