package com.apps.mycontactsapp.service.impl;

import java.util.List;
import java.util.UUID;

import com.apps.mycontactsapp.composite.ContactGroup;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.composite.ContactComponent;
import com.apps.mycontactsapp.model.User;
import com.apps.mycontactsapp.model.UserType;
import com.apps.mycontactsapp.repository.ContactGroupRepository;
import com.apps.mycontactsapp.service.ContactGroupService;

public class ContactGroupServiceImpl implements ContactGroupService {

    private final ContactGroupRepository contactGroupRepository;

    public ContactGroupServiceImpl(ContactGroupRepository contactGroupRepository) {
        this.contactGroupRepository = contactGroupRepository;
    }

    /**
     * {@inheritDoc}
     *
     * @param user      the user creating the group.
     * @param groupName the name of the group.
     * @param contacts  the initial contacts to add to the group.
     * @return the created ContactGroup.
     * @throws ValidationException if validation fails.
     */
    @Override
    public ContactGroup createGroup(User user, String groupName, List<ContactComponent> contacts)
            throws ValidationException {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new ValidationException("Group name cannot be empty.");
        }

        // Ensure uniqueness of group name for THIS user?
        // Or globally? Simpler to just create it for now,
        // but let's check if this user already has a group with this name to avoid
        // confusion.

        // Since we can't easily access name, we skip check or iterate.
        // Let's iterate.
        // Assuming groupName is accessible (package-private or we need getter if it was
        // private).
        // ContactGroup has no getter for name in the snippet provided earlier?
        // Ah, it has `getDetails` which dumps it.
        // We should probably add a getName() to ContactGroup, but strictly following
        // the plan:
        // We will just create it.

        ContactGroup group = new ContactGroup(user.getId(), groupName);
        if (contacts != null) {
            for (ContactComponent c : contacts) {
                // We could check for duplicates here too, but usually creation with list
                // implies
                // unique selection from UI.
                // However, let's be safe or just add them.
                // Since this is "create", we assume the list passed is what the user wants.
                // If the input list has duplicates, they get added.
                // But generally, the user selects "all" or specific indices.
                // Let's just add them.
                group.add(c);
            }
        }
        contactGroupRepository.save(group);
        return group;
    }

    /**
     * {@inheritDoc}
     *
     * @param user the user requesting the groups.
     * @return a list of accessible contact groups.
     */
    @Override
    public List<ContactGroup> getGroupsForUser(User user) {
        if (user.getUserType() == UserType.ADMIN) {
            return contactGroupRepository.findAll();
        } else {
            return contactGroupRepository.findByUserId(user.getId());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param user    the user requesting the details.
     * @param groupId the ID of the group.
     * @return the group details string.
     * @throws ValidationException if group not found or access denied.
     */
    @Override
    public String getGroupDetails(User user, UUID groupId) throws ValidationException {
        ContactGroup group = getGroupWithAccessCheck(user, groupId);
        return group.getDetails();
    }

    /**
     * {@inheritDoc}
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param tag     the tag to add.
     * @throws ValidationException if access denied.
     */
    @Override
    public void addTagToGroup(User user, UUID groupId, String tag) throws ValidationException {
        ContactGroup group = getGroupWithAccessCheck(user, groupId);
        if (tag == null || tag.trim().isEmpty()) {
            throw new ValidationException("Tag cannot be empty.");
        }
        group.addTag(tag);
        // Persist changes? In-memory updates map value reference, but good practice to
        // save.
        contactGroupRepository.save(group);
    }

    /**
     * {@inheritDoc}
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param tag     the tag to remove.
     * @throws ValidationException if access denied.
     */
    @Override
    public void removeTagFromGroup(User user, UUID groupId, String tag) throws ValidationException {
        ContactGroup group = getGroupWithAccessCheck(user, groupId);
        group.removeTag(tag);
        contactGroupRepository.save(group);
    }

    /**
     * {@inheritDoc}
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @throws ValidationException if access denied.
     */
    @Override
    public void deleteGroup(User user, UUID groupId) throws ValidationException {
        ContactGroup group = getGroupWithAccessCheck(user, groupId);
        // Soft delete all members
        group.delete();
        // Then remove the group itself from repository
        contactGroupRepository.delete(group);
    }

    /**
     * {@inheritDoc}
     *
     * @param groupId the ID of the group to find.
     * @return the ContactGroup if found, or null/exception based on implementation.
     */
    @Override
    public ContactGroup findGroupById(UUID groupId) {
        return contactGroupRepository.findById(groupId).orElse(null);
    }

    /**
     * Helper method to retrieve a group and enforce ACL.
     * 
     * @param user    the user requesting the group.
     * @param groupId the ID of the group.
     * @return the ContactGroup if found and accessible.
     * @throws ValidationException if group not found or access denied.
     */
    private ContactGroup getGroupWithAccessCheck(User user, UUID groupId) throws ValidationException {
        ContactGroup group = contactGroupRepository.findById(groupId)
                .orElseThrow(() -> new ValidationException("Group not found."));

        // ACL Check
        boolean isOwner = group.getUserId().equals(user.getId());
        boolean isAdmin = user.getUserType() == UserType.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ValidationException("Access Denied: You do not have permission to access this group.");
        }
        return group;
    }

    /**
     * {@inheritDoc}
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param contact the contact to add.
     * @throws ValidationException if access denied.
     */
    @Override
    public void addContactToGroup(User user, UUID groupId, ContactComponent contact) throws ValidationException {
        ContactGroup group = getGroupWithAccessCheck(user, groupId);

        // Check for duplicates
        boolean alreadyExists = group.getComponents().stream()
                .anyMatch(c -> c.getId().equals(contact.getId()));

        if (alreadyExists) {
            throw new ValidationException("Contact is already present in the group.");
        }

        // Add contact
        group.add(contact);
        contactGroupRepository.save(group);
    }

    /**
     * {@inheritDoc}
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param contact the contact to remove.
     * @throws ValidationException if access denied.
     */
    @Override
    public void removeContactFromGroup(User user, UUID groupId, ContactComponent contact) throws ValidationException {
        ContactGroup group = getGroupWithAccessCheck(user, groupId);
        // Remove contact
        group.remove(contact);
        contactGroupRepository.save(group);
    }
}
