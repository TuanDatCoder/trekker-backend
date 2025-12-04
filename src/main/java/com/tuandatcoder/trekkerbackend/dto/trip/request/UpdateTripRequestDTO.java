package com.tuandatcoder.trekkerbackend.dto.trip.request;

import com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum;
import com.tuandatcoder.trekkerbackend.enums.TripStatusEnum;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTripRequestDTO {

    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String description;

    @Size(max = 255)
    private String destination;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long coverPhotoId;
    private TripPrivacyEnum privacy;
    private TripStatusEnum status;
    private Boolean isCollaborative;
}