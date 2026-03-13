package com.example.gooo.dto;

import com.example.gooo.domain.embeddable.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Запрос на создание нового заказа")
public class CreateOrderRequest {
    @Schema(description = "Идентификатор пользователя", example = "2")
    @NotNull(message = "ID пользователя должен быть указан")
    private Long userId;

    @Schema(description = "Код валюты (например, USD, RUB)", example = "RUB")
    @NotBlank(message = "Код валюты не может быть пустым")
    @Size(min = 3, max = 3, message = "Код валюты должен содержать ровно 3 символа")
    private String currencyCode; // "USD", "RUB"
    
    @Schema(description = "Список товаров в заказе")
    @NotEmpty(message = "Список товаров не может быть пустым")
    @Valid
    private List<OrderItemRequest> items;
    
    @Schema(description = "Идентификатор способа доставки", example = "1")
    @NotNull(message = "Способ доставки должен быть указан")
    private Long shippingMethodId; // Какой тариф выбрали
    
    @Schema(description = "Адрес доставки")
    @NotNull(message = "Адрес доставки должен быть указан")
    @Valid
    private Address deliveryAddress; // Наш @Embeddable класс
    
    @Schema(description = "Контактный телефон клиента", example = "+79991234567")
    @NotBlank(message = "Контактный телефон не может быть пустым")
    private String contactPhone;
}
