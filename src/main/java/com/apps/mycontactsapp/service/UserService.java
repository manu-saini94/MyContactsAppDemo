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

    /**
     * Retrieves all users in the system.
     * This operation is restricted to Administrators only.
     *
     * @param requester the user requesting the list (must be ADMIN).
     * @return a list of all registered users.
     * @throws ValidationException if the requester is not an Admin.
     */
    java.util.List<com.apps.mycontactsapp.model.User> getAllUsers(com.apps.mycontactsapp.model.User requester)
            throws ValidationException;

    /**
     * Deletes a user by their email address.
     * This operation is restricted to Administrators only.
     * Triggers a cascade delete of all contacts owned by the target user.
     *
     * @param requester   the user requesting the deletion (must be ADMIN).
     * @param targetEmail the email of the user to delete.
     * @throws ValidationException if the requester is not an Admin or user not
     *                             found.
     */
    void deleteUser(com.apps.mycontactsapp.model.User requester, String targetEmail) throws ValidationException;

    /**
     * Deletes the specified user account.
     * This is the underlying method that performs the deletion and cascading.
     * Users can call this to delete their own account.
     *
     * @param user the user to delete.
     */
    void deleteUser(com.apps.mycontactsapp.model.User user);
}
