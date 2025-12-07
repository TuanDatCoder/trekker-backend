package com.tuandatcoder.trekkerbackend.dto.checkin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseDTO {
    private Long id;

    private Long accountId;
    private String accountUsername;
    private String accountPicture;

    private Long placeId;
    private String placeName;

    private Long tripId;
    private String tripTitle;

    private Long photoId;
    private String photoUrl;

    private String note;
    private LocalDateTime checkedInAt;
    private LocalDateTime createdAt;
}