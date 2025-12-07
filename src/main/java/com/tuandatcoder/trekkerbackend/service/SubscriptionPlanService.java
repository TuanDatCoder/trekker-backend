package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.CreateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.UpdateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.response.SubscriptionPlanResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.SubscriptionPlan;
import com.tuandatcoder.trekkerbackend.enums.SubscriptionPlanStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.SubscriptionPlanMapper;
import com.tuandatcoder.trekkerbackend.repository.SubscriptionPlanRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionPlanService {

    @Autowired private SubscriptionPlanRepository planRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private SubscriptionPlanMapper planMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkAdmin() {
        Account current = getCurrentAccount();
        if (current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage subscription plans", ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public ApiResponse<SubscriptionPlanResponseDTO> createPlan(CreateSubscriptionPlanRequestDTO dto) {
        checkAdmin();

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .currency(dto.getCurrency().toUpperCase())
                .billingCycle(dto.getBillingCycle().toUpperCase())
                .isActive(true)
                .status(SubscriptionPlanStatusEnum.ACTIVE)
                .build();

        plan = planRepository.save(plan);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CREATE_SUBSCRIPTION_PLAN,
                "Created subscription plan: " + plan.getName(),
                getCurrentAccount()
        );

        return new ApiResponse<>(201, "Subscription plan created successfully", planMapper.toDto(plan));
    }

    // Public: Lấy các gói đang active (dùng cho frontend chọn gói)
    public ApiResponse<List<SubscriptionPlanResponseDTO>> getActivePlans() {
        List<SubscriptionPlan> plans = planRepository.findAllActiveAndVisible();
        return new ApiResponse<>(200, "Active subscription plans retrieved", planMapper.toDtoList(plans));
    }

    // Admin: Lấy tất cả (kể cả inactive)
    public ApiResponse<List<SubscriptionPlanResponseDTO>> getAllPlans() {
        checkAdmin();
        List<SubscriptionPlan> plans = planRepository.findAllActive();
        return new ApiResponse<>(200, "All subscription plans retrieved", planMapper.toDtoList(plans));
    }

    public ApiResponse<SubscriptionPlanResponseDTO> getPlanById(Long id) {
        SubscriptionPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Subscription plan not found", ErrorCode.NOT_FOUND));

        return new ApiResponse<>(200, "Subscription plan retrieved", planMapper.toDto(plan));
    }

    @Transactional
    public ApiResponse<SubscriptionPlanResponseDTO> updatePlan(Long id, UpdateSubscriptionPlanRequestDTO dto) {
        checkAdmin();

        SubscriptionPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Subscription plan not found", ErrorCode.NOT_FOUND));

        planMapper.updateEntityFromDto(dto, plan);

        if (dto.getIsActive() != null) {
            plan.setActive(dto.getIsActive());
            plan.setStatus(dto.getIsActive() ? SubscriptionPlanStatusEnum.ACTIVE : SubscriptionPlanStatusEnum.INACTIVE);
        }

        plan = planRepository.save(plan);

        return new ApiResponse<>(200, "Subscription plan updated successfully", planMapper.toDto(plan));
    }

    @Transactional
    public ApiResponse<String> deletePlan(Long id) {
        checkAdmin();

        SubscriptionPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Subscription plan not found", ErrorCode.NOT_FOUND));

        plan.setDeletedAt(LocalDateTime.now());
        plan.setStatus(SubscriptionPlanStatusEnum.DELETED);
        planRepository.save(plan);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_SUBSCRIPTION_PLAN,
                "Deleted subscription plan: " + plan.getName(),
                getCurrentAccount()
        );

        return new ApiResponse<>(200, "Subscription plan deleted successfully", null);
    }
}
