package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Represents a Person contact.
 */
public class Person extends Contact {
    private final String firstName;
    private final String lastName;

    private Person(PersonBuilder builder) {
        super(builder);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
