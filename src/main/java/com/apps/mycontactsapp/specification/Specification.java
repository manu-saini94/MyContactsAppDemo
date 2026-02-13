package com.apps.mycontactsapp.specification;

/**
 * Generic Specification interface for defining search criteria.
 * Follows the Specification Design Pattern.
 *
 * @param <T> the type of object to validate.
 */
public interface Specification<T> {

    /**
     * Checks if the object satisfies the specification.
     * 
     * @param t the object to check.
     * @return true if satisfied, false otherwise.
     */
    boolean isSatisfiedBy(T t);

    /**
     * Combines this specification with another using AND logic.
     * 
     * @param other the other specification.
     * @return a new combined Specification.
     */
    default Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    /**
     * Combines this specification with another using OR logic.
     * 
     * @param other the other specification.
     * @return a new combined Specification.
     */
    default Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    /**
     * Negates this specification.
     * 
     * @return a new Specification that represents the logical NOT.
     */
    default Specification<T> not() {
        return new NotSpecification<>(this);
    }
}
