package com.apps.mycontactsapp.filter;

import com.apps.mycontactsapp.model.Contact;

/**
 * Composite filter that passes if ANY component filter passes.
 */
public class OrFilter implements ContactFilter {
    private final ContactFilter first;
    private final ContactFilter second;

    /**
     * Constructs a new OrFilter.
     * 
     * @param first  the first filter.
     * @param second the second filter.
     */
    public OrFilter(ContactFilter first, ContactFilter second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Tests if the contact satisfies EITHER filter.
     * 
     * @param contact the contact to test.
     * @return true if at least one filter passes, false otherwise.
     */
    @Override
    public boolean test(Contact contact) {
        return first.test(contact) || second.test(contact);
    }
}
