package com.example.gooo.mapper;

import com.example.gooo.domain.entity.Order;
import com.example.gooo.dto.OrderResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "order.shipment.trackingNumber", target = "trackingNumber")
    OrderResponseDTO toDto(Order order, BigDecimal totalPrice);
}
