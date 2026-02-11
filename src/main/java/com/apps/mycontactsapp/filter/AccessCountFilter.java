package com.apps.mycontactsapp.filter;

import com.apps.mycontactsapp.model.Contact;

/**
 * Filter strategy to checks if a contact has been accessed at least N times.
 * Useful for "Frequently Contacted" filtering.
 */
public class AccessCountFilter implements ContactFilter {
    private final int minCount;

    /**
     * Constructs a new AccessCountFilter.
     * 
     * @param minCount the minimum number of times the contact must have been
     *                 accessed.
     */
    public AccessCountFilter(int minCount) {
        this.minCount = minCount;
    }

    /**
     * Tests if the contact's access count meets the minimum requirement.
     * 
     * @param contact the contact to test.
     * @return true if access count >= minCount, false otherwise.
     */
    @Override
    public boolean test(Contact contact) {
        return contact.getAccessCount() >= minCount;
    }
}
