package com.apps.mycontactsapp;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.decorator.MaskedEmailDecorator;
import com.apps.mycontactsapp.decorator.UpperCaseDecorator;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.ContactDisplay;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main class for verifying implementation of Use Case 5 (View Contact Details
 * with Decorators).
 */
public class MyContactsAppMainUC5 {

    private static Scanner scanner = new Scanner(System.in);
    private static UserRepository userRepository = new UserRepositoryStub();
    private static UserService userService = new UserServiceImpl(userRepository);
    private static AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
    private static SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Main entry point for the application.
     * Initializes dependencies and starts the main menu loop.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- UC5: View Contact Details & Decorator Pattern ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Login & View Contacts");
            System.out.println("3. Exit");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    MyContactsAppMainUC2.registerUserUI(userService);
                    break;
                case 2:
                    Optional<User> loggedInUser = loginUser();
                    if (loggedInUser.isPresent()) {
                        contactViewMenu(loggedInUser.get());
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    /**
     * Handles user login flow.
     * Accessible publicly for reuse in other UCs.
     *
     * @return An Optional containing the User if login is successful, or empty
     *         otherwise.
     */
    private static Optional<User> loginUser() {
        System.out.println("\n--- Login ---");
        String email = readString("Email:");
        String password = readString("Password:");

        Optional<User> userOpt = authStrategy.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = sessionManager.createSession(user);
            System.out.println("SUCCESS: Login successful. Token: " + token);
            return Optional.of(user);
        } else {
            System.out.println("FAILED: Invalid email or password.");
            return Optional.empty();
        }
    }

    /**
     * Displays the contact viewing menu.
     * Allows creating contacts (via UC4) and viewing details with various
     * decorators.
     *
     * @param user The currently logged-in user.
     */
    private static void contactViewMenu(User user) {

        boolean viewing = true;
        while (viewing) {
            System.out.println("\n--- View Contacts Menu ---");
            System.out.println("1. Create Person Contact (via UC4)");
            System.out.println("2. Create Organization Contact (via UC4)");
            System.out.println("3. List All Contacts (Standard View)");
            System.out.println("4. View Contact Details (Standard)");
            System.out.println("5. View Contact Details (UpperCase Decorator)");
            System.out.println("6. View Contact Details (Masked Email Decorator)");
            System.out.println("7. View Contact Details (Combined Decorators)");
            System.out.println("8. Logout");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    MyContactsAppMainUC4.createPersonContact();
                    break;
                case 2:
                    MyContactsAppMainUC4.createOrganizationContact();
                    break;
                case 3:
                    // Using local because we want indices for selection elsewhere,
                    // but confusingly 'List All Contacts' could use either.
                    // Let's use UC4's strictly for this option as 'Report'.
                    MyContactsAppMainUC4.listContacts();
                    break;
                case 4:
                    viewContactDetails(null);
                    break;
                case 5:
                    viewContactDetails("UPPER");
                    break;
                case 6:
                    viewContactDetails("MASKED");
                    break;
                case 7:
                    viewContactDetails("COMBINED");
                    break;
                case 8:
                    viewing = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays details of a specific contact with valid decorators.
     *
     * @param decoratorType The type of decorator to apply ("UPPER", "MASKED",
     *                      "COMBINED", or null).
     */
    private static void viewContactDetails(String decoratorType) {
        // Accessing the shared service from UC4 where contacts are stored
        List<Contact> contacts = MyContactsAppMainUC4.contactService.getAllContacts();
        if (contacts.isEmpty()) {
            System.out.println("No contacts to view.");
            return;
        }

        MyContactsAppMainUC4.listContacts();
        int index = readInt("Select contact number to view details:") - 1;

        if (index >= 0 && index < contacts.size()) {
            Contact contact = contacts.get(index);
            ContactDisplay display = contact;

            if ("UPPER".equals(decoratorType)) {
                display = new UpperCaseDecorator(contact);
            } else if ("MASKED".equals(decoratorType)) {
                display = new MaskedEmailDecorator(contact);
            } else if ("COMBINED".equals(decoratorType)) {
                display = new UpperCaseDecorator(new MaskedEmailDecorator(contact));
            }

            System.out.println("\n--- Contact Details ---");
            System.out.println(display.getDetails());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // --- Helper Methods ---

    /**
     * Helper method to read a string from the console.
     *
     * @param prompt The prompt to display to the user.
     * @return The trimmed string input by the user.
     */
    public static String readString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }

    /**
     * Helper method to read an integer from the console.
     * Loops until a valid integer is entered.
     *
     * @param prompt The prompt to display to the user.
     * @return The integer input by the user.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
