package ru.croc.ctp.jxfw.metamodel.filter;

import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.impl.XfwRuntimeAdapterFactory;

/**
 * Базовая реализация фильтра метаданных.
 * Реализует метод фильтрации метаданных ecore путем делегирования
 * вызова методу фильтрации runtime-метаданных.
 *
 *
 * @author OKrutova
 * @since 1.6
 */
public abstract class ClassifierFilterSupport implements ClassifierFilter {

    /**
     * Значение для фильтрации.
     */
    protected final String value;

    /**
     * Конструктор.
     * @param value значение для фильтрации
     */
    public ClassifierFilterSupport(String value) {
        this.value = value;
    }

    @Override
    public boolean match(EClassifier eclassifier) {
        return match((XfwClassifier)XfwRuntimeAdapterFactory.INSTANCE.adapt(eclassifier, XfwClassifier.class));
    }




}
