package ru.croc.ctp.jxfw.core.domain.meta.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

/**
 * Связь Многие-к-Одному для JXFW доменной модели,
 * за основу взят javax.persistence.ManyToOne
 *
 * @author Nosov Alexander
 *         on 19.06.15.
 */

@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface XFWManyToOne {
    /**
     * @return (Optional) The entity class that is the target of
     *         the association.
     * 
     *         <p>Defaults to the type of the field or property
     *         that stores the association.
     */
    @SuppressWarnings("rawtypes")
    Class targetEntity() default void.class;

    /**
     * @return (Optional) The operations that must be cascaded to
     *         the target of the association.
     * 
     *         <p>By default no operations are cascaded.
     */
    CascadeType[] cascade() default {};

    /**
     * @return (Optional) Whether the association should be lazily
     *         loaded or must be eagerly fetched. The EAGER
     *         strategy is a requirement on the persistence provider runtime that
     *         the associated entity must be eagerly fetched. The LAZY
     *         strategy is a hint to the persistence provider runtime.
     */
    FetchType fetch() default FetchType.EAGER;

    /**
     * @return (Optional) Whether the association is optional. If set
     *         to false then a non-null relationship must always exist.
     */
    boolean optional() default true;

}
