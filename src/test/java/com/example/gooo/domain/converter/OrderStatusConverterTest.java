package com.example.gooo.domain.converter;

import com.example.gooo.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderStatusConverterTest {

    private final OrderStatusConverter converter = new OrderStatusConverter();

    @Test
    void testConvertToDatabaseColumn() {
        assertEquals(1, converter.convertToDatabaseColumn(OrderStatus.NEW));
        assertEquals(2, converter.convertToDatabaseColumn(OrderStatus.PENDING_PAYMENT));
        assertEquals(3, converter.convertToDatabaseColumn(OrderStatus.PROCESSING));
        assertEquals(4, converter.convertToDatabaseColumn(OrderStatus.DELIVERED));
        assertEquals(5, converter.convertToDatabaseColumn(OrderStatus.CANCELLED));
        assertEquals(6, converter.convertToDatabaseColumn(OrderStatus.REFUNDED));
    }

    @Test
    void testConvertToDatabaseColumnNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void testConvertToEntityAttribute() {
        assertEquals(OrderStatus.NEW, converter.convertToEntityAttribute(1));
        assertEquals(OrderStatus.PENDING_PAYMENT, converter.convertToEntityAttribute(2));
        assertEquals(OrderStatus.PROCESSING, converter.convertToEntityAttribute(3));
        assertEquals(OrderStatus.DELIVERED, converter.convertToEntityAttribute(4));
        assertEquals(OrderStatus.CANCELLED, converter.convertToEntityAttribute(5));
        assertEquals(OrderStatus.REFUNDED, converter.convertToEntityAttribute(6));
    }

    @Test
    void testConvertToEntityAttributeNull() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void testConvertToEntityAttributeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(99));
    }
}
