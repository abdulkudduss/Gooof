package com.example.gooo.service.impl;

import com.example.gooo.domain.entity.*;
import com.example.gooo.domain.enums.OrderStatus;
import com.example.gooo.domain.projections.OrderView;
import com.example.gooo.domain.repository.CarrierRepository;
import com.example.gooo.domain.repository.OrderRepository;
import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.domain.repository.UserRepository;
import com.example.gooo.service.strategy.ShippingPricingStrategy;
import com.example.gooo.dto.*;
import com.example.gooo.exception.ResourceNotFoundException;

import com.example.gooo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CarrierRepository carrierRepository;
    // @Component, реализующие один и тот же интерфейс, сразу в виде коллекции
    // Spring автоматически соберет сюда CdekPricingStrategy и YldamPricingStrategy
    private final List<ShippingPricingStrategy> pricingStrategies;

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


    @Override
    public DraftOrderResponse createDraftOrder(DraftOrderRequest request) {
        // 1. Находим пользователя
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // 2. Создаем базовый заказ (Черновик)
        Order order = new Order();
        order.setCustomer(user);
        order.setCurrencyCode(request.getCurrencyCode());
        order.setStatus(OrderStatus.DRAFT); // Обязательно добавьте DRAFT в enum OrderStatus

        BigDecimal itemsTotalsum = BigDecimal.ZERO;

        // 3. Добавляем товары и считаем их стоимость
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Товар не найден: " + itemRequest.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Связываем с заказом
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getCurrentPrice()); // Фиксируем цену на момент добавления

            order.getItems().add(orderItem); // Добавляем в список заказа

            // Плюсуем к общей сумме товаров: цена * количество
            itemsTotalsum = itemsTotalsum.add(
                    product.getCurrentPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );
        }

        // 4. Сохраняем сумму товаров в заказ
        order.setItemsTotal(itemsTotalsum);
        // Поля shippingTotal и totalAmount пока оставляем пустыми (null), их заполним на Шаге 3

        // 5. Сохраняем заказ в БД
        Order savedOrder = orderRepository.save(order);

        // 6. Возвращаем ID заказа и сумму фронтенду
        return new DraftOrderResponse(savedOrder.getId(), savedOrder.getItemsTotal());
    }

    @Override
    public List<ShippingCostDTO> getDeliveryOptions(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        double totalWeight = order.getItems().stream()
                .mapToDouble(item -> item.getProduct().getWeight() * item.getQuantity())
                .sum();
        log.info("Total weight for order {}: {} kg", orderId, totalWeight);
        log.info("Strategies count: {}", pricingStrategies.size());
        return pricingStrategies.stream()
                .map(strategy -> {
                    Carrier carrier = carrierRepository.findByName(strategy.getCarrierCode())
                            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found: " + strategy.getCarrierCode()));
                    BigDecimal shippingCost = strategy.calculate(order, totalWeight, carrier);
                    return new ShippingCostDTO(strategy.getCarrierCode(), shippingCost.toString());
                })
                .toList();
    }



    @Override
    public OrderResponseDTO placeOrder(Long id, OrderRequestDTO request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Заказ можно разместить только в статусе DRAFT. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PENDING_PAYMENT);

        double totalWeight = order.getItems().stream()
                .mapToDouble(item -> item.getProduct().getWeight() * item.getQuantity())
                .sum();

        log.info("Total weight for order {}: {} kg", id, totalWeight);

        Carrier carrier = carrierRepository.findByName(request.getCarrierCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Курьерская служба не найдена: " + request.getCarrierCode()));

        BigDecimal shippingCost = getPricingStrategy(request.getCarrierCode())
                .calculate(order, totalWeight, carrier);

        order.setShippingTotal(shippingCost);
        order.setTotalAmount(order.getItemsTotal().add(shippingCost));

        CourierShipment shipment = (CourierShipment) order.getShipment();

        if (shipment == null) {
            shipment = new CourierShipment();
            shipment.setOrder(order);
            shipment.setTrackingNumber(generateTrackingNumber());
        }

        shipment.setOriginAddress(request.getOriginAddress());
        shipment.setDeliveryAddress(request.getDeliveryAddress());
        shipment.setShippingCost(shippingCost);
        shipment.setCarrier(carrier);

        order.setShipment(shipment);

        orderRepository.save(order);

        log.info("Order {} placed successfully", id);

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getEmail() : null, // customerEmail
                order.getStatus().name(),
                order.getTotalAmount(),
                shipment.getTrackingNumber()
        );
    }
    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ShippingPricingStrategy getPricingStrategy(String carrierCode) {

        return pricingStrategies.stream()

                .filter(strategy -> strategy.getCarrierCode().equalsIgnoreCase(carrierCode))

                .findFirst()

                .orElseThrow(() -> new ResourceNotFoundException("Курьерcкая служба с кодом " + carrierCode + " не найден"));

    }

}
        
