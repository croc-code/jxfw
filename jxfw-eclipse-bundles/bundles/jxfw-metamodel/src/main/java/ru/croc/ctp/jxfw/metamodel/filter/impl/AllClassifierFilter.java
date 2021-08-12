package ru.croc.ctp.jxfw.metamodel.filter.impl;

import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterSupport;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

/**
 * Фильтр пропускает все метаданные.
 */
public class AllClassifierFilter extends ClassifierFilterSupport {

    /**
     * Конструктор.
     */
    public AllClassifierFilter() {
        super(null);
    }

    @Override
    public boolean match(XfwClassifier xfwClassifier) {
        return true;
    }
}
