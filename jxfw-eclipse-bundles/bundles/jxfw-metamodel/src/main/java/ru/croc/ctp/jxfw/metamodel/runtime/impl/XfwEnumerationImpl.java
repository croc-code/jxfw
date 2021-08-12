package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import ru.croc.ctp.jxfw.metamodel.impl.XfwLocalizableAdapterFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumLiteral;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwLocalizable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Реализация XfwEnumeration, оборачивающая EEnum.
 */
public class XfwEnumerationImpl extends XfwClassifierImpl implements XfwEnumeration {


    /**
     * Конструктор.
     *
     * @param xfwEnum делегат
     */
    public XfwEnumerationImpl(EEnum xfwEnum) {
        super(xfwEnum);
    }

    @Override
    public List<XfwEnumLiteral> getELiterals() {
        List<XfwEnumLiteral> result = new ArrayList<>();
        for (EEnumLiteral eLiteral : ((EEnum) target).getELiterals()) {
            result.add((XfwEnumLiteral) XfwRuntimeAdapterFactory.INSTANCE.adapt(eLiteral, XfwEnumLiteral.class));
        }
        return result;
    }

    @Override
    public XfwEnumLiteral getEEnumLiteral(String var1) {
        EEnumLiteral result = ((EEnum) target).getEEnumLiteral(var1);
        return result == null ? null : (XfwEnumLiteral) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(result, XfwEnumLiteral.class);
    }

    @Override
    public XfwEnumLiteral getEEnumLiteral(int var1) {
        EEnumLiteral result = ((EEnum) target).getEEnumLiteral(var1);
        return result == null ? null : (XfwEnumLiteral) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(result, XfwEnumLiteral.class);
    }

    @SuppressWarnings("rawtypes")
	@Override
    public String getLocalizedValue(Enum anEnum, String lang) {
        return getLocalizedFieldName(anEnum.name(), lang);
    }

    @Override
    public String getLocalizedTypeName(String lang) {
        return ((XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE.adapt(target, XfwLocalizable.class))
                .getLocalizedTypeName(lang);
    }

    @Override
    public String getLocalizedFieldName(String fieldName, String lang) {
        return ((XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE.adapt(target, XfwLocalizable.class))
                .getLocalizedFieldName(fieldName, lang);
    }

    @Override
    public Set<String> getAvailableLanguages() {
        return ((XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE.adapt(target, XfwLocalizable.class))
                .getAvailableLanguages();
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Integer convertToInt(Enum anEnum) {
        if (anEnum == null) {
            return null;
        }
        if (!anEnum.getClass().getCanonicalName().equals(getInstanceClassName())) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "{0} metadata can not convert enums of type {1}",
                    getName(), anEnum.getClass().getCanonicalName()));
        }
        XfwEnumLiteral literal = getEEnumLiteral(anEnum.name());
        if (literal == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format("No enum constant found for value {0}",
                            anEnum.name()));
        }

        return literal.getValue();
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Integer convertToInt(EnumSet enumSet) {
        if (enumSet == null) {
            return null;
        }
        if (enumSet.size() == 0) {
            return null;
        }
        int result = 0;
        for (Object enumeration : enumSet) {
            result |= convertToInt((Enum) enumeration);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public <T extends Enum> T convertToEnum(Integer integer, Class<T> targetType) {

        return convertToEnumGeneral(targetType, integer);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public <T extends Enum<T>> EnumSet<T> convertToEnumSet(Integer integer, Class<T> targetType) {

        if (integer == null) {
            return EnumSet.noneOf(targetType);
        }
        if (!targetType.getCanonicalName().equals(getInstanceClassName())) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "{0} metadata can not convert enums of type {1}",
                    getName(), targetType.getCanonicalName()));
        }

        Set<Enum> result = new HashSet<>();
        for (XfwEnumLiteral literal : getELiterals()) {
            final int value = literal.getValue();
            if ((integer & value) == value) {
                result.add(targetType.cast(
                        Enum.valueOf(targetType, literal.getName())));
            }
        }
        if (result.isEmpty()) {
            return EnumSet.noneOf(targetType);
        } else {
            return (EnumSet<T>) EnumSet.copyOf(result);
        }
    }

    @SuppressWarnings("rawtypes")
	@Override
    public <T extends Enum> T convertToEnum(String value, Class<T> targetType) {
        return convertToEnumGeneral(targetType, value);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Enum> T convertToEnumGeneral(Class<T> targetType, Object value) {
        if (value == null) {
            return null;
        }
        if (!targetType.getCanonicalName().equals(getInstanceClassName())) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "{0} metadata can not convert enums of type {1}",
                    getName(), targetType.getCanonicalName()));
        }

        XfwEnumLiteral literal;
        if (value instanceof String) {
            literal = getEEnumLiteral((String) value);
        } else if (value instanceof Integer) {
            literal = getEEnumLiteral((Integer) value);
        } else {
            literal = null;
        }

        if (literal == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format("No enum constant found for value {0}",
                            value));
        }
        return (T) Enum.valueOf(targetType, literal.getName());
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public <T extends Enum<T>> EnumSet<T> convertToEnumSet(String input_value, Class<T> targetType) {
        if (input_value == null) {
            return EnumSet.noneOf(targetType);
        }
        String[] values = input_value.split("\\s*,\\s*");
        if (values.length == 0) {
            return EnumSet.noneOf(targetType);
        }
        if (!targetType.getCanonicalName().equals(getInstanceClassName())) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "{0} metadata can not convert enums of type {1}",
                    getName(), targetType.getCanonicalName()));
        }

        Set<Enum> result = new HashSet<>();
        for (String value : Arrays.asList(values)) {
            XfwEnumLiteral literal = getEEnumLiteral(value);
            if (literal == null) {
                throw new IllegalArgumentException(
                        MessageFormat.format("No enum constant found for value {0}",
                                value));
            }
            result.add(targetType.cast(
                    Enum.valueOf(targetType, literal.getName())));
        }
        if (result.isEmpty()) {
            return EnumSet.noneOf(targetType);
        } else {
            return (EnumSet<T>) EnumSet.copyOf(result);
        }
    }


}
