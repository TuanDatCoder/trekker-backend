package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.notification.response.NotificationResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Notification;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface NotificationMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "senderName", source = "sender.name")
    @Mapping(target = "senderPicture", source = "sender.picture")
    NotificationResponseDTO toDto(Notification entity);

    List<NotificationResponseDTO> toDtoList(List<Notification> entities);
}