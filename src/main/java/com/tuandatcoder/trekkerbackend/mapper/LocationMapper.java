package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.location.request.UpdateLocationRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.location.response.LocationResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Location;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationResponseDTO toDto(Location entity);

    List<LocationResponseDTO> toDtoList(List<Location> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateLocationRequestDTO dto, @MappingTarget Location entity);
}