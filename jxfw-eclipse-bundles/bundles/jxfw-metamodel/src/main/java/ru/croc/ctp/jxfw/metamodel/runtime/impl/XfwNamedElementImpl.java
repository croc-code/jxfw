package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.ENamedElement;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwNamedElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация XfwNamedElement, оборачивающая ENamedElement.
 */
public class XfwNamedElementImpl extends AdapterImpl implements XfwNamedElement {


    /**
     * Конструктор.
     *
     * @param namedElement делегат
     */
    public XfwNamedElementImpl(ENamedElement namedElement) {
        this.target = namedElement;
    }

    @Override
    public boolean isAdapterForType(Object type) {
        return XfwNamedElement.class.isAssignableFrom((Class<?>) type);
    }


    @Override
    public List<XfwAnnotation> getEAnnotations() {
        List<XfwAnnotation> result = new ArrayList<>();
        for (EAnnotation eAnnotation : ((ENamedElement) target).getEAnnotations()) {
            result.add((XfwAnnotation) XfwRuntimeAdapterFactory.INSTANCE.adapt(eAnnotation, XfwAnnotation.class));
        }
        return result;
    }

    @Override
    public XfwAnnotation getEAnnotation(String var1) {
        EAnnotation result = ((ENamedElement) target).getEAnnotation(var1);
        return result == null ? null :
                (XfwAnnotation) XfwRuntimeAdapterFactory.INSTANCE.adapt(result, XfwAnnotation.class);
    }

    @Override
    public String getName() {
        return ((ENamedElement) target).getName();
    }


}
