package com.apps.mycontactsapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.apps.mycontactsapp.model.Contact;

/**
 * Repository interface for Contact entities.
 */
public interface ContactRepository {
    /**
     * Saves a contact (create or update).
     * 
     * @param contact the contact to save.
     */
    void save(Contact contact);

    /**
     * Finds a contact by its unique ID.
     * 
     * @param id the UUID of the contact.
     * @return an Optional containing the contact if found.
     */
    Optional<Contact> findById(UUID id);

    /**
     * Retrieves all contacts.
     * 
     * @return a list of all contacts in the repository.
     */
    List<Contact> findAll();

    /**
     * Retrieves all contacts with option to include inactive.
     * 
     * @param includeInactive whether to include soft-deleted contacts.
     * @return a list of contacts.
     */
    List<Contact> findAll(boolean includeInactive);

    /**
     * Finds contacts by user ID (owner).
     * 
     * @param userId the ID of the owner.
     * @return a list of contacts owned by the user.
     */
    List<Contact> findByUserId(Long userId);

    /**
     * Finds contacts by user ID with option to include inactive.
     * 
     * @param userId          the ID of the owner.
     * @param includeInactive whether to include soft-deleted contacts.
     * @return a list of contacts owned by the user.
     */
    List<Contact> findByUserId(Long userId, boolean includeInactive);

    /**
     * Deletes a contact (soft delete usually, depends on implementation).
     * 
     * @param contact the contact to delete.
     */
    void delete(Contact contact);

    /**
     * Permanently deletes a contact.
     * 
     * @param contact the contact to hard delete.
     */
    void hardDelete(Contact contact);

    /**
     * Deletes all contacts for a specific user.
     * 
     * @param userId the ID of the user.
     */
    void deleteByUserId(Long userId);
}
