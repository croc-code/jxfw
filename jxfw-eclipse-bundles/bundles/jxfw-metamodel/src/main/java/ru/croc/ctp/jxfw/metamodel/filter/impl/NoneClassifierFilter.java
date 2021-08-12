package ru.croc.ctp.jxfw.metamodel.filter.impl;

import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterSupport;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

/**
 * Фильтр не пропускает никакие метаданные.
 */
public class NoneClassifierFilter extends ClassifierFilterSupport {

    /**
     * Конструктор.
     */
    public NoneClassifierFilter() {
        super(null);
    }

    @Override
    public boolean match(XfwClassifier xfwClassifier) {
        return false;
    }
}
