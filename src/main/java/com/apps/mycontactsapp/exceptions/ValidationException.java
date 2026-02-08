package com.apps.mycontactsapp.exceptions;

/**
 * Base exception for all validation-related errors.
 * <p>
 * This checked exception is thrown when input data fails to meet the required
 * format or constraints.
 */
public class ValidationException extends Exception {

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ValidationException(String message) {
        super(message);
    }
}
