package com.apps.mycontactsapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.apps.mycontactsapp.composite.ContactComponent;
import com.apps.mycontactsapp.exceptions.InvalidContactException;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.factory.TagFactory;
import com.apps.mycontactsapp.util.ValidationUtil;

/**
 * Abstract base class representing a generic Contact.
 * Uses the Builder Pattern for construction.
 * Implements ContactComponent for the Composite and Decorator Patterns.
 */
public abstract class Contact implements ContactComponent {
    private final UUID id;
    private final Long userId; // Owner of the contact
    private String name;
    private final LocalDateTime createdAt;
    private boolean active = true; // Soft delete flag
    private int accessCount = 0; // Frequently contacted counter

    // UC-11: Use Set<Tag> instead of Set<String>
    private Set<Tag> tags = new HashSet<>();

    private List<PhoneNumber> phoneNumbers;
    private List<EmailAddress> emailAddresses;

    protected Contact(ContactBuilder<?, ?> builder) {
        this.id = UUID.randomUUID();
        this.userId = builder.userId;
        this.name = builder.name;
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.tags = new HashSet<>();
        this.phoneNumbers = new ArrayList<>(builder.phoneNumbers);
        this.emailAddresses = new ArrayList<>(builder.emailAddresses);
    }

    /**
     * Copy Constructor.
     * 
     * @param source the contact to copy from.
     */
    protected Contact(Contact source) {
        this.id = source.id;
        this.userId = source.userId; // Preserve ownership
        this.name = source.name;
        this.createdAt = source.createdAt;
        this.active = source.active; // Preserve active state
        this.tags = new HashSet<>(source.tags); // Shallow copy of Tag references (Flyweights are immutable)
        this.phoneNumbers = new ArrayList<>(source.phoneNumbers);
        this.emailAddresses = new ArrayList<>(source.emailAddresses);
    }

    /**
     * Creates a deep copy of the contact.
     * 
     * @return a new Contact instance with the same state.
     */
    public abstract Contact copy();

    /**
     * Creates a memento of the current state.
     * 
     * @return a ContactMemento containing the current state.
     */
    public ContactMemento createMemento() {
        return new ContactMemento(this);
    }

    /**
     * Restores the state from a memento.
     * 
     * @param memento the memento to restore from.
     */
    public void restore(ContactMemento memento) {
        this.updateStateFrom(memento.getStateSnapshot());
    }

    /**
     * Updates the state from another contact object.
     * 
     * @param source the contact to copy state from.
     */
    protected void updateStateFrom(Contact source) {
        this.name = source.name;
        this.active = source.active;
        this.tags = new HashSet<>(source.tags);
        this.phoneNumbers = new ArrayList<>(source.phoneNumbers);
        this.emailAddresses = new ArrayList<>(source.emailAddresses);
        // userId and id are final and should not change during restore
    }

    /**
     * Gets the unique identifier of the contact.
     * 
     * @return the UUID of the contact.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the user ID of the contact owner.
     * 
     * @return the owner's user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the name of the contact.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the contact.
     * 
     * @param name the new name.
     * @throws ValidationException if the name is null or empty.
     */
    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidContactException("Name cannot be empty.");
        }
        this.name = name;
    }

    /**
     * Gets the creation timestamp.
     * 
     * @return the LocalDateTime when the contact was created.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the contact is active (not soft-deleted).
     * 
     * @return true if active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the contact.
     * 
     * @param active true to activate, false to soft-delete.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the number of times this contact has been accessed/viewed.
     * 
     * @return the access count.
     */
    public int getAccessCount() {
        return accessCount;
    }

    /**
     * Increments the access count by one.
     */
    public void incrementAccessCount() {
        this.accessCount++;
    }

    /**
     * Gets the list of phone numbers.
     * 
     * @return a list of PhoneNumber objects.
     */
    public List<PhoneNumber> getPhoneNumbers() {
        return new ArrayList<>(phoneNumbers);
    }

    /**
     * Sets the list of phone numbers.
     * Validates each phone number.
     * 
     * @param phoneNumbers the new list of phone numbers.
     * @throws ValidationException if any phone number is invalid.
     */
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

    /**
     * Gets the list of email addresses.
     * 
     * @return a list of EmailAddress objects.
     */
    public List<EmailAddress> getEmailAddresses() {
        return new ArrayList<>(emailAddresses);
    }

    /**
     * Sets the list of email addresses.
     * Validates each email address.
     * 
     * @param emailAddresses the new list of email addresses.
     * @throws ValidationException if any email address is invalid.
     */
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

    /**
     * Gets the display name of the contact.
     * Subclasses must implement this to provide specific display logic.
     * 
     * @return the display name.
     */
    public abstract String getDisplayName();

    /**
     * {@inheritDoc}
     * Gets the formatted details of the contact.
     * 
     * @return the contact details string.
     */
    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getDisplayName()).append("\n");
        sb.append("Created At: ").append(getCreatedAt()).append("\n");
        sb.append("Access Count: ").append(getAccessCount()).append("\n");

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
        if (!tags.isEmpty()) {
            sb.append("Tags: ").append(tags.stream().map(Tag::getName).collect(Collectors.joining(", "))).append("\n");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     * Adds a tag to the contact.
     * Uses TagFactory to obtain Flyweight Tag instance.
     * 
     * @param tag the tag name to add.
     */
    @Override
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(TagFactory.getTag(tag));
        }
    }

    /**
     * {@inheritDoc}
     * Removes a tag from the contact.
     * 
     * @param tag the tag name to remove.
     */
    @Override
    public void removeTag(String tag) {
        if (tag != null) {
            this.tags.remove(TagFactory.getTag(tag));
        }
    }

    /**
     * {@inheritDoc}
     * Soft deletes the contact.
     */
    @Override
    public void delete() {
        this.active = false;
    }

    /**
     * {@inheritDoc}
     * Gets the set of tags as Strings for compatibility.
     * 
     * @return a Set of tag names.
     */
    @Override
    public Set<String> getTags() {
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

    /**
     * Gets the set of Tag objects (Flyweights).
     * 
     * @return a Set of Tag objects.
     */
    public Set<Tag> getTagObjects() {
        return new HashSet<>(tags);
    }

    /**
     * Generic Builder for Contact.
     */
    public static abstract class ContactBuilder<T extends ContactBuilder<T, U>, U extends Contact> {
        private Long userId;
        private String name;
        private List<PhoneNumber> phoneNumbers = new ArrayList<>();
        private List<EmailAddress> emailAddresses = new ArrayList<>();

        /**
         * Sets the user ID for the contact owner.
         * 
         * @param userId the owner's user ID.
         * @return the builder instance.
         */
        public T userId(Long userId) {
            this.userId = userId;
            return self();
        }

        /**
         * Sets the contact's name.
         * 
         * @param name the name of the contact.
         * @return the builder instance.
         */
        public T name(String name) {
            this.name = name;
            return self();
        }

        /**
         * Adds a phone number to the contact.
         * 
         * @param label  the label for the phone number (e.g., "Mobile", "Work").
         * @param number the phone number string.
         * @return the builder instance.
         */
        public T addPhoneNumber(String label, String number) {
            try {
                // Ideally propagate exception, but for Builder pattern often we validate at
                // build()
                // or validate immediately. Let's validate immediately regarding format.
                // However, the user input might be raw. Integrating strict validation:
            } catch (Exception e) {
                // Log or handle
            }
            this.phoneNumbers.add(new PhoneNumber(label, number));
            return self();
        }

        /**
         * Adds an email address to the contact.
         * 
         * @param label the label for the email address (e.g., "Personal", "Work").
         * @param email the email address string.
         * @return the builder instance.
         */
        public T addEmailAddress(String label, String email) {
            this.emailAddresses.add(new EmailAddress(label, email));
            return self();
        }

        protected void validate() throws ValidationException {
            if (userId == null) {
                throw new InvalidContactException("Contact owner (userId) is required.");
            }
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
