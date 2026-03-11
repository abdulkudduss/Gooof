package com.example.gooo.service;

import com.example.gooo.dto.CreateOrderRequest;
import com.example.gooo.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(CreateOrderRequest request);
}
