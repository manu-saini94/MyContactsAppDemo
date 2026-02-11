package com.apps.mycontactsapp.model;

/**
 * Memento class to store the state of a Contact.
 * Implements the Memento Pattern.
 */
public class ContactMemento {
    private final Contact stateSnapshot;

    /**
     * Constructs a new ContactMemento.
     * 
     * @param contact the contact to save state from.
     */
    public ContactMemento(Contact contact) {
        this.stateSnapshot = contact.copy();
    }

    /**
     * Gets the saved state snapshot.
     * 
     * @return the contact state.
     */
    public Contact getStateSnapshot() {
        return stateSnapshot;
    }
}
