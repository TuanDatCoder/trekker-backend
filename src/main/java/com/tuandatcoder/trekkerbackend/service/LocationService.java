package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.location.request.CreateLocationRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.location.request.UpdateLocationRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.location.response.LocationResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Location;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.LocationMapper;
import com.tuandatcoder.trekkerbackend.repository.LocationRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocationService {

    @Autowired private LocationRepository locationRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private LocationMapper locationMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // Tạo mới hoặc trả về location đã tồn tại (tránh duplicate)
    @Transactional
    public Location getOrCreateLocation(CreateLocationRequestDTO dto) {
        // Tìm location gần nhất (trong khoảng ±0.0001 độ ~ 10 mét)
        BigDecimal latMin = dto.getLatitude().subtract(BigDecimal.valueOf(0.0001));
        BigDecimal latMax = dto.getLatitude().add(BigDecimal.valueOf(0.0001));
        BigDecimal lngMin = dto.getLongitude().subtract(BigDecimal.valueOf(0.0001));
        BigDecimal lngMax = dto.getLongitude().add(BigDecimal.valueOf(0.0001));

        List<Location> nearby = locationRepository
                .findByLatitudeBetweenAndLongitudeBetweenAndDeletedAtIsNull(latMin, latMax, lngMin, lngMax);

        if (!nearby.isEmpty()) {
            return nearby.get(0); // trả về cái đầu tiên (có thể cải thiện sau)
        }

        Location location = Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .build();

        return locationRepository.save(location);
    }

    @Transactional
    public ApiResponse<LocationResponseDTO> createLocation(CreateLocationRequestDTO dto) {
        Account current = getCurrentAccount();

        Location location = getOrCreateLocation(dto);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_LOCATION,
                "Created location: " + dto.getLatitude() + ", " + dto.getLongitude(),
                current
        );

        return new ApiResponse<>(201, "Location created successfully", locationMapper.toDto(location));
    }

    public ApiResponse<List<LocationResponseDTO>> getAllLocations() {
        List<Location> locations = locationRepository.findAllActive();
        return new ApiResponse<>(200, "Locations retrieved successfully", locationMapper.toDtoList(locations));
    }

    public ApiResponse<LocationResponseDTO> getLocationById(Long id) {
        Location location = locationRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Location not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Location retrieved successfully", locationMapper.toDto(location));
    }

    @Transactional
    public ApiResponse<LocationResponseDTO> updateLocation(Long id, UpdateLocationRequestDTO dto) {
        Account current = getCurrentAccount();
        Location location = locationRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Location not found", ErrorCode.NOT_FOUND));

        locationMapper.updateEntityFromDto(dto, location);
        location = locationRepository.save(location);

        return new ApiResponse<>(200, "Location updated successfully", locationMapper.toDto(location));
    }

    @Transactional
    public ApiResponse<String> deleteLocation(Long id) {
        Account current = getCurrentAccount();
        Location location = locationRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Location not found", ErrorCode.NOT_FOUND));

        location.setDeletedAt(LocalDateTime.now());
        locationRepository.save(location);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_LOCATION,
                "Deleted location ID: " + id,
                current
        );

        return new ApiResponse<>(200, "Location deleted successfully", null);
    }
}