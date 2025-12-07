package com.tuandatcoder.trekkerbackend.dto.tag.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequestDTO {

    @NotNull(message = "Target type is required")
    private String targetType; // PHOTO, POST

    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotNull(message = "Tagged user ID is required")
    private Long taggedUserId;

    @NotNull(message = "X position is required")
    private BigDecimal xPosition;

    @NotNull(message = "Y position is required")
    private BigDecimal yPosition;
}
