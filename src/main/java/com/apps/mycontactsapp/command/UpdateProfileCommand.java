package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.model.User;

/**
 * Concrete command to update a user's profile information (e.g., name).
 */
public class UpdateProfileCommand implements ProfileCommand {

    private final User user;
    private final String newName;

    public UpdateProfileCommand(User user, String newName) {
        this.user = user;
        this.newName = newName;
    }

    /**
     * Executes the update profile command.
     */
    @Override
    public void execute() {
        if (newName != null && !newName.trim().isEmpty()) {
            user.setName(newName);
            System.out.println("SUCCESS: Profile name updated to '" + newName + "'.");
        } else {
            System.out.println("ERROR: Name cannot be empty.");
        }
    }
}
