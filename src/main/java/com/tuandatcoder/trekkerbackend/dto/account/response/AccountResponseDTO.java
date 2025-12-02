package com.tuandatcoder.trekkerbackend.dto.account.response;


import com.tuandatcoder.trekkerbackend.enums.AccountProviderEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountRoleEnum;
import com.tuandatcoder.trekkerbackend.enums.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {

    private Long id;

    private String username;

    private String email;

    private String name;
    private String picture;

    private AccountRoleEnum role;

    private AccountStatusEnum status;

    private AccountProviderEnum provider;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;

    private String dateOfBirth;

    private String gender;
}