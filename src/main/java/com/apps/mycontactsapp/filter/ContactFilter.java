package com.apps.mycontactsapp.filter;

import com.apps.mycontactsapp.model.Contact;

/**
 * Interface for filtering Contacts.
 * Uses the Strategy Pattern.
 */
@FunctionalInterface
public interface ContactFilter {
    /**
     * Tests if the contact satisfies the filter criteria.
     * 
     * @param contact the contact to test.
     * @return true if the contact passes the filter, false otherwise.
     */
    boolean test(Contact contact);
}
