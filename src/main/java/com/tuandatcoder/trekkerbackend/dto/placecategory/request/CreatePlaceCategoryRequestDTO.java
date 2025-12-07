package com.tuandatcoder.trekkerbackend.dto.placecategory.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceCategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 255)
    private String icon;

    @Size(max = 50)
    private String color;

    private String description;

    private Integer orderIndex;
}