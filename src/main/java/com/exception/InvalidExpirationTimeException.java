package com.exception;

public class InvalidExpirationTimeException extends RuntimeException {

    public InvalidExpirationTimeException(String message) {
        super(message);
    }

}
