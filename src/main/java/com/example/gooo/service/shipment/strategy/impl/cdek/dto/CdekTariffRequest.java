package com.example.gooo.service.shipment.strategy.impl.cdek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CdekTariffRequest {
    private Integer tariff_code; // Например, 137 (курьерская доставка)
    private Location from_location;
    private Location to_location;
    private List<Package> packages;

    @Data
    @AllArgsConstructor
    public static class Location { private Integer code; }

    @Data @AllArgsConstructor
    public static class Package { private Double weight; }
}
