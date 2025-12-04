package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.trip.request.UpdateTripRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.trip.response.TripResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Trip;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TripMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountPicture", source = "account.picture")
    @Mapping(target = "coverPhotoId", source = "coverPhoto.id")
    @Mapping(target = "coverPhotoUrl", source = "coverPhoto.url")
    TripResponseDTO toDto(Trip entity);

    // Để update từ DTO → Entity (ignore null)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateTripRequestDTO dto, @MappingTarget Trip entity);
}