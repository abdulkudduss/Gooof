package com.example.gooo.service.impl;

import com.example.gooo.domain.entity.*;
import com.example.gooo.domain.enums.OrderStatus;
import com.example.gooo.domain.projections.OrderView;
import com.example.gooo.domain.repository.CarrierRepository;
import com.example.gooo.domain.repository.OrderRepository;
import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.domain.repository.UserRepository;
import com.example.gooo.dto.*;
import com.example.gooo.dto.shipment.ShippingCostDTO;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffListResponse;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffOptionDTO;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.mapper.OrderMapper;
import com.example.gooo.service.shipment.strategy.impl.cdek.CdekClient;
import com.example.gooo.service.OrderService;
import com.example.gooo.service.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CarrierRepository carrierRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ShipmentService shipmentService;
    private final CdekClient cdekClient;

    @Override
    @Transactional
    public DraftOrderResponse createDraftOrder(DraftOrderRequest request) {
        log.info("Creating draft order for userId={}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // 1. Собираем все ID продуктов из запроса
        Map<Long, Integer> consolidatedItems = request.getItems().stream()
                .collect(Collectors.groupingBy(
                        OrderItemRequest::getProductId,
                        Collectors.summingInt(OrderItemRequest::getQuantity)
                ));

        // 2. Загружаем продукты ОДНИМ запросом (теперь только уникальные ID)
        List<Product> products = productRepository.findAllById(consolidatedItems.keySet());
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));


        Order order = new Order();
        order.setCustomer(user);
        order.setCurrencyCode(request.getCurrencyCode());
        order.setStatus(OrderStatus.DRAFT);

        BigDecimal itemsTotal = BigDecimal.ZERO;


        for (Map.Entry<Long, Integer> entry : consolidatedItems.entrySet()) {
            Long productId = entry.getKey();
            Integer totalQuantity = entry.getValue();

            Product product = productMap.get(productId);
            if (product == null) {
                throw new ResourceNotFoundException("Товар не найден: " + productId);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(totalQuantity); // Здесь уже 6 (4 + 2)
            orderItem.setPriceAtPurchase(product.getCurrentPrice());

            order.getItems().add(orderItem);

            itemsTotal = itemsTotal.add(product.getCurrentPrice()
                    .multiply(BigDecimal.valueOf(totalQuantity)));
        }

        order = orderRepository.save(order);
        return new DraftOrderResponse(order.getId(), itemsTotal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingCostDTO> getDeliveryOptions(Long id, Integer receiverCityCode) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));
        return shipmentService.calculateOptions(order, receiverCityCode);
    }

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(Long id, OrderRequestDTO request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        Carrier carrier = carrierRepository.findByName(request.getCarrierCode())
                .orElseThrow(() -> new ResourceNotFoundException("Перевозчик не найден"));

        // Рассчитываем финальную стоимость доставки (используем zipCode как код города для простоты, если receiverCityCode не передан явно)
        // В реальном приложении тут должна быть логика маппинга адреса в код города СДЭК
        Integer cityCode = Integer.parseInt(request.getDeliveryAddress().getZipCode());
        BigDecimal shippingCost = shipmentService.calculateFinalCost(order, carrier.getName(), cityCode);

        CourierShipment shipment = new CourierShipment();
        shipment.setOrder(order);
        shipment.setCarrier(carrier);
        shipment.setShippingCost(shippingCost);
        shipment.setOriginAddress(request.getOriginAddress());
        shipment.setDeliveryAddress(request.getDeliveryAddress());
        shipment.setTrackingNumber(generateTrackingNumber());

        order.setShipment(shipment);
        order.setStatus(OrderStatus.PENDING_PAYMENT); // Условно переводим в PENDING_PAYMENT при оформлении
        orderRepository.save(order);

        BigDecimal itemsTotal = order.getItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getEmail(),
                order.getStatus().name(),
                itemsTotal.add(shippingCost),
                shipment.getTrackingNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsDTO getOrderDetails(Long id) {
        OrderView projection = orderRepository.findOrderDetailsNative(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setUserName(projection.getCustomerName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dto.setDate(projection.getOrderDate().format(formatter));

        DecimalFormat df = new DecimalFormat("#,##0.00");
        dto.setTotalPrice(df.format(projection.getTotalAmount()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CdekTariffOptionDTO> getCdekTariffOptions(Long id, Integer receiverCityCode) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));
        return shipmentService.getCdekTariffOptions(order, receiverCityCode);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
