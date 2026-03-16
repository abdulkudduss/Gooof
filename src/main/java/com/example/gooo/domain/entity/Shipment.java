package com.example.gooo.domain.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
public abstract class Shipment extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Связь с выбранной компанией (СДЭК, Ылдам и т.д.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    // Стоимость доставки, которая была рассчитана в момент выбора
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal shippingCost;

    @Column(length = 100)
    private String trackingNumber;
}