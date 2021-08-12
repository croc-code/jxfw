package ru.croc.ctp.jxfw.core.export.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportFormatter;
import ru.croc.ctp.jxfw.core.export.ExportRow;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Базовая реализация поставщика данных для экспорта.
 * Разбирает строку format из клиентские настроек, форматирование
 * примитивных полей делегирует ExportFormatter.
 */
public class BaseExportDataProvider implements ExportDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(ExportDataProvider.class);

    private final ExportFormatter exportFormatter;
    private final LoadContext loadContext;
    private final List data;
    private boolean done = false;


    /**
     * Конструктор.
     * @param data данные
     * @param exportFormatter способ форматирования примитиных полей
     * @param loadContext контест загрузки
     */
    public BaseExportDataProvider(List data,
                                  ExportFormatter exportFormatter, LoadContext loadContext) {
        this.exportFormatter = exportFormatter;
        this.loadContext = loadContext;
        this.data = data;
    }

    @Override
    public Iterable<ExportRow> getMoreRows() {

        if (done) {
            return Collections.EMPTY_LIST;
        }
        List<ExportRow> result = new ArrayList<>();
        done = true;
        for (Object dataObject : data) {
            result.add(column -> {
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(dataObject);
                XfwClass xfwClass = XfwModelFactory.getInstance().find(
                        dataObject.getClass().getCanonicalName(), XfwClass.class);


                if (column.getFormat() == null) {
                    try {
                    return formatPrimitive(
                            beanWrapper.getPropertyValue(column.getPropName()), column.getPropName(), xfwClass);
                    } catch (Exception ex) {
                        logger.error("Column read error {}", column.getPropName(), ex);
                        return column.getPropName();
                    }
                }

                String format = column.getFormat();
                Set<String> properties = getPropertyPlaceholders(column.getFormat());
                for (String property : properties) {
                    try {
                        format = format.replace(
                                "{" + property + "}",
                                formatPrimitive(beanWrapper.getPropertyValue(property), property, xfwClass).toString());
                    } catch (Exception ex) {
                        format = format.replace(
                                "{" + property + "}","");
                        logger.debug("Preloads error {}", property, ex);
                    }
                }
                return format // JXFW-1069
                        .replace("<b>", "")
                        .replace("</b>", "")
                        .replace("<i>", "")
                        .replace("</i>", "");


            });
        }

        return result;
    }


    private Object formatPrimitive(Object value, String propName, XfwClass xfwClass) {
        Object result = value;
        XfwStructuralFeature feature = null;

        if (xfwClass != null) {  // may be null in case of Tuple
            feature = xfwClass.getChainingEStructuralFeature(propName);
        }
        if (feature == null) {
            logger.info("StructuralFeature {} not found", propName);
        }
        if (feature != null) {
            if (feature.isFieldOfType(Enum.class)
                    || feature.isFieldOfType(EnumSet.class)) {
                result = exportFormatter.formatEnum(value, feature, loadContext.getLocale());
            } else if (feature.isFieldOfType(Byte.class)
                    || feature.isFieldOfType(Short.class)
                    || feature.isFieldOfType(Integer.class)
                    || feature.isFieldOfType(Long.class)
                    || feature.isFieldOfType(BigInteger.class)) {
                result = exportFormatter.formatInteger(value, feature, loadContext.getLocale());
            } else if (feature.isFieldOfType(Double.class)
                    || feature.isFieldOfType(BigDecimal.class)
                    || feature.isFieldOfType(Float.class)) {
                result = exportFormatter.formatFloat(value, feature, loadContext.getLocale());
            } else if (feature.isFieldOfType(Boolean.class)) {
                result = exportFormatter.formatBoolean(value, feature, loadContext.getLocale());
            } else if (feature.isFieldOfType(LocalDate.class)
                    || feature.isFieldOfType(LocalTime.class)
                    || feature.isFieldOfType(LocalDateTime.class)
                    || feature.isFieldOfType(ZonedDateTime.class)) {
                result = exportFormatter.formatDateTime(value,
                        feature, loadContext.getLocale(), loadContext.getTimeZone());
            } else if (feature.isFieldOfType(Duration.class)) {
                result = exportFormatter.formatDuration(value, feature, loadContext.getLocale());
            }

            // FIXME JXFW-1206
            if (feature.getEAnnotation(XFWConstants.getUri("XFWProtected")) != null) {
                result = "[PROTECTED]";
            }
        }

        return result == null ? "" : result;

    }


    // валидный java-идентификатор или точка в фигурных скобках
    private static final Pattern PATTERN = Pattern.compile("\\{[\\w$.]*\\}");

    /**
     * Достает все плейсхолдеры свойств из строки формата вида {prop1.prop2}
     *
     * @param format строка формата
     * @return набор свойств
     */
    public static Set<String> getPropertyPlaceholders(String format) {

        Set<String> result = new HashSet<>();
        if (format == null) {
            return result;
        }
        Matcher matcher = PATTERN.matcher(format);

        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.start());
            ends.add(matcher.end());
        }

        for (int i = starts.size() - 1; i >= 0; i--) {
            result.add(format.substring(starts.get(i) + 1, ends.get(i) - 1));
        }

        return result;
    }


}
