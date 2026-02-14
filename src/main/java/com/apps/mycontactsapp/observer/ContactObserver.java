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

    /**
     * Called when a tag is added to a contact.
     * 
     * @param contact the contact.
     * @param tag     the tag added.
     */
    default void onContactTagged(Contact contact, com.apps.mycontactsapp.model.Tag tag) {
    }

    /**
     * Called when a tag is removed from a contact.
     * 
     * @param contact the contact.
     * @param tag     the tag removed.
     */
    default void onContactUntagged(Contact contact, com.apps.mycontactsapp.model.Tag tag) {
    }
}
