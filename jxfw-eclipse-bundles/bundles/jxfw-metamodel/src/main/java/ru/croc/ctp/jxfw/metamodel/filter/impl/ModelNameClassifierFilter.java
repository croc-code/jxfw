package ru.croc.ctp.jxfw.metamodel.filter.impl;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilterSupport;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

/**
 * Фильтр метаданных по имени файла модели.
 *
 *
 * @author OKrutova
 * @since 1.6
 */
public class ModelNameClassifierFilter extends ClassifierFilterSupport {

    /**
     * Конструктор.
     * @param value значение для фильтрации
     */
    public ModelNameClassifierFilter(String value) {
        super(value);
    }

    @Override
    public boolean match(XfwClassifier xfwClassifier) {

        return value.equals(((EClassifier)((AdapterImpl)xfwClassifier).getTarget())
                .eResource().getURI().trimFileExtension().lastSegment());

    }
}
