package com.example.gooo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Элемент заказа")
public class OrderItemRequest {
    @Schema(description = "ID товара", example = "101")
    private Long productId;
    
    @Schema(description = "Количество товара", example = "2")
    private Integer quantity;
}
