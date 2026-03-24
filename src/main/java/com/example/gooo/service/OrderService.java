package com.example.gooo.service;

import com.example.gooo.dto.*;
import com.example.gooo.dto.shipment.ShippingCostDTO;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffListResponse;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffOptionDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    OrderDetailsDTO getOrderDetails(Long id);

    DraftOrderResponse createDraftOrder(@Valid DraftOrderRequest request);

    List<ShippingCostDTO> getDeliveryOptions(Long id, Integer receiverCityCode);

    OrderResponseDTO placeOrder(Long id,OrderRequestDTO request);

    List<CdekTariffOptionDTO> getCdekTariffOptions(Long id, Integer receiverCityCode);


}
