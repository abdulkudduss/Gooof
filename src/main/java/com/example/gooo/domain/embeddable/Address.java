package com.example.gooo.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Город не может быть пустым")
    @Column(length = 100, nullable = false)
    private String city;

    @NotBlank(message = "Улица не может быть пустой")
    @Column(length = 255, nullable = false)
    private String street;

    @NotBlank(message = "Номер дома не может быть пустым")
    @Column(length = 20, nullable = false)
    private String houseNumber;

    @NotBlank(message = "Почтовый индекс не может быть пустым")
    @Column(length = 20, nullable = false)
    private String zipCode;

    @NotBlank(message = "Код страны не может быть пустым")
    @Size(min = 2, max = 2, message = "Код страны должен содержать 2 символа")
    @Column(length = 2, nullable = false)
    private String countryCode;
}
