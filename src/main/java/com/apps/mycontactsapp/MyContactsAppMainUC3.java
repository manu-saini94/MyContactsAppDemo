package com.apps.mycontactsapp;

import java.util.Optional;
import java.util.Scanner;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.command.ChangePasswordCommand;
import com.apps.mycontactsapp.command.ProfileCommand;
import com.apps.mycontactsapp.command.UpdatePreferencesCommand;
import com.apps.mycontactsapp.command.UpdateProfileCommand;
import com.apps.mycontactsapp.model.ContactSortOrder;
import com.apps.mycontactsapp.model.ContactViewType;
import com.apps.mycontactsapp.model.ProfilePreferences;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main class for verifying implementation of Use Case 3 (User Profile
 * Management).
 *
 * This class provides a menu-driven interface to test profile updates, password
 * changes, and preference management using the Command Pattern.
 */
public class MyContactsAppMainUC3 {

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
        System.out.println("--- UC3: User Profile Management ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Login & Manage Profile");
            System.out.println("3. Exit");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    MyContactsAppMainUC2.registerUserUI(userService);
                    break;
                case 2:
                    Optional<User> loggedInUser = loginUser();
                    if (loggedInUser.isPresent()) {
                        profileMenu(loggedInUser.get());
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
     * Prompts for email and password, attempts authentication, and returns the user
     * if successful.
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
     * Displays the profile management menu and handles user choices.
     * Allows viewing profile, updating name, changing password, and updating
     * preferences.
     *
     * @param user The currently logged-in user.
     */
    private static void profileMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Profile Management Menu ---");
            System.out.println("1. View Profile");
            System.out.println("2. Update Name");
            System.out.println("3. Change Password");
            System.out.println("4. Update Preferences");
            System.out.println("5. Logout");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    viewProfile(user);
                    break;
                case 2:
                    String newName = readString("Enter new name:");
                    executeCommand(new UpdateProfileCommand(user, newName));
                    break;
                case 3:
                    String newPassword = readString("Enter new password:");
                    executeCommand(new ChangePasswordCommand(user, newPassword));
                    break;
                case 4:
                    updatePreferencesUI(user);
                    break;
                case 5:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays the user's profile information.
     *
     * @param user The user whose profile is to be viewed.
     */
    private static void viewProfile(User user) {
        System.out.println("\n--- User Profile ---");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Type: " + user.getUserType());
        System.out.println("Preferences: " + user.getPreferences());
    }

    /**
     * UI method to update user profile preferences.
     * Prompts for various preference settings and updates the user's profile.
     *
     * @param user The user whose preferences are to be updated.
     */
    private static void updatePreferencesUI(User user) {
        System.out.println("\n--- Update Preferences ---");

        System.out.println("Sort Order: 1. ASC, 2. DESC, 3. RECENT");
        int sortChoice = readInt("Choose:");
        ContactSortOrder sortOrder = ContactSortOrder.NAME_ASC;
        if (sortChoice == 2)
            sortOrder = ContactSortOrder.NAME_DESC;
        else if (sortChoice == 3)
            sortOrder = ContactSortOrder.RECENTLY_ADDED;

        System.out.println("View Type: 1. LIST, 2. GRID");
        int viewChoice = readInt("Choose:");
        ContactViewType viewType = (viewChoice == 2) ? ContactViewType.GRID : ContactViewType.LIST;

        String notifInput = readString("Enable Notifications? (y/n):");
        boolean notifications = notifInput.equalsIgnoreCase("y");

        int perPage = readInt("Contacts per page:");

        String photosInput = readString("Show Photos? (y/n):");
        boolean showPhotos = photosInput.equalsIgnoreCase("y");

        String language = readString("Preferred Language:");

        ProfilePreferences newPrefs = new ProfilePreferences(sortOrder, viewType, notifications, perPage, showPhotos,
                language);
        executeCommand(new UpdatePreferencesCommand(user, newPrefs));
    }

    /**
     * Executes a ProfileCommand.
     * This method acts as a simple invoker for commands.
     *
     * @param command The command to execute.
     */
    private static void executeCommand(ProfileCommand command) {
        command.execute();
    }

    // --- Public Helper Methods (as requested) ---

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
