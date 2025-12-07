package com.tuandatcoder.trekkerbackend.dto.tag.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDTO {
    private Long id;

    private String targetType;
    private Long targetId;

    private Long taggedUserId;
    private String taggedUsername;
    private String taggedName;
    private String taggedPicture;

    private Long taggedById;
    private String taggedByUsername;

    private BigDecimal xPosition;
    private BigDecimal yPosition;

    private LocalDateTime createdAt;
}