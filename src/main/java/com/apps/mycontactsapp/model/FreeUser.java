package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Concrete implementation of a {@link User} with "FREE" privileges.
 * <p>
 * This class extends the base User and provides its own Builder implementation.
 * <p>
 * <b>Design Patterns:</b>
 * <ul>
 * <li><b>Builder Pattern:</b> Implements the Builder via {@link Builder},
 * overriding the
 * {@code self()} method and {@code build()} logic specific to Free users.</li>
 * </ul>
 */
public class FreeUser extends User {
    private FreeUser(Builder builder) {
        super(builder);
    }

    /**
     * Concrete Builder for {@link FreeUser}.
     */
    public static class Builder extends User.UserBuilder<Builder> {

        /**
         * Returns the builder instance itself (implementation of self-typed pattern).
         *
         * @return the current builder instance.
         */
        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds a new {@link FreeUser} instance.
         * <p>
         * Sets the user type to {@link UserType#FREE} and validates mandatory fields.
         *
         * @return the created {@link FreeUser}.
         * @throws ValidationException if validation fails.
         */
        @Override
        public User build() throws ValidationException {
            setUserType(UserType.FREE);
            validateMandatoryFields();
            return new FreeUser(this);
        }
    }
}
