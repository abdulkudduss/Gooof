package com.example.gooo.domain.entity;

import com.example.gooo.domain.converter.UserRoleConverter;
import com.example.gooo.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean active = true;

    @Convert(converter = UserRoleConverter.class)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();
}
