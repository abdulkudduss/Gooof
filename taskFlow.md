Ниже представлено описание task flow (выполнения задачи) для эндпоинта `GET /api/v1/orders/{id}/delivery-options` в режиме «дебага».

### 1. Уровень Controller (`OrderController`)
Запрос поступает в метод `getDeliveryOptions(Long id, Integer receiverCityCode)`.
*   **Входные данные:** `id` (ID заказа) и `receiverCityCode` (код города получателя для СДЭК).
*   **Действие:** Контроллер вызывает метод `orderService.getDeliveryOptions(id, receiverCityCode)`.

### 2. Уровень Service (`OrderServiceImpl`)
Выполняется метод `getDeliveryOptions`.
*   **Поиск заказа:** `orderRepository.findById(id)` извлекает сущность `Order` из БД. Если заказ не найден, выбрасывается `ResourceNotFoundException`.
*   **Переход к расчету:** Вызывается `shipmentService.calculateOptions(order, receiverCityCode)`.

### 3. Уровень Shipment Service (`ShipmentServiceImpl`)
Метод `calculateOptions` оркеструет расчет стоимости для всех активных перевозчиков.
*   **Расчет веса:** Приватный метод `calculateWeight(order)` суммирует вес всех товаров в заказе (`weight * quantity`).
*   **Получение перевозчиков:** `carrierRepository.findByActiveTrue()` возвращает список всех активных служб доставки (например, СДЭК, YLDAM).
*   **Цикл по перевозчикам:** Для каждого перевозчика:
    1.  `strategyFactory.getStrategy(carrier.getName())` подбирает нужную стратегию (CdekStrategy, YldamStrategy и т.д.).
    2.  Вызывается `strategy.calculate(...)`.

### 4. Уровень Стратегий (Strategy)
Здесь логика разделяется в зависимости от перевозчика:

#### А. CdekStrategy (Внешнее API)
*   **Формирование запроса:** Создается `CdekTariffRequest` с кодом тарифа 137 (Склад-Дверь), кодом города отправителя (из конфига), кодом города получателя и весом.
*   **CdekClient:**
    *   `getToken()`: Проверяет кэшированный токен. Если он просрочен или отсутствует, делает запрос к СДЭК (`/oauth/token`) и сохраняет новый.
    *   `calculateTariff(request)`: Выполняет POST-запрос к СДЭК API (`/calculator/tariff`) с Bearer-токеном.
*   **Обработка ответа:** Получает стоимость (`delivery_sum`), сроки (`period_min/max`) и возвращает `CalculationResult`.

#### Б. YldamStrategy (Внутренний расчет)
*   Берет параметры из сущности `Carrier` (базовая цена, лимит веса, цена за лишний кг).
*   Рассчитывает стоимость по формуле: `BasePrice + (ExtraWeight * PricePerExtraKg)`.
*   Возвращает `CalculationResult.success(cost)`.

### 5. Сбор и возврат результата
*   `ShipmentServiceImpl` собирает результаты всех стратегий в список объектов `ShippingCostDTO`. Если какая-то стратегия вернула ошибку (например, СДЭК недоступен), она записывается в поле `error` этого DTO, а не прерывает весь процесс.
*   `OrderServiceImpl` возвращает этот список контроллеру.
*   `OrderController` отдает JSON-ответ пользователю (HTTP 200).

**Итог:** Система динамически опрашивает всех доступных перевозчиков, используя комбинацию локальных расчетов и внешних API (СДЭК) для предоставления пользователю вариантов доставки.