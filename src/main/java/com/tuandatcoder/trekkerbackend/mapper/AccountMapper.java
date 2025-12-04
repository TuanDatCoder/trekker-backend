package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.account.response.AccountResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseDTO toDto(Account entity);
}