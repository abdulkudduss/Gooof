package com.example.gooo.service.shipment.strategy.impl;

import com.example.gooo.domain.entity.Carrier;
import com.example.gooo.domain.entity.Order;
import com.example.gooo.dto.cdek.CdekTariffOptionDTO;
import com.example.gooo.dto.cdek.CdekTariffRequest;
import com.example.gooo.dto.cdek.CdekTariffResponse;
import com.example.gooo.dto.shipment.CalculationResult;
import com.example.gooo.service.CdekClient;
import com.example.gooo.service.shipment.strategy.ShippingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CdekStrategy implements ShippingStrategy {

    private final CdekClient cdekClient;

    @Value("${cdek.api.from-location-code}")
    private Integer fromLocationCode;

    @Override
    public String getCarrierName() {
        return "CDEK";
    }

    @Override
    public CalculationResult calculate(Order order, double totalWeight, Carrier carrier, Integer receiverCityCode) {
        // Формируем запрос согласно документации СДЭК
        CdekTariffRequest request = CdekTariffRequest.builder()
                .tariff_code(137) // Режим "Склад-Дверь" (посылка до двери)
                .from_location(new CdekTariffRequest.Location(fromLocationCode))
                .to_location(new CdekTariffRequest.Location(receiverCityCode))
                .packages(List.of(new CdekTariffRequest.Package(totalWeight)))
                .build();

        try {
            return cdekClient.calculateTariff(request);
        } catch (Exception e) {
            return CalculationResult.failure("Внутренняя ошибка стратегии СДЭК: " + e.getMessage());
        }
    }

    public List<CdekTariffOptionDTO> calculateTariffList(double totalWeight, Integer receiverCityCode) {
        CdekTariffRequest request = CdekTariffRequest.builder()
                .from_location(new CdekTariffRequest.Location(fromLocationCode))
                .to_location(new CdekTariffRequest.Location(receiverCityCode))
                .packages(List.of(new CdekTariffRequest.Package(totalWeight)))
                .build();

        List<CdekTariffResponse.TariffResult> results = cdekClient.calculateTariffList(request);

        return results.stream()
                .map(res -> CdekTariffOptionDTO.builder()
                        .name(res.getTariff_name())
                        .description(res.getTariff_description())
                        .code(res.getTariff_code())
                        .cost(res.getDelivery_sum())
                        .minDays(res.getPeriod_min())
                        .maxDays(res.getPeriod_max())
                        .build())
                .collect(Collectors.toList());
    }
}