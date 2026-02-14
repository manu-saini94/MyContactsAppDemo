package com.apps.mycontactsapp.service.impl;

import java.util.List;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.Organization;
import com.apps.mycontactsapp.model.Person;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.model.UserType;
import com.apps.mycontactsapp.repository.ContactRepository;
import com.apps.mycontactsapp.service.ContactService;

/**
 * Implementation of ContactService.
 */
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Creates a new Person contact.
     * Validates that the owner exists.
     *
     * @param owner     the user creating the contact.
     * @param firstName the first name.
     * @param lastName  the last name.
     * @param phones    list of phone numbers.
     * @param emails    list of email addresses.
     * @return the created Contact.
     * @throws ValidationException if the owner is null or inputs are invalid.
     */
    @Override
    public Contact createPerson(User owner, String firstName, String lastName, List<String> phones, List<String> emails)
            throws ValidationException {
        // Validate owner
        if (owner == null) {
            throw new ValidationException("Contact owner cannot be null.");
        }

        Person.PersonBuilder builder = new Person.PersonBuilder()
                .userId(owner.getId())
                .firstName(firstName)
                .lastName(lastName);

        addDetails(builder, phones, emails);

        Person person = builder.build();
        contactRepository.save(person);
        return person;
    }

    /**
     * Creates a new Organization contact.
     * Validates that the owner exists.
     *
     * @param owner      the user creating the contact.
     * @param name       the organization name.
     * @param website    the website URL.
     * @param department the department.
     * @param phones     list of phone numbers.
     * @param emails     list of email addresses.
     * @return the created Contact.
     * @throws ValidationException if the owner is null or inputs are invalid.
     */
    @Override
    public Contact createOrganization(User owner, String name, String website, String department, List<String> phones,
            List<String> emails) throws ValidationException {
        // Validate owner
        if (owner == null) {
            throw new ValidationException("Contact owner cannot be null.");
        }

        Organization.OrganizationBuilder builder = new Organization.OrganizationBuilder()
                .userId(owner.getId())
                .website(website)
                .department(department);

        // Organization uses the base 'name' field from ContactBuilder via its own
        // builder
        builder.name(name);

        addDetails(builder, phones, emails);

        Organization organization = builder.build();
        contactRepository.save(organization);
        return organization;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * Retrieves contacts for the requesting user.
     * Defaults to including inactive contacts so users can see soft-deleted items
     * in their list (usually to restore or permanent delete).
     *
     * @param requester the user requesting the list.
     * @return a list of contacts visible to the user.
     */
    @Override
    public List<Contact> getContacts(User requester) {
        // Default behavior: include inactive so users can see soft-deleted contacts
        return getContacts(requester, true);
    }

    /**
     * Retrieves contacts with an option to include inactive ones.
     * Admin can see all contacts. Regular users can only see their own.
     *
     * @param requester       the user requesting the contacts.
     * @param includeInactive whether to include soft-deleted contacts.
     * @return a list of contacts.
     */
    @Override
    public List<Contact> getContacts(User requester, boolean includeInactive) {
        if (requester == null) {
            return List.of();
        }

        // ACL: Only Admin can request inactive contacts generally?
        // Actually, user might want to see their own trash bin.
        // But for now, let's allow it but filter by ownership unless Admin.

        if (UserType.ADMIN.equals(requester.getUserType())) {
            return contactRepository.findAll(includeInactive);
        } else {
            // Regular user can only see their own.
            // If they request inactive, they see their own inactive too.
            return contactRepository.findByUserId(requester.getId(), includeInactive);
        }
    }

    /**
     * Retrieves a specific contact by ID and increments its access count.
     * 
     * @param requester the user requesting the contact.
     * @param contactId the UUID of the contact.
     * @return the contact found.
     * @throws ValidationException if contact not found or access denied.
     */
    @Override
    public Contact getContact(User requester, java.util.UUID contactId) throws ValidationException {
        if (requester == null || contactId == null) {
            throw new ValidationException("Invalid request.");
        }

        java.util.Optional<Contact> contactOpt = contactRepository.findById(contactId);
        if (contactOpt.isEmpty()) {
            throw new ValidationException("Contact not found.");
        }

        Contact contact = contactOpt.get();
        // ACL Check
        boolean isAdmin = requester.getUserType() == UserType.ADMIN;
        boolean isOwner = contact.getUserId() != null && contact.getUserId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new ValidationException("Access Denied.");
        }

        // Use default inactive behavior matching getContacts logic?
        // Usually retrieving specific by ID allows seeing details even if inactive
        // (e.g. before restore)
        // unless specific rule. For now, allow it if ACL passes.

        // Increment access count
        contact.incrementAccessCount();
        contactRepository.save(contact); // Persist change (important for real DB, helpful for stub consistency)

        return contact;
    }

    // Duplicate removed

    private final java.util.List<com.apps.mycontactsapp.observer.ContactObserver> observers = new java.util.ArrayList<>();

    /**
     * Adds an observer for contact events.
     *
     * @param observer the observer to register.
     */
    @Override
    public void addObserver(com.apps.mycontactsapp.observer.ContactObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Contact contact) {
        for (com.apps.mycontactsapp.observer.ContactObserver observer : observers) {
            observer.onContactDeleted(contact);
        }
    }

    /**
     * Soft deletes a contact.
     * Mocks logic: verify existence, check active status, check permission (Owner
     * or Admin).
     *
     * @param requester the user requesting deletion.
     * @param contactId the UUID of the contact.
     * @throws ValidationException if not found, already deleted, or access denied.
     */
    @Override
    public void deleteContact(User requester, java.util.UUID contactId) throws ValidationException {
        java.util.Optional<Contact> contactOpt = contactRepository.findById(contactId);
        if (contactOpt.isEmpty()) {
            throw new ValidationException("Contact not found.");
        }

        Contact contact = contactOpt.get();
        if (!contact.isActive()) {
            throw new ValidationException("Contact not found."); // Treat soft-deleted as not found
        }

        // ACL Check: Admin can delete anyone's contact. Owner can delete own contact.
        boolean isAdmin = requester.getUserType() == UserType.ADMIN;
        boolean isOwner = contact.getUserId() != null && contact.getUserId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new ValidationException("Access Denied: You cannot delete this contact.");
        }

        contactRepository.delete(contact);
        notifyObservers(contact);
    }

    /**
     * Hard deletes a contact (Permanent).
     * Removes the contact completely from the repository.
     *
     * @param requester the user requesting deletion.
     * @param contactId the UUID of the contact.
     * @throws ValidationException if not found or access denied.
     */
    @Override
    public void hardDeleteContact(User requester, java.util.UUID contactId) throws ValidationException {
        java.util.Optional<Contact> contactOpt = contactRepository.findById(contactId);
        // We find even inactive contacts for hard delete if we want to allow cleaning
        // up trash
        // But if findAll filters them out, findById might not...
        // Logic: specific retrieval usually finds it.

        if (contactOpt.isEmpty()) {
            throw new ValidationException("Contact not found.");
        }

        Contact contact = contactOpt.get();

        // ACL Check for Hard Delete
        // Admin: YES
        // Owner: YES (allowing users to permanently delete their own contacts)
        boolean isAdmin = requester.getUserType() == UserType.ADMIN;
        boolean isOwner = contact.getUserId() != null && contact.getUserId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new ValidationException("Access Denied: You cannot permanently delete this contact.");
        }

        contactRepository.hardDelete(contact);
        // We could notify observers here as well if we want to log permanent deletion
        notifyObservers(contact);
    }

    /**
     * Deletes all contacts for a specific user.
     * Used for cascading user deletion.
     *
     * @param userId the ID of the user.
     */
    @Override
    public void deleteAllContactsForUser(Long userId) {
        contactRepository.deleteByUserId(userId);
    }

    /**
     * Helper method to add phone numbers and emails to the contact builder.
     * Parses simple strings into labelled value objects.
     *
     * @param builder the contact builder to populate.
     * @param phones  list of phone strings (format: "Label:Number").
     * @param emails  list of email strings (format: "Label:Email").
     */
    private void addDetails(Contact.ContactBuilder<?, ?> builder, List<String> phones, List<String> emails) {
        if (phones != null) {
            for (String s : phones) {
                if (s != null && !s.isBlank()) {
                    String[] parts = s.split(":", 2);
                    String label = parts.length > 1 ? parts[0].trim() : "Mobile";
                    String number = parts.length > 1 ? parts[1].trim() : parts[0].trim();
                    builder.addPhoneNumber(label, number);
                }
            }
        }
        if (emails != null) {
            for (String s : emails) {
                if (s != null && !s.isBlank()) {
                    String[] parts = s.split(":", 2);
                    String label = parts.length > 1 ? parts[0].trim() : "Personal";
                    String email = parts.length > 1 ? parts[1].trim() : parts[0].trim();
                    builder.addEmailAddress(label, email);
                }
            }
        }
    }

    /**
     * Searches for contacts matching the given specification.
     * 
     * @param requester the user requesting the search.
     * @param spec      the search criteria.
     * @return a list of matching contacts.
     */
    @Override
    public List<Contact> searchContacts(User requester,
            com.apps.mycontactsapp.specification.Specification<Contact> spec) {
        if (spec == null) {
            return getContacts(requester);
        }
        List<Contact> allContacts = getContacts(requester);
        return allContacts.stream()
                .filter(spec::isSatisfiedBy)
                .collect(java.util.stream.Collectors.toList());
    }
<<<<<<< Updated upstream
=======

    /**
     * {@inheritDoc}
     *
     * @param requester the user performing the tagging.
     * @param contactId the ID of the contact to tag.
     * @param tagName   the name of the tag to add.
     * @throws ValidationException if access denied.
     */
    @Override
    public void tagContact(User requester, java.util.UUID contactId, String tagName) throws ValidationException {
        Contact contact = getContact(requester, contactId); // Validates access

        com.apps.mycontactsapp.model.Tag tag = com.apps.mycontactsapp.factory.TagFactory.getTag(tagName);
        contact.addTag(tag);

        // Sync with user's global tags
        requester.addUserTag(tag);

        // Notify Observers
        for (com.apps.mycontactsapp.observer.ContactObserver observer : observers) {
            observer.onContactTagged(contact, tag);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param requester the user performing the untagging.
     * @param contactId the ID of the contact to untag.
     * @param tagName   the name of the tag to remove.
     * @throws ValidationException if access denied.
     */
    @Override
    public void untagContact(User requester, java.util.UUID contactId, String tagName) throws ValidationException {
        Contact contact = getContact(requester, contactId);

        com.apps.mycontactsapp.model.Tag tag = com.apps.mycontactsapp.factory.TagFactory.getTag(tagName);
        contact.removeTag(tagName);

        // Notify Observers
        for (com.apps.mycontactsapp.observer.ContactObserver observer : observers) {
            observer.onContactUntagged(contact, tag);
        }
    }
>>>>>>> Stashed changes
}
