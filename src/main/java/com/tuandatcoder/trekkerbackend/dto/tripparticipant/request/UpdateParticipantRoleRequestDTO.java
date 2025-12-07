package com.tuandatcoder.trekkerbackend.dto.tripparticipant.request;

import com.tuandatcoder.trekkerbackend.enums.TripParticipantRoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParticipantRoleRequestDTO {

    @NotNull(message = "Role is required")
    private TripParticipantRoleEnum role;
}