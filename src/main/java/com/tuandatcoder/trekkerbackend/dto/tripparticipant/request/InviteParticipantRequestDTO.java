package com.tuandatcoder.trekkerbackend.dto.tripparticipant.request;

import com.tuandatcoder.trekkerbackend.enums.TripParticipantRoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteParticipantRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private TripParticipantRoleEnum role;
}