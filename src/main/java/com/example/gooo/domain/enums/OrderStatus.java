package com.example.gooo.domain.enums;

import java.util.stream.Stream;

public enum OrderStatus {
    DRAFT(1),
    PENDING_PAYMENT(2),
    PROCESSING(3),
    DELIVERED(4),
    CANCELLED(5),
    REFUNDED(6);


    private final int id;

    OrderStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static OrderStatus fromId(int id) {
        return Stream.of(OrderStatus.values())
                .filter(s -> s.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus id: " + id));
    }
}
