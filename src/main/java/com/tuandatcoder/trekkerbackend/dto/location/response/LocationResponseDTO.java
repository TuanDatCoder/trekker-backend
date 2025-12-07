package com.tuandatcoder.trekkerbackend.dto.location.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDTO {
    private Long id;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String country;
    private LocalDateTime createdAt;
}
