package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.comment.response.CommentResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Comment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface CommentMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountPicture", source = "account.picture")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    CommentResponseDTO toDto(Comment entity);

    List<CommentResponseDTO> toDtoList(List<Comment> entities);
}