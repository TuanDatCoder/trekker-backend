package com.tuandatcoder.trekkerbackend.dto.photo.request;

import com.tuandatcoder.trekkerbackend.enums.PhotoMediaTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadPhotoRequestDTO {
    private MultipartFile file;
    private String caption;
    private Long tripId;
    private Long tripDayId;
    private Long tripPlanItemId;
    private Long locationId;
    private Long placeId;
    private Boolean isRealtime;
    private Boolean isPublicOnPlace;
}