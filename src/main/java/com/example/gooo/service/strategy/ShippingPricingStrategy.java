package com.example.gooo.service.strategy;

import com.example.gooo.domain.entity.Order;

import java.math.BigDecimal;

public interface ShippingPricingStrategy {
    // Идентификатор, к какой компании применяется стратегия
    String getCarrierCode();

    // Сама логика расчета
    BigDecimal calculate(Order order, double totalWeight, com.example.gooo.domain.entity.Carrier carrier);
}