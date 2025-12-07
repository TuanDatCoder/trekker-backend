package com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionPlanRequestDTO {

    private String name;
    private String description;
    @PositiveOrZero
    private BigDecimal price;
    private String currency;
    private String billingCycle;
    private Boolean isActive;
}