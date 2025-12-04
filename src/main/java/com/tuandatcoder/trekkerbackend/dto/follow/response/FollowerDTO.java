package com.tuandatcoder.trekkerbackend.dto.follow.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerDTO {
    private Long accountId;
    private String username;
    private String name;
    private String picture;
    private LocalDateTime followedAt;
}