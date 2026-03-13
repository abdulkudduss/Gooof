package com.example.gooo.domain.converter;

import com.example.gooo.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleConverterTest {

    private final UserRoleConverter converter = new UserRoleConverter();

    @Test
    void testConvertToDatabaseColumn() {
        assertEquals(1, converter.convertToDatabaseColumn(UserRole.CUSTOMER));
        assertEquals(2, converter.convertToDatabaseColumn(UserRole.ADMIN));
        assertEquals(3, converter.convertToDatabaseColumn(UserRole.MODERATOR));
    }

    @Test
    void testConvertToDatabaseColumnNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void testConvertToEntityAttribute() {
        assertEquals(UserRole.CUSTOMER, converter.convertToEntityAttribute(1));
        assertEquals(UserRole.ADMIN, converter.convertToEntityAttribute(2));
        assertEquals(UserRole.MODERATOR, converter.convertToEntityAttribute(3));
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
