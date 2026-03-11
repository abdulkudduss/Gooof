package com.example.gooo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Элемент заказа")
public class OrderItemRequest {
    @Schema(description = "ID товара", example = "101")
    @NotNull(message = "ID товара не может быть пустым")
    private Long productId;
    
    @Schema(description = "Количество товара", example = "2")
    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;
}
