package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.paymenttransaction.request.CreatePaymentTransactionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymenttransaction.response.PaymentTransactionResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.enums.PaymentTransactionStatusEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.PaymentTransactionMapper;
import com.tuandatcoder.trekkerbackend.repository.PaymentTransactionRepository;
import com.tuandatcoder.trekkerbackend.repository.UserSubscriptionRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentTransactionService {

    @Autowired private PaymentTransactionRepository transactionRepository;
    @Autowired private UserSubscriptionRepository subscriptionRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private PaymentTransactionMapper transactionMapper;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    private void checkAdmin() {
        Account current = getCurrentAccount();
        if (current.getRole() != com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum.ADMIN) {
            throw new ApiException("Only admin can manage payment transactions", ErrorCode.FORBIDDEN);
        }
    }

    // Gọi khi thanh toán thành công (từ Stripe/PayPal webhook hoặc frontend)
    @Transactional
    public ApiResponse<PaymentTransactionResponseDTO> createTransaction(CreatePaymentTransactionRequestDTO dto) {
        Account current = getCurrentAccount();

        UserSubscription subscription = subscriptionRepository.findActiveById(dto.getSubscriptionId())
                .orElseThrow(() -> new ApiException("Subscription not found", ErrorCode.NOT_FOUND));

        if (!subscription.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only pay for your own subscription", ErrorCode.FORBIDDEN);
        }

        // Kiểm tra trùng transactionId (tránh double charge)
        if (transactionRepository.findByTransactionIdAndDeletedAtIsNull(dto.getTransactionId()).isPresent()) {
            throw new ApiException("Transaction already exists", ErrorCode.USER_EXISTED);
        }

        PaymentTransaction transaction = PaymentTransaction.builder()
                .account(current)
                .subscription(subscription)
                .amount(dto.getAmount())
                .currency(dto.getCurrency().toUpperCase())
                .transactionId(dto.getTransactionId())
                .description(dto.getDescription())
                .status(PaymentTransactionStatusEnum.SUCCESS)
                .build();

        transaction = transactionRepository.save(transaction);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.PAYMENT_SUCCESS,
                "Payment successful: " + dto.getAmount() + " " + dto.getCurrency() + " - Transaction: " + dto.getTransactionId(),
                current
        );

        return new ApiResponse<>(201, "Payment recorded successfully", transactionMapper.toDto(transaction));
    }

    // USER: Lấy lịch sử thanh toán
    public ApiResponse<List<PaymentTransactionResponseDTO>> getMyTransactions() {
        Account current = getCurrentAccount();
        List<PaymentTransaction> transactions = transactionRepository.findByAccountId(current.getId());
        return new ApiResponse<>(200, "Your payment history", transactionMapper.toDtoList(transactions));
    }

    // ADMIN: Lấy tất cả giao dịch
    public ApiResponse<List<PaymentTransactionResponseDTO>> getAllTransactions() {
        checkAdmin();
        List<PaymentTransaction> transactions = transactionRepository.findAll();
        return new ApiResponse<>(200, "All payment transactions", transactionMapper.toDtoList(transactions));
    }

    // ADMIN: Xóa giao dịch (soft delete)
    @Transactional
    public ApiResponse<String> deleteTransaction(Long id) {
        checkAdmin();

        PaymentTransaction transaction = transactionRepository.findActiveById(id)
                .orElseThrow(() -> new ApiException("Transaction not found", ErrorCode.NOT_FOUND));

        transaction.setDeletedAt(LocalDateTime.now());
        transaction.setStatus(PaymentTransactionStatusEnum.DELETED);
        transactionRepository.save(transaction);

        return new ApiResponse<>(200, "Transaction deleted", null);
    }
}