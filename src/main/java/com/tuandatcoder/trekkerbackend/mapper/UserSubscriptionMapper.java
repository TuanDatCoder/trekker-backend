package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.usersubscription.response.UserSubscriptionResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.UserSubscription;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserSubscriptionMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planName", source = "plan.name")
    @Mapping(target = "planCurrency", source = "plan.currency")
    UserSubscriptionResponseDTO toDto(UserSubscription entity);

    List<UserSubscriptionResponseDTO> toDtoList(List<UserSubscription> entities);
}
