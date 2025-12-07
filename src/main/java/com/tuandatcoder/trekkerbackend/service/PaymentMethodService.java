package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.request.CreatePaymentMethodRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.request.UpdatePaymentMethodRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.response.PaymentMethodResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.PaymentMethod;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PaymentMethodMapper;
import com.tuandatcoder.trekkerbackend.repository.PaymentMethodRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentMethodService {

    @Autowired private PaymentMethodRepository paymentMethodRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PaymentMethodMapper paymentMethodMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    @Transactional
    public ApiResponse<PaymentMethodResponseDTO> createPaymentMethod(CreatePaymentMethodRequestDTO dto) {
        Account current = getCurrentAccount();

        PaymentMethod method = PaymentMethod.builder()
                .account(current)
                .type(dto.getType())
                .details(dto.getDetails())
                .isDefault(dto.getIsDefault() != null && dto.getIsDefault())
                .build();

        // Nếu là mặc định → bỏ mặc định của cái cũ
        if (method.isDefault()) {
            paymentMethodRepository.findByAccountIdAndIsDefaultTrueAndDeletedAtIsNull(current.getId())
                    .ifPresent(oldDefault -> {
                        oldDefault.setDefault(false);
                        paymentMethodRepository.save(oldDefault);
                    });
        }

        method = paymentMethodRepository.save(method);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.ADD_PAYMENT_METHOD,
                "Added payment method: " + dto.getType(),
                current
        );

        return new ApiResponse<>(201, "Payment method added successfully", paymentMethodMapper.toDto(method));
    }

    public ApiResponse<List<PaymentMethodResponseDTO>> getMyPaymentMethods() {
        Account current = getCurrentAccount();
        List<PaymentMethod> methods = paymentMethodRepository.findByAccountId(current.getId());
        return new ApiResponse<>(200, "Your payment methods", paymentMethodMapper.toDtoList(methods));
    }

    @Transactional
    public ApiResponse<PaymentMethodResponseDTO> updatePaymentMethod(Long id, UpdatePaymentMethodRequestDTO dto) {
        Account current = getCurrentAccount();

        PaymentMethod method = paymentMethodRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Payment method not found", ErrorCode.NOT_FOUND));

        if (!method.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only update your own payment method", ErrorCode.FORBIDDEN);
        }

        paymentMethodMapper.updateEntityFromDto(dto, method);

        // Xử lý mặc định
        if (dto.getIsDefault() != null && dto.getIsDefault() && !method.isDefault()) {
            paymentMethodRepository.findByAccountIdAndIsDefaultTrueAndDeletedAtIsNull(current.getId())
                    .ifPresent(oldDefault -> {
                        oldDefault.setDefault(false);
                        paymentMethodRepository.save(oldDefault);
                    });
            method.setDefault(true);
        }

        method = paymentMethodRepository.save(method);
        return new ApiResponse<>(200, "Payment method updated", paymentMethodMapper.toDto(method));
    }

    @Transactional
    public ApiResponse<String> deletePaymentMethod(Long id) {
        Account current = getCurrentAccount();

        PaymentMethod method = paymentMethodRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Payment method not found", ErrorCode.NOT_FOUND));

        if (!method.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own payment method", ErrorCode.FORBIDDEN);
        }

        method.setDeletedAt(LocalDateTime.now());
        paymentMethodRepository.save(method);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_PAYMENT_METHOD,
                "Deleted payment method",
                current
        );

        return new ApiResponse<>(200, "Payment method deleted", null);
    }
}