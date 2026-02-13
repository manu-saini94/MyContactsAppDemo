package com.apps.mycontactsapp.comparator;

import java.util.Comparator;
import com.apps.mycontactsapp.model.Contact;

/**
 * Strategy holder for Contact sorting comparisons.
 */
public class ContactComparators {

        /** Comparator for sorting by Display Name (A-Z) case-insensitive. */
        public static final Comparator<Contact> BY_NAME_ASC = Comparator.comparing(Contact::getDisplayName,
                        String.CASE_INSENSITIVE_ORDER);

        /** Comparator for sorting by Display Name (Z-A) case-insensitive. */
        public static final Comparator<Contact> BY_NAME_DESC = BY_NAME_ASC.reversed();

        /** Comparator for sorting by Date Added (Newest first). */
        public static final Comparator<Contact> BY_DATE_ADDED_NEWEST = Comparator.comparing(Contact::getCreatedAt)
                        .reversed();

        /** Comparator for sorting by Date Added (Oldest first). */
        public static final Comparator<Contact> BY_DATE_ADDED_OLDEST = Comparator.comparing(Contact::getCreatedAt);

        /** Comparator for sorting by Access Frequency (Most Frequent first). */
        public static final Comparator<Contact> BY_ACCESS_FREQUENCY = Comparator.comparingInt(Contact::getAccessCount)
                        .reversed(); // Most frequent first
}
