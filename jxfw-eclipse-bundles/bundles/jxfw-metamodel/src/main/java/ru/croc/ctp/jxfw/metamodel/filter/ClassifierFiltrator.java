package ru.croc.ctp.jxfw.metamodel.filter;

import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

import java.util.Set;

/**
 * Класс обеспечивает применение набора фильтров к метаданным.
 *
 * @since 1.6
 * @author OKrutova
 */
public interface ClassifierFiltrator {

    /**
     * Проверяет, что метаданные удовлетворяют хотя бы одному фильтру из набора.
     * @param filters набор фильтров
     * @param classifier метаданные
     * @return да\нет
     */
    boolean anyMatch(Set<ClassifierFilter> filters, EClassifier classifier);

    /**
     * Проверяет, что метаданные удовлетворяют хотя бы одному фильтру из набора.
     * @param filters набор фильтров
     * @param classifier метаданные
     * @return да\нет
     */
    boolean anyMatch(Set<ClassifierFilter> filters, XfwClassifier classifier);
}
