package com.apps.mycontactsapp.service;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Service interface for User Management operations.
 *
 * Specifically handles User Registration (UC-01) logic, abstracting the
 * business rules
 * from the controller or client layers.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * This method orchestrates the user creation process, including validation,
     * user type determination, and persistence.
     *
     * @param name     the full name of the user.
     * @param email    the email address of the user (must be unique).
     * @param password the plain text password for the user (will be hashed).
     * @param userType the type of user to create (e.g., "FREE", "PREMIUM").
     * @throws ValidationException if any input is invalid (e.g., duplicate email,
     *                             weak password, invalid user type).
     */
    void registerUser(String name, String email, String password, String userType) throws ValidationException;
}
