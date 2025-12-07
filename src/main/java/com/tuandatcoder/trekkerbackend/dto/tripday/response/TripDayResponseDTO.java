package com.tuandatcoder.trekkerbackend.dto.tripday.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDayResponseDTO {
    private Long id;
    private Long tripId;
    private String tripTitle;

    private Integer dayIndex;
    private LocalDate date;
    private String title;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
