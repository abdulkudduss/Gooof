package com.example.gooo.domain.entity;
import com.example.gooo.domain.enums.OrderStatus;
import com.example.gooo.domain.converter.OrderStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity {
    private LocalDateTime createdAt = LocalDateTime.now();

    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status = OrderStatus.DRAFT;

    @Column(length = 3, nullable = false)
    private String currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    // Сумма только за товары (рассчитывается на Шаге 1)
    @Column(precision = 19, scale = 4)
    private BigDecimal itemsTotal;

    // Сумма за доставку (добавляется на Шаге 3)
    @Column(precision = 19, scale = 4)
    private BigDecimal shippingTotal;

    // Итоговая сумма: товары + доставка
    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmount;
}