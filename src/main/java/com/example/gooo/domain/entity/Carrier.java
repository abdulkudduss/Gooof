package com.example.gooo.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carriers")
@Getter
@Setter
public class Carrier extends BaseEntity {
    private String name;

    private boolean active;

    @OneToMany(mappedBy = "carrier", cascade = CascadeType.ALL)
    private List<ShippingMethod>
            methods = new ArrayList<>();
}