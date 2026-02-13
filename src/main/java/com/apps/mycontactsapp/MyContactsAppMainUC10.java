package com.apps.mycontactsapp;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
// UUID import removed
import java.util.stream.Collectors;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.comparator.ContactComparators;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.filter.AccessCountFilter;
import com.apps.mycontactsapp.filter.AndFilter;
import com.apps.mycontactsapp.filter.ContactFilter;
import com.apps.mycontactsapp.filter.DateAddedFilter;
import com.apps.mycontactsapp.filter.OrFilter;
import com.apps.mycontactsapp.filter.TagFilter;
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
 * Main application class for UC-10: Advanced Filtering and Sorting.
 */
public class MyContactsAppMainUC10 {

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

    /**
     * Main entry point for the application.
     * Initializes dependencies and starts the main menu loop.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- UC10: Advanced Filtering & Sorting ---");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Register User");
            System.out.println("2. Login & Filter/Sort Contacts");
            System.out.println("3. Exit");

            int choice = readInt("Enter choice:");
            switch (choice) {
                case 1:
                    MyContactsAppMainUC2.registerUserUI(userService);
                    break;
                case 2:
                    Optional<User> user = loginUser();
                    user.ifPresent(u -> filterMenu(u));
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
     * Handles user login flow.
     * 
     * @return an Optional containing the authenticated User, or empty if failed.
     */
    private static Optional<User> loginUser() {
        System.out.println("\n--- Login ---");
        String email = readString("Enter Email:");
        String password = readString("Enter Password:");
        return authStrategy.authenticate(email, password).map(u -> {
            sessionManager.createSession(u);
            System.out.println("Login successful.");
            return u;
        });
    }

    /**
     * Displays the Advanced Filter Menu.
     * 
     * @param user the logged-in user.
     */
    private static void filterMenu(User user) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Advanced Filter Menu (" + user.getName() + ") ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. View a Contact (Increment Access Count)");
            System.out.println("4. Apply Filters & Sort");
            System.out.println("5. List All (No Filter)");
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
                    viewContactAndIncrement(user);
                    break;
                case 4:
                    applyFiltersAndSort(user);
                    break;
                case 5:
                    listContacts(contactService.getContacts(user));
                    break;
                case 0:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Dummy contacts method removed.

    /**
     * Views a contact's details and increments its access count.
     * 
     * @param user the logged-in user.
     */
    private static void viewContactAndIncrement(User user) {
        List<Contact> contacts = contactService.getContacts(user);
        if (contacts.isEmpty()) {
            System.out.println("No contacts.");
            return;
        }
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println((i + 1) + ". " + contacts.get(i).getDisplayName() + " (Access: "
                    + contacts.get(i).getAccessCount() + ")");
        }
        int idx = readInt("Select contact to view:") - 1;
        if (idx >= 0 && idx < contacts.size()) {
            try {
                // This call increments access count
                Contact c = contactService.getContact(user, contacts.get(idx).getId());
                System.out.println("\n--- Contact Details ---");
                System.out.println(c.getDetails());
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Applies selected filters and sorting to the contact list.
     * 
     * @param user the logged-in user.
     */
    private static void applyFiltersAndSort(User user) {
        List<Contact> allContacts = contactService.getContacts(user);

        ContactFilter activeFilter = c -> true; // Default match all

        System.out.println("\nSelect Filter 1:");
        ContactFilter f1 = selectFilter();
        if (f1 != null) {
            activeFilter = f1;
            System.out.println("Select logic for Filter 2 (1. AND, 2. OR, 3. None):");
            int logic = readInt("Choice:");
            if (logic == 1 || logic == 2) {
                System.out.println("Select Filter 2:");
                ContactFilter f2 = selectFilter();
                if (f2 != null) {
                    if (logic == 1) {
                        activeFilter = new AndFilter(f1, f2);
                    } else {
                        activeFilter = new OrFilter(f1, f2);
                    }
                }
            }
        }

        System.out.println("\nSelect Sort Order:");
        System.out.println("1. Name (A-Z)");
        System.out.println("2. Date Added (Newest First)");
        System.out.println("3. Usually Contacted (Frequency High->Low)");
        int sortChoice = readInt("Choice:");

        Comparator<Contact> comparator = ContactComparators.BY_NAME_ASC; // Default
        switch (sortChoice) {
            case 2:
                comparator = ContactComparators.BY_DATE_ADDED_NEWEST;
                break;
            case 3:
                comparator = ContactComparators.BY_ACCESS_FREQUENCY;
                break;
        }

        // Apply
        final ContactFilter finalFilter = activeFilter;
        List<Contact> results = allContacts.stream()
                .filter(finalFilter::test)
                .sorted(comparator)
                .collect(Collectors.toList());

        System.out.println("\n--- Filtered & Sorted Results ---");
        listContacts(results);
    }

    /**
     * Prompts the user to select a filter interactively.
     * 
     * @return the selected ContactFilter, or null if choice was 'No Filter'.
     */
    private static ContactFilter selectFilter() {
        System.out.println("1. Tag Filter");
        System.out.println("2. Frequently Contacted (Count >= X)");
        System.out.println("3. Recently Added (Last X Minutes - demo)");
        System.out.println("4. No Filter");
        int choice = readInt("Choice:");
        switch (choice) {
            case 1:
                String tag = readString("Enter tag:");
                return new TagFilter(tag);
            case 2:
                int count = readInt("Min access count:");
                return new AccessCountFilter(count);
            case 3:
                int mins = readInt("Minutes ago:");
                return new DateAddedFilter(LocalDateTime.now().minusMinutes(mins));
            default:
                return null;
        }
    }

    /**
     * Lists the given contacts to the console.
     * 
     * @param contacts the list of contacts to display.
     */
    private static void listContacts(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            System.out.println("No matching contacts.");
        } else {
            System.out.printf("%-5s %-20s %-10s %s%n", "No.", "Name", "Access", "Tags");
            int i = 1;
            for (Contact c : contacts) {
                System.out.printf("%-5d %-20s %-10d %s%n", i++, c.getDisplayName(), c.getAccessCount(), c.getTags());
            }
        }
    }

    /**
     * Helper to read a string from input.
     * 
     * @param prompt the prompt to display.
     * @return the trimmed input string.
     */
    private static String readString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }

    /**
     * Helper to read an integer from input.
     * 
     * @param prompt the prompt to display.
     * @return the integer value.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    /**
     * Helper to read a comma-separated list from input.
     * 
     * @param prompt the prompt to display.
     * @return a List of strings.
     */
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

    /**
     * UI flow to create a new Person contact.
     * 
     * @param user the user creating the contact.
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
            System.out.println("SUCCESS: Person contact created.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    /**
     * UI flow to create a new Organization contact.
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
}
