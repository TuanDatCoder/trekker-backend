package com.tuandatcoder.trekkerbackend.dto.notification.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadRequestDTO {
    private List<Long> notificationIds;
}