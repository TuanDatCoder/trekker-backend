package com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionPlanRequestDTO {

    @NotBlank(message = "Plan name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @PositiveOrZero
    private BigDecimal price;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Billing cycle is required")
    private String billingCycle; // MONTHLY, YEARLY
}