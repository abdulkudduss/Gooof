package com.example.gooo.service.shipment;

import com.example.gooo.domain.entity.Carrier;
import com.example.gooo.domain.entity.Order;
import com.example.gooo.domain.repository.CarrierRepository;
import com.example.gooo.dto.ShippingCostDTO;
import com.example.gooo.dto.cdek.CdekTariffOptionDTO;
import com.example.gooo.dto.shipment.CalculationResult;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.service.shipment.strategy.ShippingStrategy;
import com.example.gooo.service.shipment.strategy.impl.CdekStrategy;
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
    public List<ShippingCostDTO> calculateOptions(Order order, Integer receiverCityCode) {
        double weight = calculateWeight(order);
        List<Carrier> activeCarriers = carrierRepository.findByActiveTrue();

        return activeCarriers.stream()
                .map(carrier -> {
                    ShippingStrategy strategy = strategyFactory.getStrategy(carrier.getName());
                    // Получаем результат расчета (со стоимостью или ошибкой)
                    CalculationResult result = strategy.calculate(order, weight, carrier, receiverCityCode);

                    return ShippingCostDTO.builder()
                            .carrier(carrier.getName())
                            .cost(result.getCost())
                            .totalSum(result.getTotalSum() != null ? result.getTotalSum() : result.getCost())
                            .periodMin(result.getPeriodMin())
                            .periodMax(result.getPeriodMax())
                            .deliveryDateRange(result.getDeliveryDateRange())
                            .error(result.getError())
                            .build();
                })
                .toList();
    }

    @Override
    public BigDecimal calculateFinalCost(Order order, String carrierName, Integer receiverCityCode) {
        double weight = calculateWeight(order);
        Carrier carrier = carrierRepository.findByName(carrierName)
                .orElseThrow(() -> new ResourceNotFoundException("Перевозчик не найден"));

        CalculationResult result = strategyFactory.getStrategy(carrierName)
                .calculate(order, weight, carrier, receiverCityCode);
        
        if (!result.isSuccessful()) {
            throw new ResourceNotFoundException("Доставка данным перевозчиком (" + carrierName + ") недоступна: " + result.getError());
        }
        
        return result.getCost();
    }
    @Override
    public List<CdekTariffOptionDTO> getCdekTariffOptions(Order order, Integer receiverCityCode) {
        double weight = calculateWeight(order);
        ShippingStrategy strategy = strategyFactory.getStrategy("CDEK");
        if (strategy instanceof CdekStrategy cdekStrategy) {
            return cdekStrategy.calculateTariffList(weight, receiverCityCode);
        }
        return List.of();
    }

    private double calculateWeight(Order order) {
        return order.getItems().stream()
                .mapToDouble(i -> i.getProduct().getWeight() * i.getQuantity())
                .sum();
    }
}
