package com.tuandatcoder.trekkerbackend.mapper;

import com.tuandatcoder.trekkerbackend.dto.paymentmethod.request.UpdatePaymentMethodRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.paymentmethod.response.PaymentMethodResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.PaymentMethod;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodResponseDTO toDto(PaymentMethod entity);

    List<PaymentMethodResponseDTO> toDtoList(List<PaymentMethod> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePaymentMethodRequestDTO dto, @MappingTarget PaymentMethod entity);
}