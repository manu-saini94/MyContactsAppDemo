package com.apps.mycontactsapp;

import java.util.Optional;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main class for verifying implementation of Use Case 2 (User Authentication).
 * <p>
 * This class simulates the login flow, including user registration (setup),
 * credential verification via {@link BasicAuthenticationStrategy}, and session
 * management
 * via {@link SessionManager}.
 */
public class MyContactsAppMainUC2 {

    public static void main(String[] args) {
        System.out.println("--- Starting UC2 User Authentication Verification ---");

        // 1. Setup - Users need to exist first
        UserRepository userRepository = new UserRepositoryStub();
        UserService userService = new UserServiceImpl(userRepository);

        try {
            System.out.println("\n[Setup] Registering user 'Alice'...");
            userService.registerUser("Alice", "alice@example.com", "StrongPass123", "FREE");
            System.out.println("[Setup] User 'Alice' registered successfully.");
        } catch (ValidationException e) {
            System.err.println("[Setup] Failed: " + e.getMessage());
            return;
        }

        // 2. Initialize Auth Strategy and Session Manager
        AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
        SessionManager sessionManager = SessionManager.getInstance();

        // 3. Test Successful Login
        System.out.println("\nTest 1: Login with valid credentials...");
        Optional<User> authenticatedUser = authStrategy.authenticate("alice@example.com", "StrongPass123");

        if (authenticatedUser.isPresent()) {
            System.out.println("SUCCESS: Authentication successful for " + authenticatedUser.get().getName());
            String token = sessionManager.createSession(authenticatedUser.get());
            System.out.println("SUCCESS: Session created. Token: " + token);

            // Verify session retrieval
            User sessionUser = sessionManager.getUserFromSession(token);
            if (sessionUser != null && sessionUser.getEmail().equals("alice@example.com")) {
                System.out.println("SUCCESS: User retrieved from valid session.");
            } else {
                System.err.println("FAILED: Could not retrieve user from session.");
            }

        } else {
            System.err.println("FAILED: Authentication failed for valid credentials.");
        }

        // 4. Test Invalid Password
        System.out.println("\nTest 2: Login with incorrect password...");
        Optional<User> failedAuth = authStrategy.authenticate("alice@example.com", "WrongPass123");
        if (!failedAuth.isPresent()) {
            System.out.println("SUCCESS: Authentication failed as expected.");
        } else {
            System.err.println("FAILED: Authentication succeeded with wrong password!");
        }

        // 5. Test Non-existent User
        System.out.println("\nTest 3: Login with non-existent user...");
        Optional<User> noUserAuth = authStrategy.authenticate("nobody@example.com", "AnyPass");
        if (!noUserAuth.isPresent()) {
            System.out.println("SUCCESS: Authentication failed as expected.");
        } else {
            System.err.println("FAILED: Authentication succeeded for non-existent user!");
        }

        // 6. Test OAuth (Template)
        System.out.println("\nTest 4: OAuth Login (Template)...");
        // We just verify it compiles and returns empty/throws as implemented
        com.apps.mycontactsapp.auth.OAuthAuthenticationStrategy oauth = new com.apps.mycontactsapp.auth.OAuthAuthenticationStrategy();
        Optional<User> oauthResult = oauth.authenticate("Google", "token123");
        if (!oauthResult.isPresent()) {
            System.out.println("SUCCESS: OAuth template returned empty Optional as expected.");
        }

        System.out.println("\n--- Verification Complete ---");
    }
}
