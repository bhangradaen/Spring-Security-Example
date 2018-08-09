package com.exception;

public class MissingJwtException extends RuntimeException {

    public MissingJwtException(String message) {
        super(message);
    }

}
