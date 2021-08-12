package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EReference;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;

/**
 * Реализация XfwReference, оборачивающая EReference.
 */
public class XfwReferenceImpl extends XfwStructuralFeatureImpl implements XfwReference {


    /**
     * Конструктор.
     *
     * @param reference делегат
     */
    public XfwReferenceImpl(EReference reference) {
        super(reference);
    }

    @Override
    public XfwReference getEOpposite() {
        EReference result = ((EReference) target).getEOpposite();
        return result == null ? null : (XfwReference) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(result, XfwReference.class);
    }

    @Override
    public XfwClass getEReferenceType() {
        return (XfwClass) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(((EReference) target).getEReferenceType(), XfwClass.class);
    }

}
