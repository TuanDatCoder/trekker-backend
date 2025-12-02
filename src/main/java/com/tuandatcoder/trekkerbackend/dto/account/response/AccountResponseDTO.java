package com.tuandatcoder.trekkerbackend.dto.account.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDTO {
    private Long id;
    private String username;
    private String email;

}