package com.apps.mycontactsapp.service;

import java.util.List;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;

/**
 * Service interface for managing contacts system-wide.
 * Defines operations for creating and retrieving contacts.
 */
public interface ContactService {

        /**
         * Creates a new Person contact.
         *
         * @param firstName the first name of the person.
         * @param lastName  the last name of the person.
         * @param phones    list of phone numbers (optional).
         * @param emails    list of email addresses (optional).
         * @throws ValidationException if contact details are invalid.
         */
        void createPerson(String firstName, String lastName, List<String> phones, List<String> emails)
                        throws ValidationException;

        /**
         * Creates a new Organization contact.
         *
         * @param name       the organization name.
         * @param website    the organization website URL (optional).
         * @param department the department name (optional).
         * @param phones     list of phone numbers (optional).
         * @param emails     list of email addresses (optional).
         * @throws ValidationException if contact details are invalid.
         */
        void createOrganization(String name, String website, String department, List<String> phones,
                        List<String> emails) throws ValidationException;

        /**
         * Retrieves all stored contacts.
         *
         * @return a list of all contacts, or an empty list if none exist.
         */
        List<Contact> getAllContacts();
}
