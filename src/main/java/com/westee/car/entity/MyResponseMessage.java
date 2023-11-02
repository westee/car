package com.westee.car.entity;

public enum MyResponseMessage {
    OK,
    FAIL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
