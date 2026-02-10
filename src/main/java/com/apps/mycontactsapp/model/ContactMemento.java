package com.apps.mycontactsapp.model;

/**
 * Memento class to store the state of a Contact.
 * Implements the Memento Pattern.
 */
public class ContactMemento {
    private final Contact stateSnapshot;

    public ContactMemento(Contact contact) {
        this.stateSnapshot = contact.copy();
    }

    public Contact getStateSnapshot() {
        return stateSnapshot;
    }
}
