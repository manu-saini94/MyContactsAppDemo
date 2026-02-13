package com.apps.mycontactsapp.auth;

import java.util.Optional;
import com.apps.mycontactsapp.model.User;

/**
 * Template implementation of {@link AuthenticationStrategy} for OAuth.
 *
 * This class serves as a placeholder for future OAuth implementation (e.g.,
 * Google, Facebook login).
 * currently, it does not perform any actual authentication.
 *
 * Design Patterns:
 * - Strategy Pattern: Implements a separate strategy for OAuth,
 * allowing the system
 * to switch between Basic Auth and OAuth without changing the core
 * authentication logic.
 */
public class OAuthAuthenticationStrategy implements AuthenticationStrategy {

    /**
     * Authenticates a user using OAuth credentials.
     *
     * Note: This is currently a template. Real implementation would involve:
     * 1. Validating the OAuth token with the provider.
     * 2. Retrieving user details from the provider.
     * 3. Matching or creating a local user account.
     *
     * @param provider the OAuth provider name (passed as identifier).
     * @param token    the OAuth access token (passed as secret).
     * @return {@link Optional#empty()} as implementation is pending.
     */
    @Override
    public Optional<User> authenticate(String provider, String token) {
        // Template: Logic for OAuth would go here.
        // For now, we return empty to indicate no successful auth in this stub.
        return Optional.empty();
    }
}
