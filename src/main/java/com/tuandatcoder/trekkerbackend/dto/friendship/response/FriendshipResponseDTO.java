package com.tuandatcoder.trekkerbackend.dto.friendship.response;

import com.tuandatcoder.trekkerbackend.enums.FriendshipStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponseDTO {
    private Long id;
    private Long requesterId;
    private String requesterUsername;
    private String requesterName;
    private String requesterPicture;

    private Long receiverId;
    private String receiverUsername;
    private String receiverName;
    private String receiverPicture;

    private FriendshipStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}