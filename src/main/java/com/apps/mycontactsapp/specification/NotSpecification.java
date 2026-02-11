package com.apps.mycontactsapp.specification;

/**
 * Specification implementation for logical NOT operation.
 * 
 * @param <T> the type of object.
 */
public class NotSpecification<T> implements Specification<T> {

    private final Specification<T> spec;

    public NotSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public boolean isSatisfiedBy(T t) {
        return !spec.isSatisfiedBy(t);
    }
}
