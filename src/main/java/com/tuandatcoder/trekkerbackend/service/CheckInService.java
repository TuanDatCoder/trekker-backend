package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.checkin.request.CreateCheckInRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.checkin.request.UpdateCheckInRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.checkin.response.CheckInResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.CheckInMapper;
import com.tuandatcoder.trekkerbackend.repository.*;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInService {

    @Autowired private UserPlaceCheckInRepository checkInRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private CheckInMapper checkInMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    @Transactional
    public ApiResponse<CheckInResponseDTO> createCheckIn(CreateCheckInRequestDTO dto) {
        Account current = getCurrentAccount();

        Place place = placeRepository.findActiveById(dto.getPlaceId())
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        // Kiểm tra đã check-in chưa
        if (checkInRepository.existsByAccountIdAndPlaceIdAndDeletedAtIsNull(current.getId(), dto.getPlaceId())) {
            throw new ApiException("You have already checked in at this place", ErrorCode.USER_EXISTED);
        }

        Trip trip = null;
        if (dto.getTripId() != null) {
            trip = tripRepository.findActiveById(dto.getTripId())
                    .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));
            if (!trip.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only check in to your own trip", ErrorCode.TRIP_FORBIDDEN);
            }
        }

        Photo photo = null;
        if (dto.getPhotoId() != null) {
            photo = photoRepository.findActiveById(dto.getPhotoId())
                    .orElseThrow(() -> new ApiException("Photo not found", ErrorCode.PHOTO_NOT_FOUND));
            if (!photo.getAccount().getId().equals(current.getId())) {
                throw new ApiException("You can only use your own photo", ErrorCode.PHOTO_FORBIDDEN);
            }
        }

        UserPlaceCheckIn checkIn = UserPlaceCheckIn.builder()
                .account(current)
                .place(place)
                .trip(trip)
                .photo(photo)
                .note(dto.getNote())
                .checkedInAt(LocalDateTime.now())
                .build();

        checkIn = checkInRepository.save(checkIn);

        // Tăng totalCheckins cho Place
        place.setTotalCheckins(place.getTotalCheckins() + 1);
        placeRepository.save(place);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CHECK_IN_PLACE,
                "Checked in at place: " + place.getName(),
                current
        );

        return new ApiResponse<>(201, "Check-in successful", checkInMapper.toDto(checkIn));
    }

    public ApiResponse<List<CheckInResponseDTO>> getMyCheckIns() {
        Account current = getCurrentAccount();
        List<UserPlaceCheckIn> checkIns = checkInRepository.findByAccountId(current.getId());
        return new ApiResponse<>(200, "Your check-ins retrieved", checkInMapper.toDtoList(checkIns));
    }

    public ApiResponse<List<CheckInResponseDTO>> getCheckInsByPlace(Long placeId) {
        placeRepository.findActiveById(placeId)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        List<UserPlaceCheckIn> checkIns = checkInRepository.findByPlaceId(placeId);
        return new ApiResponse<>(200, "Check-ins retrieved", checkInMapper.toDtoList(checkIns));
    }

    @Transactional
    public ApiResponse<CheckInResponseDTO> updateCheckIn(Long id, UpdateCheckInRequestDTO dto) {
        Account current = getCurrentAccount();

        UserPlaceCheckIn checkIn = checkInRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Check-in not found", ErrorCode.NOT_FOUND));

        if (!checkIn.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only edit your own check-in", ErrorCode.FORBIDDEN);
        }

        if (dto.getNote() != null) {
            checkIn.setNote(dto.getNote());
        }

        checkIn = checkInRepository.save(checkIn);
        return new ApiResponse<>(200, "Check-in updated", checkInMapper.toDto(checkIn));
    }

    @Transactional
    public ApiResponse<String> deleteCheckIn(Long id) {
        Account current = getCurrentAccount();

        UserPlaceCheckIn checkIn = checkInRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Check-in not found", ErrorCode.NOT_FOUND));

        if (!checkIn.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own check-in", ErrorCode.FORBIDDEN);
        }

        checkIn.setDeletedAt(LocalDateTime.now());
        checkInRepository.save(checkIn);

        // Giảm totalCheckins
        Place place = checkIn.getPlace();
        place.setTotalCheckins(Math.max(0, place.getTotalCheckins() - 1));
        placeRepository.save(place);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_CHECK_IN,
                "Deleted check-in at place: " + place.getName(),
                current
        );

        return new ApiResponse<>(200, "Check-in deleted", null);
    }
}
