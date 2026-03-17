package com.example.gooo.service;

import com.example.gooo.dto.cdek.CdekTariffRequest;
import com.example.gooo.dto.cdek.CdekTariffResponse;
import com.example.gooo.dto.cdek.CdekTokenResponse;
import com.example.gooo.dto.shipment.CalculationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CdekClient {

    @Value("${cdek.api.url}")
    private String apiUrl;
    @Value("${cdek.api.account}")
    private String account;
    @Value("${cdek.api.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");
            map.add("client_id", account);
            map.add("client_secret", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            var response = restTemplate.postForObject(apiUrl + "/oauth/token", request, CdekTokenResponse.class);

            if (response == null || response.access_token() == null) {
                log.error("CDEK token response is empty");
                return null;
            }
            return response.access_token();
        } catch (Exception e) {
            log.error("Failed to get CDEK token: {}", e.getMessage());
            return null;
        }
    }


    public List<CdekTariffResponse.TariffResult> calculateTariffList(CdekTariffRequest requestBody) {
        String token = getToken();
        if (token == null) {
            log.warn("CDEK calculation list skipped: token is null");
            return List.of();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Согласно документации, для tarifflist tariff_code не передается
        requestBody.setTariff_code(null);
        HttpEntity<CdekTariffRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<CdekTariffResponse> response =
                    restTemplate.postForEntity(
                            apiUrl + "/calculator/tarifflist",
                            entity,
                            CdekTariffResponse.class
                    );

            CdekTariffResponse body = response.getBody();
            if (body == null || body.getTariff_codes() == null) {
                log.warn("CDEK API returned no tariffs for tarifflist request: {}", requestBody);
                return List.of();
            }

            return body.getTariff_codes();

        } catch (Exception e) {
            log.error("Error calling CDEK tarifflist API: {}", e.getMessage());
            return List.of();
        }
    }

    public CalculationResult calculateTariff(CdekTariffRequest requestBody) {
        String token = getToken();
        if (token == null) {
            log.warn("CDEK calculation skipped: token is null");
            return CalculationResult.failure("Ошибка авторизации в СДЭК");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CdekTariffRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<CdekTariffResponse> response =
                    restTemplate.postForEntity(
                            apiUrl + "/calculator/tariff",
                            entity,
                            CdekTariffResponse.class
                    );

            CdekTariffResponse body = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMsg = "Ошибка API СДЭК: " + response.getStatusCode();
                if (body != null && body.getErrors() != null) {
                    errorMsg += " " + body.getErrors().stream()
                            .map(e -> e.getCode() + ": " + e.getMessage())
                            .collect(Collectors.joining(", "));
                }
                log.error(errorMsg);
                return CalculationResult.failure(errorMsg);
            }

            if (body == null) {
                return CalculationResult.failure("Пустой ответ от СДЭК");
            }

            // Если был указан конкретный тариф (tariff_code в запросе), результат будет в корневом delivery_sum
            if (body.getDelivery_sum() != null) {
                return CalculationResult.success(body.getDelivery_sum());
            }

            // Если запрашивались все тарифы (через /calculator/tariffs - но мы используем /tariff)
            if (body.getTariff_codes() == null || body.getTariff_codes().isEmpty()) {
                String errorMsg = "СДЭК не вернул доступных тарифов для данного направления";
                if (body.getErrors() != null && !body.getErrors().isEmpty()) {
                    errorMsg = body.getErrors().stream()
                            .map(e -> e.getCode() + ": " + e.getMessage())
                            .collect(Collectors.joining(", "));
                }
                log.warn("CDEK API returned no tariffs for request: {}. Reason: {}", requestBody, errorMsg);
                return CalculationResult.failure(errorMsg);
            }

            return CalculationResult.success(body.getTariff_codes().get(0).getDelivery_sum());

        } catch (HttpClientErrorException e) {
            CdekTariffResponse errorBody = e.getResponseBodyAs(CdekTariffResponse.class);
            String errorMsg = "Ошибка СДЭК (" + e.getStatusCode() + ")";
            if (errorBody != null && errorBody.getErrors() != null) {
                errorMsg = errorBody.getErrors().stream()
                        .map(err -> err.getCode() + ": " + err.getMessage())
                        .collect(Collectors.joining(", "));
            }
            log.error("CDEK API HttpClientError: {}. Body: {}", e.getMessage(), e.getResponseBodyAsString());
            return CalculationResult.failure(errorMsg);
        } catch (RestClientException e) {
            log.error("Error calling CDEK API: {}", e.getMessage());
            return CalculationResult.failure("Ошибка сетевого взаимодействия со СДЭК: " + e.getMessage());
        }
    }
}