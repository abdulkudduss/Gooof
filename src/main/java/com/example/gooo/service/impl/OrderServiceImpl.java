package com.example.gooo.service.impl;

import com.example.gooo.domain.entity.CourierShipment;
import com.example.gooo.domain.entity.Order;
import com.example.gooo.domain.entity.OrderItem;
import com.example.gooo.domain.entity.Product;
import com.example.gooo.domain.entity.ShippingMethod;
import com.example.gooo.domain.enums.OrderStatus;
import com.example.gooo.domain.repository.OrderRepository;
import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.domain.repository.ShippingMethodRepository;
import com.example.gooo.dto.CreateOrderRequest;
import com.example.gooo.dto.OrderItemRequest;
import com.example.gooo.dto.OrderResponseDTO;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ShippingMethodRepository shippingMethodRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        // 1. Создаем объект заказа
        Order order = new Order();
        order.setCurrencyCode(request.getCurrencyCode());
        order.setStatus(OrderStatus.NEW);

        // 2. Оптимизация: загружаем все продукты одним запросом (по желанию)
        // 3. Считаем позиции
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Важно для двусторонней связи
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getCurrentPrice());

            order.getItems().add(orderItem);

            BigDecimal itemTotal = product.getCurrentPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalOrderAmount = totalOrderAmount.add(itemTotal);
        }

        // 4. Логика доставки (у вас она верная)
        ShippingMethod method = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Метод доставки не найден"));

        CourierShipment shipment = new CourierShipment();
        shipment.setOrder(order);
        shipment.setShippingMethod(method);
        shipment.setDeliveryAddress(request.getDeliveryAddress());
        shipment.setContactPhone(request.getContactPhone());
        shipment.setTrackingNumber(generateTrackingNumber()); // Вынесите в метод

        order.setShipment(shipment);

        // 5. Сохраняем (CascadeType.ALL сделает всё за нас)
        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder, totalOrderAmount);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponseDTO convertToResponse(Order order, BigDecimal totalPrice) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalPrice(totalPrice);
        dto.setTrackingNumber(order.getShipment().getTrackingNumber());
        return dto;
    }
}
