package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.planfeature.response.PlanFeatureResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.PlanFeature;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanFeatureMapper {

    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planName", source = "plan.name")
    @Mapping(target = "featureId", source = "feature.id")
    @Mapping(target = "featureName", source = "feature.name")
    @Mapping(target = "featureDescription", source = "feature.description")
    PlanFeatureResponseDTO toDto(PlanFeature entity);

    List<PlanFeatureResponseDTO> toDtoList(List<PlanFeature> entities);
}