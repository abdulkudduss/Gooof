package com.example.gooo.controller;

import com.example.gooo.dto.CreateOrderRequest;
import com.example.gooo.dto.OrderResponseDTO;
import com.example.gooo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Управление заказами")
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
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody CreateOrderRequest request) {
        // Вызываем бизнес-логику
        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
