package com.apps.mycontactsapp.filter;

import com.apps.mycontactsapp.model.Contact;

/**
 * Filter strategy to check if a contact has a specific tag.
 */
public class TagFilter implements ContactFilter {
    private final String tag;

    /**
     * Constructs a new TagFilter.
     * 
     * @param tag the tag to filter by (case-insensitive).
     */
    public TagFilter(String tag) {
        this.tag = tag;
    }

    /**
     * Tests if the contact has the specified tag.
     * 
     * @param contact the contact to test.
     * @return true if the contact has the tag, false otherwise.
     */
    @Override
    public boolean test(Contact contact) {
        if (tag == null || tag.trim().isEmpty()) {
            return true;
        }
        return contact.getTags().stream()
                .anyMatch(t -> t.equalsIgnoreCase(tag));
    }
}
