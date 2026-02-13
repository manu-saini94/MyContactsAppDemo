package com.apps.mycontactsapp.auth;

import java.util.Optional;

import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.util.PasswordHasher;

/**
 * Concrete implementation of {@link AuthenticationStrategy} for Basic
 * Authentication.
 *
 * This class authenticates users using their email (identifier) and password
 * (secret).
 *
 * Design Patterns:
 * - Strategy Pattern: Implements one of the strategies for
 * authentication (Basic Auth).
 */
public class BasicAuthenticationStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;

    /**
     * Constructs a new BasicAuthenticationStrategy with the required user
     * repository.
     *
     * @param userRepository the repository to lookup user details.
     */
    public BasicAuthenticationStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user by verifying their email and password.
     *
     * The method:
     * 1. Hashes the provided plain text password.
     * 2. Retrieves the stored user (if any) using a helper method (simulated
     * findByEmail).
     * 3. Compares the stored password hash with the computed hash.
     *
     * Note: Since UserRepository currently only has `existsByEmail` and `save`,
     * we are assuming for this implementation that `save` returns the user or we'd
     * need a `findByEmail`.
     * Given the limitations of the current Repository interface, I'll iterate
     * through a known list or assume meaningful stub behavior.
     * However, to be cleaner, I would ideally add `findByEmail` to
     * {@link UserRepository}.
     * Since I shouldn't modify UC1 classes heavily, I will simulate it via the stub
     * or just assume for now.
     * Wait, I can't implement this properly without `findByEmail`.
     * I will assume for now that I can add `findByEmail` to the interface as it's a
     * minor addition for UC2 necessity.
     * Or, I will check if I can use existing methods. `save` returns User. `exists`
     * returns boolean.
     * I MUST add `findByEmail` to `UserRepository` to make this work.
     *
     * @param email    the user's email address.
     * @param password the user's plain text password.
     * @return an {@link Optional} containing the user if credentials match, empty
     *         otherwise.
     */
    @Override
    public Optional<User> authenticate(String email, String password) {
        // Hashing the input password
        String hashedPassword = PasswordHasher.hash(password);

        // Fetching user - requires finding the user object.
        // Since I need to limit changes to UC1 classes, but finding a user is critical
        // for auth,
        // I will add findByEmail to the repository interface as it is a natural
        // extension.
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPasswordHash().equals(hashedPassword)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
