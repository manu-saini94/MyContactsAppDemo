package com.apps.mycontactsapp.service.impl;

import java.util.List;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.Organization;
import com.apps.mycontactsapp.model.Person;
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
     *
     * @param firstName the first name of the person.
     * @param lastName  the last name of the person.
     * @param phones    list of phone numbers.
     * @param emails    list of email addresses.
     * @throws ValidationException if the first name or last name is invalid or
     *                             missing.
     */
    @Override
    public void createPerson(String firstName, String lastName, List<String> phones, List<String> emails)
            throws ValidationException {
        Person.PersonBuilder builder = new Person.PersonBuilder()
                .firstName(firstName)
                .lastName(lastName);

        addDetails(builder, phones, emails);

        Person person = builder.build();
        contactRepository.save(person);
    }

    /**
     * {@inheritDoc}
     *
     * @param name       the organization name.
     * @param website    the organization website URL.
     * @param department the department name.
     * @param phones     list of phone numbers.
     * @param emails     list of email addresses.
     * @throws ValidationException if the organization name is invalid or missing.
     */
    @Override
    public void createOrganization(String name, String website, String department, List<String> phones,
            List<String> emails) throws ValidationException {
        Organization.OrganizationBuilder builder = new Organization.OrganizationBuilder()
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
     *
     * @return a list of all contacts.
     */
    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /**
     * Helper method to add phone numbers and emails to the contact builder.
     * Parses simple strings into labelled value objects.
     *
     * @param builder the contact builder to populate.
     * @param phones  list of phone strings (format: "Label:Number" or "Number").
     * @param emails  list of email strings (format: "Label:Email" or "Email").
     */
    private void addDetails(Contact.ContactBuilder<?, ?> builder, List<String> phones, List<String> emails) {
        if (phones != null) {
            for (String phone : phones) {
                // Simple parsing: Label:Number or default label "Mobile"
                String[] parts = phone.split(":");
                if (parts.length == 2) {
                    builder.addPhoneNumber(parts[0].trim(), parts[1].trim());
                } else {
                    builder.addPhoneNumber("Mobile", phone.trim());
                }
            }
        }
        if (emails != null) {
            for (String email : emails) {
                String[] parts = email.split(":");
                if (parts.length == 2) {
                    builder.addEmailAddress(parts[0].trim(), parts[1].trim());
                } else {
                    builder.addEmailAddress("Email", email.trim());
                }
            }
        }
    }
}
