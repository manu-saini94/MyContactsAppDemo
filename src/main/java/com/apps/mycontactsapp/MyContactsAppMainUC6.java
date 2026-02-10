package com.apps.mycontactsapp;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import com.apps.mycontactsapp.command.CommandInvoker;
import com.apps.mycontactsapp.command.UpdateContactCommand;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.Person;
import com.apps.mycontactsapp.model.Organization;
import com.apps.mycontactsapp.model.PhoneNumber;
import com.apps.mycontactsapp.model.EmailAddress;

/**
 * Main class for verifying implementation of Use Case 6 (Edit Contact).
 */
public class MyContactsAppMainUC6 {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CommandInvoker commandInvoker = new CommandInvoker();

    /**
     * Main entry point for the application.
     * Initializes dependencies and starts the main menu loop.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- UC6: Edit Contact & Undo/Redo ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Login & Manage Contacts");
            System.out.println("3. Exit");

            int choice = MyContactsAppMainUC4.readInt("Enter choice:");

            switch (choice) {
                case 1:
                    MyContactsAppMainUC2.registerUserUI(MyContactsAppMainUC4.userService);
                    break;
                case 2:
                    var userOpt = MyContactsAppMainUC4.loginUser();
                    if (userOpt.isPresent()) {
                        contactMenu(userOpt.get());
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting UC6...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }

    /**
     * Displays the contact management menu for UC6.
     * Adds options for Editing and Undo/Redo on top of UC4 functionality.
     *
     * @param user The currently logged-in user.
     */
    private static void contactMenu(com.apps.mycontactsapp.model.User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Contact Management (UC6 Extended) ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. List All Contacts");
            System.out.println("4. Edit Contact");
            System.out.println("5. Undo Last Edit");
            System.out.println("6. Redo Last Edit");
            System.out.println("7. Logout");

            int choice = MyContactsAppMainUC4.readInt("Enter choice:");

            try {
                switch (choice) {
                    case 1:
                        MyContactsAppMainUC4.createPersonContact(user);
                        break;
                    case 2:
                        MyContactsAppMainUC4.createOrganizationContact(user);
                        break;
                    case 3:
                        MyContactsAppMainUC4.listContacts(user);
                        break;
                    case 4:
                        editContactLoop(user);
                        break;
                    case 5:
                        commandInvoker.undo();
                        System.out.println("Undo completed.");
                        break;
                    case 6:
                        commandInvoker.redo();
                        System.out.println("Redo completed.");
                        break;
                    case 7:
                        loggedIn = false;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the loop for editing a specific contact.
     * Selects a contact and presents edit options based on its type.
     *
     * @param user The filtered user to edit contacts for.
     * @throws ValidationException If validation fails during edit selection.
     */
    private static void editContactLoop(com.apps.mycontactsapp.model.User user) throws ValidationException {
        MyContactsAppMainUC4.listContacts(user);
        List<Contact> contacts = MyContactsAppMainUC4.contactService.getContacts(user);
        if (contacts.isEmpty())
            return;

        int index = MyContactsAppMainUC4.readInt("Select contact number to edit:") - 1;
        if (index < 0 || index >= contacts.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Contact contact = contacts.get(index);
        System.out.println("Selected: " + contact.getDisplayName());

        boolean editing = true;
        while (editing) {
            System.out.println("\nEdit Options for: " + contact.getDisplayName());

            if (contact instanceof Person) {
                System.out.println("1. Edit First Name");
                System.out.println("2. Edit Last Name");
                System.out.println("3. Edit Phone Numbers");
                System.out.println("4. Edit Email Addresses");
                System.out.println("5. Back");

                int choice = MyContactsAppMainUC4.readInt("Enter choice:");
                if (choice == 5)
                    editing = false;
                else
                    handlePersonEdit((Person) contact, choice);
            } else if (contact instanceof Organization) {
                System.out.println("1. Edit Organization Name");
                System.out.println("2. Edit Website");
                System.out.println("3. Edit Department");
                System.out.println("4. Edit Phone Numbers");
                System.out.println("5. Edit Email Addresses");
                System.out.println("6. Back");

                int choice = MyContactsAppMainUC4.readInt("Enter choice:");
                if (choice == 6)
                    editing = false;
                else
                    handleOrganizationEdit((Organization) contact, choice);
            }
        }
    }

    /**
     * Handles editing options specific to Person contacts.
     *
     * @param person The Person contact to edit.
     * @param choice The user's menu choice.
     */
    private static void handlePersonEdit(Person person, int choice) {
        switch (choice) {
            case 1:
                String newFirst = MyContactsAppMainUC4.readString("New First Name:");
                executeSafe(new UpdateContactCommand(person, () -> {
                    try {
                        person.setFirstName(newFirst);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                }));
                break;
            case 2:
                String newLast = MyContactsAppMainUC4.readString("New Last Name:");
                executeSafe(new UpdateContactCommand(person, () -> {
                    try {
                        person.setLastName(newLast);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                }));
                break;
            case 3:
                editPhones(person);
                break;
            case 4:
                editEmails(person);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Handles editing options specific to Organization contacts.
     *
     * @param org    The Organization contact to edit.
     * @param choice The user's menu choice.
     */
    private static void handleOrganizationEdit(Organization org, int choice) {
        switch (choice) {
            case 1:
                String newName = MyContactsAppMainUC4.readString("New Organization Name:");
                executeSafe(new UpdateContactCommand(org, () -> {
                    try {
                        org.setName(newName);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                }));
                break;
            case 2:
                String newWeb = MyContactsAppMainUC4.readString("New Website:");
                executeSafe(new UpdateContactCommand(org, () -> {
                    try {
                        org.setWebsite(newWeb);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                }));
                break;
            case 3:
                String newDept = MyContactsAppMainUC4.readString("New Department:");
                executeSafe(new UpdateContactCommand(org, () -> {
                    try {
                        org.setDepartment(newDept);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                }));
                break;
            case 4:
                editPhones(org);
                break;
            case 5:
                editEmails(org);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Prompts for and updates phone numbers for a contact.
     * Replaces the entire list of phone numbers.
     *
     * @param contact The contact to update.
     */
    private static void editPhones(Contact contact) {
        List<String> newPhones = MyContactsAppMainUC4
                .readList("Enter new phone numbers (comma separated, format Label:Number):");
        System.out.println("Note: This will replace the entire list.");
        executeSafe(new UpdateContactCommand(contact, () -> {
            try {
                List<PhoneNumber> pList = new ArrayList<>();
                for (String s : newPhones) {
                    String[] parts = s.split(":", 2);
                    String label = parts.length > 1 ? parts[0].trim() : "Mobile";
                    String number = parts.length > 1 ? parts[1].trim() : parts[0].trim();
                    pList.add(new PhoneNumber(label, number));
                }
                contact.setPhoneNumbers(pList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * Prompts for and updates email addresses for a contact.
     * Replaces the entire list of email addresses.
     *
     * @param contact The contact to update.
     */
    private static void editEmails(Contact contact) {
        List<String> newEmails = MyContactsAppMainUC4
                .readList("Enter new email addresses (comma separated, format Label:Email):");
        System.out.println("Note: This will replace the entire list.");
        executeSafe(new UpdateContactCommand(contact, () -> {
            try {
                List<EmailAddress> eList = new ArrayList<>();
                for (String s : newEmails) {
                    String[] parts = s.split(":", 2);
                    String label = parts.length > 1 ? parts[0].trim() : "Personal";
                    String email = parts.length > 1 ? parts[1].trim() : parts[0].trim();
                    eList.add(new EmailAddress(label, email));
                }
                contact.setEmailAddresses(eList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * Safely executes a command, handling potential exceptions.
     * Wraps execution in a try-catch block to handle ValidationException and
     * RuntimeException.
     *
     * @param cmd The command to execute.
     */
    private static void executeSafe(UpdateContactCommand cmd) {
        try {
            commandInvoker.executeCommand(cmd);
            System.out.println("Update successful.");
        } catch (ValidationException e) {
            System.out.println("Update failed: " + e.getMessage());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ValidationException) {
                System.out.println("Update failed: " + e.getCause().getMessage());
            } else {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
