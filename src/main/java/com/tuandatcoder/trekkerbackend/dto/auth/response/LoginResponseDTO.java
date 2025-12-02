// File: src/main/java/com/tuandatcoder/trekkerbackend/dto/auth/response/LoginResponseDTO.java

package com.tuandatcoder.trekkerbackend.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
    private AccountResponseDTO user;

    // Constructor cho login thường + Google login (có user)
    public LoginResponseDTO(String accessToken, String refreshToken, AccountResponseDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public LoginResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}