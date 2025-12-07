package com.tuandatcoder.trekkerbackend.dto.tripparticipant.response;

import com.tuandatcoder.trekkerbackend.enums.TripParticipantRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.TripParticipantStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripParticipantResponseDTO {
    private Long id;

    private Long tripId;
    private String tripTitle;

    private Long userId;
    private String username;
    private String name;
    private String picture;

    private TripParticipantRoleEnum role;
    private TripParticipantStatusEnum status;

    private Long invitedById;
    private String invitedByUsername;

    private LocalDateTime invitedAt;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
}