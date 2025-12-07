package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.placecategory.request.UpdatePlaceCategoryRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placecategory.response.PlaceCategoryResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.PlaceCategory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlaceCategoryMapper {

    PlaceCategoryResponseDTO toDto(PlaceCategory entity);

    List<PlaceCategoryResponseDTO> toDtoList(List<PlaceCategory> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePlaceCategoryRequestDTO dto, @MappingTarget PlaceCategory entity);
}