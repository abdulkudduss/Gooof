package com.example.gooo.domain.converter;

import com.example.gooo.domain.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserRole attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return UserRole.fromId(dbData);
    }
}
