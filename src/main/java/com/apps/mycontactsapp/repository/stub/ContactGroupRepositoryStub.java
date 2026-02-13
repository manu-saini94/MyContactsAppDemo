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

    @Override
    public void save(ContactGroup contactGroup) {
        storage.put(contactGroup.getId(), contactGroup);
    }

    @Override
    public Optional<ContactGroup> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ContactGroup> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(group -> group.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ContactGroup> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(ContactGroup contactGroup) {
        storage.remove(contactGroup.getId());
    }
}
