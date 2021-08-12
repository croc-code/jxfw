package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.Map;

/**
 * Аннотация в метамодели.
 */
public interface XfwAnnotation {

    /* EAnnotation methods*/

    /**
     * URI аннотации.
     * @return имя.
     */
    String getSource();

    /**
     * Атрибуты аннотации.
     * @return набор атрибутов.
     */
    Map<String, String> getDetails();

    //EModelElement getEModelElement();

    // EList<EObject> getContents();

    //EList<EObject> getReferences();
}
