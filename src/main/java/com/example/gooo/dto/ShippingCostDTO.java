package com.example.gooo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingCostDTO {
    private String carrier;
    private BigDecimal cost;
    private BigDecimal totalSum;
    private Integer periodMin;
    private Integer periodMax;
    private String deliveryDateRange;
    private String error;

    public ShippingCostDTO(String carrier, BigDecimal cost) {
        this.carrier = carrier;
        this.cost = cost;
    }
}
