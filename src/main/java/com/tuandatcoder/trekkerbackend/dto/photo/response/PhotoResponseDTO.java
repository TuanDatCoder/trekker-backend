package com.tuandatcoder.trekkerbackend.dto.photo.response;

import com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDTO {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private String accountPicture;

    private Long tripId;
    private String tripTitle;

    private String url;
    private String thumbnailUrl;
    private String caption;
    private PhotoMediaTypeEnum mediaType;
    private Double fileSizeMb;
    private Integer width;
    private Integer height;
    private boolean isRealtime;
    private LocalDateTime takenAt;
    private LocalDateTime createdAt;
}
