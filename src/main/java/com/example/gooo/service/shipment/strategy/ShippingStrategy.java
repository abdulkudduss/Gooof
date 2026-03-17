package com.example.gooo.service.shipment.strategy;

import com.example.gooo.domain.entity.Carrier;
import com.example.gooo.domain.entity.Order;
import com.example.gooo.dto.shipment.CalculationResult;

public interface ShippingStrategy {
    String getCarrierName();

    // Возвращаем CalculationResult вместо BigDecimal
    CalculationResult calculate(Order order, double totalWeight, Carrier carrier, Integer receiverCityCode);
}