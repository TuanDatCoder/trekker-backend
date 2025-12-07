package com.tuandatcoder.trekkerbackend.dto.paymenttransaction.response;

import com.tuandatcoder.trekkerbackend.enums.PaymentTransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponseDTO {
    private Long id;

    private Long accountId;
    private String accountUsername;

    private Long subscriptionId;
    private String planName;

    private BigDecimal amount;
    private String currency;

    private PaymentTransactionStatusEnum status;
    private String transactionId;
    private String description;

    private LocalDateTime createdAt;
}