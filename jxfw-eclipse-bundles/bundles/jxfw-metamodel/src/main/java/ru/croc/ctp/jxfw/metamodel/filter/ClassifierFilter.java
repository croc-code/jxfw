package ru.croc.ctp.jxfw.metamodel.filter;

import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

/**
 * Фильтр метаданных.
 *
 * @author OKrutova
 * @since 1.6
 */
public interface ClassifierFilter {

    /**
     * Метаданные удовлетворяют условию фильтра.
     * @param eclassifier метаданные ecore
     * @return да\нет
     */
    boolean match(EClassifier eclassifier);

    /**
     * Метаданные удовлетворяют условию фильтра.
     * @param xfwClassifier runtime- метаданные
     * @return да\нет
     */
    boolean match(XfwClassifier xfwClassifier);

}
