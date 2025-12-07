package com.tuandatcoder.trekkerbackend.dto.location.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequestDTO {

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String country;
}