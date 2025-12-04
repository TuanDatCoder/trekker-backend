package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.comment.request.CreateCommentRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.comment.request.UpdateCommentRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.comment.response.CommentResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Comment;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.CommentMapper;
import com.tuandatcoder.trekkerbackend.repository.CommentRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired private CommentRepository commentRepository;
    @Autowired private CommentMapper commentMapper;
    @Autowired private AccountUtils accountUtils;
    @Autowired private ActivityLogger activityLogger;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // CREATE COMMENT / REPLY
    @Transactional
    public ApiResponse<CommentResponseDTO> createComment(
            String targetType, Long targetId, CreateCommentRequestDTO dto) {

        Account current = getCurrentAccount();

        Comment parent = null;
        if (dto.getParentCommentId() != null) {
            parent = commentRepository.findActiveById(dto.getParentCommentId())
                    .orElseThrow(() -> new ApiException("Parent comment not found", ErrorCode.NOT_FOUND));
            // Tăng totalReplies của parent
            parent.setTotalReplies(parent.getTotalReplies() + 1);
            commentRepository.save(parent);
        }

        Comment comment = Comment.builder()
                .account(current)
                .targetType(targetType)
                .targetId(targetId)
                .parentComment(parent)
                .content(dto.getContent())
                .totalReactions(0)
                .totalReplies(0)
                .build();

        comment = commentRepository.save(comment);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.COMMENT,
                "Commented on " + targetType + " ID " + targetId +
                        (parent != null ? " (reply to comment " + parent.getId() + ")" : ""),
                current
        );

        return new ApiResponse<>(201, "Comment added successfully", commentMapper.toDto(comment));
    }

    // GET COMMENTS (chỉ root, reply load riêng hoặc nested sau)
    public ApiResponse<List<CommentResponseDTO>> getComments(String targetType, Long targetId) {
        List<Comment> comments = commentRepository.findRootComments(targetType, targetId);
        List<CommentResponseDTO> dtos = commentMapper.toDtoList(comments);
        return new ApiResponse<>(200, "Comments retrieved successfully", dtos);
    }

    // GET REPLIES
    public ApiResponse<List<CommentResponseDTO>> getReplies(Long parentCommentId) {
        List<Comment> replies = commentRepository.findRepliesByParentId(parentCommentId);
        return new ApiResponse<>(200, "Replies retrieved successfully", commentMapper.toDtoList(replies));
    }

    // UPDATE
    @Transactional
    public ApiResponse<CommentResponseDTO> updateComment(Long commentId, UpdateCommentRequestDTO dto) {
        Comment comment = commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new ApiException("Comment not found", ErrorCode.NOT_FOUND));

        Account current = getCurrentAccount();
        if (!comment.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only edit your own comment", ErrorCode.FORBIDDEN);
        }

        comment.setContent(dto.getContent());
        comment = commentRepository.save(comment);

        return new ApiResponse<>(200, "Comment updated successfully", commentMapper.toDto(comment));
    }

    // DELETE (soft delete)
    @Transactional
    public ApiResponse<String> deleteComment(Long commentId) {
        Comment comment = commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new ApiException("Comment not found", ErrorCode.NOT_FOUND));

        Account current = getCurrentAccount();
        if (!comment.getAccount().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own comment", ErrorCode.FORBIDDEN);
        }

        // Nếu là comment gốc có reply → không xóa thật, chỉ ẩn nội dung
        if (comment.getTotalReplies() > 0) {
            comment.setContent("[This comment has been deleted]");
            comment.setDeletedAt(LocalDateTime.now());
            commentRepository.save(comment);
            return new ApiResponse<>(200, "Comment deleted (has replies)", null);
        }

        // Nếu là reply → giảm totalReplies của parent
        if (comment.getParentComment() != null) {
            Comment parent = comment.getParentComment();
            parent.setTotalReplies(Math.max(0, parent.getTotalReplies() - 1));
            commentRepository.save(parent);
        }

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);

        activityLogger.log(
                com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum.DELETE_COMMENT,
                "Deleted comment ID " + commentId,
                current
        );

        return new ApiResponse<>(200, "Comment deleted successfully", null);
    }
}