package com.apps.mycontactsapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.apps.mycontactsapp.composite.ContactGroup;

/**
 * Repository interface for managing ContactGroup persistence.
 */
public interface ContactGroupRepository {

    /**
     * Saves a contact group.
     *
     * @param contactGroup the group to save.
     */
    void save(ContactGroup contactGroup);

    /**
     * Finds a group by its ID.
     *
     * @param id the UUID of the group.
     * @return an Optional containing the group if found.
     */
    Optional<ContactGroup> findById(UUID id);

    /**
     * Finds all groups belonging to a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of contact groups.
     */
    List<ContactGroup> findByUserId(Long userId);

    /**
     * Finds all groups in the system.
     *
     * @return a list of all contact groups.
     */
    List<ContactGroup> findAll();

    /**
     * Deletes a group.
     *
     * @param contactGroup the group to delete.
     */
    void delete(ContactGroup contactGroup);
}
