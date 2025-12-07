package com.tuandatcoder.trekkerbackend.dto.planfeature.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanFeatureRequestDTO {

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotNull(message = "Feature ID is required")
    private Long featureId;
}