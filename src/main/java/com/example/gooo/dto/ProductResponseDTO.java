package com.example.gooo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Информация о товаре")
public class ProductResponseDTO {
    @Schema(description = "Идентификатор товара", example = "1")
    private Long id;
    
    @Schema(description = "Название товара", example = "iPhone 15")
    private String name;
    
    @Schema(description = "Артикул товара", example = "IPH15-BLK")
    private String sku;
    
    @Schema(description = "Текущая цена товара", example = "999.99")
    private BigDecimal price;
}
