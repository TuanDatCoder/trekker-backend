package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.albumphoto.response.AlbumPhotoResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.AlbumPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumPhotoMapper {

    @Mapping(target = "photoId", source = "photo.id")
    @Mapping(target = "photoUrl", source = "photo.url")
    @Mapping(target = "photoThumbnailUrl", source = "photo.thumbnailUrl")
    AlbumPhotoResponseDTO toDto(AlbumPhoto entity);
}
