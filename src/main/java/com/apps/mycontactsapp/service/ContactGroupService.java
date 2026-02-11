package com.apps.mycontactsapp.service;

import java.util.List;
import java.util.UUID;

import com.apps.mycontactsapp.composite.ContactGroup;
import com.apps.mycontactsapp.exceptions.ValidationException;
import com.apps.mycontactsapp.composite.ContactComponent;
import com.apps.mycontactsapp.model.User;

/**
 * Service interface for managing Contact Groups.
 * Enforces business logic and Access Control (ACL).
 */
public interface ContactGroupService {

    /**
     * Creates a new contact group for a user.
     *
     * @param user      the user creating the group.
     * @param groupName the name of the group.
     * @param contacts  the initial contacts to add to the group.
     * @return the created ContactGroup.
     * @throws ValidationException if validation fails.
     */
    ContactGroup createGroup(User user, String groupName, List<ContactComponent> contacts) throws ValidationException;

    /**
     * Retrieves all accessible groups for a user.
     * Rules:
     * - ADMIN: Returns ALL groups in the system.
     * - OTHERS: Returns only groups owned by the user.
     *
     * @param user the user requesting the groups.
     * @return a list of accessible contact groups.
     */
    List<ContactGroup> getGroupsForUser(User user);

    /**
     * Gets details of a specific group.
     *
     * @param user    the user requesting the details.
     * @param groupId the ID of the group.
     * @return the group details string.
     * @throws ValidationException if group not found or access denied.
     */
    String getGroupDetails(User user, UUID groupId) throws ValidationException;

    /**
     * Adds a tag to a group.
     * Admin or Owner only.
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param tag     the tag to add.
     * @throws ValidationException if access denied.
     */
    void addTagToGroup(User user, UUID groupId, String tag) throws ValidationException;

    /**
     * Removes a tag from a group.
     * Admin or Owner only.
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param tag     the tag to remove.
     * @throws ValidationException if access denied.
     */
    void removeTagFromGroup(User user, UUID groupId, String tag) throws ValidationException;

    /**
     * Deletes a group.
     * Admin or Owner only.
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @throws ValidationException if access denied.
     */
    void deleteGroup(User user, UUID groupId) throws ValidationException;

    /**
     * Helper to find a group by ID.
     * Sometimes needed for internal logic or selecting a group object directly in
     * UI flow.
     * 
     * @param groupId the ID of the group to find.
     * @return the ContactGroup if found, or null/exception based on implementation.
     */
    ContactGroup findGroupById(UUID groupId);

    /**
     * Adds a contact to a group.
     * Admin or Owner only.
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param contact the contact to add.
     * @throws ValidationException if access denied.
     */
    void addContactToGroup(User user, UUID groupId, ContactComponent contact) throws ValidationException;

    /**
     * Removes a contact from a group.
     * Admin or Owner only.
     *
     * @param user    the user performing the action.
     * @param groupId the ID of the group.
     * @param contact the contact to remove.
     * @throws ValidationException if access denied.
     */
    void removeContactFromGroup(User user, UUID groupId, ContactComponent contact) throws ValidationException;
}
