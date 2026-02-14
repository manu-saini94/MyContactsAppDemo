package com.apps.mycontactsapp.repository.stub;

import java.util.HashMap;
import java.util.Map;

import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.repository.UserRepository;

/**
 * In-memory implementation of UserRepository (Stub).
 */
public class UserRepositoryStub implements UserRepository {
    private final Map<String, User> users = new HashMap<>();
    private long idCounter = 1;

    /**
     * Saves or updates a user.
     * 
     * @param user the user to save.
     * @return the saved user.
     */
    @Override
    public User save(User user) {
        if (user.getId() != null) {
            // Update existing user
            users.put(user.getEmail(), user);
            return user;
        }

        // Generate new ID
        long newId = idCounter++;

        User newUser;
        try {
            if (user instanceof com.apps.mycontactsapp.model.FreeUser) {
                newUser = new com.apps.mycontactsapp.model.FreeUser.Builder()
                        .id(newId)
                        .name(user.getName())
                        .email(user.getEmail())
                        .passwordHash(user.getPasswordHash())
                        .createdAt(user.getCreatedAt())
                        .build();
            } else if (user instanceof com.apps.mycontactsapp.model.PremiumUser) {
                newUser = new com.apps.mycontactsapp.model.PremiumUser.Builder()
                        .id(newId)
                        .name(user.getName())
                        .email(user.getEmail())
                        .passwordHash(user.getPasswordHash())
                        .createdAt(user.getCreatedAt())
                        .build();
            } else if (user instanceof com.apps.mycontactsapp.model.AdminUser) {
                newUser = new com.apps.mycontactsapp.model.AdminUser.Builder()
                        .id(newId)
                        .name(user.getName())
                        .email(user.getEmail())
                        .passwordHash(user.getPasswordHash())
                        .createdAt(user.getCreatedAt())
                        .build();
            } else {
                throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
            }
        } catch (com.apps.mycontactsapp.exceptions.ValidationException e) {
            // Should not happen for an already valid user being cloned
            throw new RuntimeException("Failed to clone user for saving", e);
        }

        System.out.println("STUB: Saving user " + newUser.getName() + " (ID: " + newId + ")");
        users.put(newUser.getEmail(), newUser);
        return newUser;
    }

    /**
     * Finds a user by email.
     * 
     * @param email the email address.
     * @return an Optional containing the user if found.
     */
    @Override
    public java.util.Optional<User> findByEmail(String email) {
        return java.util.Optional.ofNullable(users.get(email));
    }

    /**
     * Checks if a user exists by email.
     * 
     * @param email the email address.
     * @return true if exists, false otherwise.
     */
    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(email);
    }

    /**
     * Finds all users.
     * 
     * @return a list of all users.
     */
    @Override
    public java.util.List<User> findAll() {
        return new java.util.ArrayList<>(users.values());
    }

    /**
     * Deletes a user.
     * 
     * @param user the user to delete.
     */
    @Override
    public void delete(User user) {
        if (user != null) {
            users.remove(user.getEmail());
        }
    }

}
