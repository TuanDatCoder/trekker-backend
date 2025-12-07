package com.tuandatcoder.trekkerbackend.dto.paymentmethod.response;

import com.tuandatcoder.trekkerbackend.enums.PaymentMethodTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponseDTO {
    private Long id;
    private PaymentMethodTypeEnum type;
    private String details; // masked hoặc token
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}