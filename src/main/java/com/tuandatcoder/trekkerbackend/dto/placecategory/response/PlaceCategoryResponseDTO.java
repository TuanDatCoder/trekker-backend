package com.tuandatcoder.trekkerbackend.dto.placecategory.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCategoryResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String icon;
    private String color;
    private String description;
    private Integer orderIndex;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}