package ru.croc.ctp.jxfw.core.domain.meta.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

/**
 * Связь Многие-ко-Многим для JXFW доменной модели, 
 * за основу взят javax.persistence.ManyToMany 
 *
 * @author Nosov Alexander
 *         on 19.06.15.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XFWManyToMany {

    /**
     * @return (Optional) The entity class that is the target of the
     *         association. Optional only if the collection-valued
     *         relationship property is defined using Java generics.  Must be
     *         specified otherwise.
     *          Defaults to the parameterized type of
     *          the collection when defined using generics.
     */
    Class<?> targetEntity() default void.class;

    /**
     * @return (Optional) The operations that must be cascaded to the target
     *         of the association.
     * 
     *          <p>When the target collection is a {@link java.util.Map
     *          java.util.Map}, the <code>cascade</code> element applies to the
     *          map value.
     * 
     *          <p>Defaults to no operations being cascaded.
     */
    CascadeType[] cascade() default {};

    /**
     * @return (Optional) Whether the association should be lazily loaded or
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
    
}
