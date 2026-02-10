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
     * {@inheritDoc}
     */
    @Override
    public void createPerson(User owner, String firstName, String lastName, List<String> phones, List<String> emails)
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createOrganization(User owner, String name, String website, String department, List<String> phones,
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contact> getContacts(User requester) {
        if (requester == null) {
            return List.of();
        }

        if (UserType.ADMIN.equals(requester.getUserType())) {
            return contactRepository.findAll();
        } else {
            return contactRepository.findByUserId(requester.getId());
        }
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
}
