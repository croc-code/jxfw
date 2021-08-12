package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EStructuralFeature;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.HashMap;
import java.util.Map;


/**
 * Реализация XfwStructuralFeature, оборачивающая EStructuralFeature.
 */
public class XfwStructuralFeatureImpl extends XfwNamedElementImpl implements XfwStructuralFeature {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();

    static {
        PRIMITIVE_WRAPPER_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(char.class, Character.class);
        PRIMITIVE_WRAPPER_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(double.class, Double.class);
    }

    /**
     * Конструктор.
     *
     * @param feature делегат
     */
    public XfwStructuralFeatureImpl(EStructuralFeature feature) {
        super(feature);
    }


    @Override
    public boolean isTransient() {
        return ((EStructuralFeature) target).isTransient();
    }

    @Override
    public String getDefaultValueLiteral() {
        return ((EStructuralFeature) target).getDefaultValueLiteral();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getDefaultValue() {
        XfwClassifier etype = getEType();
        if (etype instanceof XfwEnumeration) {
            if (isMany()) {
                return ((XfwEnumeration) etype).convertToEnumSet(getDefaultValueLiteral(),
                        (Class<Enum>) etype.getInstanceClass());
            } else {
                return ((XfwEnumeration) etype).convertToEnum(getDefaultValueLiteral(),
                        (Class<Enum>) etype.getInstanceClass());
            }
        }
        return ((EStructuralFeature) target).getDefaultValue();
    }

    @Override
    public boolean isUnsettable() {
        return ((EStructuralFeature) target).isUnsettable();
    }


    @Override
    public XfwClass getEContainingClass() {
        return (XfwClass) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(((EStructuralFeature) target).getEContainingClass(), XfwClass.class);
    }

    @Override
    public int getLowerBound() {
        return ((EStructuralFeature) target).getLowerBound();
    }

    @Override
    public int getUpperBound() {
        return ((EStructuralFeature) target).getUpperBound();
    }

    @Override
    public boolean isMany() {
        return ((EStructuralFeature) target).isMany();
    }

    @Override
    public boolean isRequired() {
        return ((EStructuralFeature) target).isRequired();
    }

    @Override
    public XfwClassifier getEType() {

        return (XfwClassifier) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(((EStructuralFeature) target).getEType(), XfwClassifier.class);
    }


    private Class<?> primitiveWrapper(Class<?> clazz) {

        Class<?> result = PRIMITIVE_WRAPPER_MAP.get(clazz);
        return result == null ? clazz : result;

    }

    @Override
    public boolean isFieldOfType(Class<?> clazz) {
        return primitiveWrapper(clazz).isAssignableFrom(
                primitiveWrapper(getEType().getInstanceClass()));
    }


}
