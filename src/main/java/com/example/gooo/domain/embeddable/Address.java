package com.example.gooo.domain.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String city;
    private String street;
    private String houseNumber;
    private String zipCode;
    private String countryCode;
}
