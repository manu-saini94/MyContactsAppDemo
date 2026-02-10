package com.apps.mycontactsapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.apps.mycontactsapp.exceptions.InvalidContactException;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.util.ValidationUtil;

/**
 * Abstract base class representing a generic Contact.
 * Uses the Builder Pattern for construction.
 * Implements ContactDisplay for the Decorator Pattern.
 */
public abstract class Contact implements ContactDisplay {
    private final UUID id;
    private String name;
    private final LocalDateTime createdAt;
    private List<PhoneNumber> phoneNumbers;
    private List<EmailAddress> emailAddresses;

    protected Contact(ContactBuilder<?, ?> builder) {
        this.id = UUID.randomUUID();
        this.name = builder.name;
        this.createdAt = LocalDateTime.now();
        this.phoneNumbers = new ArrayList<>(builder.phoneNumbers);
        this.emailAddresses = new ArrayList<>(builder.emailAddresses);
    }

    // Copy Constructor
    protected Contact(Contact source) {
        this.id = source.id;
        this.name = source.name;
        this.createdAt = source.createdAt;
        this.phoneNumbers = new ArrayList<>(source.phoneNumbers);
        this.emailAddresses = new ArrayList<>(source.emailAddresses);
    }

    public abstract Contact copy();

    public ContactMemento createMemento() {
        return new ContactMemento(this);
    }

    public void restore(ContactMemento memento) {
        this.updateStateFrom(memento.getStateSnapshot());
    }

    protected void updateStateFrom(Contact source) {
        this.name = source.name;
        this.phoneNumbers = new ArrayList<>(source.phoneNumbers);
        this.emailAddresses = new ArrayList<>(source.emailAddresses);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidContactException("Name cannot be empty.");
        }
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return new ArrayList<>(phoneNumbers);
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) throws ValidationException {
        if (phoneNumbers != null) {
            for (PhoneNumber p : phoneNumbers) {
                ValidationUtil.validatePhoneNumber(p.getNumber());
            }
            this.phoneNumbers = new ArrayList<>(phoneNumbers);
        } else {
            this.phoneNumbers = new ArrayList<>();
        }
    }

    public List<EmailAddress> getEmailAddresses() {
        return new ArrayList<>(emailAddresses);
    }

    public void setEmailAddresses(List<EmailAddress> emailAddresses) throws ValidationException {
        if (emailAddresses != null) {
            for (EmailAddress e : emailAddresses) {
                ValidationUtil.validateEmail(e.getEmail());
            }
            this.emailAddresses = new ArrayList<>(emailAddresses);
        } else {
            this.emailAddresses = new ArrayList<>();
        }
    }

    public abstract String getDisplayName();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getDisplayName()).append("\n");
        sb.append("Created At: ").append(getCreatedAt()).append("\n");

        if (!phoneNumbers.isEmpty()) {
            sb.append("Phone Numbers:\n");
            for (PhoneNumber p : phoneNumbers) {
                sb.append("  - ").append(p).append("\n");
            }
        }

        if (!emailAddresses.isEmpty()) {
            sb.append("Emails:\n");
            for (EmailAddress e : emailAddresses) {
                sb.append("  - ").append(e).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Generic Builder for Contact.
     */
    public static abstract class ContactBuilder<T extends ContactBuilder<T, U>, U extends Contact> {
        private String name;
        private List<PhoneNumber> phoneNumbers = new ArrayList<>();
        private List<EmailAddress> emailAddresses = new ArrayList<>();

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T addPhoneNumber(String label, String number) {
            try {
                // Ideally propagate exception, but for Builder pattern often we validate at
                // build()
                // or validate immediately. Let's validate immediately regarding format.
                // However, the user input might be raw. Integrating strict validation:
                // ValidationUtil.validatePhoneNumber(number); // Uncomment for strict
            } catch (Exception e) {
                // Log or handle
            }
            this.phoneNumbers.add(new PhoneNumber(label, number));
            return self();
        }

        public T addEmailAddress(String label, String email) {
            this.emailAddresses.add(new EmailAddress(label, email));
            return self();
        }

        protected void validate() throws ValidationException {
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidContactException("Contact name is required.");
            }
            for (PhoneNumber p : phoneNumbers) {
                ValidationUtil.validatePhoneNumber(p.getNumber());
            }
            for (EmailAddress e : emailAddresses) {
                ValidationUtil.validateEmail(e.getEmail());
            }
        }

        protected abstract T self();

        public abstract U build() throws ValidationException;
    }
}
