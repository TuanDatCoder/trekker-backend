package com.tuandatcoder.trekkerbackend.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequestDTO {

    @NotBlank(message = "Content is required")
    @Size(max = 5000)
    private String content;
}