package com.tuandatcoder.trekkerbackend.dto.friendship.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private Long userId;
    private String username;
    private String name;
    private String picture;
    private LocalDateTime friendSince; // khi ACCEPTED
}