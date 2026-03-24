package com.example.gooo.controller;

import com.example.gooo.service.shipment.ShipmentService;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipment")
@RequiredArgsConstructor
@Tag(name = "Shipment Controller", description = "Управление доставкой и тарифами")
public class ShipmentController {
    private final ShipmentService shipmentService;
    @GetMapping("/tariffs")
    @Operation(summary = "Получение всех доступных тарифов СДЭК для тестирования и отладки")
    public ResponseEntity<CdekTariffListResponse> getCdekTariffsList() {
        return ResponseEntity.ok(shipmentService.getAvailableTariffList());
    }
}
