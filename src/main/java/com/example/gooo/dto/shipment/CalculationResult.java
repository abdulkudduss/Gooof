package com.example.gooo.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CalculationResult {
    private final BigDecimal cost;
    private final String error;

    public static CalculationResult success(BigDecimal cost) {
        return new CalculationResult(cost, null);
    }

    public static CalculationResult failure(String error) {
        return new CalculationResult(null, error);
    }

    public boolean isSuccessful() {
        return cost != null;
    }
}
