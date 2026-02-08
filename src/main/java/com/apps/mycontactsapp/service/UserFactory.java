package com.apps.mycontactsapp.service;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.FreeUser;
import com.apps.mycontactsapp.model.PremiumUser;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.model.UserType;

/**
 * Factory class for creating {@link User} instances.
 * <p>
 * <b>Design Patterns:</b>
 * <ul>
 * <li><b>Factory Pattern:</b> This class implements the Factory Pattern to
 * encapsulate the logic
 * of creating different types of {@link User} objects (e.g., Free, Premium)
 * based on input criteria.
 * It decouples the client code from the specific classes being
 * instantiated.</li>
 * </ul>
 */
public class UserFactory {

    /**
     * Private constructor to prevent instantiation of this utility/factory class.
     */
    private UserFactory() {

    }

    /**
     * Creates a {@link User} instance based on the provided {@link UserType}.
     *
     * @param userType       the type of user to create (e.g., FREE, PREMIUM).
     * @param id             the unique identifier for the user (can be null for new
     *                       users).
     * @param name           the user's name.
     * @param email          the user's email address.
     * @param hashedPassword the user's password (already hashed or to be hashed by
     *                       builder).
     *                       <i>Note: The Builder currently hashes the password, so
     *                       this should be the plain password if calling Builder
     *                       key methods.</i>
     * @return a concrete implementation of {@link User} (e.g., {@link FreeUser} or
     *         {@link PremiumUser}).
     * @throws ValidationException if the user type is unsupported or validation
     *                             fails during build.
     */
    public static User createUser(
            UserType userType,
            Long id,
            String name,
            String email,
            String hashedPassword) throws ValidationException {

        switch (userType) {
            case FREE:
                return new FreeUser.Builder()
                        .id(id)
                        .name(name)
                        .email(email)
                        .password(hashedPassword)
                        .build();

            case PREMIUM:
                return new PremiumUser.Builder()
                        .id(id)
                        .name(name)
                        .email(email)
                        .password(hashedPassword)
                        .build();

            default:
                throw new ValidationException("Unsupported user type");
        }
    }
}
