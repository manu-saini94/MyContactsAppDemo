package com.apps.mycontactsapp.decorator;

import com.apps.mycontactsapp.model.ContactDisplay;

/**
 * Decorator that converts contact details to uppercase.
 */
public class UpperCaseDecorator extends BaseContactDecorator {

    /**
     * Constructor.
     *
     * @param wrappedContact the ContactDisplay instance to decorate.
     */
    public UpperCaseDecorator(ContactDisplay wrappedContact) {
        super(wrappedContact);
    }

    /**
     * {@inheritDoc}
     * Returns details in uppercase.
     */
    @Override
    public String getDetails() {
        return super.getDetails().toUpperCase();
    }
}
