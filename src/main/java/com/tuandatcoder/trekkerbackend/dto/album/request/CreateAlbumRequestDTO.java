package com.tuandatcoder.trekkerbackend.dto.album.request;

import com.tuandatcoder.trekkerbackend.enums.AlbumPrivacyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlbumRequestDTO {

    @NotBlank(message = "Album title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    private Long tripId; // Nếu null → album cá nhân, nếu có → album của trip

    private Long coverPhotoId; // Optional khi tạo

    @NotBlank(message = "Privacy is required")
    private AlbumPrivacyEnum privacy;
}