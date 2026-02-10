package com.apps.mycontactsapp.repository.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.apps.mycontactsapp.model.Contact;
import com.apps.mycontactsapp.repository.ContactRepository;

/**
 * In-memory implementation of ContactRepository (Stub).
 */
public class ContactRepositoryStub implements ContactRepository {
    private final Map<UUID, Contact> contacts = new HashMap<>();

    @Override
    public void save(Contact contact) {
        contacts.put(contact.getId(), contact);
    }

    @Override
    public Optional<Contact> findById(UUID id) {
        return Optional.ofNullable(contacts.get(id));
    }

    @Override
    public List<Contact> findAll() {
        return findAll(false);
    }

    @Override
    public List<Contact> findAll(boolean includeInactive) {
        List<Contact> result = new ArrayList<>();
        for (Contact c : contacts.values()) {
            if (includeInactive || c.isActive()) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public List<Contact> findByUserId(Long userId) {
        return findByUserId(userId, false);
    }

    @Override
    public List<Contact> findByUserId(Long userId, boolean includeInactive) {
        List<Contact> result = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            Long contactUserId = contact.getUserId();
            if (contactUserId != null && contactUserId.equals(userId)) {
                if (includeInactive || contact.isActive()) {
                    result.add(contact);
                }
            }
        }
        return result;
    }

    @Override
    public void delete(Contact contact) {
        if (contact != null) {
            contact.setActive(false);
            // In a real DB, we would save the updated state.
            // Since this is in-memory and 'contact' is a reference to the object in the
            // map,
            // setting it to false updates it in the map directly.
            // But to be safe/consistent with "save", we can put it back.
            contacts.put(contact.getId(), contact);
        }
    }

    @Override
    public void hardDelete(Contact contact) {
        if (contact != null) {
            contacts.remove(contact.getId());
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        // Collect IDs to remove to avoid ConcurrentModificationException
        List<UUID> idsToRemove = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            if (userId.equals(contact.getUserId())) {
                idsToRemove.add(contact.getId());
            }
        }
        for (UUID id : idsToRemove) {
            contacts.remove(id);
        }
    }
}
