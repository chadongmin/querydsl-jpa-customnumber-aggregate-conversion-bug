package com.querydslbug.entity;


import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;

public class MyCustomNumber extends Number implements Comparable<MyCustomNumber> {
    @Serial
    private static final long serialVersionUID = 7747403523947398858L;

    private final BigDecimal bigDecimal;

    public MyCustomNumber(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public MyCustomNumber(String value) {
        this.bigDecimal = new BigDecimal(value);
    }

    @Override
    public String toString() {
        return bigDecimal.toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyCustomNumber that = (MyCustomNumber) o;
        return bigDecimal == null
                ? that.bigDecimal == null
                : bigDecimal.compareTo(that.bigDecimal) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bigDecimal);
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimal;
    }

    @Override
    public long longValue() {
        return bigDecimal.longValue();
    }

    @Override
    public int intValue() {
        return bigDecimal.intValue();
    }

    @Override
    public float floatValue() {
        return bigDecimal.floatValue();
    }

    @Override
    public double doubleValue() {
        return bigDecimal.doubleValue();
    }

    @Override
    public int compareTo(MyCustomNumber o) {
        return getBigDecimalValue().compareTo(o.getBigDecimalValue());
    }
}