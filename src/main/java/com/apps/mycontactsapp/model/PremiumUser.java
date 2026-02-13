package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Concrete implementation of a {@link User} with "PREMIUM" privileges.
 *
 * This class extends the base User and provides its own Builder implementation.
 *
 * Design Patterns:
 * - Builder Pattern: Implements the same pattern as {@link FreeUser},
 * allowing
 * for consistent object construction for premium users.
 */
public class PremiumUser extends User {

    private PremiumUser(Builder builder) {
        super(builder);
    }

    /**
     * Concrete Builder for {@link PremiumUser}.
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
         * Builds a new {@link PremiumUser} instance.
         *
         * Sets the user type to {@link UserType#PREMIUM} and validates mandatory
         * fields.
         *
         * @return the created {@link PremiumUser}.
         * @throws ValidationException if validation fails.
         */
        @Override
        public User build() throws ValidationException {
            setUserType(UserType.PREMIUM);
            validateMandatoryFields();
            return new PremiumUser(this);
        }
    }
}
