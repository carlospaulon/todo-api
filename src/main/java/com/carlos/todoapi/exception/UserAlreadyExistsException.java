package com.carlos.todoapi.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
