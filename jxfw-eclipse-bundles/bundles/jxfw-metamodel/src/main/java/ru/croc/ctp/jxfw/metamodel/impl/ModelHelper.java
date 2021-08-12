package ru.croc.ctp.jxfw.metamodel.impl;

import static ru.croc.ctp.jxfw.metamodel.XFWConstants.SEARCH_CLASS_ANNOTATION_SOURCE;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

/**
 * Хелпер для генерации TO сервисов.
 *
 * @author Nosov Alexander
 */
public class ModelHelper {
    /**
     * Проверка - является ли класс структурного элемента метамодели, комплексным.
     * 
     * @param ref - структурный элемент метамодели
     * @return true - является, false - не является
     */
    public static boolean isComplexType(EStructuralFeature ref) {
        return ref.getEType() instanceof XFWClass && ((XFWClass) ref.getEType()).isComplexType();
    }

    /**
     * Проверка будут ли использованы возможности полнотекстового поиска для класса.
     *
     * @param xfwClass класс
     * @return да/нет
     */
    public static boolean useFulltext(XFWClass xfwClass) {
        EAnnotation annotation = xfwClass.getEAnnotation(SEARCH_CLASS_ANNOTATION_SOURCE.getUri());
        return annotation != null;
    }

    /**
     * Проверка будут ли использованы возможности полнотекстового поиска для класса по умолчанию.
     *
     * @param xfwClass класс
     * @return да/нет
     */
    public static boolean useFulltextByDefault(XFWClass xfwClass) {
        EAnnotation annotation = xfwClass.getEAnnotation(SEARCH_CLASS_ANNOTATION_SOURCE.getUri());
        return Boolean.valueOf(annotation.getDetails().get("useFulltextByDefault"));
    }


}
