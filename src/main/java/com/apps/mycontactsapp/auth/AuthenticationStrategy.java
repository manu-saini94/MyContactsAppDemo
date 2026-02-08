package com.apps.mycontactsapp.auth;

import java.util.Optional;
import com.apps.mycontactsapp.model.User;

/**
 * Strategy interface for user authentication.
 * <p>
 * This interface defines the contract for different authentication mechanisms
 * (e.g., Basic Auth, OAuth).
 * <p>
 * <b>Design Patterns:</b>
 * <ul>
 * <li><b>Strategy Pattern:</b> This interface acts as the Strategy in the
 * Strategy Pattern,
 * allowing the authentication algorithm to be selected at runtime.</li>
 * </ul>
 */
public interface AuthenticationStrategy {

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param identifier the unique identifier for the user (e.g., email, username).
     * @param secret     the secret credential (e.g., password, token).
     * @return an {@link Optional} containing the authenticated {@link User} if
     *         successful,
     *         or an empty {@link Optional} if authentication fails.
     */
    Optional<User> authenticate(String identifier, String secret);
}
