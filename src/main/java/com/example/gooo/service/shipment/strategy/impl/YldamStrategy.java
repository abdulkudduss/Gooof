package com.example.gooo.service.shipment.strategy.impl;

import com.example.gooo.domain.entity.Carrier;
import com.example.gooo.domain.entity.Order;
import com.example.gooo.dto.shipment.CalculationResult;
import com.example.gooo.service.shipment.strategy.ShippingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class YldamStrategy implements ShippingStrategy {
    @Override
    public String getCarrierName() {
        return "YLDAM";
    }

    @Override
    public CalculationResult calculate(Order order, double totalWeight, Carrier carrier, Integer receiverCityCode) {
        BigDecimal basePrice = carrier.getBasePrice();
        Double baseWeightLimit = carrier.getBaseWeightLimit();
        BigDecimal pricePerExtraKg = carrier.getPricePerExtraKg();

        if (totalWeight <= baseWeightLimit) return CalculationResult.success(basePrice);
        BigDecimal cost = basePrice.add(BigDecimal.valueOf(Math.ceil(totalWeight - baseWeightLimit)).multiply(pricePerExtraKg));
        return CalculationResult.success(cost);
    }
}