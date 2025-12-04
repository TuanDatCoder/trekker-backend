package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.photo.response.PhotoResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Photo;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface PhotoMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountPicture", source = "account.picture")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    PhotoResponseDTO toDto(Photo entity);
}