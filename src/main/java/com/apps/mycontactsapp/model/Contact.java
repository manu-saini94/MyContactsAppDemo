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
 */
public abstract class Contact {
    private final UUID id;
    private final String name;
    private final LocalDateTime createdAt;
    private final List<PhoneNumber> phoneNumbers;
    private final List<EmailAddress> emailAddresses;

    protected Contact(ContactBuilder<?, ?> builder) {
        this.id = UUID.randomUUID();
        this.name = builder.name;
        this.createdAt = LocalDateTime.now();
        this.phoneNumbers = new ArrayList<>(builder.phoneNumbers);
        this.emailAddresses = new ArrayList<>(builder.emailAddresses);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return new ArrayList<>(phoneNumbers);
    }

    public List<EmailAddress> getEmailAddresses() {
        return new ArrayList<>(emailAddresses);
    }

    public abstract String getDisplayName();

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
