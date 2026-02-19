package com.example.customer.exception;

public class FieldValidationException extends RuntimeException {
    
    public FieldValidationException(String message) {
        super(message);
    }
    
    public FieldValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
