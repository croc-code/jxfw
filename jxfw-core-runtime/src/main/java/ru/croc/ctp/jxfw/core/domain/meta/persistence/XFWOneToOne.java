package ru.croc.ctp.jxfw.core.domain.meta.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.EAGER;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

/**
 * This annotation defines a single-valued association to 
 * another entity that has one-to-one multiplicity. It is not 
 * normally necessary to specify the associated target entity 
 * explicitly since it can usually be inferred from the type 
 * of the object being referenced.
 *
 * <pre>
 *    Example 1: One-to-one association that maps a foreign key column
 *
 *    On Customer class:
 *
 *    &#064;OneToOne(optional=false)
 *    &#064;JoinColumn(
 *        name="CUSTREC_ID", unique=true, nullable=false, updatable=false)
 *    public CustomerRecord getCustomerRecord() { return customerRecord; }
 *
 *    On CustomerRecord class:
 *
 *    &#064;OneToOne(optional=false, mappedBy="customerRecord")
 *    public Customer getCustomer() { return customer; }
 *
 *    Example 2: One-to-one association that assumes both the source and target share the same primary key values. 
 *
 *    On Employee class:
 *
 *    &#064;Entity
 *    public class Employee {
 *        &#064;Id Integer id;
 *    
 *        &#064;OneToOne &#064;PrimaryKeyJoinColumn
 *        EmployeeInfo info;
 *        ...
 *    }
 *
 *    On EmployeeInfo class:
 *
 *    &#064;Entity
 *    public class EmployeeInfo {
 *        &#064;Id Integer id;
 *        ...
 *    }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface XFWOneToOne {

    /** 
     * @return (Optional) The entity class that is the target of 
     *         the association. 
     *
     *         <p>Defaults to the type of the field or property 
     *         that stores the association. 
     */
    Class<?> targetEntity() default void.class;

    /**
     * @return (Optional) The operations that must be cascaded to 
     *         the target of the association.
     *
     *         <p>By default no operations are cascaded.
     */
    CascadeType[] cascade() default {};

    /** 
     * @return (Optional) Whether the association should be lazily 
     *         loaded or must be eagerly fetched. The {@link FetchType#EAGER EAGER} 
     *         strategy is a requirement on the persistence provider runtime that 
     *         the associated entity must be eagerly fetched. The {@link FetchType#LAZY 
     *         LAZY} strategy is a hint to the persistence provider runtime.
     */
    FetchType fetch() default EAGER;

    /** 
     * @return (Optional) Whether the association is optional. If set 
     *         to false then a non-null relationship must always exist.
     */
    boolean optional() default true;

    /** @return (Optional) The field that owns the relationship. This 
      *         element is only specified on the inverse (non-owning) 
      *         side of the association.
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
