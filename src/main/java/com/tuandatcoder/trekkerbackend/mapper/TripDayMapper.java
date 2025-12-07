package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.tripday.request.UpdateTripDayRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripday.response.TripDayResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.TripDay;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripDayMapper {

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    TripDayResponseDTO toDto(TripDay entity);

    List<TripDayResponseDTO> toDtoList(List<TripDay> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateTripDayRequestDTO dto, @MappingTarget TripDay entity);
}