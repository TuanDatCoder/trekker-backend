package com.tuandatcoder.trekkerbackend.dto.post.request;

import com.tuandatcoder.trekkerbackend.enums.PostPrivacyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequestDTO {

    @Size(max = 255)
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Long tripId;

    private Long coverPhotoId;

    @NotNull(message = "Privacy is required")
    private PostPrivacyEnum privacy;
}