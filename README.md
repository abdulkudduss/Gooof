# Gooo Order Service

Gooo is a simple e-commerce order management service built with Spring Boot. It handles products, order processing, and shipment management.

## 🚀 Features

- **Product Management**: Fetch a list of products or view detailed product information.
- **Order Processing**: Create orders with multiple items, automatic price calculation, and basic validation.
- **Shipment Integration**: Each order automatically creates a courier shipment with tracking numbers and delivery address details.
- **API Documentation**: Integrated OpenAPI (Swagger) for easy endpoint exploration.
- **Global Exception Handling**: Structured error responses for common API failures.
- **Data Initialization**: Automatic database seeding with test products and shipping methods.
- **Mapping**: Clean DTO mapping using MapStruct.
- **Logging**: Detailed logging for request tracing and debugging.

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 3+** (Web MVC, Data JPA)
- **PostgreSQL** (Relational Database)
- **Lombok** (Boilerplate reduction)
- **MapStruct** (Entity-DTO mapping)
- **SpringDoc OpenAPI** (Swagger UI)
- **Hibernate Validator** (Request validation)

## 📁 Project Structure

- `com.example.gooo.controller`: REST Controllers.
- `com.example.gooo.service`: Business logic layer.
- `com.example.gooo.domain`: Entity models, enums, and repositories.
- `com.example.gooo.dto`: Data Transfer Objects for API requests and responses.
- `com.example.gooo.mapper`: MapStruct interfaces.
- `com.example.gooo.exception`: Custom exceptions and global exception handler.
- `com.example.gooo.init`: Database seed data configuration.

## ⚙️ Configuration

The application uses PostgreSQL. Configuration is located in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/appdb
spring.datasource.username=boss
spring.datasource.password=boss
spring.jpa.hibernate.ddl-auto=update
```

## 🏃 How to Run

1. **Prerequisites**:
   - JDK 17
   - Maven
   - Running PostgreSQL instance with a database named `appdb`.

2. **Clone and Build**:
   ```bash
   git clone https://github.com/abdulkudduss/Gooof.git
   cd Gooo
   ./mvnw clean compile
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`.

## 📖 API Documentation

Once the application is running, you can access the Swagger UI at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Key Endpoints

#### Products
- `GET /api/v1/products`: List all products.
- `GET /api/v1/products/{id}`: Get product details.

#### Orders
- `POST /api/v1/orders`: Create a new order.

## 📝 Example Request (Create Order)

**POST `/api/v1/orders`**
```json
{
  "currencyCode": "RUB",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "shippingMethodId": 1,
  "deliveryAddress": {
    "city": "Moscow",
    "street": "Arbat",
    "houseNumber": "1",
    "zipCode": "123456",
    "countryCode": "RU"
  },
  "contactPhone": "+79991234567"
}
```
