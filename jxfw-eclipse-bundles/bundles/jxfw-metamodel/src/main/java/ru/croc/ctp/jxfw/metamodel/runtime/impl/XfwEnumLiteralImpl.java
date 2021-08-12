package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EEnumLiteral;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumLiteral;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;

/**
 * Реализация XfwEnumLiteral, оборачивающая EEnumLiteral.
 */
public class XfwEnumLiteralImpl extends XfwNamedElementImpl implements XfwEnumLiteral {


    /**
     * Конструктор.
     *
     * @param enumLiteral делегат
     */
    public XfwEnumLiteralImpl(EEnumLiteral enumLiteral) {
        super(enumLiteral);
    }


    @Override
    public int getValue() {
        return ((EEnumLiteral) target).getValue();
    }

    @Override
    public String getLiteral() {
        return ((EEnumLiteral) target).getLiteral();
    }

    @Override
    public XfwEnumeration getEEnum() {
        return (XfwEnumeration) XfwRuntimeAdapterFactory.INSTANCE.adapt(
                ((EEnumLiteral) target).getEEnum(), XfwEnumeration.class);
    }

}
