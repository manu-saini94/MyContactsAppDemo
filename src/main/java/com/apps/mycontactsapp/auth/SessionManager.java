package com.apps.mycontactsapp.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.apps.mycontactsapp.model.User;

/**
 * Singleton class for managing active user sessions.
 * <p>
 * This manager handles the creation, retrieval, and invalidation of session
 * tokens.
 * <p>
 * <b>Design Patterns:</b>
 * <ul>
 * <li><b>Singleton Pattern:</b> Ensures that only one instance of the
 * SessionManager exists
 * throughout the application lifecycle, providing a global point of access for
 * session state.</li>
 * </ul>
 */
public class SessionManager {

    private static SessionManager instance;
    private final Map<String, User> activeSessions;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private SessionManager() {
        activeSessions = new HashMap<>();
    }

    /**
     * Returns the single instance of the SessionManager.
     * <p>
     * Creates the instance if it doesn't exist (Lazy Initialization).
     *
     * @return the singleton {@link SessionManager} instance.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Creates a new session for the authenticated user.
     *
     * @param user the authenticated {@link User}.
     * @return a unique session token (UUID string).
     */
    public String createSession(User user) {
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, user);
        return token;
    }

    /**
     * Retrieves the user associated with a given session token.
     *
     * @param token the session token.
     * @return the {@link User} associated with the token, or {@code null} if the
     *         session is invalid.
     */
    public User getUserFromSession(String token) {
        return activeSessions.get(token);
    }

    /**
     * Invalidates (removes) a session.
     *
     * @param token the session token to invalidate.
     */
    public void invalidateSession(String token) {
        activeSessions.remove(token);
    }
}
