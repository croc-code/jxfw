package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.List;

/**
 * Именованный элемент в метамодели.
 */
public interface XfwNamedElement {

    /* EObject methods*/
    // слишком низкоуровневые, не выставляем в рантайм.

    /* EModelElement methods*/

    /**
     * Список аннотаций на данном элементе.
     * @return аннотации
     */
    List<XfwAnnotation> getEAnnotations();

    /**
     * Аннотация на данном элементе с данныи URI.
     * @param var1 URI искомой аннотации
     * @return  метаданные аннотации или null
     */
    XfwAnnotation getEAnnotation(String var1);

    /* ENamedElement methods*/

    /**
     * Имя элемента метаданных.
     * @return имя
     */
    String getName();


}
