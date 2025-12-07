package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.place.request.UpdatePlaceRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.place.response.PlaceResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Place;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface PlaceMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "locationName", source = "location.city")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    PlaceResponseDTO toDto(Place entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePlaceRequestDTO dto, @MappingTarget Place entity);
}