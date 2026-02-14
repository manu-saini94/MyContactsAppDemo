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

    /**
     * Saves or updates a contact.
     * 
     * @param contact the contact to save.
     */
    @Override
    public void save(Contact contact) {
        contacts.put(contact.getId(), contact);
    }

    /**
     * Finds a contact by its ID.
     * 
     * @param id the UUID of the contact.
     * @return an Optional containing the contact if found.
     */
    @Override
    public Optional<Contact> findById(UUID id) {
        return Optional.ofNullable(contacts.get(id));
    }

    /**
     * Finds all contacts.
     * 
     * @return a list of all contacts.
     */
    @Override
    public List<Contact> findAll() {
        return findAll(false);
    }

    /**
     * Finds all contacts, optionally including inactive ones.
     * 
     * @param includeInactive true to include inactive contacts.
     * @return a list of contacts.
     */
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

    /**
     * Finds all contacts for a specific user.
     * 
     * @param userId the user ID.
     * @return a list of contacts.
     */
    @Override
    public List<Contact> findByUserId(Long userId) {
        return findByUserId(userId, false);
    }

    /**
     * Finds all contacts for a specific user, optionally including inactive ones.
     * 
     * @param userId          the user ID.
     * @param includeInactive true to include inactive contacts.
     * @return a list of contacts.
     */
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

    /**
     * Soft deletes a contact.
     * 
     * @param contact the contact to delete.
     */
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

    /**
     * Hard deletes a contact (permanent removal).
     * 
     * @param contact the contact to hard delete.
     */
    @Override
    public void hardDelete(Contact contact) {
        if (contact != null) {
            contacts.remove(contact.getId());
        }
    }

    /**
     * Deletes all contacts belonging to a specific user.
     * 
     * @param userId the user ID.
     */
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
