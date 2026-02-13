package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Concrete implementation of a {@link User} with "ADMIN" privileges.
 *
 * This class extends the base User and provides its own Builder implementation.
 * Admins have full access to all contacts.
 */
public class AdminUser extends User {
    private AdminUser(Builder builder) {
        super(builder);
    }

    /**
     * Concrete Builder for {@link AdminUser}.
     */
    public static class Builder extends User.UserBuilder<Builder> {

        /**
         * Returns the builder instance itself.
         *
         * @return the current builder instance.
         */
        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds a new {@link AdminUser} instance.
         *
         * Sets the user type to {@link UserType#ADMIN} and validates mandatory
         * fields.
         *
         * @return the created {@link AdminUser}.
         * @throws ValidationException if validation fails.
         */
        @Override
        public User build() throws ValidationException {
            setUserType(UserType.ADMIN);
            validateMandatoryFields();
            return new AdminUser(this);
        }
    }
}
