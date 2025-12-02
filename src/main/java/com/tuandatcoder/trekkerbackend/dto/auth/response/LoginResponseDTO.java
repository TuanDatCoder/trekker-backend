package com.tuandatcoder.trekkerbackend.dto.auth.response;

import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private AccountResponseDTO user;
}
