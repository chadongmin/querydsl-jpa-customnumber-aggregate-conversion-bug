package com.querydslbug.entity;


import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CustomConverter implements AttributeConverter<MyCustomNumber, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(MyCustomNumber attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getBigDecimalValue();
    }

    @Override
    public MyCustomNumber convertToEntityAttribute(BigDecimal dbData) {
        if (dbData == null) {
            return null;
        }

        return new MyCustomNumber(dbData);
    }
}