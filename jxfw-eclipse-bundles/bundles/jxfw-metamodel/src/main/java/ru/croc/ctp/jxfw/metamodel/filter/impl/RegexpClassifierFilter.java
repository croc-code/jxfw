package ru.croc.ctp.jxfw.metamodel.filter.impl;

import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterSupport;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

import java.util.regex.Pattern;

/**
 * Фильтр метаданных по маске, примененной к  полному имени класса.
 *
 * @author OKrutova
 * @since 1.6
 */
public class RegexpClassifierFilter extends ClassifierFilterSupport {

    private final Pattern pattern;

    /**
     * Конструктор.
     * @param value значение для фильтрации
     */
    public RegexpClassifierFilter(String value) {
        super(value);
        pattern = Pattern.compile(value);
    }

    @Override
    public boolean match(XfwClassifier xfwClassifier) {

        return pattern.matcher(xfwClassifier.getInstanceClassName()).matches();
    }
}
