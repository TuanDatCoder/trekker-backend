package com.tuandatcoder.trekkerbackend.dto.planfeature.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanFeatureResponseDTO {
    private Long id;
    private Long planId;
    private String planName;
    private Long featureId;
    private String featureName;
    private String featureDescription;
    private LocalDateTime createdAt;
}