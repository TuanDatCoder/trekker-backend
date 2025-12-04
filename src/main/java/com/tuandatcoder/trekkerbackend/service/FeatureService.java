package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.feature.request.FeatureRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.feature.response.FeatureResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Feature;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.FeatureMapper;
import com.tuandatcoder.trekkerbackend.repository.FeatureRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    @Autowired private FeatureRepository featureRepository;
    @Autowired private FeatureMapper featureMapper;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;

    private void checkAdmin() {
        Account current = accountUtils.getCurrentAccount();
        if (current == null || current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage features", ErrorCode.FORBIDDEN);
        }
    }

    // CREATE
    @Transactional
    public ApiResponse<FeatureResponseDTO> createFeature(FeatureRequestDTO dto) {
        checkAdmin();

        if (featureRepository.existsByNameAndDeletedAtIsNull(dto.getName())) {
            throw new ApiException("Feature name already exists", ErrorCode.USER_EXISTED); // tái sử dụng
        }

        Feature feature = Feature.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        feature = featureRepository.save(feature);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_FEATURE,
                "Created feature: " + feature.getName(),
                accountUtils.getCurrentAccount()
        );

        return new ApiResponse<>(201, "Feature created successfully", featureMapper.toDto(feature));
    }

    // READ ALL (active only)
    public ApiResponse<List<FeatureResponseDTO>> getAllFeatures() {
        List<Feature> features = featureRepository.findAllActive();
        List<FeatureResponseDTO> dtos = features.stream()
                .map(featureMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Features retrieved successfully", dtos);
    }

    // READ ONE
    public ApiResponse<FeatureResponseDTO> getFeatureById(Long id) {
        Feature feature = featureRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Feature not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Feature retrieved successfully", featureMapper.toDto(feature));
    }

    // UPDATE
    @Transactional
    public ApiResponse<FeatureResponseDTO> updateFeature(Long id, FeatureRequestDTO dto) {
        checkAdmin();

        Feature feature = featureRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Feature not found", ErrorCode.NOT_FOUND));

        // Kiểm tra tên trùng (trừ chính nó)
        if (!feature.getName().equals(dto.getName()) &&
                featureRepository.existsByNameAndDeletedAtIsNull(dto.getName())) {
            throw new ApiException("Feature name already exists", ErrorCode.USER_EXISTED);
        }

        featureMapper.updateEntityFromDto(dto, feature);
        if (dto.getIsActive() != null) {
            feature.setActive(dto.getIsActive());
        }

        feature = featureRepository.save(feature);

        return new ApiResponse<>(200, "Feature updated successfully", featureMapper.toDto(feature));
    }

    // DELETE (soft delete)
    @Transactional
    public ApiResponse<String> deleteFeature(Long id) {
        checkAdmin();

        Feature feature = featureRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Feature not found", ErrorCode.NOT_FOUND));

        feature.setDeletedAt(LocalDateTime.now());
        featureRepository.save(feature);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_FEATURE,
                "Deleted feature: " + feature.getName(),
                accountUtils.getCurrentAccount()
        );

        return new ApiResponse<>(200, "Feature deleted successfully", null);
    }
}