package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.util.PasswordHasher;
import com.apps.mycontactsapp.util.ValidationUtil;

/**
 * Concrete command to change a user's password.
 * Handles validation and hashing of the new password.
 */
public class ChangePasswordCommand implements ProfileCommand {

    private final User user;
    private final String newPassword;

    public ChangePasswordCommand(User user, String newPassword) {
        this.user = user;
        this.newPassword = newPassword;
    }

    /**
     * Executes the password change command.
     */
    @Override
    public void execute() {
        try {
            ValidationUtil.validatePassword(newPassword);
            String hashedPassword = PasswordHasher.hash(newPassword);
            user.setPasswordHash(hashedPassword);
            System.out.println("SUCCESS: Password changed successfully.");
        } catch (ValidationException e) {
            System.out.println("ERROR: Password change failed. " + e.getMessage());
        }
    }
}
