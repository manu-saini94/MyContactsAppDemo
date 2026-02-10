package com.apps.mycontactsapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.repository.ContactRepository;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.ContactRepositoryStub;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.ContactService;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.ContactServiceImpl;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main class for verifying implementation of Use Case 4 (Create Contact).
 */
public class MyContactsAppMainUC4 {

    private static Scanner scanner = new Scanner(System.in);
    private static UserRepository userRepository = new UserRepositoryStub();
    public static UserService userService = new UserServiceImpl(userRepository);
    private static AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
    private static SessionManager sessionManager = SessionManager.getInstance();

    private static ContactRepository contactRepository = new ContactRepositoryStub();
    public static ContactService contactService = new ContactServiceImpl(contactRepository);

    /**
     * Main entry point for the application.
     * Initializes dependencies and starts the main menu loop.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- UC4: Create Contact ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Login & Manage Contacts");
            System.out.println("3. Exit");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    MyContactsAppMainUC2.registerUserUI(userService);
                    break;
                case 2:
                    Optional<User> loggedInUser = loginUser();
                    if (loggedInUser.isPresent()) {
                        contactMenu(loggedInUser.get());
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
    public static Optional<User> loginUser() {
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
     * Displays the contact management menu.
     * Allows creating different types of contacts and listing them.
     *
     * @param user The currently logged-in user (context).
     */
    private static void contactMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Contact Management Menu ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. List All Contacts");
            System.out.println("4. Logout");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    createPersonContact(user);
                    break;
                case 2:
                    createOrganizationContact(user);
                    break;
                case 3:
                    listContacts(user);
                    break;
                case 4:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * UI flow for creating a Person contact.
     * Prompts for first name, last name, phones, and emails.
     */
    public static void createPersonContact(User user) {
        System.out.println("\n--- Create Person Contact ---");
        String firstName = readString("First Name:");
        String lastName = readString("Last Name:");

        List<String> phones = readList("Enter phone numbers (comma separated, format Label:Number or just Number):");
        List<String> emails = readList("Enter emails (comma separated, format Label:Email or just Email):");

        try {
            contactService.createPerson(user, firstName, lastName, phones, emails);
            System.out.println("SUCCESS: Person contact created.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * UI flow for creating an Organization contact.
     * Prompts for organization name, website, department, phones, and emails.
     */
    public static void createOrganizationContact(User user) {
        System.out.println("\n--- Create Organization Contact ---");
        String name = readString("Organization Name:");
        String website = readString("Website:");
        String department = readString("Department:");

        List<String> phones = readList("Enter phone numbers (comma separated):");
        List<String> emails = readList("Enter emails (comma separated):");

        try {
            contactService.createOrganization(user, name, website, department, phones, emails);
            System.out.println("SUCCESS: Organization contact created.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Lists all existing contacts to the console.
     */
    public static void listContacts(User user) {
        List<Contact> contacts = contactService.getContacts(user);
        System.out.println("\n--- All Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            for (Contact c : contacts) {
                System.out.println(c.getDisplayName() + " [" + c.getClass().getSimpleName() + "]");
                // Print details if needed logic
            }
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

    /**
     * Helper method to read a comma-separated list of strings from the console.
     *
     * @param prompt The prompt to display to the user.
     * @return A list of strings parsed from the input.
     */
    public static List<String> readList(String prompt) {
        System.out.println(prompt);
        String input = scanner.nextLine().trim();
        List<String> list = new ArrayList<>();
        if (!input.isEmpty()) {
            String[] items = input.split(",");
            for (String item : items) {
                list.add(item.trim());
            }
        }
        return list;
    }
}
