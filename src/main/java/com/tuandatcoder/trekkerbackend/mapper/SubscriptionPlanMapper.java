package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.request.UpdateSubscriptionPlanRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.subscriptionplan.response.SubscriptionPlanResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.SubscriptionPlan;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {

    SubscriptionPlanResponseDTO toDto(SubscriptionPlan entity);

    List<SubscriptionPlanResponseDTO> toDtoList(List<SubscriptionPlan> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateSubscriptionPlanRequestDTO dto, @MappingTarget SubscriptionPlan entity);
}