package com.example.gooo;

import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffRequest;
import com.example.gooo.dto.shipment.CalculationResult;
import com.example.gooo.service.shipment.strategy.impl.cdek.CdekClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CdekIntegrationTest {

    @Autowired
    private CdekClient cdekClient;

    @Test
    void testCdekToken() {
        String token = cdekClient.getToken();
        System.out.println("TOKEN: " + token);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testCalculateTariff() {
        CdekTariffRequest request = CdekTariffRequest.builder()
                .tariff_code(137)
                .from_location(new CdekTariffRequest.Location(44))
                .to_location(new CdekTariffRequest.Location(270))
                .packages(List.of(new CdekTariffRequest.Package(1.0)))
                .build();

        CalculationResult result = cdekClient.calculateTariff(request);

        assertTrue(result.isSuccessful());
        assertNotNull(result.getCost());
        assertNotNull(result.getTotalSum());
        assertNotNull(result.getPeriodMin());
        assertNotNull(result.getPeriodMax());
    }
}