package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.apache.commons.lang3.StringUtils;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.DateTimeEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.EmptyValueEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.HrefEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.IReportFormatter;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ImageEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.PercentageEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.SimpleReportCellClass;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.StringEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.TemplateEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractComponentClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.DateTimeEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyValueEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HrefEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ImageEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.PercentageEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.SimpleReportCellClassEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.StringEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TemplateEvaluatorClass;

import java.util.Hashtable;
import java.util.Map;

/**
 * Дефолтная реализация фабрики объектов.
 * Created by vsavenkov on 03.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings("rawtypes")
public class ReportObjectFactory {

    /**
     * Создает и возвращает объект форматтера по данным из узла xml.
     *
     * @param formatterClass - {@link AbstractFormatterClass}
     * @return IReportFormatter
     */
    public static IReportFormatter getFormatter(AbstractFormatterClass formatterClass) {
        String typeName = formatterClass.getClass().getTypeName();
        if (typeName.equals(HrefEvaluatorClass.class.getName())) {
            return new HrefEvaluator();
        } else if (typeName.equals(DateTimeEvaluatorClass.class.getName())) {
            return new DateTimeEvaluator();
        } else if (typeName.equals(EmptyValueEvaluatorClass.class.getName())) {
            return new EmptyValueEvaluator();
        } else if (typeName.equals(ImageEvaluatorClass.class.getName())) {
            return new ImageEvaluator();
        } else if (typeName.equals(PercentageEvaluatorClass.class.getName())) {
            return new PercentageEvaluator();
        } else if (typeName.equals(SimpleReportCellClassEvaluatorClass.class.getName())) {
            return new SimpleReportCellClass();
        } else if (typeName.equals(StringEvaluatorClass.class.getName())) {
            return new StringEvaluator();
        } else if (typeName.equals(TemplateEvaluatorClass.class.getName())) {
            return new TemplateEvaluator();
        } else {
            throw new RuntimeException("Не найдена подходящая реализация IReportFormatter для "
                    + typeName);
        }
    }
}
