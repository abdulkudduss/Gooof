package com.example.gooo.domain.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "shipping_methods")
@Getter @Setter
public class ShippingMethod extends BaseEntity {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Carrier carrier;
}