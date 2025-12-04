package com.tuandatcoder.trekkerbackend.dto.albumphoto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPhotoResponseDTO {
    private Long id;
    private Long photoId;
    private String photoUrl;
    private String photoThumbnailUrl;
    private Integer orderIndex;
    private LocalDateTime addedAt;
}