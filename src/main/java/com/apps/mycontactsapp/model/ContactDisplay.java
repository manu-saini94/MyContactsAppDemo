package com.apps.mycontactsapp.model;

/**
 * Interface for displaying contact details.
 * Part of the Decorator Pattern for flexible display formatting.
 */
public interface ContactDisplay {

    /**
     * Gets the formatted details of the contact.
     *
     * @return the contact details as a string.
     */
    String getDetails();
}
