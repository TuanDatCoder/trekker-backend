package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.paymenttransaction.response.PaymentTransactionResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.PaymentTransaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface PaymentTransactionMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "subscriptionId", source = "subscription.id")
    @Mapping(target = "planName", source = "subscription.plan.name")
    PaymentTransactionResponseDTO toDto(PaymentTransaction entity);

    List<PaymentTransactionResponseDTO> toDtoList(List<PaymentTransaction> entities);
}