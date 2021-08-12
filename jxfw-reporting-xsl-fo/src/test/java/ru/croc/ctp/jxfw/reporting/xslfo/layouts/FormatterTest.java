package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportObjectFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.DateTimeEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.EmptyValueEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.HrefEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.IReportFormatter;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ImageEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.PercentageEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.SimpleReportCellClass;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.StringEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.TemplateEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.types.DateTimeEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyValueEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HrefEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ImageEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.PercentageEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.SimpleReportCellClassEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.StringEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TemplateEvaluatorClass;

import static junit.framework.TestCase.assertTrue;

public class FormatterTest {

    @Test
    public void getFormatter() {
        IReportFormatter formatter = ReportObjectFactory.getFormatter(new HrefEvaluatorClass());
        assertTrue(formatter instanceof HrefEvaluator);

        formatter = ReportObjectFactory.getFormatter(new DateTimeEvaluatorClass());
        assertTrue(formatter instanceof DateTimeEvaluator);

        formatter = ReportObjectFactory.getFormatter(new EmptyValueEvaluatorClass());
        assertTrue(formatter instanceof EmptyValueEvaluator);

        formatter = ReportObjectFactory.getFormatter(new ImageEvaluatorClass());
        assertTrue(formatter instanceof ImageEvaluator);

        formatter = ReportObjectFactory.getFormatter(new PercentageEvaluatorClass());
        assertTrue(formatter instanceof PercentageEvaluator);

        formatter = ReportObjectFactory.getFormatter(new SimpleReportCellClassEvaluatorClass());
        assertTrue(formatter instanceof SimpleReportCellClass);

        formatter = ReportObjectFactory.getFormatter(new StringEvaluatorClass());
        assertTrue(formatter instanceof StringEvaluator);

        formatter = ReportObjectFactory.getFormatter(new TemplateEvaluatorClass());
        assertTrue(formatter instanceof TemplateEvaluator);
    }
}
