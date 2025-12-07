package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.reaction.response.ReactionResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Reaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface ReactionMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountPicture", source = "account.picture")
    ReactionResponseDTO toDto(Reaction entity);

    List<ReactionResponseDTO> toDtoList(List<Reaction> entities);
}