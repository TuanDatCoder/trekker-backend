package com.tuandatcoder.trekkerbackend.dto.feature.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequestDTO {

    @NotBlank(message = "Feature name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String description;

    private Boolean isActive; // null = không thay đổi khi update
}