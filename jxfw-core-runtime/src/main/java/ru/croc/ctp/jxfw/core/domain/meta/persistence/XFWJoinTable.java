package ru.croc.ctp.jxfw.core.domain.meta.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

/**
 * This annotation is used in the mapping of associations. It
 * is specified on the owning side of a many-to-many association,
 * or in a unidirectional one-to-many association.
 *
 * <p>If the <code>JoinTable</code> annotation is missing, the
 * default values of the annotation elements apply.  The name
 * of the join table is assumed to be the table names of the
 * associated primary tables concatenated together (owning side
 * first) using an underscore.
 *
 * <pre>
 *
 *    Example:
 *    &#064;JoinTable(
 *    name="CUST_PHONE",
 *    joinColumns=
 *        &#064;JoinColumn(name="CUST_ID", referencedColumnName="ID"),
 *    inverseJoinColumns=
 *        &#064;JoinColumn(name="PHONE_ID", referencedColumnName="ID")
 *    )
 * </pre>
 * Описание механизма связей в XFW, за основу взят javax.persistence.JoinTable
 *
 * @author Nosov Alexander
 *         on 23.06.15.
 * @see javax.persistence.JoinTable
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XFWJoinTable {

    /**
     * (Optional) The name of the join table.
     * 
     * <p>Defaults to the concatenated names of
     * the two associated primary entity tables,
     * separated by an underscore.
     *
     * @return The name of the join table.
     */
    String name() default "";

    /**
     * (Optional) The catalog of the table.
     * 
     * <p>Defaults to the default catalog.
     *
     * @return The catalog of the table.
     */
    String catalog() default "";

    /**
     * (Optional) The schema of the table.
     * 
     * <p>Defaults to the default schema for user.
     *
     * @return The schema of the table.
     */
    String schema() default "";

    /**
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity owning the
     * association (i.e. the owning side of
     * the association).
     * 
     * <p>Uses the same defaults as for {@link JoinColumn}.
     *
     * @return The foreign key columns.
     */
    JoinColumn[] joinColumns() default {};

    /**
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity that does
     * not own the association (i.e. the
     * inverse side of the association).
     * 
     * <p>Uses the same defaults as for {@link JoinColumn}.
     *
     * @return The foreign key columns.
     */
    JoinColumn[] inverseJoinColumns() default {};

    /**
     * (Optional) Unique constraints that are
     * to be placed on the table. These are
     * only used if table generation is in effect.
     * 
     * <p>Defaults to no additional constraints.
     *
     * @return Unique constraints.
     */
    UniqueConstraint[] uniqueConstraints() default {};

}
