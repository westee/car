package com.westee.car.exceptions;

import org.springframework.http.HttpStatus;

public class MyHttpException extends RuntimeException {
    private final int statusCode;
    private final String message;

    public MyHttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public static Exception success(String message) {
        return new MyHttpException(HttpStatus.OK.value(), message);
    }

    public static MyHttpException badRequest(String message) {
        return new MyHttpException(HttpStatus.BAD_REQUEST.value(), message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
