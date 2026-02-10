package com.apps.mycontactsapp.decorator;

import com.apps.mycontactsapp.model.ContactDisplay;

/**
 * Abstract decorator for ContactDisplay.
 * Implements the Decorator Pattern to allow dynamic behavior addition.
 */
public abstract class BaseContactDecorator implements ContactDisplay {
    protected final ContactDisplay wrappedContact;

    /**
     * Constructor accepting the component to be decorated.
     *
     * @param wrappedContact the ContactDisplay instance to decorate.
     */
    public BaseContactDecorator(ContactDisplay wrappedContact) {
        this.wrappedContact = wrappedContact;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetails() {
        return wrappedContact.getDetails();
    }
}
