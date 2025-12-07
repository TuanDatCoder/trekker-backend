package com.tuandatcoder.trekkerbackend.dto.paymentmethod.request;

import com.tuandatcoder.trekkerbackend.enums.PaymentMethodTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentMethodRequestDTO {

    @NotNull(message = "Type is required")
    private PaymentMethodTypeEnum type;

    @NotBlank(message = "Details/token is required")
    private String details; // token từ Stripe, PayPal, hoặc encrypted card

    private Boolean isDefault;
}