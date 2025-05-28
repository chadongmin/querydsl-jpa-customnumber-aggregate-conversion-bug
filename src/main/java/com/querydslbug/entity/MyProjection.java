package com.querydslbug.entity;


import java.util.Objects;

import com.querydsl.core.annotations.QueryProjection;

public class MyProjection {
    private final String type;
    private final MyCustomNumber total;

    @QueryProjection
    public MyProjection(String type, MyCustomNumber total) {
        this.type = type;
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public MyCustomNumber getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "MyProjection{type='%s', total=%s}".formatted(type, total);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyProjection that = (MyProjection) o;
        return Objects.equals(type, that.type) && Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, total);
    }
}
