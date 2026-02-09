package com.apps.mycontactsapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.apps.mycontactsapp.model.Contact;

/**
 * Repository interface for Contact entities.
 */
public interface ContactRepository {
    void save(Contact contact);

    Optional<Contact> findById(UUID id);

    List<Contact> findAll();
}
