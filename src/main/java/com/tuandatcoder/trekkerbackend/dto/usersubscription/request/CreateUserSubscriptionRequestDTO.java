package com.tuandatcoder.trekkerbackend.dto.usersubscription.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserSubscriptionRequestDTO {

    @NotNull(message = "Plan ID is required")
    private Long planId;
}