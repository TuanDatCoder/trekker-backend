package com.tuandatcoder.trekkerbackend.dto.placecategory.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlaceCategoryRequestDTO {

    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String icon;

    @Size(max = 50)
    private String color;

    private String description;

    private Integer orderIndex;

    private Boolean isActive;
}