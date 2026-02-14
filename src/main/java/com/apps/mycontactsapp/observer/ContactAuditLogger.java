package com.apps.mycontactsapp.observer;

import com.apps.mycontactsapp.model.Contact;
import java.time.LocalDateTime;

/**
 * Concrete Observer that logs contact deletions to the console (Audit Log).
 */
public class ContactAuditLogger implements ContactObserver {

    /**
     * Called when a contact is deleted.
     * Logs the deletion event.
     * 
     * @param contact the deleted contact.
     */
    @Override
    public void onContactDeleted(Contact contact) {
        System.out.println("[AUDIT LOG] Contact Deleted: " + contact.getName() +
                " (ID: " + contact.getId() + ")" +
                " at " + LocalDateTime.now());
    }


    /**
     * Called when a tag is added to a contact.
     * Logs the tagging event.
     * 
     * @param contact the contact.
     * @param tag     the added tag.
     */



    @Override
    public void onContactTagged(Contact contact, com.apps.mycontactsapp.model.Tag tag) {
        System.out.println("[AUDIT LOG] Tag Added: '" + tag.getName() + "' to " + contact.getName() +
                " at " + LocalDateTime.now());
    }

    /**
     * Called when a tag is removed from a contact.
     * Logs the untagging event.
     * 
     * @param contact the contact.
     * @param tag     the removed tag.
     */
    @Override
    public void onContactUntagged(Contact contact, com.apps.mycontactsapp.model.Tag tag) {
        System.out.println("[AUDIT LOG] Tag Removed: '" + tag.getName() + "' from " + contact.getName() +
                " at " + LocalDateTime.now());
    }

}
