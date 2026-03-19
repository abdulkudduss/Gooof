package com.example.gooo.service.shipment.strategy.impl.cdek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CdekTariffOptionDTO {
    private String name;
    private String description;
    private Integer code;
    private BigDecimal cost;
    private Integer minDays;
    private Integer maxDays;
}
