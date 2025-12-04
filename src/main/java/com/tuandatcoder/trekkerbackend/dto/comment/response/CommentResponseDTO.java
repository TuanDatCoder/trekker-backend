package com.tuandatcoder.trekkerbackend.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private String accountName;
    private String accountPicture;

    private String content;
    private Long parentCommentId;
    private Integer totalReactions;
    private Integer totalReplies;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Danh sách reply (nếu cần load nested)
    private List<CommentResponseDTO> replies;
}