package com.apps.mycontactsapp.repository;

import com.apps.mycontactsapp.model.User;

/**
 * Repository interface for managing {@link User} entities.
 *
 * This interface defines the contract for data access operations related to
 * users.
 * Implementations should handle the persistence logic.
 */
public interface UserRepository {

    /**
     * Saves a user to the data store.
     *
     * If the user is new, it should be created. If it exists, it should be updated.
     *
     * @param user the {@link User} entity to save. Must not be null.
     * @return the saved {@link User} entity, possibly with generated fields (e.g.,
     *         ID) populated.
     */
    User save(User user);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for.
     * @return an {@link java.util.Optional} containing the user if found, or empty
     *         otherwise.
     */
    java.util.Optional<com.apps.mycontactsapp.model.User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     *
     * This method is useful for validation to prevent duplicate registrations.
     *
     * @param email the email address to check.
     * @return {@code true} if a user with the given email exists, {@code false}
     *         otherwise.
     */
    boolean existsByEmail(String email);
}
