package com.apps.mycontactsapp.exceptions;

/**
 * Exception thrown when user creation fails due to system or logic errors.
 * <p>
 * This exception is typically unused in the current validation flow but
 * available for
 * broader creation issues.
 */
public class UserCreationException extends Exception {

    /**
     * Constructs a new UserCreationException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UserCreationException(String message) {
        super(message);
    }
}
