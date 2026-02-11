package com.apps.mycontactsapp.decorator;

import com.apps.mycontactsapp.composite.ContactComponent;

/**
 * Abstract decorator for ContactComponent.
 * Implements the Decorator Pattern to allow dynamic behavior addition.
 */
public abstract class BaseContactDecorator implements ContactComponent {
    protected final ContactComponent wrappedContact;

    /**
     * Constructor accepting the component to be decorated.
     *
     * @param wrappedContact the ContactComponent instance to decorate.
     */
    public BaseContactDecorator(ContactComponent wrappedContact) {
        this.wrappedContact = wrappedContact;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * {@inheritDoc}
     * Delegates to the wrapped component.
     * 
     * @return the details of the wrapped component.
     */
    @Override
    public String getDetails() {
        return wrappedContact.getDetails();
    }

    /**
     * {@inheritDoc}
     * Delegates to the wrapped component.
     * 
     * @param tag the tag to add.
     */
    @Override
    public void addTag(String tag) {
        wrappedContact.addTag(tag);
    }

    /**
     * {@inheritDoc}
     * Delegates to the wrapped component.
     * 
     * @param tag the tag to remove.
     */
    @Override
    public void removeTag(String tag) {
        wrappedContact.removeTag(tag);
    }

    /**
     * {@inheritDoc}
     * Delegates to the wrapped component.
     */
    @Override
    public void delete() {
        wrappedContact.delete();
    }

    /**
     * {@inheritDoc}
     * Delegates to the wrapped component.
     * 
     * @return the UUID of the wrapped component.
     */
    @Override
    public java.util.UUID getId() {
        return wrappedContact.getId();
    }
}
