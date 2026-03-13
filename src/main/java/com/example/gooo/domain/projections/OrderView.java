package com.example.gooo.domain.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderView {
    LocalDateTime getOrderDate();
    String getCustomerName();
    BigDecimal getTotalAmount();
}
