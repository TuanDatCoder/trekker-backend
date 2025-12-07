package com.tuandatcoder.trekkerbackend.dto.checkin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCheckInRequestDTO {
    private String note;
}