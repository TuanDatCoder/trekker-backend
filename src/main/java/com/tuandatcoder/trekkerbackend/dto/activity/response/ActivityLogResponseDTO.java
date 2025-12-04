package com.tuandatcoder.trekkerbackend.dto.activity.response;

import com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponseDTO {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private String accountName;

    private ActivityActionTypeEnum actionType;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;
}