package com.querydslbug.entity;

import java.util.Comparator;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class MyEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 30, scale = 10)
    @Convert(converter = CustomConverter.class)
    private MyCustomNumber myCustomNumber;

    public MyEntity() {
        // nop
    }

    public MyEntity(String uuid, String type, MyCustomNumber myCustomNumber) {
        this.id = UUID.fromString(uuid);
        this.type = type;
        this.myCustomNumber = myCustomNumber;
    }

    public static Comparator<MyEntity> comparator() {
        return Comparator.comparing(MyEntity::getId)
                .thenComparing(MyEntity::getType)
                .thenComparing(MyEntity::getMyCustomType);
    }

    @Override
    public String toString() {
        return "MyEntity{id=%s, type='%s', myCustomNumber=%s}".formatted(id, type, myCustomNumber);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MyCustomNumber getMyCustomType() {
        return myCustomNumber;
    }

    public void setMyCustomType(MyCustomNumber myCustomNumber) {
        this.myCustomNumber = myCustomNumber;
    }
}
