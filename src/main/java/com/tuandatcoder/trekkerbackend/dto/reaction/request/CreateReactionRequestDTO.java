package com.tuandatcoder.trekkerbackend.dto.reaction.request;

import com.tuandatcoder.trekkerbackend.enums.ReactionTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReactionRequestDTO {

    @NotNull(message = "Reaction type is required")
    private ReactionTypeEnum reactionType;
}