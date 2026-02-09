package com.apps.mycontactsapp.exceptions;

/**
 * Exception thrown when contact validation fails.
 */
public class InvalidContactException extends ValidationException {
    public InvalidContactException(String message) {
        super(message);
    }
}
