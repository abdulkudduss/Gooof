package com.example.gooo.dto;

import com.example.gooo.domain.embeddable.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Запрос на создание нового заказа")
public class CreateOrderRequest {
    @Schema(description = "Код валюты (например, USD, RUB)", example = "RUB")
    private String currencyCode; // "USD", "RUB"
    
    @Schema(description = "Список товаров в заказе")
    private List<OrderItemRequest> items;
    
    @Schema(description = "Идентификатор способа доставки", example = "1")
    private Long shippingMethodId; // Какой тариф выбрали
    
    @Schema(description = "Адрес доставки")
    private Address deliveryAddress; // Наш @Embeddable класс
    
    @Schema(description = "Контактный телефон клиента", example = "+79991234567")
    private String contactPhone;
}
