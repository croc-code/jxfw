package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAttribute;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;

/**
 * Реализация XfwAttribute, оборачивающая XFWAnnotation.
 */
public class XfwAttributeImpl extends XfwStructuralFeatureImpl implements XfwAttribute {

    private final boolean isSystemField;
    private final boolean isIdField;
    private final boolean isVersionField;

    /**
     * Конструктор.
     *
     * @param attribute делегат
     */
    public XfwAttributeImpl(EAttribute attribute) {
        super(attribute);
        this.isSystemField = ((XFWAttribute) target).isSystemField();
        this.isIdField = ((XFWAttribute) target).isIdField();
        this.isVersionField = ((XFWAttribute) target).isVersionField();
    }


    @Override
    public XfwClassifier getEAttributeType() {
        return (XfwClassifier) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(((XFWAttribute) target).getEAttributeType(), XfwClassifier.class);

    }

    @Override
    public int getMaxLength() {
        return ((XFWAttribute) target).getMaxLength();
    }

    @Override
    public boolean getGenerateBlobInfoFields() {
        return ((XFWAttribute) target).getGenerateBlobInfoFields();
    }

    @Override
    public String getContentSizeSuffix() {
        return ((XFWAttribute) target).getContentSizeSuffix();
    }

    @Override
    public String getFileNameSuffix() {
        return ((XFWAttribute) target).getFileNameSuffix();
    }

    @Override
    public String getContentTypeSuffix() {
        return ((XFWAttribute) target).getContentTypeSuffix();
    }

    @Override
    public boolean isSystemField() {
        return isSystemField;
    }

    @Override
    public boolean isIdField() {
        return isIdField;
    }

    @Override
    public boolean isVersionField() {
        return isVersionField;
    }

}
