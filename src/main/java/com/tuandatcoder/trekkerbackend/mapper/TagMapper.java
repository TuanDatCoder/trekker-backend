package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.tag.response.TagResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Tag;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TagMapper {

    @Mapping(target = "taggedUserId", source = "account.id")
    @Mapping(target = "taggedUsername", source = "account.username")
    @Mapping(target = "taggedName", source = "account.name")
    @Mapping(target = "taggedPicture", source = "account.picture")
    @Mapping(target = "taggedById", source = "taggedBy.id")
    @Mapping(target = "taggedByUsername", source = "taggedBy.username")
    TagResponseDTO toDto(Tag entity);

    List<TagResponseDTO> toDtoList(List<Tag> entities);
}