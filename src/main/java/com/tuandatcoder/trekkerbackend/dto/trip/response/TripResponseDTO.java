package com.tuandatcoder.trekkerbackend.dto.trip.response;

import com.tuandatcoder.trekkerbackend.enums.TripPrivacyEnum;
import com.tuandatcoder.trekkerbackend.enums.TripStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDTO {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private String accountName;
    private String accountPicture;

    private String title;
    private String description;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long coverPhotoId;
    private String coverPhotoUrl;

    private TripStatusEnum status;
    private TripPrivacyEnum privacy;
    private boolean isCollaborative;

    private Integer totalDays;
    private Integer totalPhotos;
    private Integer totalParticipants;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}