package com.example.gooo.domain.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal priceAtPurchase;
}