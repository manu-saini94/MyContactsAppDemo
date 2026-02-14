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

    public Contact getContact() {
        return contact;
    }

    public Tag getTag() {
        return tag;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(contact, tag);
    }

    @Override
    public String toString() {
        return tag.getName() + " (assigned: " + assignedAt + ")";
    }
}
