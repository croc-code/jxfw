package ru.croc.ctp.jxfw.metamodel.filter.impl;

import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterFactory;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Базовая реализация.
 *
 * @author OKrutova
 * @since 1.6
 */

public class ClassifierFilterFactoryImpl implements ClassifierFilterFactory {

    /**
     * Экземпляр фабрики.
     */
    public static ClassifierFilterFactory INSTANCE = new ClassifierFilterFactoryImpl();

    private ClassifierFilterFactoryImpl(){

    }

    @Override
    public ClassifierFilter createFilter(String type, String value) {

        ClassifierFilterType filterType = ClassifierFilterType.valueOf(type);
        switch (filterType) {
            case modelName:
                return new ModelNameClassifierFilter(value);
            case regexp:
                return new RegexpClassifierFilter(value);
            case className:
            default:
                return new ClassNameClassifierFilter(value);
        }
    }

    @Override
    public Set<ClassifierFilter> createFilters(Map<String, String> filters) {
        Set<ClassifierFilter> result = new HashSet<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            result.add(createFilter(entry.getKey(), entry.getValue()));
        }
        return result;
    }


    @Override
    public Set<ClassifierFilter> createFilters(String[] classes, String[] models, String[] regexps,
                                                      ClassifierFilter defaultFilter) {
        Set<ClassifierFilter> result = new HashSet<>();
        for (String value : classes) {
            result.add(createFilter(ClassifierFilterType.className.name(), value));
        }
        for (String value : models) {
            result.add(createFilter(ClassifierFilterType.modelName.name(), value));
        }
        for (String value : regexps) {
            result.add(createFilter(ClassifierFilterType.regexp.name(), value));
        }
        if (result.size() == 0) {
            result.add(defaultFilter);
        }
        return result;
    }



}
