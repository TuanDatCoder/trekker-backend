package com.tuandatcoder.trekkerbackend.dto.albumphoto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderAlbumPhotosRequestDTO {

    @NotEmpty(message = "Photo order map cannot be empty")
    private Map<Long, Integer> photoIdToOrder; // photoId -> orderIndex
}