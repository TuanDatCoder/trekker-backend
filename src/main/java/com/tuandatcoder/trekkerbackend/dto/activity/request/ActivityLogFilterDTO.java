package com.tuandatcoder.trekkerbackend.dto.activity.request;

import com.tuandatcoder.trekkerbackend.enums.ActivityActionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogFilterDTO {
    private Long accountId;
    private ActivityActionTypeEnum actionType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer page = 0;
    private Integer size = 20;
}