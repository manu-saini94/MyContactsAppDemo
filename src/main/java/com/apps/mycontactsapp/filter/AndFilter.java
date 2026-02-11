package com.apps.mycontactsapp.filter;

import com.apps.mycontactsapp.model.Contact;

/**
 * Composite filter that passes only if ALL component filters pass.
 */
public class AndFilter implements ContactFilter {
    private final ContactFilter first;
    private final ContactFilter second;

    /**
     * Constructs a new AndFilter.
     * 
     * @param first  the first filter.
     * @param second the second filter.
     */
    public AndFilter(ContactFilter first, ContactFilter second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Tests if the contact satisfies BOTH filters.
     * 
     * @param contact the contact to test.
     * @return true if both filters pass, false otherwise.
     */
    @Override
    public boolean test(Contact contact) {
        return first.test(contact) && second.test(contact);
    }
}
