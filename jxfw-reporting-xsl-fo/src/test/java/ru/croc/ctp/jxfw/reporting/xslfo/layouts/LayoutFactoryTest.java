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
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyValueEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HeaderLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HrefEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ImageEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDetailLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.PercentageEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.SimpleReportCellClassEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.StringEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TableLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TemplateEvaluatorClass;

import static junit.framework.TestCase.assertTrue;

public class LayoutFactoryTest {

    @Test
    public void getLayout() {
        IReportLayout layout = ReportLayoutFactory.getInstance(new HeaderLayoutClass());
        assertTrue(layout instanceof HeaderLayout);

        layout = ReportLayoutFactory.getInstance(new EmptyLayoutClass());
        assertTrue(layout instanceof EmptyTableLayout);

        layout = ReportLayoutFactory.getInstance(new TableLayoutClass());
        assertTrue(layout instanceof TableLayout);

        layout = ReportLayoutFactory.getInstance(new MasterDetailLayoutClass());
        assertTrue(layout instanceof MasterDetailLayout);
    }
}
