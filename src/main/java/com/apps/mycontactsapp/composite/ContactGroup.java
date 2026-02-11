package com.apps.mycontactsapp.composite;

import java.util.ArrayList;
import java.util.UUID;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Composite class representing a group of ContactComponents.
 * Allows treating a group of contacts as a single object.
 */
public class ContactGroup implements ContactComponent {
    private final UUID id;
    private final Long userId;
    private final List<ContactComponent> components = new ArrayList<>();
    private String groupName;

    /**
     * Creates a new ContactGroup.
     * 
     * @param userId    the ID of the user who owns the group.
     * @param groupName the name of the group.
     */
    public ContactGroup(Long userId, String groupName) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.groupName = groupName;
    }

    /**
     * Gets the name of the group.
     * 
     * @return the group name.
     */
    public String getName() {
        return groupName;
    }

    /**
     * Gets the unique identifier of the group.
     * 
     * @return the UUID of the group.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the user ID of the group owner.
     * 
     * @return the owner's user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Adds a component (contact or another group) to this group.
     * 
     * @param component the component to add.
     */
    public void add(ContactComponent component) {
        components.add(component);
    }

    /**
     * Removes a component from this group.
     * 
     * @param component the component to remove.
     */
    public void remove(ContactComponent component) {
        components.remove(component);
    }

    /**
     * Removes all components from this group.
     */
    public void clear() {
        components.clear();
    }

    /**
     * Gets a shallow copy of the components in this group.
     * 
     * @return a list of ContactComponents.
     */
    public List<ContactComponent> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * Gets the details of the group and all its components.
     * 
     * @return a formatted string containing group details.
     */
    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Group: ").append(groupName).append("\n");
        sb.append("------------------------\n");
        // Use Java 8 Stream to aggregate details
        String details = components.stream()
                .map(ContactComponent::getDetails)
                .collect(Collectors.joining("\n------------------------\n"));
        sb.append(details);
        return sb.toString();
    }

    /**
     * Adds a tag to all components in the group.
     * 
     * @param tag the tag to add.
     */
    @Override
    public void addTag(String tag) {
        // Apply tag to all components in the group
        components.forEach(component -> component.addTag(tag));
    }

    /**
     * Removes a tag from all components in the group.
     * 
     * @param tag the tag to remove.
     */
    @Override
    public void removeTag(String tag) {
        // Remove tag from all components in the group
        components.forEach(component -> component.removeTag(tag));
    }

    /**
     * Soft deletes all components in the group.
     */
    @Override
    public void delete() {
        // Delete all components (Soft Delete)
        components.forEach(ContactComponent::delete);
    }

    /**
     * Gets a set of all unique tags in the group.
     * 
     * @return a Set of all tags present in the group's components.
     */
    @Override
    public Set<String> getTags() {
        // Aggregate all unique tags from all components
        return components.stream()
                .flatMap(c -> c.getTags().stream())
                .collect(Collectors.toSet());
    }
}
