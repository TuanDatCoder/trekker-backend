package com.tuandatcoder.trekkerbackend.dto.post.request;

import com.tuandatcoder.trekkerbackend.enums.PostPrivacyEnum;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequestDTO {

    @Size(max = 255)
    private String title;

    private String content;

    private Long coverPhotoId;

    private PostPrivacyEnum privacy;
}