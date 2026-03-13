package com.example.gooo.controller;

import com.example.gooo.dto.CreateOrderRequest;
import com.example.gooo.dto.OrderDetailsDTO;
import com.example.gooo.dto.OrderResponseDTO;
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

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Управление заказами")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Создать новый заказ", description = "Принимает данные заказа и создает его в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Метод доставки или товар не найден",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Request to create order for userId: {}, currency: {}", request.getUserId(), request.getCurrencyCode());
        // Вызываем бизнес-логику
        OrderResponseDTO response = orderService.createOrder(request);
        log.info("Order created successfully with ID: {}", response.getOrderId());
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

}
