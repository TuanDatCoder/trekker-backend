package com.tuandatcoder.trekkerbackend.dto.usersubscription.response;

import com.tuandatcoder.trekkerbackend.enums.UserSubscriptionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionResponseDTO {
    private Long id;

    private Long accountId;
    private String accountUsername;

    private Long planId;
    private String planName;
    private String planCurrency;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private UserSubscriptionStatusEnum status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}