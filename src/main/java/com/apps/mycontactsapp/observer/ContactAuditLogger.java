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
}
