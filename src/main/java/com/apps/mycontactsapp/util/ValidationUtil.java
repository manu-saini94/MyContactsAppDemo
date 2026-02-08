package com.apps.mycontactsapp.util;

import java.util.regex.Pattern;
import com.apps.mycontactsapp.exceptions.InvalidEmailException;
import com.apps.mycontactsapp.exceptions.InvalidPasswordException;

/**
 * Utility class for input validation.
 * <p>
 * Contains static methods to validate common data formats like emails and
 * passwords.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");

    /**
     * Validates that an email string matches the standard email format.
     *
     * @param email the email address to validate.
     * @throws InvalidEmailException if the email is null or does not match the
     *                               expected format.
     */
    public static void validateEmail(String email) throws InvalidEmailException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }

    /**
     * Validates that a password meets the security complexity requirements.
     * <p>
     * Requirements:
     * <ul>
     * <li>At least one uppercase letter</li>
     * <li>At least one lowercase letter</li>
     * <li>At least one digit</li>
     * <li>Minimum length of 8 characters</li>
     * </ul>
     *
     * @param password the password string to validate.
     * @throws InvalidPasswordException if the password is null or does not meet
     *                                  complexity rules.
     */
    public static void validatePassword(String password) throws InvalidPasswordException {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidPasswordException(
                    "Password must contain upper, lower case letters and a digit (min 8 chars)");
        }
    }
}
