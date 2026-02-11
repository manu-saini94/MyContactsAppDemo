package com.apps.mycontactsapp.composite;

import java.util.Set;

/**
 * Component interface for the Composite Pattern.
 * Defines operations that can be performed on individual contacts or groups of
 * contacts.
 * Also used by Decorators.
 */
public interface ContactComponent {

    /**
     * Gets the formatted details of the contact.
     *
     * @return the contact details as a string.
     */
    String getDetails();

    /**
     * Gets the unique identifier of the component.
     * 
     * @return the unique UUID of the component.
     */
    java.util.UUID getId();

    /**
     * Adds a tag to the contact or group of contacts.
     * 
     * @param tag the tag to add.
     */
    void addTag(String tag);

    /**
     * Removes a tag from the contact or group of contacts.
     * 
     * @param tag the tag to remove.
     */
    void removeTag(String tag);

    /**
     * Soft deletes the contact or group of contacts.
     */
    void delete();

    /**
     * Optional: Gets tags associated with this component.
     * Implementing classes may return an empty set or aggregated tags.
     * 
     * @return a Set of strings representing the tags.
     */
    default Set<String> getTags() {
        return java.util.Collections.emptySet();
    }
}
