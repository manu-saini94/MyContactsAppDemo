package com.apps.mycontactsapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.command.ChangePasswordCommand;
import com.apps.mycontactsapp.command.CommandInvoker;
import com.apps.mycontactsapp.command.ProfileCommand;
import com.apps.mycontactsapp.command.UpdateContactCommand;
import com.apps.mycontactsapp.command.UpdatePreferencesCommand;
import com.apps.mycontactsapp.command.UpdateProfileCommand;
import com.apps.mycontactsapp.comparator.ContactComparators;
import com.apps.mycontactsapp.composite.ContactComponent;
import com.apps.mycontactsapp.composite.ContactGroup;
import com.apps.mycontactsapp.decorator.MaskedEmailDecorator;
import com.apps.mycontactsapp.decorator.UpperCaseDecorator;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.filter.AccessCountFilter;
import com.apps.mycontactsapp.filter.AndFilter;
import com.apps.mycontactsapp.filter.ContactFilter;
import com.apps.mycontactsapp.filter.DateAddedFilter;
import com.apps.mycontactsapp.filter.OrFilter;
import com.apps.mycontactsapp.filter.TagFilter;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.ContactSortOrder;
import com.apps.mycontactsapp.model.ContactViewType;
import com.apps.mycontactsapp.model.Organization;
import com.apps.mycontactsapp.model.Person;
import com.apps.mycontactsapp.model.ProfilePreferences;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.model.UserType;
import com.apps.mycontactsapp.observer.ContactAuditLogger;
import com.apps.mycontactsapp.repository.ContactGroupRepository;
import com.apps.mycontactsapp.repository.ContactRepository;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.ContactGroupRepositoryStub;
import com.apps.mycontactsapp.repository.stub.ContactRepositoryStub;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.ContactGroupService;
import com.apps.mycontactsapp.service.ContactService;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.ContactGroupServiceImpl;
import com.apps.mycontactsapp.service.impl.ContactServiceImpl;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;
import com.apps.mycontactsapp.specification.ContactSpecifications;
import com.apps.mycontactsapp.specification.Specification;

/**
 * Main application class for MyContactsApp.
 * This class serves as the single entry point for the application, providing
 * access to all features including user registration, authentication,
 * contact management, grouping, and administrative functions.
 * 
 * <p>
 * It simulates a console-based UI for the end user.
 * </p>
 */
public class MyContactsAppMain {

    // --- Dependencies ---
    private static final Scanner scanner = new Scanner(System.in);

    private static final UserRepository userRepository = new UserRepositoryStub();
    private static final UserService userService = new UserServiceImpl(userRepository);

    private static final ContactRepository contactRepository = new ContactRepositoryStub();
    private static final ContactService contactService = new ContactServiceImpl(contactRepository);

    private static final ContactGroupRepository contactGroupRepository = new ContactGroupRepositoryStub();
    private static final ContactGroupService contactGroupService = new ContactGroupServiceImpl(contactGroupRepository);

    private static final AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
    private static final SessionManager sessionManager = SessionManager.getInstance();

    private static final CommandInvoker commandInvoker = new CommandInvoker();

    // --- Initialization Block ---
    static {
        // Wire circular dependency
        if (userService instanceof UserServiceImpl) {
            ((UserServiceImpl) userService).setContactService(contactService);
        }
        // Register Observers for auditing
        contactService.addObserver(new ContactAuditLogger());
    }

    /**
     * Main entry point for the application.
     * Initializes the main menu loop.
     * 
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("        Welcome to MyContactsApp          ");
        System.out.println("==========================================");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Register User");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            // Strict input range check (1-3)
            int choice = readInt("Enter choice:", 1, 3);

            switch (choice) {
                case 1:
                    registerUserUI();
                    break;
                case 2:
                    Optional<User> userOpt = loginUserUI();
                    userOpt.ifPresent(MyContactsAppMain::userSessionLoop);
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
            }
        }
        scanner.close();
    }

    // =========================================================================
    // Authentication
    // =========================================================================

    /**
     * Handles the user registration UI flow.
     */
    private static void registerUserUI() {
        System.out.println("\n--- Register User ---");
        String name = readString("Name:");
        String email = readString("Email:");
        String password = readString("Password:");
        String userType = readString("User Type (FREE/PREMIUM/ADMIN):");

        try {
            userService.registerUser(name, email, password, userType);
            System.out.println("SUCCESS: User registered successfully.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Handles the user login UI flow.
     * 
     * @return an Optional containing the authenticated User, or empty if login
     *         fails.
     */
    private static Optional<User> loginUserUI() {
        System.out.println("\n--- Login ---");
        String email = readString("Email:");
        String password = readString("Password:");

        Optional<User> userOpt = authStrategy.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            sessionManager.createSession(user);
            System.out.println("SUCCESS: Login successful.");
            System.out.println("Welcome, " + user.getName());
            return Optional.of(user);
        } else {
            System.out.println("FAILED: Invalid email or password.");
            return Optional.empty();
        }
    }

    // =========================================================================
    // User Session Loop
    // =========================================================================

    /**
     * Main dashboard loop for a logged-in user.
     * 
     * @param user the authenticated user.
     */
    private static void userSessionLoop(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- User Dashboard (" + user.getName() + ") ---");
            System.out.println("1. My Profile");
            System.out.println("2. Contacts Management");
            System.out.println("3. Groups Management");

            int maxOption = 3;
            if (UserType.ADMIN.equals(user.getUserType())) {
                System.out.println("4. Admin Operations");
                maxOption = 4;
            }
            System.out.println("0. Logout");

            // Range check 0 to maxOption
            int choice = readInt("Enter choice:", 0, maxOption);

            switch (choice) {
                case 1:
                    profileMenu(user);
                    break;
                case 2:
                    contactsMenu(user);
                    break;
                case 3:
                    groupsMenu(user);
                    break;
                case 4:
                    adminMenu(user);
                    break;
                case 0:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    // =========================================================================
    // Profile Management
    // =========================================================================

    /**
     * UI menu for profile management.
     * 
     * @param user the logged-in user.
     */
    private static void profileMenu(User user) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Profile Management ---");
            System.out.println("1. View Profile");
            System.out.println("2. Update Name");
            System.out.println("3. Change Password");
            System.out.println("4. Update Preferences");
            System.out.println("5. Back");

            int choice = readInt("Enter choice:", 1, 5);

            switch (choice) {
                case 1:
                    System.out.println("Name: " + user.getName());
                    System.out.println("Email: " + user.getEmail());
                    System.out.println("Type: " + user.getUserType());
                    System.out.println("Preferences: " + user.getPreferences());
                    break;
                case 2:
                    String newName = readString("Enter new name:");
                    executeProfileCommand(new UpdateProfileCommand(user, newName));
                    break;
                case 3:
                    String newPassword = readString("Enter new password:");
                    executeProfileCommand(new ChangePasswordCommand(user, newPassword));
                    break;
                case 4:
                    updatePreferencesUI(user);
                    break;
                case 5:
                    inMenu = false;
                    break;
            }
        }
    }

    /**
     * UI flow for updating user preferences.
     * 
     * @param user the user updating preferences.
     */
    private static void updatePreferencesUI(User user) {
        System.out.println("Sort Order: 1. ASC, 2. DESC, 3. RECENT");
        int sortChoice = readInt("Choose:", 1, 3);
        ContactSortOrder sortOrder = ContactSortOrder.NAME_ASC;
        if (sortChoice == 2)
            sortOrder = ContactSortOrder.NAME_DESC;
        else if (sortChoice == 3)
            sortOrder = ContactSortOrder.RECENTLY_ADDED;

        System.out.println("View Type: 1. LIST, 2. GRID");
        int viewChoice = readInt("Choose:", 1, 2);
        ContactViewType viewType = (viewChoice == 2) ? ContactViewType.GRID : ContactViewType.LIST;

        String notifInput = readString("Enable Notifications? (y/n):");
        boolean notifications = "y".equalsIgnoreCase(notifInput);

        int perPage = readInt("Contacts per page:");
        String photosInput = readString("Show Photos? (y/n):");
        boolean showPhotos = "y".equalsIgnoreCase(photosInput);
        String language = readString("Preferred Language:");

        ProfilePreferences newPrefs = new ProfilePreferences(sortOrder, viewType, notifications, perPage, showPhotos,
                language);
        executeProfileCommand(new UpdatePreferencesCommand(user, newPrefs));
    }

    /**
     * Helper to execute a profile command.
     * 
     * @param command the command to execute.
     */
    private static void executeProfileCommand(ProfileCommand command) {
        command.execute();
        System.out.println("Profile updated successfully.");
    }

    // =========================================================================
    // Contacts Management
    // =========================================================================

    /**
     * UI menu for contact management.
     * 
     * @param user the logged-in user.
     */
    private static void contactsMenu(User user) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Contacts Management ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. List All Contacts");
            System.out.println("4. Search Contacts");
            System.out.println("5. Advanced Filter & Sort");
            System.out.println("6. View Contact Details");
            System.out.println("7. Edit Contact");
            System.out.println("8. Delete Contact");
            System.out.println("9. Back");

            int choice = readInt("Enter choice:", 1, 9);

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
                    searchContactsUI(user);
                    break;
                case 5:
                    advancedFilterSortUI(user);
                    break;
                case 6:
                    viewContactDetailsWithDecoratorsUI(user);
                    break;
                case 7:
                    editContactUI(user);
                    break;
                case 8:
                    deleteContactUI(user);
                    break;
                case 9:
                    inMenu = false;
                    break;
            }
        }
    }

    /**
     * UI flow to create a Person contact.
     * 
     * @param user the user.
     */
    private static void createPersonContact(User user) {
        System.out.println("\n--- Create Person Contact ---");
        String firstName = readString("First Name:");
        String lastName = readString("Last Name:");

        List<String> phones = readList("Phone numbers (comma separated, Label:Number):");
        List<String> emails = readList("Emails (comma separated, Label:Email):");
        List<String> tags = readList("Tags (comma separated):");

        try {
            Contact contact = contactService.createPerson(user, firstName, lastName, phones, emails);
            for (String tag : tags)
                contact.addTag(tag);
            System.out.println("SUCCESS: Person contact created.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * UI flow to create an Organization contact.
     * 
     * @param user the user.
     */
    private static void createOrganizationContact(User user) {
        System.out.println("\n--- Create Organization Contact ---");
        String name = readString("Organization Name:");
        String website = readString("Website:");
        String dept = readString("Department:");

        List<String> phones = readList("Phone numbers (comma separated):");
        List<String> emails = readList("Emails (comma separated):");
        List<String> tags = readList("Tags (comma separated):");

        try {
            Contact contact = contactService.createOrganization(user, name, website, dept, phones, emails);
            for (String tag : tags)
                contact.addTag(tag);
            System.out.println("SUCCESS: Organization contact created.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Helper to list contacts in a formatted way.
     * 
     * @param contacts list of contacts to display.
     */
    private static void listContacts(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }
        System.out.printf("%-5s %-25s %-15s %-10s%n", "No.", "Name", "Type", "Active");
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            System.out.printf("%-5d %-25s %-15s %-10s%n", (i + 1), c.getDisplayName(), c.getClass().getSimpleName(),
                    c.isActive());
        }
    }

    /**
     * UI flow for searching contacts using Specification pattern.
     * 
     * @param user the user.
     */
    private static void searchContactsUI(User user) {
        System.out.println("\n--- Search Contacts ---");
        System.out.println("1. Name (contains)");
        System.out.println("2. Phone (contains)");
        System.out.println("3. Email (contains)");
        System.out.println("4. Tag (exact)");
        System.out.println("5. Advanced (Name AND Tag)");
        System.out.println("6. Advanced (Name OR Phone)");
        System.out.println("7. Back");

        int choice = readInt("Enter choice:", 1, 7);
        Specification<Contact> spec = null;

        switch (choice) {
            case 1:
                spec = ContactSpecifications.nameContains(readString("Name part:"));
                break;
            case 2:
                spec = ContactSpecifications.phoneContains(readString("Phone part:"));
                break;
            case 3:
                spec = ContactSpecifications.emailContains(readString("Email part:"));
                break;
            case 4:
                spec = ContactSpecifications.tagContains(readString("Tag:"));
                break;
            case 5:
                spec = ContactSpecifications.nameContains(readString("Name part:"))
                        .and(ContactSpecifications.tagContains(readString("Tag:")));
                break;
            case 6:
                spec = ContactSpecifications.nameContains(readString("Name part:"))
                        .or(ContactSpecifications.phoneContains(readString("Phone part:")));
                break;
            case 7:
                return;
        }

        if (spec != null) {
            List<Contact> results = contactService.searchContacts(user, spec);
            System.out.println("Found " + results.size() + " results:");
            listContacts(results);
        }
    }

    /**
     * UI flow for advanced filtering and sorting.
     * 
     * @param user the user.
     */
    private static void advancedFilterSortUI(User user) {
        System.out.println("\n--- Advanced Filter & Sort ---");
        ContactFilter filter = selectContactFilter();

        System.out.println("Sort Order: 1. Name, 2. Date Added, 3. Frequency");
        int sort = readInt("Choice:", 1, 3);
        Comparator<Contact> comp = ContactComparators.BY_NAME_ASC;
        if (sort == 2)
            comp = ContactComparators.BY_DATE_ADDED_NEWEST;
        if (sort == 3)
            comp = ContactComparators.BY_ACCESS_FREQUENCY;

        List<Contact> results = contactService.getContacts(user).stream()
                .filter(filter != null ? filter::test : c -> true)
                .sorted(comp)
                .collect(Collectors.toList());

        System.out.println("Results:");
        listContacts(results);
    }

    /**
     * Helper to select a filter interactively.
     * 
     * @return the selected ContactFilter or null.
     */
    private static ContactFilter selectContactFilter() {
        System.out.println("Filter: 1. Tag, 2. Access Count >= X, 3. None");
        int c = readInt("Choice:", 1, 3);
        if (c == 1)
            return new TagFilter(readString("Tag:"));
        if (c == 2)
            return new AccessCountFilter(readInt("Min Access Count:"));
        return null; // Case 3
    }

    /**
     * UI flow to view contact details with optional decorators.
     * 
     * @param user the user.
     */
    private static void viewContactDetailsWithDecoratorsUI(User user) {
        List<Contact> contacts = contactService.getContacts(user);
        listContacts(contacts);
        int idx = readInt("Select contact:", 1, contacts.size()) - 1;

        Contact contact = contacts.get(idx);

        // Increment access count implicitly by retrieving it via service
        try {
            contactService.getContact(user, contact.getId());
        } catch (ValidationException e) {
        }

        System.out.println("View Mode: 1. Normal, 2. UpperCase, 3. Masked Email, 4. Both");
        int d = readInt("Choice:", 1, 4);
        ContactComponent display = contact;
        if (d == 2)
            display = new UpperCaseDecorator(display);
        if (d == 3)
            display = new MaskedEmailDecorator(display);
        if (d == 4)
            display = new UpperCaseDecorator(new MaskedEmailDecorator(display));

        System.out.println("\n--- Contact Details ---");
        System.out.println(display.getDetails());
    }

    /**
     * UI flow to edit a contact.
     * 
     * @param user the user.
     */
    private static void editContactUI(User user) {
        System.out.println("\n--- Edit Contact (Undo/Redo Supported) ---");
        System.out.println("1. Edit a Contact");
        System.out.println("2. Undo Last Edit");
        System.out.println("3. Redo Last Edit");

        int choice = readInt("Choice:", 1, 3);
        if (choice == 2) {
            commandInvoker.undo();
            System.out.println("Undone previous action.");
            return;
        }
        if (choice == 3) {
            try {
                commandInvoker.redo();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            System.out.println("Redone previous action.");
            return;
        }

        List<Contact> contacts = contactService.getContacts(user);
        listContacts(contacts);
        int idx = readInt("Select contact:", 1, contacts.size()) - 1;

        Contact contact = contacts.get(idx);

        System.out.println("Editing " + contact.getDisplayName());
        if (contact instanceof Person) {
            String newFirst = readString("New First Name (enter to skip):");
            if (!newFirst.isEmpty()) {
                executeSafeCommand(new UpdateContactCommand(contact, () -> {
                    try {
                        ((Person) contact).setFirstName(newFirst);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        } else if (contact instanceof Organization) {
            String newName = readString("New Org Name (enter to skip):");
            if (!newName.isEmpty()) {
                executeSafeCommand(new UpdateContactCommand(contact, () -> {
                    try {
                        ((Organization) contact).setName(newName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        }
    }

    /**
     * Executes a command and handles potential validation errors.
     * 
     * @param cmd the command to execute.
     */
    private static void executeSafeCommand(UpdateContactCommand cmd) {
        try {
            commandInvoker.executeCommand(cmd);
            System.out.println("Update successful.");
        } catch (ValidationException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    /**
     * UI flow to delete a contact.
     * 
     * @param user the user.
     */
    private static void deleteContactUI(User user) {
        List<Contact> contacts = contactService.getContacts(user);
        listContacts(contacts);
        int idx = readInt("Select contact to delete:", 1, contacts.size()) - 1;

        Contact contact = contacts.get(idx);
        String type = readString("Type: 1. Soft Delete (Trash), 2. Hard Delete (Permanent)");

        try {
            if ("2".equals(type)) {
                contactService.hardDeleteContact(user, contact.getId());
                System.out.println("Contact permanently deleted.");
            } else {
                contactService.deleteContact(user, contact.getId());
                System.out.println("Contact moved to trash (Soft Delete).");
            }
        } catch (ValidationException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // Groups Management
    // =========================================================================

    /**
     * UI menu for groups management.
     * 
     * @param user the user.
     */
    private static void groupsMenu(User user) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Groups Management ---");
            System.out.println("1. Create Group");
            System.out.println("2. Manage Groups (List/Add/Remove/Tag)");
            System.out.println("3. Back");

            int choice = readInt("Choice:", 1, 3);
            switch (choice) {
                case 1:
                    createGroupUI(user);
                    break;
                case 2:
                    manageGroupsUI(user);
                    break;
                case 3:
                    inMenu = false;
                    break;
            }
        }
    }

    /**
     * UI flow to create a new group.
     * 
     * @param user the user.
     */
    private static void createGroupUI(User user) {
        String name = readString("Group Name:");
        List<Contact> contacts = contactService.getContacts(user);
        listContacts(contacts);

        List<ContactComponent> selected = new ArrayList<>();
        String selection = readString("Enter contact numbers (comma separated) or 'all':");
        if ("all".equalsIgnoreCase(selection)) {
            selected.addAll(contacts);
        } else {
            for (String s : selection.split(",")) {
                try {
                    int i = Integer.parseInt(s.trim()) - 1;
                    if (i >= 0 && i < contacts.size())
                        selected.add(contacts.get(i));
                } catch (Exception e) {
                }
            }
        }

        try {
            contactGroupService.createGroup(user, name, selected);
            System.out.println("Group created successfully.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * UI flow to manage existing groups.
     * 
     * @param user the user.
     */
    private static void manageGroupsUI(User user) {
        List<ContactGroup> groups = contactGroupService.getGroupsForUser(user);
        if (groups.isEmpty()) {
            System.out.println("No groups found.");
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getName());
        }

        int idx = readInt("Select group:", 1, groups.size()) - 1;
        ContactGroup group = groups.get(idx);

        System.out.println("1. View Details, 2. Add Contact, 3. Tag Group");
        int c = readInt("Choice:", 1, 3);
        try {
            if (c == 1)
                System.out.println(contactGroupService.getGroupDetails(user, group.getId()));
            if (c == 2) {
                List<Contact> contacts = contactService.getContacts(user);
                listContacts(contacts);
                int ci = readInt("Select contact:", 1, contacts.size()) - 1;
                contactGroupService.addContactToGroup(user, group.getId(), contacts.get(ci));
                System.out.println("Added to group.");
            }
            if (c == 3) {
                String tag = readString("Tag:");
                contactGroupService.addTagToGroup(user, group.getId(), tag);
                System.out.println("Group tagged.");
            }
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // Admin Operations
    // =========================================================================

    /**
     * UI menu for admin operations.
     * 
     * @param admin the admin user.
     */
    private static void adminMenu(User admin) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Admin Operations ---");
            System.out.println("1. List All Users");
            System.out.println("2. Global Contact Search");
            System.out.println("3. Delete User");
            System.out.println("4. Back");

            int choice = readInt("Choice:", 1, 4);
            switch (choice) {
                case 1:
                    try {
                        List<User> users = userService.getAllUsers(admin);
                        for (User u : users)
                            System.out.println(u.getName() + " (" + u.getEmail() + ") - " + u.getUserType());
                    } catch (ValidationException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 2:
                    String query = readString("Name query (global):");
                    Specification<Contact> spec = ContactSpecifications.nameContains(query);
                    List<Contact> results = contactService.searchContacts(admin, spec);
                    System.out.println("Found " + results.size() + " matches across all users.");
                    listContacts(results);
                    break;
                case 3:
                    String email = readString("User Email to delete:");
                    try {
                        userService.deleteUser(admin, email);
                        System.out.println("User deleted.");
                    } catch (ValidationException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    inMenu = false;
                    break;
            }
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Reads a line of text from the console.
     * 
     * @param prompt the text to display before input.
     * @return the trimmed input string.
     */
    private static String readString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }

    /**
     * Reads an integer from the console, looping until valid input is received.
     * 
     * @param prompt the text to display before input.
     * @return the parsed integer.
     */
    private static int readInt(String prompt) {
        return readInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Reads an integer within a specific range from the console.
     * Continues passing until a valid number within range is entered.
     * 
     * @param prompt the text to display before input.
     * @param min    the minimum acceptable value.
     * @param max    the maximum acceptable value.
     * @return the parsed integer ensuring min <= val <= max.
     */
    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            try {
                String line = scanner.nextLine().trim();
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                // Loop continues
            }
        }
    }

    /**
     * Reads a comma-separated list of strings from the console.
     * 
     * @param prompt the text to display before input.
     * @return a List of trimmed strings.
     */
    private static List<String> readList(String prompt) {
        System.out.println(prompt);
        String input = scanner.nextLine().trim();
        List<String> list = new ArrayList<>();
        if (!input.isEmpty()) {
            for (String s : input.split(","))
                list.add(s.trim());
        }
        return list;
    }
}
