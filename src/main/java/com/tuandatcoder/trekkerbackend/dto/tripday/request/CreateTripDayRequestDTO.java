package com.tuandatcoder.trekkerbackend.dto.tripday.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripDayRequestDTO {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Day index is required")
    private Integer dayIndex;

    private LocalDate date;

    private String title;
    private String description;
}