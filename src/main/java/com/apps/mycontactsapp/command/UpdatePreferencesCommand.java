package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.model.ProfilePreferences;
import com.apps.mycontactsapp.model.User;

/**
 * Concrete command to update a user's profile preferences.
 */
public class UpdatePreferencesCommand implements ProfileCommand {

    private final User user;
    private final ProfilePreferences newPreferences;

    public UpdatePreferencesCommand(User user, ProfilePreferences newPreferences) {
        this.user = user;
        this.newPreferences = newPreferences;
    }

    @Override
    public void execute() {
        if (newPreferences != null) {
            user.setPreferences(newPreferences);
            System.out.println("SUCCESS: Profile preferences updated.");
            System.out.println("Current Preferences: " + user.getPreferences());
        } else {
            System.out.println("ERROR: Preferences cannot be null.");
        }
    }
}
