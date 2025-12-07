package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.reaction.request.CreateReactionRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.reaction.response.ReactionResponseDTO;
import com.tuandatcoder.trekkerbackend.dto.reaction.response.ReactionSummaryDTO;
import com.tuandatcoder.trekkerbackend.entity.*;
import com.tuandatcoder.trekkerbackend.enums.ReactionTargetTypeEnum;
import com.tuandatcoder.trekkerbackend.enums.ReactionTypeEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.ReactionMapper;
import com.tuandatcoder.trekkerbackend.repository.CommentRepository;
import com.tuandatcoder.trekkerbackend.repository.PhotoRepository;
import com.tuandatcoder.trekkerbackend.repository.PostRepository;
import com.tuandatcoder.trekkerbackend.repository.ReactionRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    @Autowired private ReactionRepository reactionRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;
    @Autowired private ReactionMapper reactionMapper;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private PostRepository postRepository;



    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    @Transactional
    public ApiResponse<ReactionResponseDTO> react(
            ReactionTargetTypeEnum targetType, Long targetId, CreateReactionRequestDTO dto) {

        Account current = getCurrentAccount();

        // Kiểm tra target có tồn tại không (tùy chọn, có thể thêm sau)
        // validateTargetExists(targetType, targetId);

        // Kiểm tra đã react chưa
        Reaction existing = reactionRepository
                .findByAccountIdAndTargetTypeAndTargetIdAndDeletedAtIsNull(current.getId(), targetType, targetId)
                .orElse(null);

        if (existing != null) {
            if (existing.getReactionType() == dto.getReactionType()) {
                // Bấm lại cùng reaction → bỏ react
                existing.setDeletedAt(LocalDateTime.now());
                reactionRepository.save(existing);

                updateTotalReactions(targetType, targetId, -1);

                activityLogger.log(
                        com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.REMOVE_REACTION,
                        "Removed reaction on " + targetType + " ID " + targetId,
                        current
                );

                return new ApiResponse<>(200, "Reaction removed", null);
            } else {
                // Đổi loại reaction
                existing.setReactionType(dto.getReactionType());
                reactionRepository.save(existing);

                activityLogger.log(
                        com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.CHANGE_REACTION,
                        "Changed reaction to " + dto.getReactionType() + " on " + targetType + " ID " + targetId,
                        current
                );

                return new ApiResponse<>(200, "Reaction updated", reactionMapper.toDto(existing));
            }
        }

        // Tạo mới reaction
        Reaction reaction = Reaction.builder()
                .account(current)
                .targetType(targetType)
                .targetId(targetId)
                .reactionType(dto.getReactionType())
                .build();

        reaction = reactionRepository.save(reaction);
        updateTotalReactions(targetType, targetId, +1);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.REACT,
                "Reacted " + dto.getReactionType() + " on " + targetType + " ID " + targetId,
                current
        );

        return new ApiResponse<>(201, "Reacted successfully", reactionMapper.toDto(reaction));
    }

    public ApiResponse<ReactionSummaryDTO> getReactionSummary(ReactionTargetTypeEnum targetType, Long targetId) {
        List<Reaction> reactions = reactionRepository.findByTarget(targetType, targetId);

        Map<ReactionTypeEnum, Integer> countMap = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.summingInt(r -> 1)));

        Account current = accountUtils.getCurrentAccount();
        ReactionTypeEnum myReaction = null;
        if (current != null) {
            myReaction = reactionRepository
                    .findByAccountIdAndTargetTypeAndTargetIdAndDeletedAtIsNull(current.getId(), targetType, targetId)
                    .map(Reaction::getReactionType)
                    .orElse(null);
        }

        ReactionSummaryDTO summary = new ReactionSummaryDTO();
        summary.setTotalReactions(reactions.size());
        summary.setReactionCount(countMap);
        summary.setMyReaction(myReaction);

        return new ApiResponse<>(200, "Reaction summary retrieved", summary);
    }

    public ApiResponse<List<ReactionResponseDTO>> getReactions(ReactionTargetTypeEnum targetType, Long targetId) {
        List<Reaction> reactions = reactionRepository.findByTarget(targetType, targetId);
        return new ApiResponse<>(200, "Reactions retrieved", reactionMapper.toDtoList(reactions));
    }

    // Helper: Cập nhật totalReactions cho Post, Photo, Comment...
    private void updateTotalReactions(ReactionTargetTypeEnum targetType, Long targetId, int delta) {
        switch (targetType) {
            case POST -> {
                Post post = postRepository.findActiveById(targetId).orElse(null);
                if (post != null) {
                    post.setTotalReactions(Math.max(0, post.getTotalReactions() + delta));
                    postRepository.save(post);
                }
            }
            case PHOTO -> {
                Photo photo = photoRepository.findActiveById(targetId).orElse(null);
                if (photo != null) {
                    photo.setTotalReactions(Math.max(0, photo.getTotalReactions() + delta));
                    photoRepository.save(photo);
                }
            }
            case COMMENT -> {
                Comment comment = commentRepository.findActiveById(targetId).orElse(null);
                if (comment != null) {
                    comment.setTotalReactions(Math.max(0, comment.getTotalReactions() + delta));
                    commentRepository.save(comment);
                }
            }
        }
    }
}