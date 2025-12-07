package com.tuandatcoder.trekkerbackend.dto.tripday.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTripDayRequestDTO {

    private Integer dayIndex;
    private LocalDate date;
    private String title;
    private String description;
}