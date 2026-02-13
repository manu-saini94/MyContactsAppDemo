package com.apps.mycontactsapp.observer;

import com.apps.mycontactsapp.model.Contact;

/**
 * Observer interface for receiving notifications about Contact events.
 * 
 * Design Pattern: Observer Pattern
 */
public interface ContactObserver {
    /**
     * Called when a contact is deleted (soft deleted).
     * 
     * @param contact the contact that was deleted.
     */
    void onContactDeleted(Contact contact);
}
