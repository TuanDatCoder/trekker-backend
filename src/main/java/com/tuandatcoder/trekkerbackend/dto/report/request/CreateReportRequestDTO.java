package com.tuandatcoder.trekkerbackend.dto.report.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequestDTO {

    @NotBlank(message = "Target type is required")
    private String targetType; // PHOTO, POST, COMMENT, USER

    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String description;
}