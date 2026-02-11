package com.apps.mycontactsapp.model;

/**
 * Value object representing an email address with a label (e.g., "Personal",
 * "Work").
 */
public class EmailAddress {
    private final String label;
    private final String email;

    /**
     * Constructs a new EmailAddress.
     * 
     * @param label the label (e.g. "Work").
     * @param email the email address string.
     */
    public EmailAddress(String label, String email) {
        this.label = label;
        this.email = email;
    }

    /**
     * Gets the label.
     * 
     * @return the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the email address.
     * 
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return label + ": " + email;
    }
}
