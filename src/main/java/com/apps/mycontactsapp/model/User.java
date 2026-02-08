package com.apps.mycontactsapp.model;

import java.time.LocalDateTime;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.util.PasswordHasher;
import com.apps.mycontactsapp.util.ValidationUtil;

/**
 * Abstract base class representing a User in the system.
 * <p>
 * <b>Design Patterns:</b>
 * <ul>
 * <li><b>Builder Pattern:</b> This class uses a generic, self-typed Builder
 * Pattern ({@link UserBuilder})
 * to construct instances. This handles optional parameters and complex
 * validation logic
 * during object creation, enforcing immutability.</li>
 * </ul>
 */
public abstract class User {
    private final Long id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final LocalDateTime createdAt;
    private final UserType userType;

    /**
     * Protected constructor to be called by the Builder.
     *
     * @param builder the builder instance containing user data.
     */
    protected User(UserBuilder<?> builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.createdAt = builder.createdAt;
        this.userType = builder.userType;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserType getUserType() {
        return userType;
    }

    /**
     * Generic self-typed Builder for constructing {@link User} instances.
     * <p>
     * This builder supports inheritance, allowing subclasses of {@link User} (like
     * {@link FreeUser} and {@link PremiumUser})
     * to share common building logic while maintaining a fluent interface.
     *
     * @param <T> the concrete type of the Builder (Recursive Generics).
     */
    public static abstract class UserBuilder<T extends UserBuilder<T>> {

        private Long id;
        private String name;
        private String email;
        private String passwordHash;
        private LocalDateTime createdAt = LocalDateTime.now();
        private UserType userType;

        /**
         * Sets the user type.
         *
         * @param userType the {@link UserType} for the user.
         */
        protected void setUserType(UserType userType) {
            this.userType = userType;
        }

        /**
         * Sets the user ID.
         *
         * @param id the unique identifier for the user.
         * @return the builder instance.
         */
        public T id(Long id) {
            this.id = id;
            return self();
        }

        /**
         * Sets the user's full name.
         *
         * @param name the full name.
         * @return the builder instance.
         */
        public T name(String name) {
            this.name = name;
            return self();
        }

        /**
         * Sets and validates the user's email address.
         *
         * @param email the email address.
         * @return the builder instance.
         * @throws ValidationException if the email format is invalid.
         */
        public T email(String email) throws ValidationException {
            ValidationUtil.validateEmail(email);
            this.email = email.toLowerCase();
            return self();
        }

        /**
         * Sets, validates, and hashes the user's password.
         *
         * @param plainPassword the plain text password.
         * @return the builder instance.
         * @throws ValidationException if the password does not meet security
         *                             requirements.
         */
        public T password(String plainPassword) throws ValidationException {
            ValidationUtil.validatePassword(plainPassword);
            this.passwordHash = PasswordHasher.hash(plainPassword);
            return self();
        }

        /**
         * Validates that all mandatory fields are set before building the object.
         *
         * @throws ValidationException if any mandatory field is missing.
         */
        protected void validateMandatoryFields() throws ValidationException {
            if (email == null) {
                throw new ValidationException("Email is mandatory");
            }
            if (passwordHash == null) {
                throw new ValidationException("Password is mandatory");
            }
            if (userType == null) {
                throw new ValidationException("User type is mandatory");
            }
        }

        /**
         * Returns the builder instance itself.
         * <p>
         * This method is the key to the "self-typed" pattern, allowing fluent chaining
         * across inheritance hierarchies.
         *
         * @return the builder instance.
         */
        protected abstract T self();

        /**
         * Builds the final {@link User} object.
         *
         * @return the constructed {@link User} instance.
         * @throws ValidationException if validation fails.
         */
        public abstract User build() throws ValidationException;
    }
}
