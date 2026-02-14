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

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
