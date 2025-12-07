package com.tuandatcoder.trekkerbackend.dto.paymenttransaction.request;

import com.tuandatcoder.trekkerbackend.enums.PaymentTransactionStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionRequestDTO {

    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Transaction ID from gateway is required")
    private String transactionId;

    private String description;
}
