package com.tuandatcoder.trekkerbackend.dto.usersubscription.request;

import com.tuandatcoder.trekkerbackend.enums.UserSubscriptionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserSubscriptionRequestDTO {

    private UserSubscriptionStatusEnum status;
    private LocalDateTime endDate;
}