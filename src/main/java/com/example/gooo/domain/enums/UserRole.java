package com.example.gooo.domain.enums;
import java.util.stream.Stream;

public enum UserRole {
    CUSTOMER(1),
    ADMIN(2),
    MODERATOR(3);

    private final int id;

    UserRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static UserRole fromId(int id) {
        return Stream.of(UserRole.values())
                .filter(r -> r.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown UserRole id: " + id));
    }
}
