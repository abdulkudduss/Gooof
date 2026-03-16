package com.example.gooo.service.shipment.strategy;

import com.example.gooo.domain.entity.Carrier;
import com.example.gooo.domain.entity.Order;

import java.math.BigDecimal;

public interface ShippingStrategy {
    // Возвращает имя, которое в точности совпадает с полем name в БД
    String getCarrierName();

    BigDecimal calculate(Order order, double totalWeight, Carrier carrier);
}