package com.example.gooo.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class CalculationResult {
    private final BigDecimal cost;
    private final BigDecimal totalSum;
    private final Integer periodMin;
    private final Integer periodMax;
    private final String deliveryDateRange;
    private final String error;

    public static CalculationResult success(BigDecimal cost) {
        return CalculationResult.builder().cost(cost).build();
    }

    public static CalculationResult failure(String error) {
        return CalculationResult.builder().error(error).build();
    }

    public boolean isSuccessful() {
        return cost != null;
    }
}
