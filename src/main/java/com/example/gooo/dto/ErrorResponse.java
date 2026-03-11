package com.example.gooo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Структура сообщения об ошибке")
public class ErrorResponse {
    @Schema(description = "HTTP статус код", example = "404")
    private int status;
    
    @Schema(description = "Тип ошибки", example = "Not Found")
    private String error;
    
    @Schema(description = "Описание ошибки", example = "Товар не найден")
    private String message;
    
    @Schema(description = "Временная метка ошибки")
    private LocalDateTime timestamp;
}
