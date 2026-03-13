package com.example.gooo.domain.entity;

import com.example.gooo.domain.embeddable.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String firstName;
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Embedded
    private Address defaultAddress;
}
