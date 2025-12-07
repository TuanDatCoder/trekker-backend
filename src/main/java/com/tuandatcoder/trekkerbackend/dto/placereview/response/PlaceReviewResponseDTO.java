package com.tuandatcoder.trekkerbackend.dto.placereview.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceReviewResponseDTO {
    private Long id;
    private Long placeId;
    private String placeName;

    private Long accountId;
    private String accountUsername;
    private String accountName;
    private String accountPicture;

    private Integer rating;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}