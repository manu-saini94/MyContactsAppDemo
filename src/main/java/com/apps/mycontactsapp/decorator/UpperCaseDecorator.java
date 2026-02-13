package com.apps.mycontactsapp.decorator;

import com.apps.mycontactsapp.composite.ContactComponent;

/**
 * Decorator that converts contact details to uppercase.
 */
public class UpperCaseDecorator extends BaseContactDecorator {

    /**
     * Constructor.
     *
     * @param wrappedContact the ContactComponent instance to decorate.
     */
    public UpperCaseDecorator(ContactComponent wrappedContact) {
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
