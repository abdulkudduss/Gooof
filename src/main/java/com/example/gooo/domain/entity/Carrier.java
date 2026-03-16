package com.example.gooo.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carriers")
@Getter
@Setter
public class Carrier extends BaseEntity {
    @Column(length = 100, nullable = false)
    private String name;

    private boolean active;

    @Column(nullable = false)
    private Double baseWeightLimit; // Базовый вес (например, 5.0 для СДЭК)

    @Column(nullable = false)
    private BigDecimal basePrice; // Базовая цена (например, 200 для СДЭК)

    @Column(nullable = false)
    private BigDecimal pricePerExtraKg; // Цена за каждый доп. кг (например, 40 для СДЭК)
}