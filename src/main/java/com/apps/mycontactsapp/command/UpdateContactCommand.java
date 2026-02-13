package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.model.ContactMemento;

/**
 * Command to update a contact's information.
 * Supports Undo/Redo using Memento pattern.
 */
public class UpdateContactCommand implements Command {
    private final Contact contact;
    private final ContactMemento oldState;
    private ContactMemento newState;
    private final Runnable updateAction;

    public UpdateContactCommand(Contact contact, Runnable updateAction) {
        this.contact = contact;
        this.oldState = contact.createMemento();
        this.updateAction = updateAction;
    }

    @Override
    public void execute() throws ValidationException {
        if (newState == null) {
            // First execution
            updateAction.run();
            // Capture the state after update for Redo
            this.newState = contact.createMemento();
        } else {
            // Redo: restore the new state
            contact.restore(newState);
        }
    }

    @Override
    public void undo() {
        contact.restore(oldState);
    }
}
