package com.tuandatcoder.trekkerbackend.dto.albumphoto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPhotosToAlbumRequestDTO {

    @NotEmpty(message = "Photo IDs list cannot be empty")
    private List<@NotNull Long> photoIds;
}