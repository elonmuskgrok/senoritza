package com.siva.exception;

public class TaxTrackerException extends RuntimeException {
    public TaxTrackerException(String message) {
        super(message);
    }
    public TaxTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}
