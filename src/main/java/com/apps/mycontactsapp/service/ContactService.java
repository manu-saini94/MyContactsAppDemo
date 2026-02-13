package com.apps.mycontactsapp.service;

import java.util.List;
import java.util.UUID;

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
         * @return the created Contact.
         * @throws ValidationException if contact details are invalid.
         */
        Contact createPerson(User owner, String firstName, String lastName, List<String> phones, List<String> emails)
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
         * @return the created Contact.
         * @throws ValidationException if contact details are invalid.
         */
        Contact createOrganization(User owner, String name, String website, String department, List<String> phones,
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

        /**
         * Retrieves contacts with an option to include inactive ones.
         * RESTRICTED: Only ADMIN or the specific user can see their own inactive
         * contacts.
         *
         * @param requester       the user requesting the contacts.
         * @param includeInactive whether to include soft-deleted contacts.
         * @return a list of contacts.
         */
        List<Contact> getContacts(User requester, boolean includeInactive);

        /**
         * Retrieves a specific contact by ID and increments its access count.
         * 
         * @param requester the user requesting the contact.
         * @param contactId the UUID of the contact.
         * @return the contact found.
         * @throws ValidationException if contact not found or access denied.
         */
        Contact getContact(User requester, UUID contactId) throws ValidationException;

        /**
         * Deletes a contact (soft delete).
         *
         * @param requester the user requesting the deletion.
         * @param contactId the ID of the contact to delete.
         * @throws ValidationException if the contact is not found or access is denied.
         */
        void deleteContact(User requester, UUID contactId) throws ValidationException;

        /**
         * Permanently deletes a contact (Hard Delete).
         *
         * @param requester the user requesting the deletion.
         * @param contactId the ID of the contact to delete.
         * @throws ValidationException if the contact is not found or access is denied.
         */
        void hardDeleteContact(User requester, UUID contactId) throws ValidationException;

        /**
         * Deletes all contacts for a specific user.
         * Intended for internal use (Cascade Delete) or Admin operations.
         *
         * @param userId the ID of the user whose contacts should be deleted.
         */
        void deleteAllContactsForUser(Long userId);

        /**
         * Adds an observer to receive contact event notifications.
         *
         * @param observer the observer to add.
         */
        void addObserver(com.apps.mycontactsapp.observer.ContactObserver observer);

        /**
         * Searches for contacts matching the given specification.
         * 
         * @param requester the user requesting the search.
         * @param spec      the search criteria.
         * @return a list of matching contacts.
         */
        List<Contact> searchContacts(User requester, com.apps.mycontactsapp.specification.Specification<Contact> spec);
}
