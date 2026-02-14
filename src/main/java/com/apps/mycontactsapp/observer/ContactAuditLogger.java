package com.apps.mycontactsapp.observer;

import com.apps.mycontactsapp.model.Contact;
import java.time.LocalDateTime;

/**
 * Concrete Observer that logs contact deletions to the console (Audit Log).
 */
public class ContactAuditLogger implements ContactObserver {

    @Override
    public void onContactDeleted(Contact contact) {
        System.out.println("[AUDIT LOG] Contact Deleted: " + contact.getName() +
                " (ID: " + contact.getId() + ")" +
                " at " + LocalDateTime.now());
    }

    @Override
    public void onContactTagged(Contact contact, com.apps.mycontactsapp.model.Tag tag) {
        System.out.println("[AUDIT LOG] Tag Added: '" + tag.getName() + "' to " + contact.getName() +
                " at " + LocalDateTime.now());
    }

    @Override
    public void onContactUntagged(Contact contact, com.apps.mycontactsapp.model.Tag tag) {
        System.out.println("[AUDIT LOG] Tag Removed: '" + tag.getName() + "' from " + contact.getName() +
                " at " + LocalDateTime.now());
    }
}
