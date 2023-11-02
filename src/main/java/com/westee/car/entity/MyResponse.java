package com.westee.car.entity;

public record MyResponse<T>(String message, T data) {

    public static <T> MyResponse<T> of(String message, T data) {
        return new MyResponse<T>(message, data);
    }

    public static <T> MyResponse<T> of(String message) {
        return new MyResponse<T>(message, null);
    }

    public static <T> MyResponse<T> ok(T data) {
        return new MyResponse<T>(MyResponseMessage.OK.toString(), data);
    }
}
