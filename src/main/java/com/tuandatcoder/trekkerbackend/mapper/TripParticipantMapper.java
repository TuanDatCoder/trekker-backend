package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.tripparticipant.response.TripParticipantResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.TripParticipant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TripParticipantMapper {

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    @Mapping(target = "userId", source = "account.id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "name", source = "account.name")
    @Mapping(target = "picture", source = "account.picture")
    @Mapping(target = "invitedById", source = "invitedBy.id")
    @Mapping(target = "invitedByUsername", source = "invitedBy.username")
    TripParticipantResponseDTO toDto(TripParticipant entity);

    List<TripParticipantResponseDTO> toDtoList(List<TripParticipant> entities);
}