package com.apps.mycontactsapp.specification;

/**
 * Specification implementation for logical NOT operation.
 * 
 * @param <T> the type of object.
 */
public class NotSpecification<T> implements Specification<T> {

    private final Specification<T> spec;

    /**
     * Constructs a new NotSpecification.
     * 
     * @param spec the specification to negate.
     */
    public NotSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    /**
     * {@inheritDoc}
     * Returns true if the wrapped specification is NOT satisfied.
     */
    @Override
    public boolean isSatisfiedBy(T t) {
        return !spec.isSatisfiedBy(t);
    }
}
