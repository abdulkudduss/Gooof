package com.example.gooo.service.shipment.strategy.impl;

import com.example.gooo.domain.entity.Order;
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
    public BigDecimal calculate(Order order, double totalWeight, com.example.gooo.domain.entity.Carrier carrier) {
        BigDecimal basePrice = carrier.getBasePrice();
        double baseWeightLimit = carrier.getBaseWeightLimit();
        BigDecimal pricePerExtraKg = carrier.getPricePerExtraKg();

        if (totalWeight <= baseWeightLimit) return basePrice;
        return basePrice.add(BigDecimal.valueOf(Math.ceil(totalWeight - baseWeightLimit)).multiply(pricePerExtraKg));
    }
}