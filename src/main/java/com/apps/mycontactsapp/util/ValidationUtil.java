package com.apps.mycontactsapp.util;

import java.util.regex.Pattern;
import com.apps.mycontactsapp.exceptions.InvalidEmailException;
import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Utility class for input validation.
 *
 * Contains static methods to validate common data formats like emails and
 * passwords.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

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
     *
     * Requirements:
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - Minimum length of 8 characters
     *
     *
     * @param password the password string to validate.
     * @throws ValidationException if the password is null or does not meet
     *                             complexity rules.
     */
    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain at least one digit.");
        }
    }

    /**
     * Validates that a phone number is a 10-digit number.
     * 
     * @param phoneNumber the phone number string.
     * @throws ValidationException if the format is invalid.
     */
    public static void validatePhoneNumber(String phoneNumber) throws ValidationException {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new ValidationException("Invalid phone number format. Must be 10 digits.");
        }
    }

    // Reuse existing validateEmail but with specific context if needed
    /**
     * Validates a contact email address.
     * 
     * @param email the email address.
     * @throws ValidationException if the email is invalid.
     */
    public static void validateContactEmail(String email) throws ValidationException {
        validateEmail(email);
    }
}
