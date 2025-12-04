package com.tuandatcoder.trekkerbackend.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDTO {

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Comment too long")
    private String content;

    private Long parentCommentId;
}