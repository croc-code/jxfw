package ru.croc.ctp.jxfw.metamodel.filter.impl;

import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFiltrator;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

import java.util.Set;

/**
 * Базовая реализация.
 */
public class ClassifierFiltratorImpl implements ClassifierFiltrator {

    @Override
    public boolean anyMatch(Set<ClassifierFilter> filters, EClassifier classifier) {
        for (ClassifierFilter filter : filters) {
            if (filter.match(classifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean anyMatch(Set<ClassifierFilter> filters, XfwClassifier classifier) {
        for (ClassifierFilter filter : filters) {
            if (filter.match(classifier)) {
                return true;
            }
        }
        return false;
    }


}
