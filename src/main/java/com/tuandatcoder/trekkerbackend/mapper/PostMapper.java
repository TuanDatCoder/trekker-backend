package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.post.request.UpdatePostRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.post.response.PostResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface PostMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountPicture", source = "account.picture")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    @Mapping(target = "coverPhotoId", source = "coverPhoto.id")
    @Mapping(target = "coverPhotoUrl", source = "coverPhoto.url")
    PostResponseDTO toDto(Post entity);

    List<PostResponseDTO> toDtoList(List<Post> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePostRequestDTO dto, @MappingTarget Post entity);
}
