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
        return new ArrayList<>(contacts.values());
    }

    @Override
    public List<Contact> findByUserId(Long userId) {
        List<Contact> userContacts = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            Long contactUserId = contact.getUserId();
            if (contactUserId != null && contactUserId.equals(userId)) {
                userContacts.add(contact);
            }
        }
        return userContacts;
    }
}
