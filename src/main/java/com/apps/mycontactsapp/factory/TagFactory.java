package com.apps.mycontactsapp.factory;

import com.apps.mycontactsapp.model.Tag;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for managing Tag instances.
 * Implements the Flyweight Pattern to ensure unique Tag objects are shared.
 */
public class TagFactory {
    // Pool of flyweight Tag objects
    private static final Map<String, Tag> tagPool = new HashMap<>();

    /**
     * Returns a Tag instance for the given name.
     * If the tag already exists, returns the existing instance.
     * Otherwise, creates a new instance, stores it in the pool, and returns it.
     *
     * @param name the name of the tag
     * @return the Flyweight Tag instance
     */
    public static Tag getTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        String normalizedKey = name.trim(); // simple normalization

        // Check if tag exists in pool
        if (!tagPool.containsKey(normalizedKey)) {
            tagPool.put(normalizedKey, new Tag(normalizedKey));
        }

        return tagPool.get(normalizedKey);
    }

    /**
     * Clears the tag pool. Useful for testing or constraints.
     */
    public static void clear() {
        tagPool.clear();
    }

    /**
     * Returns the size of the pool. Useful for verification.
     */
    public static int getPoolSize() {
        return tagPool.size();
    }

    /**
     * Returns a set of all tags currently in the pool.
     * 
     * @return a Set of Tag objects.
     */
    public static java.util.Set<Tag> getAllTags() {
        return new java.util.HashSet<>(tagPool.values());
    }
}
