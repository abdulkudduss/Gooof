package com.example.gooo.service.impl;

import com.example.gooo.domain.entity.*;
import com.example.gooo.domain.enums.OrderStatus;
import com.example.gooo.domain.projections.OrderView;
import com.example.gooo.domain.repository.OrderRepository;
import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.domain.repository.ShippingMethodRepository;
import com.example.gooo.domain.repository.UserRepository;
import com.example.gooo.dto.*;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.mapper.OrderMapper;
import com.example.gooo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        log.info("Creating order for userId={}, {} items, shippingMethodId={}, currency={}",
                request.getUserId(),
                request.getItems() != null ? request.getItems().size() : 0,
                request.getShippingMethodId(), request.getCurrencyCode());
        // 1. Создаем объект заказа
        Order order = new Order();
        order.setCurrencyCode(request.getCurrencyCode());
        order.setStatus(OrderStatus.NEW);

        // Находим пользователя
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        order.setCustomer(user);

        // 2. Оптимизация: загружаем все продукты одним запросом (по желанию)
        // 3. Считаем позиции
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));
            log.debug("Adding item: productId={}, qty={}, unitPrice={}",
                    product.getId(), itemRequest.getQuantity(), product.getCurrentPrice());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Важно для двусторонней связи
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getCurrentPrice());

            order.getItems().add(orderItem);

            BigDecimal itemTotal = product.getCurrentPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalOrderAmount = totalOrderAmount.add(itemTotal);
        }
        log.info("Items calculated, total={} {}", totalOrderAmount, request.getCurrencyCode());

        // 4. Логика доставки (у вас она верная)
        ShippingMethod method = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Метод доставки не найден"));
        log.debug("Using shipping method id={} name={}", method.getId(), method.getName());

        CourierShipment shipment = new CourierShipment();
        shipment.setOrder(order);
        shipment.setShippingMethod(method);
        shipment.setDeliveryAddress(request.getDeliveryAddress());
        shipment.setContactPhone(request.getContactPhone());
        shipment.setTrackingNumber(generateTrackingNumber()); // Вынесите в метод

        order.setShipment(shipment);

        // 5. Сохраняем (CascadeType.ALL сделает всё за нас)
        Order savedOrder = orderRepository.save(order);
        log.info("Order persisted with id={}", savedOrder.getId());

        return orderMapper.toDto(savedOrder, totalOrderAmount);
    }


    @Override
    @Transactional(readOnly = true)
    public OrderDetailsDTO getOrderDetails(Long id) {
        // 1. Получаем данные из БД через нативный запрос
        OrderView projection = orderRepository.findOrderDetailsNative(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        // 2. Создаем и заполняем DTO
        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setUserName(projection.getCustomerName());

        // 3. Форматируем дату в строку (например, "12.03.2024 18:00")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dto.setDate(projection.getOrderDate().format(formatter));

        // 4. Используем DecimalFormat для красивого отображения суммы (например, "1 250,50")
        DecimalFormat df = new DecimalFormat("#,##0.00");
        dto.setTotalPrice(df.format(projection.getTotalAmount()));

        return dto;
    }


    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
