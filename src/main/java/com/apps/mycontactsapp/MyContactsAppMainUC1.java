package com.apps.mycontactsapp;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main class for verifying implementation of Use Case 1 (User Registration).
 * <p>
 * This class simulates the application entry point and runs a series of tests
 * against the
 * {@link com.apps.mycontactsapp.service.UserService} implementation using a
 * stubbed repository.
 */
public class MyContactsAppMainUC1 {

    public static void main(String[] args) {
        // 1. Setup
        UserRepository userRepository = new UserRepositoryStub();
        UserService userService = new UserServiceImpl(userRepository);

        System.out.println("--- Starting UC1 User Registration Verification ---");

        // 2. Test Success Case
        try {
            System.out.println("\nTest 1: Registering new user 'Alice'...");
            userService.registerUser("Alice", "alice@example.com", "StrongPass123", "FREE");
            System.out.println("SUCCESS: User 'Alice' registered.");
        } catch (ValidationException e) {
            System.err.println("FAILED: " + e.getMessage());
        }

        // 3. Test Duplicate Email
        try {
            System.out.println("\nTest 2: Registering duplicate user 'alice@example.com'...");
            userService.registerUser("Alice Duplicate", "alice@example.com", "password456", "PREMIUM");
            System.err.println("FAILED: Should have thrown ValidationException for duplicate email.");
        } catch (ValidationException e) {
            System.out.println("SUCCESS: Caught expected exception: " + e.getMessage());
        }

        // 4. Test Invalid User Type
        try {
            System.out.println("\nTest 3: Registering with invalid user type 'INVALID_TYPE'...");
            userService.registerUser("Bob", "bob@example.com", "password789", "INVALID_TYPE");
            System.err.println("FAILED: Should have thrown ValidationException for invalid user type.");
        } catch (ValidationException e) {
            System.out.println("SUCCESS: Caught expected exception: " + e.getMessage());
        }

        // 5. Test Invalid Password (empty)
        try {
            System.out.println("\nTest 4: Registering with empty password...");
            userService.registerUser("Charlie", "charlie@example.com", "", "FREE");
            System.err.println("FAILED: Should have thrown ValidationException for empty password.");
        } catch (ValidationException e) {
            System.out.println("SUCCESS: Caught expected exception: " + e.getMessage());
        }

        System.out.println("\n--- Verification Complete ---");
    }

}
