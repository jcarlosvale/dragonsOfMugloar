package com.dragonsofmugloar.api.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Throwable {
    public CustomException(HttpStatus statusCode, String message) {
        super(String.format("Unexpected error %s - %s [CODE - BODY]", statusCode, message));
    }
}
