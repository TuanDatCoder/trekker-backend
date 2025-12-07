package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.place.request.CreatePlaceRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.place.request.UpdatePlaceRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.place.response.PlaceResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Place;
import com.tuandatcoder.trekkerbackend.entity.PlaceCategory;
import com.tuandatcoder.trekkerbackend.entity.Location;
import com.tuandatcoder.trekkerbackend.enums.PlaceStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PlaceMapper;
import com.tuandatcoder.trekkerbackend.repository.*;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    @Autowired private PlaceRepository placeRepository;
    @Autowired private PlaceCategoryRepository categoryRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PlaceMapper placeMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    @Transactional
    public ApiResponse<PlaceResponseDTO> createPlace(CreatePlaceRequestDTO dto) {
        Account current = getCurrentAccount();

        String slug = generateSlug(dto.getName());
        if (placeRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            slug += "-" + System.currentTimeMillis();
        }

        Place.PlaceBuilder builder = Place.builder()
                .name(dto.getName())
                .slug(slug)
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .website(dto.getWebsite())
                .openingHours(dto.getOpeningHours())
                .priceRange(dto.getPriceRange())
                .description(dto.getDescription())
                .createdBy(current)
                .status(PlaceStatusEnum.ACTIVE);

        if (dto.getCategoryId() != null) {
            PlaceCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ApiException("Category not found", ErrorCode.NOT_FOUND));
            builder.category(category);
        }

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new ApiException("Location not found", ErrorCode.NOT_FOUND));
            builder.location(location);
        }

        Place place = placeRepository.save(builder.build());

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_PLACE,
                "Created place: " + place.getName(),
                current
        );

        return new ApiResponse<>(201, "Place created successfully", placeMapper.toDto(place));
    }

    public ApiResponse<List<PlaceResponseDTO>> getAllPlaces() {
        List<Place> places = placeRepository.findAllActive();
        List<PlaceResponseDTO> dtos = places.stream()
                .map(placeMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Places retrieved successfully", dtos);
    }

    public ApiResponse<PlaceResponseDTO> getPlaceById(Long id) {
        Place place = placeRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Place retrieved successfully", placeMapper.toDto(place));
    }

    public ApiResponse<PlaceResponseDTO> getPlaceBySlug(String slug) {
        Place place = placeRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Place retrieved successfully", placeMapper.toDto(place));
    }

    @Transactional
    public ApiResponse<PlaceResponseDTO> updatePlace(Long id, UpdatePlaceRequestDTO dto) {
        Account current = getCurrentAccount();
        Place place = placeRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        if (!place.getCreatedBy().getId().equals(current.getId()) && current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("You can only edit your own place", ErrorCode.FORBIDDEN);
        }

        placeMapper.updateEntityFromDto(dto, place);

        if (dto.getName() != null) {
            place.setSlug(generateSlug(dto.getName()));
        }

        if (dto.getCategoryId() != null) {
            PlaceCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ApiException("Category not found", ErrorCode.NOT_FOUND));
            place.setCategory(category);
        }

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new ApiException("Location not found", ErrorCode.NOT_FOUND));
            place.setLocation(location);
        }

        if (dto.getIsActive() != null) {
            place.setIsActive(dto.getIsActive());
            place.setStatus(dto.getIsActive() ? PlaceStatusEnum.ACTIVE : PlaceStatusEnum.INACTIVE);
        }

        place = placeRepository.save(place);

        return new ApiResponse<>(200, "Place updated successfully", placeMapper.toDto(place));
    }

    @Transactional
    public ApiResponse<String> deletePlace(Long id) {
        Account current = getCurrentAccount();
        Place place = placeRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Place not found", ErrorCode.NOT_FOUND));

        if (!place.getCreatedBy().getId().equals(current.getId()) && current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("You can only delete your own place", ErrorCode.FORBIDDEN);
        }

        place.setDeletedAt(LocalDateTime.now());
        place.setStatus(PlaceStatusEnum.DELETED);
        placeRepository.save(place);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_PLACE,
                "Deleted place: " + place.getName(),
                current
        );

        return new ApiResponse<>(200, "Place deleted successfully", null);
    }
}