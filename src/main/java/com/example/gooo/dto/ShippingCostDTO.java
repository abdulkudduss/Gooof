package com.example.gooo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingCostDTO {
    private String carrier;
    private BigDecimal cost;
    private String error;

    public ShippingCostDTO(String carrier, BigDecimal cost) {
        this.carrier = carrier;
        this.cost = cost;
    }
}
