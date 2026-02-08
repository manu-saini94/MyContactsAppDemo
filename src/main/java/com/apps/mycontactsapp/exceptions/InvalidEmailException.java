package com.apps.mycontactsapp.exceptions;

/**
 * Exception thrown when an email address format is invalid.
 * <p>
 * This is a specific subtype of {@link ValidationException}.
 */
public class InvalidEmailException extends ValidationException {

    /**
     * Constructs a new InvalidEmailException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}
