package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;

import java.util.Map;

/**
 * Реализация XfwAnnotation, оборачивающая EAnnotation.
 */
public class XfwAnnotationImpl extends AdapterImpl implements XfwAnnotation {


    /**
     * Конструктор.
     *
     * @param annotation делегат
     */
    public XfwAnnotationImpl(EAnnotation annotation) {
        this.target = annotation;
    }

    @Override
    public boolean isAdapterForType(Object type) {
        return XfwAnnotation.class.isAssignableFrom((Class<?>) type);
    }


    @Override
    public String getSource() {
        return ((EAnnotation) target).getSource();
    }

    @Override
    public Map<String, String> getDetails() {
        return ((EAnnotation) target).getDetails().map();
    }


}
