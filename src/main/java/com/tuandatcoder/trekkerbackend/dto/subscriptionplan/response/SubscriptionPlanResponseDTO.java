package com.tuandatcoder.trekkerbackend.dto.subscriptionplan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String billingCycle;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}