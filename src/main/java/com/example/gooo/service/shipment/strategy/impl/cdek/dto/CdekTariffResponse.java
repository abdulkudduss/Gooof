package com.example.gooo.service.shipment.strategy.impl.cdek.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// Ответ от калькулятора
@Data
public class CdekTariffResponse {
    private BigDecimal delivery_sum;
    private BigDecimal total_sum;
    private Integer period_min;
    private Integer period_max;
    private DateRange delivery_date_range;
    private List<TariffResult> tariff_codes;
    private List<Error> errors;

    @Data
    public static class DateRange {
        private String min;
        private String max;
    }

    @Data
    public static class TariffResult {
        private String tariff_name;
        private String tariff_description;
        private Integer tariff_code;
        private BigDecimal delivery_sum;
        private BigDecimal total_sum;
        private Integer period_min;
        private Integer period_max;
        private DateRange delivery_date_range;
    }

    @Data
    public static class Error {
        private String code;
        private String message;
    }
}