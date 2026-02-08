package com.apps.mycontactsapp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility class for hashing passwords.
 * <p>
 * This class provides methods to securely hash passwords using SHA-256
 * algorithm.
 */
public class PasswordHasher {

    /**
     * Hashes a plain text password using SHA-256.
     *
     * @param plainPassword the plain text password to hash.
     * @return the Base64 encoded string of the hashed password.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    public static String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
