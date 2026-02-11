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
import com.apps.mycontactsapp.model.UserType;
import com.apps.mycontactsapp.repository.ContactRepository;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.ContactRepositoryStub;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.ContactService;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.ContactServiceImpl;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;
import com.apps.mycontactsapp.specification.ContactSpecifications;
import com.apps.mycontactsapp.specification.Specification;

/**
 * Main application class for UC-09: Search Contacts.
 * Demonstrates:
 * 1. Specification Pattern for building complex queries.
 * 2. Search by Name, Phone, Email, Tag.
 * 3. Composite Search (AND/OR).
 * 4. Admin vs User search visibility.
 */
public class MyContactsAppMainUC9 {

    private static final UserRepository userRepository = new UserRepositoryStub();
    private static final UserService userService = new UserServiceImpl(userRepository);
    private static final ContactRepository contactRepository = new ContactRepositoryStub();
    private static final ContactService contactService = new ContactServiceImpl(contactRepository);
    private static final AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    static {
        if (userService instanceof UserServiceImpl) {
            ((UserServiceImpl) userService).setContactService(contactService);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- UC9: Search & Filter Contacts (Specification Pattern) ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Login & Search Contacts");
            System.out.println("3. Exit");

            int choice = readInt("Enter choice:");
            switch (choice) {
                case 1:
                    MyContactsAppMainUC7.readString("Press Enter to continue...");
                    MyContactsAppMainUC2.registerUserUI(userService);
                    break;
                case 2:
                    Optional<User> user = loginUser();
                    user.ifPresent(u -> searchMenu(u));
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

    private static void searchMenu(User user) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Search Menu (" + user.getName() + " - " + user.getUserType() + ") ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. List All My Contacts");
            System.out.println("4. Search by Name (contains)");
            System.out.println("5. Search by Phone (contains)");
            System.out.println("6. Search by Email (contains)");
            System.out.println("7. Search by Tag (exact match)");
            System.out.println("8. Advanced Search (Name AND Tag)");
            System.out.println("9. Advanced Search (Name OR Phone)");
            System.out.println("10. Advanced Search (Tag AND Email)");
            System.out.println("11. Advanced Search (Name OR Email)");
            if (UserType.ADMIN.equals(user.getUserType())) {
                System.out.println("12. Admin Global Search (Name)");
            }
            System.out.println("0. Logout");

            int choice = readInt("Enter choice:");
            switch (choice) {
                case 1:
                    createPersonContact(user);
                    break;
                case 2:
                    createOrganizationContact(user);
                    break;
                case 3:
                    listContacts(contactService.getContacts(user));
                    break;
                case 4:
                    String name = readString("Enter name part:");
                    performSearch(user, ContactSpecifications.nameContains(name));
                    break;
                case 5:
                    String phone = readString("Enter phone part:");
                    performSearch(user, ContactSpecifications.phoneContains(phone));
                    break;
                case 6:
                    String email = readString("Enter email part:");
                    performSearch(user, ContactSpecifications.emailContains(email));
                    break;
                case 7:
                    String tag = readString("Enter tag:");
                    performSearch(user, ContactSpecifications.tagContains(tag));
                    break;
                case 8:
                    String n = readString("Enter name part:");
                    String t = readString("Enter tag:");
                    Specification<Contact> specAnd = ContactSpecifications.nameContains(n)
                            .and(ContactSpecifications.tagContains(t));
                    performSearch(user, specAnd);
                    break;
                case 9:
                    String n2 = readString("Enter name part:");
                    String p2 = readString("Enter phone part:");
                    Specification<Contact> specOr = ContactSpecifications.nameContains(n2)
                            .or(ContactSpecifications.phoneContains(p2));
                    performSearch(user, specOr);
                    break;
                case 10:
                    String t3 = readString("Enter tag:");
                    String e3 = readString("Enter email part:");
                    Specification<Contact> specTagEmail = ContactSpecifications.tagContains(t3)
                            .and(ContactSpecifications.emailContains(e3));
                    performSearch(user, specTagEmail);
                    break;
                case 11:
                    String n4 = readString("Enter name part:");
                    String e4 = readString("Enter email part:");
                    Specification<Contact> specNameEmail = ContactSpecifications.nameContains(n4)
                            .or(ContactSpecifications.emailContains(e4));
                    performSearch(user, specNameEmail);
                    break;
                case 12:
                    if (UserType.ADMIN.equals(user.getUserType())) {
                        String adminName = readString("Enter name part (searches ALL users' contacts):");
                        performSearch(user, ContactSpecifications.nameContains(adminName));
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case 0:
                    inMenu = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void performSearch(User user, Specification<Contact> spec) {
        System.out.println("Searching...");
        List<Contact> results = contactService.searchContacts(user, spec);
        if (results.isEmpty()) {
            System.out.println("No contacts found matching criteria.");
        } else {
            System.out.println("Found " + results.size() + " contacts:");
            listContacts(results);
        }
    }

    private static void listContacts(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to list.");
            return;
        }
        System.out.printf("%-5s %-20s %-15s %-10s%n", "No.", "Name", "Type", "Active");
        int i = 1;
        for (Contact c : contacts) {
            String type = c.getClass().getSimpleName();
            System.out.printf("%-5d %-20s %-15s %-10s%n", i++, c.getDisplayName(), type, c.isActive());
            if (!c.getTags().isEmpty()) {
                System.out.println("      Tags: " + c.getTags());
            }
            if (!c.getPhoneNumbers().isEmpty()) {
                System.out.println("      Phone: " + c.getPhoneNumbers().get(0));
            }
            if (!c.getEmailAddresses().isEmpty()) {
                System.out.println("      Email: " + c.getEmailAddresses().get(0));
            }
        }
    }

    /**
     * UI flow to create a new Person contact.
     */
    private static void createPersonContact(User user) {
        System.out.println("\n--- Create Person Contact ---");
        String firstName = readString("First Name:");
        String lastName = readString("Last Name:");

        List<String> phones = readList("Enter phone numbers (comma separated, format Label:Number or just Number):");
        List<String> emails = readList("Enter emails (comma separated, format Label:Email or just Email):");
        List<String> tags = readList("Enter tags (comma separated):");

        try {
            Contact contact = contactService.createPerson(user, firstName, lastName, phones, emails);
            for (String tag : tags) {
                contact.addTag(tag);
            }
            // Tags are not saved automatically if they are just on the object, unless
            // repository is in-memory and keeps reference.
            // In RepositoryStub, it saves the object. Since we modified the object AFTER
            // createPerson (which saves it),
            // we should ideally add tags before saving or save again.
            // But createPerson saves immediately.
            // Since it's a stub storing in-memory map, modifying the returned reference
            // modifies the stored object.
            // So this works for stub.
            // In real DB, we would need contactService.addTag(contactId, tag).

            System.out.println("SUCCESS: Person contact created.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    /**
     * UI flow to create a new Organization contact.
     */
    private static void createOrganizationContact(User user) {
        System.out.println("\n--- Create Organization Contact ---");
        String name = readString("Organization Name:");
        String website = readString("Website:");
        String department = readString("Department:");

        List<String> phones = readList("Enter phone numbers (comma separated):");
        List<String> emails = readList("Enter emails (comma separated):");
        List<String> tags = readList("Enter tags (comma separated):");

        try {
            Contact contact = contactService.createOrganization(user, name, website, department, phones, emails);
            for (String tag : tags) {
                contact.addTag(tag);
            }
            System.out.println("SUCCESS: Organization contact created.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
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

    private static List<String> readList(String prompt) {
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
