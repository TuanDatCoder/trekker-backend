package com.tuandatcoder.trekkerbackend.mapper;


import com.tuandatcoder.trekkerbackend.dto.friendship.response.FriendshipResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Friendship;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface FriendshipMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requesterUsername", source = "requester.username")
    @Mapping(target = "requesterName", source = "requester.name")
    @Mapping(target = "requesterPicture", source = "requester.picture")

    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    @Mapping(target = "receiverName", source = "receiver.name")
    @Mapping(target = "receiverPicture", source = "receiver.picture")
    FriendshipResponseDTO toDto(Friendship entity);

    List<FriendshipResponseDTO> toDtoList(List<Friendship> entities);
}