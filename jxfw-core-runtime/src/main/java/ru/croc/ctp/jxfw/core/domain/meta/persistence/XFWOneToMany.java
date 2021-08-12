package ru.croc.ctp.jxfw.core.domain.meta.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

/**
 * Связь Один-ко-Многим для JXFW доменной модели,
 * за основу взят javax.persistence.OneToMany.
 * 
 * @author Nosov Alexander
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XFWOneToMany {

    /**
     * @return (Optional) The entity class that is the target
     *         of the association. Optional only if the collection
     *         property is defined using Java generics.
     *         Must be specified otherwise.
     *
     *         <p>Defaults to the parameterized type of
     *         the collection when defined using generics.
     */
    @SuppressWarnings("rawtypes")
    Class targetEntity() default void.class;

    /**
     * @return (Optional) The operations that must be cascaded to
     *         the target of the association.
     * 
     *         <p>Defaults to no operations being cascaded.
     *
     *         <p>When the target collection is a {@link java.util.Map
     *         java.util.Map}, the <code>cascade</code> element applies to the
     *         map value.
     */
    CascadeType[] cascade() default {};

    /** @return (Optional) Whether the association should be lazily loaded or
     *         must be eagerly fetched. The EAGER strategy is a requirement on
     *         the persistence provider runtime that the associated entities
     *         must be eagerly fetched.  The LAZY strategy is a hint to the
     *         persistence provider runtime.
     */
    FetchType fetch() default FetchType.LAZY;

    /**
     * @return The field that owns the relationship. Required unless
     *         the relationship is unidirectional.
     */
    String mappedBy() default "";

    /**
     * @return (Optional) Whether to apply the remove operation to entities that have
     *         been removed from the relationship and to cascade the remove operation to
     *         those entities.
     * 
     * @since Java Persistence 2.0
     */
    boolean orphanRemoval() default false;

}
