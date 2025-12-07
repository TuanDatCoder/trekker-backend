package com.tuandatcoder.trekkerbackend.dto.place.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponseDTO {
    private Long id;
    private String name;
    private String slug;

    private Long categoryId;
    private String categoryName;

    private Long locationId;
    private String locationName;

    private String address;
    private String phone;
    private String website;
    private String openingHours;
    private String priceRange;
    private String description;

    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer totalCheckins;

    private boolean isVerified;
    private boolean isActive;

    private Long createdById;
    private String createdByUsername;
    private String createdByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}