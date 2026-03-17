package com.example.gooo;

import com.example.gooo.dto.cdek.CdekTariffRequest;
import com.example.gooo.dto.cdek.CdekTariffResponse;
import com.example.gooo.dto.shipment.CalculationResult;
import com.example.gooo.service.CdekClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
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
    void testCalculateTariffList() {
        CdekTariffRequest request = CdekTariffRequest.builder()
                .from_location(new CdekTariffRequest.Location(44))
                .to_location(new CdekTariffRequest.Location(270))
                .packages(List.of(new CdekTariffRequest.Package(1.0)))
                .build();

        List<CdekTariffResponse.TariffResult> results = cdekClient.calculateTariffList(request);
        System.out.println("[DEBUG_LOG] CDEK Tariff List Size: " + results.size());
        results.forEach(res -> System.out.println("[DEBUG_LOG] Tariff: " + res.getTariff_name() + " (" + res.getTariff_code() + ") = " + res.getDelivery_sum()));
        
        assertNotNull(results);
        // В песочнице должен вернуться хотя бы один тариф
        assertFalse(results.isEmpty(), "Tariff list should not be empty for Moscow -> Novosibirsk");
    }
}