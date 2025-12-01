package com.tuandatcoder.trekkerbackend.auth;

import com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private String email;
    private String name;
    private String password;
    private AccountRoleEnum role;
}
