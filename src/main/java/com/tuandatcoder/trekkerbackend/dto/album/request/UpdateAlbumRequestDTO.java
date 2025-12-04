package com.tuandatcoder.trekkerbackend.dto.album.request;

import com.tuandatcoder.trekkerbackend.enums.AlbumPrivacyEnum;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAlbumRequestDTO {

    @Size(max = 255)
    private String title;

    private String description;

    private Long coverPhotoId;

    private AlbumPrivacyEnum privacy;
}