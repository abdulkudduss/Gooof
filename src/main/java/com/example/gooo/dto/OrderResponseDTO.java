package com.example.gooo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "Ответ о созданном заказе")
public class OrderResponseDTO {
    @Schema(description = "ID созданного заказа", example = "42")
    private Long orderId;

    @Schema(description = "Email покупателя", example = "customer@example.com")
    private String customerEmail;

    @Schema(description = "Статус заказа", example = "NEW")
    private String status;
    
    @Schema(description = "Общая стоимость заказа", example = "1500.50")
    private BigDecimal totalPrice;
    
    @Schema(description = "Трек-номер отправления", example = "TRK-ABC12345")
    private String trackingNumber;



}
