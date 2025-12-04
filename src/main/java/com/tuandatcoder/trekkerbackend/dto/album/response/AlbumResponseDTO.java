package com.tuandatcoder.trekkerbackend.dto.album.response;

import com.tuandatcoder.trekkerbackend.enums.AlbumPrivacyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponseDTO {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private String accountName;
    private String accountPicture;

    private Long tripId;
    private String tripTitle;

    private String title;
    private String description;

    private Long coverPhotoId;
    private String coverPhotoUrl;

    private AlbumPrivacyEnum privacy;
    private Integer totalPhotos;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}