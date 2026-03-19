package com.example.gooo.service.shipment.strategy.impl.cdek.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdekTariffListResponse {

    private List<TariffCode> tariff_codes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class TariffCode {
        private String name;
        private List<DeliveryMode> delivery_modes;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class DeliveryMode {
        private Integer delivery_mode;
        private String delivery_mode_name;
        private Integer tariff_code;
    }
}
