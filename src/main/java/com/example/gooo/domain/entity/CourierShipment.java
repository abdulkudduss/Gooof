package com.example.gooo.domain.entity;

import com.example.gooo.domain.embeddable.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shipments_courier")
@Getter
@Setter
public class CourierShipment extends Shipment {
    @Embedded
    private Address deliveryAddress;

    @Column(length = 20)
    private String entranceCode;

    @Column(length = 20)
    private String contactPhone;
}
