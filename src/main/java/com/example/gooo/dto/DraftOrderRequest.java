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
public class DraftOrderRequest {
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "Валюта обязательна")
    @Size(max = 3)
    private String currencyCode;

    @NotEmpty(message = "Корзина не может быть пустой")
    @Valid
    private List<OrderItemRequest> items;
}
