package com.tuandatcoder.trekkerbackend.dto.tripplanitem.request;

import com.tuandatcoder.trekkerbackend.enums.TripPlanItemCategoryEnum;
import com.tuandatcoder.trekkerbackend.enums.TripPlanItemStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTripPlanItemRequestDTO {

    private Long tripDayId;
    private Long placeId;
    private Long locationId;

    private String title;
    private String description;
    private TripPlanItemCategoryEnum category;
    private TripPlanItemStatusEnum status;
    private Integer orderIndex;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualTime;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private String notes;
}
