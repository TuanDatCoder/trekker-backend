package com.tuandatcoder.trekkerbackend.dto.trip.request;

import com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum;
import com.tuandatcoder.trekkerbackend.enums.TripStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Description too long")
    private String description;

    @NotBlank(message = "Destination is required")
    @Size(max = 255)
    private String destination;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Long coverPhotoId; // optional khi tạo

    @NotNull
    private TripPrivacyEnum privacy;

    private boolean isCollaborative = false;
}