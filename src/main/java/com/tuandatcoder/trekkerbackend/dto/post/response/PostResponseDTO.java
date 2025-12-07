package com.tuandatcoder.trekkerbackend.dto.post.response;

import com.tuandatcoder.trekkerbackend.enums.PostPrivacyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;

    private Long accountId;
    private String accountUsername;
    private String accountName;
    private String accountPicture;

    private Long tripId;
    private String tripTitle;

    private String title;
    private String content;

    private Long coverPhotoId;
    private String coverPhotoUrl;

    private PostPrivacyEnum privacy;

    private Integer viewCount;
    private Integer totalReactions;
    private Integer totalComments;

    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}