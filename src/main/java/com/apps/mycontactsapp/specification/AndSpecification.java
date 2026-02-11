package com.apps.mycontactsapp.specification;

/**
 * Specification implementation for logical AND operation.
 * 
 * @param <T> the type of object.
 */
public class AndSpecification<T> implements Specification<T> {

    private final Specification<T> first;
    private final Specification<T> second;

    /**
     * Constructs a new AndSpecification.
     * 
     * @param first  the first specification.
     * @param second the second specification.
     */
    public AndSpecification(Specification<T> first, Specification<T> second) {
        this.first = first;
        this.second = second;
    }

    /**
     * {@inheritDoc}
     * Returns true if both specifications are satisfied.
     */
    @Override
    public boolean isSatisfiedBy(T t) {
        return first.isSatisfiedBy(t) && second.isSatisfiedBy(t);
    }
}
