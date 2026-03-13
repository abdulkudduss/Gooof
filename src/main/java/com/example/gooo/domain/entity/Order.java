package com.example.gooo.domain.entity;
import com.example.gooo.domain.enums.OrderStatus;
import com.example.gooo.domain.converter.OrderStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity {
    private LocalDateTime createdAt = LocalDateTime.now();

    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status = OrderStatus.NEW;

    @Column(length = 3, nullable = false)
    private String currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;
}