package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.album.request.UpdateAlbumRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.album.response.AlbumResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Album;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface AlbumMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountPicture", source = "account.picture")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripTitle", source = "trip.title")
    @Mapping(target = "coverPhotoId", source = "coverPhoto.id")
    @Mapping(target = "coverPhotoUrl", source = "coverPhoto.url")
    AlbumResponseDTO toDto(Album entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateAlbumRequestDTO dto, @MappingTarget Album entity);
}