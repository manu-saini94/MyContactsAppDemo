package com.apps.mycontactsapp;

import java.util.Scanner;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.repository.UserRepository;
import com.apps.mycontactsapp.repository.stub.UserRepositoryStub;
import com.apps.mycontactsapp.service.UserService;
import com.apps.mycontactsapp.service.impl.UserServiceImpl;

/**
 * Main class for verifying implementation of Use Case 1 (User Registration).
 *
 * This class provides a menu-driven interface to test the user registration
 * functionality interactively, using a Scanner directly within the class.
 */
public class MyContactsAppMainUC1 {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize dependencies
        UserRepository userRepository = new UserRepositoryStub();
        UserService userService = new UserServiceImpl(userRepository);

        System.out.println("--- UC1: User Registration Menu ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Exit");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    registerUserUI(userService);
                    break;
                case 2:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    public static void registerUserUI(UserService userService) {
        System.out.println("\n--- Register User ---");
        String name = readString("Name:");
        String email = readString("Email:");
        String password = readString("Password:");
        String userType = readString("User Type (FREE/PREMIUM):");

        try {
            userService.registerUser(name, email, password, userType);
            System.out.println("SUCCESS: User registered successfully.");
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // --- Input Helper Methods ---

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
}
