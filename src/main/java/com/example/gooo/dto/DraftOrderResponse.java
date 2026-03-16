package com.example.gooo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DraftOrderResponse {
    private Long orderId;
    private BigDecimal itemsTotal; // Сумма только за товары (без доставки)
}