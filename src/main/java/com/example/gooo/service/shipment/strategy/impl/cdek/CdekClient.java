package com.example.gooo.service.shipment.strategy.impl.cdek;

import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffListResponse;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffRequest;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTariffResponse;
import com.example.gooo.service.shipment.strategy.impl.cdek.dto.CdekTokenResponse;
import com.example.gooo.dto.shipment.CalculationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CdekClient {

    private final String apiUrl;
    private final String account;
    private final String password;
    private final RestClient restClient;

    // Переменные для кэширования токена в памяти
    private String cachedToken = null;
    private long tokenExpiresAt = 0;

    public CdekClient(
            @Value("${cdek.api.url}") String apiUrl,
            @Value("${cdek.api.account}") String account,
            @Value("${cdek.api.password}") String password,
            RestClient.Builder restClientBuilder
    ) {
        this.apiUrl = apiUrl;
        this.account = account;
        this.password = password;
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
    }

    /**
     * Получение токена доступа OAuth2 от СДЭК с кэшированием.
     * Метод синхронизирован (synchronized), чтобы при одновременном запросе от нескольких
     * пользователей мы не отправляли несколько запросов за токеном одновременно.
     */
    public synchronized String getToken() {
        // Проверяем, есть ли кэшированный токен и не истек ли срок его действия
        // Берем запас времени, чтобы не отправить протухающий в эту же секунду токен
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return cachedToken;
        }

        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");
            map.add("client_id", account);
            map.add("client_secret", password);

            CdekTokenResponse response = restClient.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(map)
                    .retrieve()
                    .body(CdekTokenResponse.class);

            if (response == null || response.access_token() == null) {
                throw new IllegalStateException("CDEK token response is empty");
            }

            // Обновляем кэш. По документации токен живет 3600 сек (1 час).
            // Кэшируем на 3500 секунд (58 минут), чтобы гарантированно избежать просрочки.
            this.cachedToken = response.access_token();
            this.tokenExpiresAt = System.currentTimeMillis() + (3500 * 1000L);

            log.info("Successfully fetched and cached new CDEK token");
            return cachedToken;

        } catch (Exception e) {
            log.error("Failed to get CDEK token: {}", e.getMessage());
            // Выбрасываем исключение, чтобы не разводить логику проверок на null ниже
            throw new RuntimeException("Ошибка авторизации в СДЭК: " + e.getMessage(), e);
        }
    }

    /**
     * Получение списка доступных тарифов для заданных параметров.
     */
    public List<CdekTariffResponse.TariffResult> calculateTariffList(CdekTariffRequest requestBody) {
        String token = getToken();
        requestBody.setTariff_code(null);

        try {
            CdekTariffResponse body = restClient.post()
                    .uri("/calculator/tarifflist")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(CdekTariffResponse.class);

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

    private String formatDateRange(CdekTariffResponse.DateRange range) {
        if (range == null) return null;
        if (range.getMin() != null && range.getMin().equals(range.getMax())) {
            return range.getMin();
        }
        return range.getMin() + " - " + range.getMax();
    }

    /**
     * Расчет конкретного тарифа или получение основного результата расчета.
     */
    public CalculationResult calculateTariff(CdekTariffRequest requestBody) {
        String token = getToken();

        try {
            
            ResponseEntity<CdekTariffResponse> responseEntity = restClient.post()
                    .uri("/calculator/tariff")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(CdekTariffResponse.class);

            CdekTariffResponse body = responseEntity.getBody();

            if (body == null) {
                return CalculationResult.failure("Пустой ответ от СДЭК");
            }

            if (body.getDelivery_sum() != null) {
                return CalculationResult.builder()
                        .cost(body.getDelivery_sum())
                        .totalSum(body.getTotal_sum())
                        .periodMin(body.getPeriod_min())
                        .periodMax(body.getPeriod_max())
                        .deliveryDateRange(formatDateRange(body.getDelivery_date_range()))
                        .build();
            }

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

            CdekTariffResponse.TariffResult first = body.getTariff_codes().get(0);
            return CalculationResult.builder()
                    .cost(first.getDelivery_sum())
                    .totalSum(first.getTotal_sum())
                    .periodMin(first.getPeriod_min())
                    .periodMax(first.getPeriod_max())
                    .deliveryDateRange(formatDateRange(first.getDelivery_date_range()))
                    .build();

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

    public CdekTariffListResponse getTariffList() {
        String token = getToken();
        try{
            return restClient.get().uri("/calculator/alltariffs")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(CdekTariffListResponse.class);
        }catch (Exception e) {
            log.error("Error fetching CDEK tariff list: {}", e.getMessage());
            CdekTariffListResponse response = new CdekTariffListResponse();
            response.setTariff_codes(List.of()); // Возвращаем пустой список при ошибке\
            return response;
        }
    }
}