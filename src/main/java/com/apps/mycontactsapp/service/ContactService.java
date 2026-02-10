package com.apps.mycontactsapp.service;

import java.util.List;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.User;

/**
 * Service interface for managing contacts system-wide.
 * Defines operations for creating and retrieving contacts.
 */
public interface ContactService {

        /**
         * Creates a new Person contact.
         *
         * @param owner     the user who owns the contact.
         * @param firstName the first name of the person.
         * @param lastName  the last name of the person.
         * @param phones    list of phone numbers (optional).
         * @param emails    list of email addresses (optional).
         * @throws ValidationException if contact details are invalid.
         */
        void createPerson(User owner, String firstName, String lastName, List<String> phones, List<String> emails)
                        throws ValidationException;

        /**
         * Creates a new Organization contact.
         *
         * @param owner      the user who owns the contact.
         * @param name       the organization name.
         * @param website    the organization website URL (optional).
         * @param department the department name (optional).
         * @param phones     list of phone numbers (optional).
         * @param emails     list of email addresses (optional).
         * @throws ValidationException if contact details are invalid.
         */
        void createOrganization(User owner, String name, String website, String department, List<String> phones,
                        List<String> emails) throws ValidationException;

        /**
         * Retrieves contacts based on the requester's permissions.
         * If the requester is an ADMIN, returns all contacts.
         * Otherwise, returns only the contacts owned by the requester.
         *
         * @param requester the user requesting the contacts.
         * @return a filtered list of contacts.
         */
        List<Contact> getContacts(User requester);
}
