package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HeaderLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDetailLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TableLayoutClass;

/**
 * Фабрика объектов представления фрагмента отчета (ReportLayout).
 * Created by vsavenkov on 15.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ReportLayoutFactory {

    /**
     * Создает объект формирования представления фрагмента отчета (IReportLayout),
     * вызывая соответствующий метоd ReportObjectFactory.
     *
     * @param profile - элемент профиля с аттрибутами "assembly" и "class"
     * @return объект формирования представления фрагмента отчета
     */
    public static IReportLayout getInstance(AbstractLayoutClass profile) {
        if (profile instanceof HeaderLayoutClass) {
            return new HeaderLayout();
        } else if (profile instanceof EmptyLayoutClass) {
            return new EmptyTableLayout();
        } else if (profile instanceof MasterDetailLayoutClass) {
            return new MasterDetailLayout();
        } else if (profile instanceof TableLayoutClass) {
            return new TableLayout();
        }

        throw new RuntimeException("Не найдена подходящая реализация IReportLayout для "
                + profile.getClass().getTypeName());
    }
}
