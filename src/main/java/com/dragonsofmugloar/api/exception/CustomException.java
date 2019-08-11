package com.dragonsofmugloar.api.exception;

import org.springframework.http.HttpStatus;

/**
 * A generic exception used by the application and printing the exception information in a custom way.
 */
public class CustomException extends Throwable {
    public CustomException(HttpStatus statusCode, String message) {
        super(String.format("Error %s - %s [CODE - BODY]", statusCode, message));
    }

    public CustomException(HttpStatus statusCode) {
        super(String.format("Http error - %s [HttpCode]", statusCode));
    }
}
