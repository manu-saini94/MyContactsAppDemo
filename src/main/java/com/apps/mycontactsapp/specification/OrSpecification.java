package com.apps.mycontactsapp.specification;

/**
 * Specification implementation for logical OR operation.
 * 
 * @param <T> the type of object.
 */
public class OrSpecification<T> implements Specification<T> {

    private final Specification<T> first;
    private final Specification<T> second;

    /**
     * Constructs a new OrSpecification.
     * 
     * @param first  the first specification.
     * @param second the second specification.
     */
    public OrSpecification(Specification<T> first, Specification<T> second) {
        this.first = first;
        this.second = second;
    }

    /**
     * {@inheritDoc}
     * Returns true if either specification is satisfied.
     */
    @Override
    public boolean isSatisfiedBy(T t) {
        return first.isSatisfiedBy(t) || second.isSatisfiedBy(t);
    }
}
