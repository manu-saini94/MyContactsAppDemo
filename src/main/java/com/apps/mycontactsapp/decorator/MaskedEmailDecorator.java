package com.apps.mycontactsapp.decorator;

import com.apps.mycontactsapp.composite.ContactComponent;

/**
 * Decorator that masks email addresses in the contact details.
 * Example: "user@example.com" -> "u***@example.com"
 */
public class MaskedEmailDecorator extends BaseContactDecorator {

    /**
     * Constructor.
     *
     * @param wrappedContact the ContactComponent instance to decorate.
     */
    public MaskedEmailDecorator(ContactComponent wrappedContact) {
        super(wrappedContact);
    }

    /**
     * {@inheritDoc}
     * Masks email addresses found in the details string.
     */
    @Override
    public String getDetails() {
        String details = super.getDetails();
        // Regex to find emails and mask them.
        // Simplified masking logic for demonstration within the string block.
        // A robust solution might match the email pattern and replace.
        return details.replaceAll("([a-zA-Z0-9])[^@]+@([a-zA-Z0-9.-]+)", "$1***@$2");
    }
}
