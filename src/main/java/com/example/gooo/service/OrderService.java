package com.example.gooo.service;

import com.example.gooo.dto.*;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    OrderDetailsDTO getOrderDetails(Long id);

    DraftOrderResponse createDraftOrder(@Valid DraftOrderRequest request);

    List<ShippingCostDTO> getDeliveryOptions(Long id);

    OrderResponseDTO placeOrder(Long id,OrderRequestDTO request);
}
