package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.request.CreateUserSubscriptionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.request.UpdateUserSubscriptionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.usersubscription.response.UserSubscriptionResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.enums.UserSubscriptionStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.UserSubscriptionMapper;
import com.tuandatcoder.trekkerbackend.repository.SubscriptionPlanRepository;
import com.tuandatcoder.trekkerbackend.repository.UserSubscriptionRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSubscriptionService {

    @Autowired private UserSubscriptionRepository subscriptionRepository;
    @Autowired private SubscriptionPlanRepository planRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private UserSubscriptionMapper subscriptionMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkAdmin() {
        Account current = getCurrentAccount();
        if (current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage user subscriptions", ErrorCode.FORBIDDEN);
        }
    }

    // USER: Đăng ký gói (gọi sau khi thanh toán thành công)
    @Transactional
    public ApiResponse<UserSubscriptionResponseDTO> subscribe(CreateUserSubscriptionRequestDTO dto) {
        Account current = getCurrentAccount();

        SubscriptionPlan plan = planRepository.findActiveById(dto.getPlanId())
                .orElseThrow(() -> new ApiException("Subscription plan not found", ErrorCode.NOT_FOUND));

        // Kiểm tra đã có gói active chưa
        subscriptionRepository.findActiveSubscriptionByAccount(current.getId())
                .ifPresent(active -> {
                    throw new ApiException("You already have an active subscription", ErrorCode.USER_EXISTED);
                });

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1); // Ví dụ: gói 1 tháng

        UserSubscription subscription = UserSubscription.builder()
                .account(current)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status(UserSubscriptionStatusEnum.ACTIVE)
                .build();

        subscription = subscriptionRepository.save(subscription);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.SUBSCRIBE_PLAN,
                "Subscribed to plan: " + plan.getName(),
                current
        );

        return new ApiResponse<>(201, "Subscribed successfully", subscriptionMapper.toDto(subscription));
    }

    // USER: Lấy gói hiện tại
    public ApiResponse<UserSubscriptionResponseDTO> getMyActiveSubscription() {
        Account current = getCurrentAccount();

        UserSubscription subscription = subscriptionRepository.findActiveSubscriptionByAccount(current.getId())
                .orElse(null);

        if (subscription == null) {
            return new ApiResponse<>(200, "No active subscription", null);
        }

        return new ApiResponse<>(200, "Active subscription retrieved", subscriptionMapper.toDto(subscription));
    }

    // USER: Lấy lịch sử gói
    public ApiResponse<List<UserSubscriptionResponseDTO>> getMySubscriptionHistory() {
        Account current = getCurrentAccount();
        List<UserSubscription> history = subscriptionRepository.findByAccountId(current.getId());
        return new ApiResponse<>(200, "Subscription history retrieved", subscriptionMapper.toDtoList(history));
    }

    // ADMIN: Cập nhật trạng thái (hủy, gia hạn…)
    @Transactional
    public ApiResponse<UserSubscriptionResponseDTO> updateSubscription(Long id, UpdateUserSubscriptionRequestDTO dto) {
        checkAdmin();

        UserSubscription subscription = subscriptionRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Subscription not found", ErrorCode.NOT_FOUND));

        if (dto.getStatus() != null) {
            subscription.setStatus(dto.getStatus());
        }
        if (dto.getEndDate() != null) {
            subscription.setEndDate(dto.getEndDate());
        }

        subscription = subscriptionRepository.save(subscription);
        return new ApiResponse<>(200, "Subscription updated", subscriptionMapper.toDto(subscription));
    }

    // ADMIN: Xóa (soft delete)
    @Transactional
    public ApiResponse<String> deleteSubscription(Long id) {
        checkAdmin();

        UserSubscription subscription = subscriptionRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Subscription not found", ErrorCode.NOT_FOUND));

        subscription.setDeletedAt(LocalDateTime.now());
        subscription.setStatus(UserSubscriptionStatusEnum.DELETED);
        subscriptionRepository.save(subscription);

        return new ApiResponse<>(200, "Subscription deleted", null);
    }
}