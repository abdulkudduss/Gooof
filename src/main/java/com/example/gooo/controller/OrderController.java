package com.example.gooo.controller;

import com.example.gooo.dto.*;
import com.example.gooo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Управление заказами")
@Slf4j
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/draft")
    @Operation(summary = "Шаг 1: Создание черновика заказа (товары из корзины)")
    public ResponseEntity<DraftOrderResponse> createDraftOrder(@Valid @RequestBody DraftOrderRequest request) {

        // Передаем в сервис на обработку и сохранение
        DraftOrderResponse response = orderService.createDraftOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "Получить детали заказа", description = "Возвращает детализированную информацию о заказе по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о заказе получена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailsDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public OrderDetailsDTO getOrderDetails(@PathVariable Long id) {
        log.info("Request to fetch order details for id: {}", id);
        return orderService.getOrderDetails(id);
    }

    @GetMapping("/{id}/delivery-options")
    @Operation(summary = "Шаг 2: Получение доступных вариантов доставки для заказа")
    public List<ShippingCostDTO> getDeliveryOptions(@PathVariable Long id) {
        // Логика получения доступных вариантов доставки для заказа
        // Например, можно вызвать сервис, который вернет список ShippingCostDTO
        return orderService.getDeliveryOptions(id);
    }



    @PostMapping("/{id}/confirm")
    @Operation(summary = "Шаг 3: Подтверждение заказа и выбор способа доставки")
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long id,@RequestBody OrderRequestDTO request) {
        // Логика подтверждения заказа и выбора способа доставки
        OrderResponseDTO response = orderService.placeOrder( id,request);
        return ResponseEntity.ok(response);
    }
}
