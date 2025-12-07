package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.tripplanitem.request.UpdateTripPlanItemRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.tripplanitem.response.TripPlanItemResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.TripPlanItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripPlanItemMapper {

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    @Mapping(target = "tripDayId", source = "tripDay.id")
    @Mapping(target = "tripDayIndex", source = "tripDay.dayIndex")
    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "locationAddress", source = "location.address")
    TripPlanItemResponseDTO toDto(TripPlanItem entity);

    List<TripPlanItemResponseDTO> toDtoList(List<TripPlanItem> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateTripPlanItemRequestDTO dto, @MappingTarget TripPlanItem entity);
}