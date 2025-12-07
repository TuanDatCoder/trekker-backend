package com.tuandatcoder.trekkerbackend.controller;
import com.tuandatcoder.trekkerbackend.dto.ApiResponse;

import com.tuandatcoder.trekkerbackend.dto.reaction.request.CreateReactionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.reaction.response.ReactionResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.reaction.response.ReactionSummaryDTO;
import com.tuandatcoder.trekkerbackend.enums.ReactionTargetTypeEnum;
import com.tuandatcoder.trekkerbackend.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reactions")
public class ReactionController {

    @Autowired private ReactionService reactionService;

    @PostMapping("/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<ReactionResponseDTO>> react(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @Valid @RequestBody CreateReactionRequestDTO dto) {

        ReactionTargetTypeEnum type = ReactionTargetTypeEnum.valueOf(targetType.toUpperCase());
        ApiResponse<ReactionResponseDTO> response = reactionService.react(type, targetId, dto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/summary/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<ReactionSummaryDTO>> getSummary(
            @PathVariable String targetType,
            @PathVariable Long targetId) {

        ReactionTargetTypeEnum type = ReactionTargetTypeEnum.valueOf(targetType.toUpperCase());
        return ResponseEntity.ok(reactionService.getReactionSummary(type, targetId));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<ApiResponse<List<ReactionResponseDTO>>> getReactions(
            @PathVariable String targetType,
            @PathVariable Long targetId) {

        ReactionTargetTypeEnum type = ReactionTargetTypeEnum.valueOf(targetType.toUpperCase());
        return ResponseEntity.ok(reactionService.getReactions(type, targetId));
    }
}
