package com.tuandatcoder.trekkerbackend.dto.tripplanitem.response;

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
public class TripPlanItemResponseDTO {
    private Long id;

    private Long tripId;
    private String tripTitle;

    private Long tripDayId;
    private Integer tripDayIndex;

    private Long placeId;
    private String placeName;

    private Long locationId;
    private String locationAddress;

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}