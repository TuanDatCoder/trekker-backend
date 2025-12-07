package com.tuandatcoder.trekkerbackend.dto.reaction.response;

import com.tuandatcoder.trekkerbackend.enums.ReactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionSummaryDTO {
    private Integer totalReactions;
    private Map<ReactionTypeEnum, Integer> reactionCount;
    private ReactionTypeEnum myReaction; // null nếu chưa react
}