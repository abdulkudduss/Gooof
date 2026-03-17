package com.example.gooo.service.shipment;

import com.example.gooo.domain.entity.Order;
import com.example.gooo.dto.ShippingCostDTO;
import com.example.gooo.dto.cdek.CdekTariffOptionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ShipmentService {
    // Теперь передаем код города для точного расчета через API
    List<ShippingCostDTO> calculateOptions(Order order, Integer receiverCityCode);

    BigDecimal calculateFinalCost(Order order, String carrierName, Integer receiverCityCode);

    List<CdekTariffOptionDTO> getCdekTariffOptions(Order order, Integer receiverCityCode);
}