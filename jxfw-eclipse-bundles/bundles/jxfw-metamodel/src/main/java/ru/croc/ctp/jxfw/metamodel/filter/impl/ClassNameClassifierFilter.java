package ru.croc.ctp.jxfw.metamodel.filter.impl;

import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterSupport;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

/**
 * Фильтр метаданных по имени класса (полному или короткому).
 *
 *
 * @author OKrutova
 * @since 1.6
 */
public class ClassNameClassifierFilter extends ClassifierFilterSupport {

    /**
     * Конструктор.
     * @param value значение для фильтрации
     */
    public ClassNameClassifierFilter(String value) {
        super(value);
    }

    @Override
    public boolean match(XfwClassifier xfwClassifier) {

        return value.equals(xfwClassifier.getInstanceClassName())
                 || value.equals(xfwClassifier.getName());

    }
}
