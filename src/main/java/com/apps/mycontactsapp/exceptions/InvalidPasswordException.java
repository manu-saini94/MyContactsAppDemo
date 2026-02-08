package com.apps.mycontactsapp.exceptions;

/**
 * Exception thrown when a password does not meet complexity requirements.
 * <p>
 * This is a specific subtype of {@link ValidationException}.
 */
public class InvalidPasswordException extends ValidationException {

    /**
     * Constructs a new InvalidPasswordException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}
