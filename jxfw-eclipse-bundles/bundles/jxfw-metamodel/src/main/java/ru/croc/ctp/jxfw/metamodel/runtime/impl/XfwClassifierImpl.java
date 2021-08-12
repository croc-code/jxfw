package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;


/**
 * Реализация XfwClassifier, оборачивающая EClassifier.
 */
public class XfwClassifierImpl extends XfwNamedElementImpl implements XfwClassifier {

    /**
     * Конструктор.
     *
     * @param eclassifier делегат
     */
    public XfwClassifierImpl(EClassifier eclassifier) {
        super(eclassifier);
    }

    @Override
    public String getInstanceClassName() {
        return ((EClassifier)target).getInstanceClassName();
    }

    @Override
    public Class<?> getInstanceClass() {
        return ((EClassifier)target).getInstanceClass();
    }

    @Override
    public Object getDefaultValue() {
        return ((EClassifier)target).getDefaultValue();
    }

    @Override
    public String getInstanceTypeName() {
        return ((EClassifier)target).getInstanceTypeName();
    }

    @Override
    public boolean isInstance(Object var1) {
        return ((EClassifier)target).isInstance(var1);
    }

}
