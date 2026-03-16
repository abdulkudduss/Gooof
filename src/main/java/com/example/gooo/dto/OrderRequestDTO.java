package com.example.gooo.dto;

import com.example.gooo.domain.embeddable.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на обновление информации о заказе")
public class OrderRequestDTO {
    @NotBlank
    private String carrierCode;

    @NotNull
    private Address originAddress;

    @NotNull
    private Address deliveryAddress;
}
