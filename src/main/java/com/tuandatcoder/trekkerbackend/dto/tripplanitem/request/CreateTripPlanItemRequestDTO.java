package com.tuandatcoder.trekkerbackend.dto.tripplanitem.request;

import com.tuandatcoder.trekkerbackend.enums.TripPlanItemCategoryEnum;
import com.tuandatcoder.trekkerbackend.enums.TripPlanItemStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripPlanItemRequestDTO {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    private Long tripDayId;
    private Long placeId;
    private Long locationId;

    private String title;
    private String description;

    private TripPlanItemCategoryEnum category;

    @NotNull(message = "Status is required")
    private TripPlanItemStatusEnum status;

    private Integer orderIndex;
    private LocalDateTime scheduledTime;
    private BigDecimal estimatedCost;
    private String notes;
}