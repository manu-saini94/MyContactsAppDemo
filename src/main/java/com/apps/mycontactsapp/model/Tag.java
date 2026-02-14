package com.apps.mycontactsapp.model;

import java.util.Objects;

/**
 * Represents a Tag for organizing contacts.
 * specific to UC-11.
 * implemented as an Immutable Flyweight object.
 */
public class Tag {
    private final String name;

    /**
     * Constructor. Should primarily be used by TagFactory to ensure Flyweight
     * pattern.
     * 
     * @param name the name of the tag.
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Gets the tag name.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks equality based on tag name.
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
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    /**
     * Generates a hash code based on tag name.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Returns the string representation (the tag name).
     * 
     * @return the tag name.
     */
    @Override
    public String toString() {
        return name;
    }
}
