package com.apps.mycontactsapp;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.observer.ContactAuditLogger;
import com.apps.mycontactsapp.repository.ContactRepository;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.ContactRepositoryStub;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.ContactService;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.ContactServiceImpl;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main application class for UC7: Delete Contact.
 * Demonstrates:
 * 1. Soft Deletion of contacts.
 * 2. Observer Pattern (Audit Logging).
 * 3. Access Control (Owner vs Admin).
 */
public class MyContactsAppMainUC7 {

    private static final UserRepository userRepository = new UserRepositoryStub();
    private static final UserService userService = new UserServiceImpl(userRepository);
    private static final ContactRepository contactRepository = new ContactRepositoryStub();
    private static final ContactService contactService = new ContactServiceImpl(contactRepository);
    private static final AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    // Static block or simple init method to wire dependencies
    static {
        if (userService instanceof UserServiceImpl) {
            ((UserServiceImpl) userService).setContactService(contactService);
        }
    }

    /**
     * Main entry point for the application.
     * Initializes the DI container and starts the main loop.
     *
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- UC7: Delete Contact (Soft Delete & Observers) ---");

        // Register Observer
        contactService.addObserver(new ContactAuditLogger());
        System.out.println("DEBUG: ContactAuditLogger registered.");

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
                    Optional<User> user = loginUser();
                    user.ifPresent(u -> contactMenu(u));
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Handles user login process.
     * Prompts for email and password and delegates authentication to AuthStrategy.
     *
     * @return an Optional containing the authenticated User, or empty if failed.
     */
    private static Optional<User> loginUser() {
        System.out.println("\n--- Login ---");
        String email = readString("Enter Email:");
        String password = readString("Enter Password:");

        Optional<User> userOpt = authStrategy.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = sessionManager.createSession(user);
            System.out.println("Login successful! Token: " + token);
            return Optional.of(user);
        } else {
            System.out.println("Login failed.");
            return Optional.empty();
        }
    }

    /**
     * Displays the contact management menu for a logged-in user.
     * Options vary based on user role (Admin vs User).
     *
     * @param user the currently logged-in user.
     */
    private static void contactMenu(User user) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Contact Menu (" + user.getName() + " - " + user.getUserType() + ") ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. List All Contacts");
            System.out.println("4. Delete Contact (Soft)");
            System.out.println("5. Hard Delete Contact (Permanent)");
            if (com.apps.mycontactsapp.model.UserType.ADMIN.equals(user.getUserType())) {
                System.out.println("6. Delete User (Cascade Delete)");
                System.out.println("7. Manage Users (Admin Only)");
            }
            System.out.println("8. Logout");

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
                    deleteContactUI(user, false);
                    break;
                case 5:
                    deleteContactUI(user, true);
                    break;
                case 6:
                    if (com.apps.mycontactsapp.model.UserType.ADMIN.equals(user.getUserType())) {
                        deleteUserUI(user);
                        if (!userRepository.existsByEmail(user.getEmail())) {
                            inMenu = false; // User deleted themselves
                        }
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case 7:
                    if (com.apps.mycontactsapp.model.UserType.ADMIN.equals(user.getUserType())) {
                        adminUserManagementUI(user);
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case 8:
                    inMenu = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays the Admin-specific User Management menu.
     * Allows listing and deleting users.
     *
     * @param admin the logged-in admin user.
     */
    private static void adminUserManagementUI(User admin) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Admin User Management ---");
            System.out.println("1. List All Users");
            System.out.println("2. Delete A User");
            System.out.println("3. Back to Main Menu");

            int choice = readInt("Enter choice:");
            switch (choice) {
                case 1:
                    listAllUsers(admin);
                    break;
                case 2:
                    deleteOtherUserUI(admin);
                    break;
                case 3:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Lists all users in the system.
     *
     * @param admin the logged-in admin user requesting the list.
     */
    private static void listAllUsers(User admin) {
        try {
            List<User> users = userService.getAllUsers(admin);
            System.out.println("\n--- All Users ---");
            System.out.printf("%-5s %-20s %-30s %-10s%n", "No.", "Name", "Email", "Type");
            int i = 1;
            for (User u : users) {
                System.out.printf("%-5d %-20s %-30s %-10s%n", i++, u.getName(), u.getEmail(), u.getUserType());
            }
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * UI flow for an Admin to delete another user.
     * prompts for email and confirms deletion.
     *
     * @param admin the logged-in admin user.
     */
    private static void deleteOtherUserUI(User admin) {
        listAllUsers(admin);
        String email = readString("Enter email of user to delete (or leave empty to cancel):");
        if (email.isEmpty())
            return;

        if (email.equalsIgnoreCase(admin.getEmail())) {
            System.out.println("You cannot delete yourself from here. Use 'Delete User' in main menu.");
            return;
        }

        String confirm = readString(
                "WARNING: This will delete user " + email + " and ALL their contacts. Are you sure? (Y/N)")
                .toUpperCase();
        if ("Y".equals(confirm)) {
            try {
                userService.deleteUser(admin, email);
                System.out.println("User " + email + " deleted successfully.");
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * UI flow to create a new Person contact.
     * Prompts for details and calls the service.
     *
     * @param user the user creating the contact.
     */
    private static void createPersonContact(User user) {
        System.out.println("\n--- Create Person Contact ---");
        String firstName = readString("First Name:");
        String lastName = readString("Last Name:");

        List<String> phones = readList("Enter phone numbers (comma separated, format Label:Number or just Number):");
        List<String> emails = readList("Enter emails (comma separated, format Label:Email or just Email):");

        try {
            contactService.createPerson(user, firstName, lastName, phones, emails);
            System.out.println("SUCCESS: Person contact created.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    /**
     * UI flow to create a new Organization contact.
     * Prompts for details and calls the service.
     *
     * @param user the user creating the contact.
     */
    private static void createOrganizationContact(User user) {
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
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    /**
     * Lists contacts for the current user.
     *
     * @param user the user requesting the list.
     */
    private static void listContacts(User user) {
        try {
            // ADMIN sees all (including soft deleted if we pass true, but for regular list
            // view maybe just active?)
            // If we want admin to manage trash, we should add that option.
            // For "List All Contacts" let's stick to active ones mostly, unless Admin wants
            // to see all?
            // Current req says: "If logged-in user is ADMIN... able to delete all the
            // users... If free/premium ... able to delete only his contacts"
            // It doesn't explicitly say Admin needs to see deleted contacts in the main
            // list, but previous task did.
            // Let's use the default behavior (active only for users), but for Admin let's
            // see.
            // Actually, let's keep it simple: getContacts(user) returns what's appropriate.
            List<Contact> contacts = contactService.getContacts(user);
            if (contacts.isEmpty()) {
                System.out.println("No contacts found.");
            } else {
                System.out.printf("%-5s %-20s %-10s %-10s%n", "No.", "Name", "ID", "Active");
                int i = 1;
                for (Contact c : contacts) {
                    System.out.printf("%-5d %-20s %-10s %-10s%n", i++, c.getDisplayName(),
                            c.getId().toString().substring(0, 8) + "...", c.isActive());
                }
            }
        } catch (Exception e) {
            System.out.println("Error listing contacts: " + e.getMessage());
        }
    }

    /**
     * UI flow for deleting a contact.
     * Supports both Soft Delete and Hard Delete based on the flag.
     *
     * @param user         the user performing the operation.
     * @param isHardDelete true for permanent delete, false for soft delete.
     */
    private static void deleteContactUI(User user, boolean isHardDelete) {
        try {
            // Retrieve contacts - For Admin deletion, we might want to see soft deleted
            // ones too?
            List<Contact> contacts;
            if (com.apps.mycontactsapp.model.UserType.ADMIN.equals(user.getUserType())) {
                contacts = contactService.getContacts(user, true); // Admin sees all including inactive
            } else {
                contacts = contactService.getContacts(user);
            }

            if (contacts.isEmpty()) {
                System.out.println("No contacts to delete.");
                return;
            }

            System.out.printf("%-5s %-20s %-10s %-10s%n", "No.", "Name", "ID", "Active");
            int i = 1;
            for (Contact c : contacts) {
                System.out.printf("%-5d %-20s %-10s %-10s%n", i++, c.getDisplayName(),
                        c.getId().toString().substring(0, 8) + "...", c.isActive());
            }

            System.out.println("Enter the number of the contact to delete (or 0 to cancel):");
            int index = readInt("Choice:");

            if (index == 0)
                return;
            if (index < 1 || index > contacts.size()) {
                System.out.println("Invalid number.");
                return;
            }

            Contact contact = contacts.get(index - 1);
            String action = isHardDelete ? "PERMANENTLY DELETE" : "delete";
            String confirm = readString(
                    "Are you sure you want to " + action + " '" + contact.getDisplayName() + "'? (Y/N)").toUpperCase();

            if ("Y".equals(confirm)) {
                if (isHardDelete) {
                    contactService.hardDeleteContact(user, contact.getId());
                    System.out.println("Contact PERMANENTLY deleted.");
                } else {
                    contactService.deleteContact(user, contact.getId());
                    System.out.println("Contact deleted successfully (Soft Delete).");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (ValidationException e) {
            System.out.println("Delete failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error during deletion: " + e.getMessage());
        }
    }

    /**
     * UI flow for a user to delete their own account.
     * Triggers a cascade delete.
     *
     * @param user the user deleting their account.
     */
    private static void deleteUserUI(User user) {
        System.out.println("\n--- Delete User (Cascade Delete) ---");
        // Simple logic: Delete SELF
        // In a real app, Admin might delete others by email.
        // Here we demonstrate simple cascade by deleting the currently logged-in user.

        String confirm = readString("WARNING: This will delete YOUR account and ALL your contacts. Are you sure? (Y/N)")
                .toUpperCase();
        if ("Y".equals(confirm)) {
            try {
                userService.deleteUser(user);
                System.out.println("User account and all contacts deleted successfully.");
            } catch (Exception e) {
                System.out.println("Error deleting user: " + e.getMessage());
            }
        } else {
            System.out.println("User deletion cancelled.");
        }
    }

    // --- Helper Methods ---

    public static String readString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }

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

    public static List<String> readList(String prompt) {
        System.out.println(prompt);
        String input = scanner.nextLine().trim();
        java.util.List<String> list = new java.util.ArrayList<>();
        if (!input.isEmpty()) {
            String[] items = input.split(",");
            for (String item : items) {
                list.add(item.trim());
            }
        }
        return list;
    }
}
