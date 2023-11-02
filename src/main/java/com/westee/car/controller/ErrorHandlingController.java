package com.westee.car.controller;

import com.westee.car.entity.MyResponse;
import com.westee.car.exceptions.MyHttpException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ErrorHandlingController {
    @ExceptionHandler(MyHttpException.class)
    public @ResponseBody
    MyResponse<?> onError(HttpServletResponse response, MyHttpException e) {
        response.setStatus(e.getStatusCode());
        return MyResponse.of(e.getMessage(), null);
    }
}
