package com.example.gooo.service.shipment;

import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.service.shipment.strategy.ShippingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShippingStrategyFactory {

    private final List<ShippingStrategy> strategies;

    public ShippingStrategy getStrategy(String carrierName) {
        return strategies.stream()
                .filter(s -> s.getCarrierName().equalsIgnoreCase(carrierName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Стратегия расчета не найдена для: " + carrierName));
    }
}