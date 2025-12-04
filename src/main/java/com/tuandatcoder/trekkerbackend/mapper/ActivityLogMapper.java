package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.activity.response.ActivityLogResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface ActivityLogMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountName", source = "account.name")
    ActivityLogResponseDTO toDto(ActivityLog entity);
}