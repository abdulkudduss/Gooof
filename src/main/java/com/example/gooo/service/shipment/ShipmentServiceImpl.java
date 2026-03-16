package com.example.gooo.service.shipment;

import com.example.gooo.domain.entity.Carrier;
import com.example.gooo.domain.entity.Order;
import com.example.gooo.domain.repository.CarrierRepository;
import com.example.gooo.dto.ShippingCostDTO;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.service.shipment.strategy.ShippingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShippingStrategyFactory strategyFactory;
    private final CarrierRepository carrierRepository;

    @Override
    public List<ShippingCostDTO> calculateOptions(Order order) {
        double weight = calculateWeight(order);

        // 1. Достаем из БД ТОЛЬКО активных перевозчиков (где active = true)
        List<Carrier> activeCarriers = carrierRepository.findByActiveTrue();

        // 2. Считаем цену для каждого активного перевозчика
        return activeCarriers.stream()
                .map(carrier -> {
                    ShippingStrategy strategy = strategyFactory.getStrategy(carrier.getName());
                    BigDecimal cost = strategy.calculate(order, weight, carrier);

                    return new ShippingCostDTO(carrier.getName(), cost.toString());
                })
                .toList();
    }

    @Override
    public BigDecimal calculateFinalCost(Order order, String carrierName) {
        double weight = calculateWeight(order);

        // Ищем перевозчика по имени, чтобы взять его тарифы
        Carrier carrier = carrierRepository.findByName(carrierName)
                .orElseThrow(() -> new ResourceNotFoundException("Перевозчик не найден: " + carrierName));

        // Если кто-то пытается оформить заказ через отключенную доставку - бросаем ошибку
        if (!carrier.isActive()) {
            throw new IllegalStateException("Выбранный способ доставки временно недоступен");
        }

        return strategyFactory.getStrategy(carrierName).calculate(order, weight, carrier);
    }

    private double calculateWeight(Order order) {
        return order.getItems().stream()
                .mapToDouble(i -> i.getProduct().getWeight() * i.getQuantity())
                .sum();
    }
}
