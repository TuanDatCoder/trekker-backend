package com.tuandatcoder.trekkerbackend.dto.paymentmethod.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentMethodRequestDTO {
    private String details;
    private Boolean isDefault;
}