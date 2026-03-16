package com.example.gooo.domain.entity;

import com.example.gooo.domain.embeddable.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shipments_courier")
@Getter
@Setter
public class CourierShipment extends Shipment {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "origin_city")),
            @AttributeOverride(name = "street", column = @Column(name = "origin_street")),
            @AttributeOverride(name = "houseNumber", column = @Column(name = "origin_house_number")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "origin_zip_code")),
            @AttributeOverride(name = "countryCode", column = @Column(name = "origin_country_code"))
    })
    private Address originAddress;
    @Embedded
    private Address deliveryAddress;

    @Column(length = 20)
    private String entranceCode;

    @Column(length = 20)
    private String contactPhone;
}
