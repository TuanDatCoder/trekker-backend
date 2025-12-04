package com.tuandatcoder.trekkerbackend.dto.follow.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDTO {
    private Long followingId;
    private String followingUsername;
    private String followingName;
    private String followingPicture;
    private boolean isFollowing; // true = đang follow, false = đã unfollow
    private int totalFollowers;
    private int totalFollowing;
}
