package com.apps.mycontactsapp.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Association class representing the link between a Contact and a Tag.
 * Tracks when the tag was assigned.
 * Implements UC-12 specific requirement for an Association Class.
 */
public class ContactTag {
    private final Contact contact;
    private final Tag tag;
    private final LocalDateTime assignedAt;

    public ContactTag(Contact contact, Tag tag) {
        this.contact = contact;
        this.tag = tag;
        this.assignedAt = LocalDateTime.now();
    }

    /**
     * Gets the associated Contact.
     * 
     * @return the Contact.
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Gets the associated Tag.
     * 
     * @return the Tag.
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Gets the timestamp when the tag was assigned.
     * 
     * @return the LocalDateTime of assignment.
     */
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    /**
     * Checks equality based on Contact and Tag.
     * 
     * @param o the object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ContactTag that = (ContactTag) o;
        // Identity is based on the Contact and the Tag
        return Objects.equals(contact, that.contact) && Objects.equals(tag, that.tag);
    }

    /**
     * Generates a hash code based on Contact and Tag.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(contact, tag);
    }

    /**
     * Returns a string representation of the ContactTag.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {
        return tag.getName() + " (assigned: " + assignedAt + ")";
    }
}
