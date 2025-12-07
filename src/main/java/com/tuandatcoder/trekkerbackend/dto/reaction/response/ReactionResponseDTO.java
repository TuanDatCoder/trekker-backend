package com.tuandatcoder.trekkerbackend.dto.reaction.response;

import com.tuandatcoder.trekkerbackend.enums.ReactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponseDTO {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private String accountPicture;

    private ReactionTypeEnum reactionType;
    private LocalDateTime createdAt;
}