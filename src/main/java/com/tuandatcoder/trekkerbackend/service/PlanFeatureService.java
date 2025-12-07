package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.planfeature.request.CreatePlanFeatureRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.planfeature.response.PlanFeatureResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PlanFeatureMapper;
import com.tuandatcoder.trekkerbackend.repository.FeatureRepository;
import com.tuandatcoder.trekkerbackend.repository.PlanFeatureRepository;
import com.tuandatcoder.trekkerbackend.repository.SubscriptionPlanRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanFeatureService {

    @Autowired private PlanFeatureRepository planFeatureRepository;
    @Autowired private SubscriptionPlanRepository planRepository;
    @Autowired private FeatureRepository featureRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PlanFeatureMapper planFeatureMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkAdmin() {
        Account current = getCurrentAccount();
        if (current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage plan features", ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<PlanFeatureResponseDTO> addFeatureToPlan(CreatePlanFeatureRequestDTO dto) {
        checkAdmin();

        SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new ApiException("Subscription plan not found", ErrorCode.NOT_FOUND));

        Feature feature = featureRepository.findActiveById(dto.getFeatureId())
                .orElseThrow(() -> new ApiException("Feature not found", ErrorCode.NOT_FOUND));

        if (planFeatureRepository.existsByPlanIdAndFeatureIdAndDeletedAtIsNull(dto.getPlanId(), dto.getFeatureId())) {
            throw new ApiException("This feature is already added to the plan", ErrorCode.USER_EXISTED);
        }

        PlanFeature planFeature = PlanFeature.builder()
                .plan(plan)
                .feature(feature)
                .build();

        planFeature = planFeatureRepository.save(planFeature);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.ADD_FEATURE_TO_PLAN,
                "Added feature '" + feature.getName() + "' to plan '" + plan.getName() + "'",
                getCurrentAccount()
        );

        return new ApiResponse<>(201, "Feature added to plan successfully", planFeatureMapper.toDto(planFeature));
    }

    public ApiResponse<List<PlanFeatureResponseDTO>> getFeaturesByPlanId(Long planId) {
        planRepository.findById(planId)
                .orElseThrow(() -> new ApiException("Subscription plan not found", ErrorCode.NOT_FOUND));

        List<PlanFeature> planFeatures = planFeatureRepository.findByPlanIdActive(planId);
        return new ApiResponse<>(200, "Plan features retrieved successfully", planFeatureMapper.toDtoList(planFeatures));
    }

    public ApiResponse<List<PlanFeatureResponseDTO>> getAllPlanFeatures() {
        List<PlanFeature> planFeatures = planFeatureRepository.findAllActive();
        return new ApiResponse<>(200, "All plan features retrieved successfully", planFeatureMapper.toDtoList(planFeatures));
    }

    @Transactional
    public ApiResponse<String> removeFeatureFromPlan(Long planFeatureId) {
        checkAdmin();

        PlanFeature planFeature = planFeatureRepository.findActiveById(planFeatureId)
                .orElseThrow(() -> new ApiException("Plan feature not found", ErrorCode.NOT_FOUND));

        planFeature.setDeletedAt(LocalDateTime.now());
        planFeatureRepository.save(planFeature);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.REMOVE_FEATURE_FROM_PLAN,
                "Removed feature '" + planFeature.getFeature().getName() +
                        "' from plan '" + planFeature.getPlan().getName() + "'",
                getCurrentAccount()
        );

        return new ApiResponse<>(200, "Feature removed from plan successfully", null);
    }
}