package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.placereview.request.UpdatePlaceReviewRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.placereview.response.PlaceReviewResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.PlaceReview;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface PlaceReviewMapper {

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountPicture", source = "account.picture")
    PlaceReviewResponseDTO toDto(PlaceReview entity);

    List<PlaceReviewResponseDTO> toDtoList(List<PlaceReview> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePlaceReviewRequestDTO dto, @MappingTarget PlaceReview entity);
}