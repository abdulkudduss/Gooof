package com.example.gooo.service.shipment;

import com.example.gooo.domain.entity.Order;
import com.example.gooo.dto.ShippingCostDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ShipmentService {
    List<ShippingCostDTO> calculateOptions(Order order);
    BigDecimal calculateFinalCost(Order order, String carrierCode);
}
