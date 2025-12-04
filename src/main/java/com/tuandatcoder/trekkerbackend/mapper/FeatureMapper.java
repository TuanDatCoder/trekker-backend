package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.feature.request.FeatureRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.feature.response.FeatureResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Feature;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeatureMapper {

    FeatureResponseDTO toDto(Feature entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FeatureRequestDTO dto, @MappingTarget Feature entity);
}