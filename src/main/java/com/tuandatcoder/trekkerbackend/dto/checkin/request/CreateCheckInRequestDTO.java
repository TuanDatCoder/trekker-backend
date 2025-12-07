package com.tuandatcoder.trekkerbackend.dto.checkin.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCheckInRequestDTO {

    @NotNull(message = "Place ID is required")
    private Long placeId;

    private Long tripId;
    private Long photoId;
    private String note;
}