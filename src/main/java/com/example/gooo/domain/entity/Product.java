package com.example.gooo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {
    private String name;
    private String sku;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal currentPrice;
}