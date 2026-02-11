package com.apps.mycontactsapp.specification;

import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.EmailAddress;
import com.apps.mycontactsapp.model.PhoneNumber;

/**
 * Factory class for creating Contact specifications.
 */
public class ContactSpecifications {

    private ContactSpecifications() {
        // Prevent instantiation
    }

    /**
     * Specification to check if contact name contains the given string
     * (case-insensitive).
     */
    public static Specification<Contact> nameContains(String namePart) {
        return contact -> {
            if (namePart == null || namePart.trim().isEmpty()) {
                return true; // Empty search matches all
            }
            String lowerNamePart = namePart.toLowerCase();
            return contact.getDisplayName() != null &&
                    contact.getDisplayName().toLowerCase().contains(lowerNamePart);
        };
    }

    /**
     * Specification to check if contact has a phone number containing the given
     * string.
     */
    public static Specification<Contact> phoneContains(String phonePart) {
        return contact -> {
            if (phonePart == null || phonePart.trim().isEmpty()) {
                return true;
            }
            return contact.getPhoneNumbers().stream()
                    .map(PhoneNumber::getNumber)
                    .anyMatch(num -> num.contains(phonePart));
        };
    }

    /**
     * Specification to check if contact has an email containing the given string.
     */
    public static Specification<Contact> emailContains(String emailPart) {
        return contact -> {
            if (emailPart == null || emailPart.trim().isEmpty()) {
                return true;
            }
            String lowerEmailPart = emailPart.toLowerCase();
            return contact.getEmailAddresses().stream()
                    .map(EmailAddress::getEmail)
                    .anyMatch(email -> email.toLowerCase().contains(lowerEmailPart));
        };
    }

    /**
     * Specification to check if contact has a tag containing the given string
     * (case-insensitive).
     */
    public static Specification<Contact> tagContains(String tagPart) {
        return contact -> {
            if (tagPart == null || tagPart.trim().isEmpty()) {
                return true;
            }
            String lowerTagPart = tagPart.toLowerCase();
            return contact.getTags().stream()
                    .anyMatch(t -> t.toLowerCase().contains(lowerTagPart));
        };
    }
}
