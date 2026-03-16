package com.example.gooo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class ShippingCostDTO {
    String carrier;
    String cost;


}
