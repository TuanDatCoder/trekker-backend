package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.request.CreateTripPlanItemRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.request.UpdateTripPlanItemRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.response.TripPlanItemResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.TripPlanItemMapper;
import com.tuandatcoder.trekkerbackend.repository.*;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripPlanItemService {

    @Autowired private TripPlanItemRepository planItemRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private TripDayRepository tripDayRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private TripPlanItemMapper planItemMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkTripOwnership(Trip trip) {
        Account current = getCurrentAccount();
        if (!trip.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only manage items in your own trip", ErrorCode.TRIP_FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<TripPlanItemResponseDTO> createPlanItem(CreateTripPlanItemRequestDTO dto) {
        Account current = getCurrentAccount();

        Trip trip = tripRepository.findActiveById(dto.getTripId())
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        checkTripOwnership(trip);

        TripDay tripDay = null;
        if (dto.getTripDayId() != null) {
            tripDay = tripDayRepository.findActiveById(dto.getTripDayId())
                    .orElseThrow(() -> new ApiException("Trip day not found", ErrorCode.NOT_FOUND));
            if (!tripDay.getTrip().getId().equals(trip.getId())) {
                throw new ApiException("Trip day does not belong to this trip", ErrorCode.FORBIDDEN);
            }
        }

        Place place = dto.getPlaceId() != null ? placeRepository.findActiveById(dto.getPlaceId()).orElse(null) : null;
        Location location = dto.getLocationId() != null ? locationRepository.findActiveById(dto.getLocationId()).orElse(null) : null;

        TripPlanItem item = TripPlanItem.builder()
                .trip(trip)
                .tripDay(tripDay)
                .place(place)
                .location(location)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .orderIndex(dto.getOrderIndex())
                .scheduledTime(dto.getScheduledTime())
                .estimatedCost(dto.getEstimatedCost())
                .notes(dto.getNotes())
                .build();

        item = planItemRepository.save(item);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_TRIP_PLAN_ITEM,
                "Created plan item: " + (item.getTitle() != null ? item.getTitle() : "No title"),
                current
        );

        return new ApiResponse<>(201, "Plan item created successfully", planItemMapper.toDto(item));
    }

    public ApiResponse<List<TripPlanItemResponseDTO>> getPlanItemsByTrip(Long tripId) {
        Trip trip = tripRepository.findActiveById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", ErrorCode.TRIP_NOT_FOUND));

        List<TripPlanItem> items = planItemRepository.findByTripIdActive(tripId);
        return new ApiResponse<>(200, "Plan items retrieved", planItemMapper.toDtoList(items));
    }

    public ApiResponse<List<TripPlanItemResponseDTO>> getPlanItemsByDay(Long tripDayId) {
        tripDayRepository.findActiveById(tripDayId)
                .orElseThrow(() -> new ApiException("Trip day not found", ErrorCode.NOT_FOUND));

        List<TripPlanItem> items = planItemRepository.findByTripDayIdActive(tripDayId);
        return new ApiResponse<>(200, "Plan items retrieved", planItemMapper.toDtoList(items));
    }

    @Transactional
    public ApiResponse<TripPlanItemResponseDTO> updatePlanItem(Long id, UpdateTripPlanItemRequestDTO dto) {
        TripPlanItem item = planItemRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Plan item not found", ErrorCode.NOT_FOUND));

        checkTripOwnership(item.getTrip());

        planItemMapper.updateEntityFromDto(dto, item);

        if (dto.getTripDayId() != null) {
            TripDay day = tripDayRepository.findActiveById(dto.getTripDayId())
                    .orElseThrow(() -> new ApiException("Trip day not found", ErrorCode.NOT_FOUND));
            item.setTripDay(day);
        }
        if (dto.getPlaceId() != null) {
            Place place = placeRepository.findActiveById(dto.getPlaceId()).orElse(null);
            item.setPlace(place);
        }
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findActiveById(dto.getLocationId()).orElse(null);
            item.setLocation(location);
        }

        item = planItemRepository.save(item);
        return new ApiResponse<>(200, "Plan item updated", planItemMapper.toDto(item));
    }

    @Transactional
    public ApiResponse<String> deletePlanItem(Long id) {
        TripPlanItem item = planItemRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Plan item not found", ErrorCode.NOT_FOUND));

        checkTripOwnership(item.getTrip());

        item.setDeletedAt(LocalDateTime.now());
        planItemRepository.save(item);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_TRIP_PLAN_ITEM,
                "Deleted plan item ID: " + id,
                getCurrentAccount()
        );

        return new ApiResponse<>(200, "Plan item deleted successfully", null);
    }
}