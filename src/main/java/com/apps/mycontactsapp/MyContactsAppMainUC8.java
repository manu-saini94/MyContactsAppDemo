package com.apps.mycontactsapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import com.apps.mycontactsapp.auth.AuthenticationStrategy;
import com.apps.mycontactsapp.auth.BasicAuthenticationStrategy;
import com.apps.mycontactsapp.auth.SessionManager;
import com.apps.mycontactsapp.composite.ContactGroup;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.composite.ContactComponent;
import com.apps.mycontactsapp.model.User;
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

/**
 * Main class for verifying Bulk Operations (UC-08) using Composite Pattern.
 * Interactive version with User Registration, Login, and End-to-End flow.
 */
public class MyContactsAppMainUC8 {

    private static Scanner scanner = new Scanner(System.in);

    // Dependencies
    private static UserRepository userRepository = new UserRepositoryStub();
    private static UserService userService = new UserServiceImpl(userRepository);
    private static AuthenticationStrategy authStrategy = new BasicAuthenticationStrategy(userRepository);
    private static SessionManager sessionManager = SessionManager.getInstance();

    private static ContactRepository contactRepository = new ContactRepositoryStub();
    private static ContactService contactService = new ContactServiceImpl(contactRepository);

    private static ContactGroupRepository contactGroupRepository = new ContactGroupRepositoryStub();
    private static ContactGroupService contactGroupService = new ContactGroupServiceImpl(contactGroupRepository);

    /**
     * Main entry point for the application.
     * 
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("--- UC8: Bulk Operations (Composite Pattern) - End-to-End ---");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Register User");
            System.out.println("2. Login & Bulk Operations");
            System.out.println("3. Exit");

            int choice = readInt("Enter choice:");

            switch (choice) {
                case 1:
                    MyContactsAppMainUC2.registerUserUI(userService);
                    break;
                case 2:
                    Optional<User> loggedInUser = loginUser();
                    if (loggedInUser.isPresent()) {
                        bulkOperationsMenu(loggedInUser.get());
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }

    /**
     * Handles user login flow.
     * 
     * @return an Optional containing the logged-in User if successful, empty
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
     * Displays the bulk operations menu.
     * 
     * @param user the logged-in user.
     */
    private static void bulkOperationsMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Bulk Operations Menu ---");
            System.out.println("1. Create Person Contact");
            System.out.println("2. Create Organization Contact");
            System.out.println("3. List All Contacts");
            System.out.println("4. Create New Group");
            System.out.println("5. Manage Groups (View/Tag/Delete)");
            System.out.println("6. Logout");

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
                    createGroup(user);
                    break;
                case 5:
                    manageGroups(user);
                    break;
                case 6:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * UI flow to create a Person contact.
     * 
     * @param user the owner of the contact.
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
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * UI flow to create an Organization contact.
     * 
     * @param user the owner of the contact.
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
        }
    }

    /**
     * Lists all contacts for the user.
     * 
     * @param user the user whose contacts to list.
     */
    private static void listContacts(User user) {
        List<Contact> contacts = contactService.getContacts(user);
        System.out.println("\n--- All Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);
                System.out.println((i + 1) + ". " + c.getDisplayName() + " [" + c.getClass().getSimpleName()
                        + "] (Active: " + c.isActive() + ")");
                if (!c.getTags().isEmpty()) {
                    System.out.println("   Tags: " + c.getTags());
                }
            }
        }
    }

    /**
     * UI flow to create a new group.
     * 
     * @param user the user creating the group.
     */
    private static void createGroup(User user) {
        List<Contact> contacts = contactService.getContacts(user);
        if (contacts.isEmpty()) {
            System.out.println("No contacts available to group. Create some contacts first.");
            return;
        }

        String groupName = readString("Enter Group Name:");
        // Basic check purely for UI feedback, explicit validation happens in service

        // We can't easily check name duplication here without parsing getDetails string
        // but service handles logic.

        System.out.println("\n--- Select Contacts to Add ---");
        listContacts(user);
        System.out.println("Enter contact numbers to add (comma separated), or 'all':");

        String input = scanner.nextLine().trim();
        List<ContactComponent> selectedContacts = new ArrayList<>();

        if ("all".equalsIgnoreCase(input)) {
            selectedContacts.addAll(contacts);
            System.out.println("Selected all contacts.");
        } else {
            String[] parts = input.split(",");
            for (String part : parts) {
                try {
                    int index = Integer.parseInt(part.trim()) - 1;
                    if (index >= 0 && index < contacts.size()) {
                        selectedContacts.add(contacts.get(index));
                        System.out.println("Selected: " + contacts.get(index).getDisplayName());
                    } else {
                        System.out.println("Invalid index ignored: " + (index + 1));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input ignored: " + part);
                }
            }
        }

        if (selectedContacts.isEmpty()) {
            System.out.println("No contacts selected. Group not created.");
        } else {
            try {
                contactGroupService.createGroup(user, groupName, selectedContacts);
                System.out.println("Group '" + groupName + "' created successfully.");
            } catch (ValidationException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * UI flow to manage existing groups.
     * 
     * @param user the user managing the groups.
     */
    private static void manageGroups(User user) {
        List<ContactGroup> groups = contactGroupService.getGroupsForUser(user);

        if (groups.isEmpty()) {
            System.out.println("No groups found.");
            return;
        }

        System.out.println("\n--- Accessible Groups ---");
        for (int i = 0; i < groups.size(); i++) {
            ContactGroup group = groups.get(i);
            // We need a way to display name summarily.
            // Parsing getDetails or just showing index + ID.
            // Let's use getDetails first line or similar if public,
            // but effectively we can just print the whole details or a summary.
            // Since we don't have getName publicly exposed on interface ContactComponent,
            // we have to cast or use getDetails.
            // ContactGroup concrete class has no public getter for name unless we add it to
            // interface or class.
            // Let's rely on the fact we have a List<ContactGroup> where we know they are
            // ContactGroups.
            // But we didn't add getName() to ContactGroup in previous step, so we can't use
            // it easily.
            // Wait, we did modify ContactGroup in Step 87-92 to add id and userId, but we
            // kept `groupName` private without getter?
            // Let me check. Yes, I missed adding a public getter for groupName in the
            // previous `replace_file_content`.
            // I should have added it.
            // For now, I'll use a substring of getDetails() or just "Group " + i.
            // Actually, getDetails() starts with "Group: [Name]".

            System.out.println((i + 1) + ". " + group.getName() + " (ID: " + group.getId() + ")");
        }

        int index = readInt("Select group number to manage:") - 1;
        if (index < 0 || index >= groups.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        ContactGroup selectedGroup = groups.get(index);
        UUID groupId = selectedGroup.getId();

        System.out.println("\n--- Manage Group: " + selectedGroup.getName() + " ---");
        System.out.println("1. View Details");
        System.out.println("2. Apply Tag (all members)");
        System.out.println("3. Remove Tag (all members)");
        System.out.println("4. Add Contact to Group");
        System.out.println("5. Remove Contact from Group");
        System.out.println("6. Delete Group");
        System.out.println("7. Back");

        int choice = readInt("Enter choice:");
        try {
            switch (choice) {
                case 1:
                    System.out.println(contactGroupService.getGroupDetails(user, groupId));
                    break;
                case 2:
                    String tag = readString("Enter tag to apply:");
                    contactGroupService.addTagToGroup(user, groupId, tag);
                    System.out.println("Tag '" + tag + "' applied.");
                    break;
                case 3:
                    String removeTag = readString("Enter tag to remove:");
                    contactGroupService.removeTagFromGroup(user, groupId, removeTag);
                    System.out.println("Tag '" + removeTag + "' removed.");
                    break;
                case 4:
                    addContactToGroupUI(user, groupId);
                    break;
                case 5:
                    removeContactFromGroupUI(user, groupId);
                    break;
                case 6:
                    String confirm = readString(
                            "Are you sure? This will delete the group and soft-delete its members? (y/n):");
                    if ("y".equalsIgnoreCase(confirm)) {
                        contactGroupService.deleteGroup(user, groupId);
                        System.out.println("Group deleted.");
                    }
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (ValidationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * UI helper to add a contact to a group.
     * 
     * @param user    the user.
     * @param groupId the group ID.
     * @throws ValidationException if validation fails.
     */
    private static void addContactToGroupUI(User user, UUID groupId) throws ValidationException {
        System.out.println("\n--- Add Contact to Group ---");
        List<Contact> contacts = contactService.getContacts(user);
        if (contacts.isEmpty()) {
            System.out.println("No contacts available to add.");
            return;
        }
        listContacts(user);
        int index = readInt("Enter contact number to add:") - 1;
        if (index >= 0 && index < contacts.size()) {
            Contact contact = contacts.get(index);
            contactGroupService.addContactToGroup(user, groupId, contact);
            System.out.println("Contact '" + contact.getDisplayName() + "' added to group.");
        } else {
            System.out.println("Invalid contact selection.");
        }
    }

    /**
     * UI helper to remove a contact from a group.
     * 
     * @param user    the user.
     * @param groupId the group ID.
     * @throws ValidationException if validation fails.
     */
    private static void removeContactFromGroupUI(User user, UUID groupId) throws ValidationException {
        System.out.println("\n--- Remove Contact from Group ---");
        ContactGroup group = contactGroupService.findGroupById(groupId);
        // Note: findGroupById doesn't check ACL, but we are inside a context where user
        // ownership was likely verified
        // to get to this menu. However, for robust UI, we should probably use the
        // service method that gets details/components
        // but wait, generic ContactGroupService doesn't expose getComponents directly
        // with ACL.
        // But we can cast the result of findGroupById if we trust the flow, OR better:
        // we should really have a method for this.
        // But since we are in the 'main' which is the UI layer, and we know we own the
        // group (passed ACL in manageGroups list),
        // we can assume safety or use the Service to get components? Service doesn't
        // explicitly expose getComponents list.
        // We'll rely on findGroupById for the object.
        if (group == null) {
            System.out.println("Group not found.");
            return;
        }

        // We need to verify access again if we are strict, but let's assume valid.
        // Actually, we can use the list from the group object directly.
        List<ContactComponent> members = group.getComponents();
        if (members.isEmpty()) {
            System.out.println("Group is empty.");
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i).getDetails().split("\n")[0]);
        }

        int index = readInt("Enter member number to remove:") - 1;
        if (index >= 0 && index < members.size()) {
            ContactComponent contact = members.get(index);
            contactGroupService.removeContactFromGroup(user, groupId, contact);
            System.out.println("Member removed from group.");
        } else {
            System.out.println("Invalid selection.");
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
                return Integer.parseInt(scanner.nextLine().trim());
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
