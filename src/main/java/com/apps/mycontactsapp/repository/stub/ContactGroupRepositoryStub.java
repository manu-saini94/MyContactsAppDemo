package com.apps.mycontactsapp.repository.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.apps.mycontactsapp.composite.ContactGroup;
import com.apps.mycontactsapp.repository.ContactGroupRepository;

/**
 * In-memory implementation of ContactGroupRepository.
 */
public class ContactGroupRepositoryStub implements ContactGroupRepository {

    private final Map<UUID, ContactGroup> storage = new HashMap<>();

    /**
     * Saves or updates a contact group.
     * 
     * @param contactGroup the contact group to save.
     */
    @Override
    public void save(ContactGroup contactGroup) {
        storage.put(contactGroup.getId(), contactGroup);
    }

    /**
     * Finds a contact group by its ID.
     * 
     * @param id the UUID of the group.
     * @return an Optional containing the group if found.
     */
    @Override
    public Optional<ContactGroup> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Finds all contact groups for a specific user.
     * 
     * @param userId the ID of the user.
     * @return a list of contact groups.
     */
    @Override
    public List<ContactGroup> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(group -> group.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Finds all contact groups in the repository.
     * 
     * @return a list of all contact groups.
     */
    @Override
    public List<ContactGroup> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Deletes a contact group.
     * 
     * @param contactGroup the group to delete.
     */
    @Override
    public void delete(ContactGroup contactGroup) {
        storage.remove(contactGroup.getId());
    }
}
