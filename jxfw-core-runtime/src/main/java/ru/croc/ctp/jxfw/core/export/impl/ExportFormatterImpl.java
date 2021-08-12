package ru.croc.ctp.jxfw.core.export.impl;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import ru.croc.ctp.jxfw.core.export.ExportFormatter;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Дефолтная реализация форматирования.
 * Для boolean формирует строку с текстои Да или Нет с учетом локали.
 * Для перечислений формирует строку с учетом именований элементов перечисления из метаданных.
 * Остальные типы оставляет без изменений.
 */
public class ExportFormatterImpl implements ExportFormatter {

    private final MessageSource messageSource;

    private static final String YES_MESSAGE = "jxfw.yes";
    private static final String NO_MESSAGE = "jxfw.no";

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * Конструктор.
     *
     * @param messageSource messageSource
     */
    public ExportFormatterImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Object formatBoolean(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale) {
        if (Boolean.TRUE.equals(value)) {
            return messageSource.getMessage(YES_MESSAGE, ArrayUtils.toArray(), locale);
        } else if (Boolean.FALSE.equals(value)) {
            return messageSource.getMessage(NO_MESSAGE, ArrayUtils.toArray(), locale);
        }
        return value;
    }

    @Override
    public Object formatInteger(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale) {
        return value;
    }

    @Override
    public Object formatFloat(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale) {
        return value;
    }

    @Override
    public Object formatEnum(Object value1, XfwStructuralFeature xfwStructuralFeature, Locale locale) {
        if (xfwStructuralFeature != null && xfwStructuralFeature.getEType() instanceof XfwEnumeration) {
            XfwEnumeration xfwEnumeration = (XfwEnumeration) xfwStructuralFeature.getEType();
            if (!xfwStructuralFeature.isMany() && (value1 instanceof Enum)) {
                Enum value = (Enum) value1;
                return xfwEnumeration.getLocalizedValue(value, locale.getLanguage());
            } else if (xfwStructuralFeature.isMany() && (value1 instanceof EnumSet)) {
                EnumSet value = (EnumSet) value1;
                return StreamSupport.stream(value)
                        .map(enum1 -> xfwEnumeration
                                .getLocalizedValue((Enum) enum1, locale.getLanguage()))
                        .collect(Collectors.joining(","));
            } else {
                return value1;
            }
        }
        return value1;
    }


    @Override
    public Object formatDateTime(Object value, XfwStructuralFeature xfwStructuralFeature,
                                 Locale locale, TimeZone timeZone) {

        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(DATE_FORMAT + " " + TIME_FORMAT));
        } else if (value instanceof ZonedDateTime) {
            return ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern(DATE_FORMAT + " " + TIME_FORMAT + "X"));
        } else if (value instanceof LocalTime) {
            return ((LocalTime) value).format(DateTimeFormatter.ofPattern(TIME_FORMAT));
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
        return value;
    }

    @Override
    public Object formatDuration(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale) {
        return value;
    }

    @Override
    public boolean supportsFormat(OutputFormat format) {
        return true;
    }
}
