package com.example.gooo.domain.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
public abstract class Shipment extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShippingMethod shippingMethod;

    @Column(length = 100)
    private String trackingNumber;
    private LocalDateTime shippedAt;
}