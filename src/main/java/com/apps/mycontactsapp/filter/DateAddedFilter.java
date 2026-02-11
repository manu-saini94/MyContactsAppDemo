package com.apps.mycontactsapp.filter;

import java.time.LocalDateTime;
import com.apps.mycontactsapp.model.Contact;

/**
 * Filter strategy to check if a contact was added after a certain date.
 */
public class DateAddedFilter implements ContactFilter {
    private final LocalDateTime since;

    /**
     * Constructs a new DateAddedFilter.
     * 
     * @param since the date/time to filter contacts created after (inclusive).
     */
    public DateAddedFilter(LocalDateTime since) {
        this.since = since;
    }

    /**
     * Tests if the contact was created after the specified date.
     * 
     * @param contact the contact to test.
     * @return true if created at or after the 'since' date, false otherwise.
     */
    @Override
    public boolean test(Contact contact) {
        if (since == null) {
            return true;
        }
        return contact.getCreatedAt().isAfter(since) || contact.getCreatedAt().isEqual(since);
    }
}
