package com.tuandatcoder.trekkerbackend.dto.place.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceRequestDTO {

    @NotBlank(message = "Place name is required")
    @Size(max = 255)
    private String name;

    private Long categoryId;

    private Long locationId;

    private String address;

    @Size(max = 50)
    private String phone;

    @Size(max = 255)
    private String website;

    private String openingHours; // JSON string

    @Size(max = 10)
    private String priceRange;

    private String description;
}