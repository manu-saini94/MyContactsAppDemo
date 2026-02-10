package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Represents a Person contact.
 */
public class Person extends Contact {
    private String firstName;
    private String lastName;

    private Person(PersonBuilder builder) {
        super(builder);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
    }

    // Copy Constructor
    private Person(Person source) {
        super(source);
        this.firstName = source.firstName;
        this.lastName = source.lastName;
    }

    @Override
    public Contact copy() {
        return new Person(this);
    }

    @Override
    protected void updateStateFrom(Contact source) {
        super.updateStateFrom(source);
        if (source instanceof Person) {
            Person p = (Person) source;
            this.firstName = p.firstName;
            this.lastName = p.lastName;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws ValidationException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name cannot be empty.");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) throws ValidationException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name cannot be empty.");
        }
        this.lastName = lastName;
    }

    @Override
    public String getDisplayName() {
        return (firstName + " " + lastName).trim();
    }

    public static class PersonBuilder extends Contact.ContactBuilder<PersonBuilder, Person> {
        private String firstName;
        private String lastName;

        public PersonBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PersonBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        @Override
        protected PersonBuilder self() {
            return this;
        }

        @Override
        public Person build() throws ValidationException {
            if (firstName == null && lastName == null) {
                throw new ValidationException("At least one name (first or last) is required.");
            }
            // Set the base name for searching/sorting
            super.name((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName));
            validate();
            return new Person(this);
        }
    }
}
