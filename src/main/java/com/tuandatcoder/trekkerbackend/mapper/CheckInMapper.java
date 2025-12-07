package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.checkin.response.CheckInResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.UserPlaceCheckIn;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface CheckInMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountPicture", source = "account.picture")
    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    @Mapping(target = "photoId", source = "photo.id")
    @Mapping(target = "photoUrl", source = "photo.url")
    CheckInResponseDTO toDto(UserPlaceCheckIn entity);

    List<CheckInResponseDTO> toDtoList(List<UserPlaceCheckIn> entities);
}