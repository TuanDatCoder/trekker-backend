package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tripday.request.CreateTripDayRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripday.request.UpdateTripDayRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripday.response.TripDayResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Trip;
import com.tuandatcoder.trekkerbackend.entity.TripDay;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.TripDayMapper;
import com.tuandatcoder.trekkerbackend.repository.TripDayRepository;
import com.tuandatcoder.trekkerbackend.repository.TripRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripDayService {

    @Autowired private TripDayRepository tripDayRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private TripDayMapper tripDayMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkTripOwnership(Trip trip) {
        Account current = getCurrentAccount();
        if (!trip.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only manage days in your own trip", ErrorCode.TRIP_FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<TripDayResponseDTO> createTripDay(CreateTripDayRequestDTO dto) {
        Account current = getCurrentAccount();

        Trip trip = tripRepository.findActiveById(dto.getTripId())
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        checkTripOwnership(trip);

        // Kiểm tra trùng dayIndex
        if (tripDayRepository.existsByTripIdAndDayIndexAndDeletedAtIsNull(dto.getTripId(), dto.getDayIndex())) {
            throw new ApiException("Day index already exists", ErrorCode.USER_EXISTED);
        }

        TripDay tripDay = TripDay.builder()
                .trip(trip)
                .dayIndex(dto.getDayIndex())
                .date(dto.getDate())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        tripDay = tripDayRepository.save(tripDay);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_TRIP_DAY,
                "Created day " + dto.getDayIndex() + " for trip ID " + dto.getTripId(),
                current
        );

        return new ApiResponse<>(201, "Trip day created successfully", tripDayMapper.toDto(tripDay));
    }

    public ApiResponse<List<TripDayResponseDTO>> getTripDays(Long tripId) {
        Trip trip = tripRepository.findActiveById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        // Kiểm tra quyền xem (nếu private)
        Account current = accountUtils.getCurrentAccount();
        if (trip.getPrivacy() == com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum.PRIVATE) {
            if (current == null || !trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("Forbidden", ErrorCode.TRIP_FORBIDDEN);
            }
        }

        List<TripDay> days = tripDayRepository.findByTripIdActive(tripId);
        return new ApiResponse<>(200, "Trip days retrieved successfully", tripDayMapper.toDtoList(days));
    }

    public ApiResponse<TripDayResponseDTO> getTripDayById(Long id) {
        TripDay tripDay = tripDayRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Trip day not found", ErrorCode.NOT_FOUND));

        // Kiểm tra quyền
        checkTripOwnership(tripDay.getTrip());

        return new ApiResponse<>(200, "Trip day retrieved successfully", tripDayMapper.toDto(tripDay));
    }

    @Transactional
    public ApiResponse<TripDayResponseDTO> updateTripDay(Long id, UpdateTripDayRequestDTO dto) {
        TripDay tripDay = tripDayRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Trip day not found", ErrorCode.NOT_FOUND));

        checkTripOwnership(tripDay.getTrip());

        // Kiểm tra trùng dayIndex nếu thay đổi
        if (dto.getDayIndex() != null && !dto.getDayIndex().equals(tripDay.getDayIndex())) {
            if (tripDayRepository.existsByTripIdAndDayIndexAndDeletedAtIsNull(tripDay.getTrip().getId(), dto.getDayIndex())) {
                throw new ApiException("Day index already exists", ErrorCode.USER_EXISTED);
            }
        }

        tripDayMapper.updateEntityFromDto(dto, tripDay);
        tripDay = tripDayRepository.save(tripDay);

        return new ApiResponse<>(200, "Trip day updated successfully", tripDayMapper.toDto(tripDay));
    }

    @Transactional
    public ApiResponse<String> deleteTripDay(Long id) {
        TripDay tripDay = tripDayRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Trip day not found", ErrorCode.NOT_FOUND));

        checkTripOwnership(tripDay.getTrip());

        tripDay.setDeletedAt(LocalDateTime.now());
        tripDayRepository.save(tripDay);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_TRIP_DAY,
                "Deleted day " + tripDay.getDayIndex() + " from trip ID " + tripDay.getTrip().getId(),
                getCurrentAccount()
        );

        return new ApiResponse<>(200, "Trip day deleted successfully", null);
    }
}