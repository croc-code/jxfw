package ru.croc.ctp.jxfw.metamodel.runtime;


/**
 * Элемент перечисления в метамодели.
 */
public interface XfwEnumLiteral extends XfwNamedElement {
    /* EEnumLiteral methods */

    /**
     * Значение элемента перечисления из @XFWEnumId.
     * @return значение
     */
    int getValue();

    //Enumerator getInstance();


    /**
     * Имя элемента перечисления.
     * @return имя
     */
    String getLiteral();

    /**
     * Метаданные перечисления, содержащего данный элемент.
     * @return метаданные
     */
    XfwEnumeration getEEnum();
}
