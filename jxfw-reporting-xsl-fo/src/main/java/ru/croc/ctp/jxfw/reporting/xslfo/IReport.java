package ru.croc.ctp.jxfw.reporting.xslfo;

import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.MakeReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.XslfoCache;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import java.io.OutputStream;

/**
 * Интерфейс для объекта построителя отчета.
 * Created by vsavenkov on 13.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public interface IReport {

    /**
     * Главный метод класса:
     * 1. Вызывает IParamProcessor.process (обработчик параметров)
     * 2. Вызывает метод  ILayout.make представления отчета
     *
     * @param outputStream поток для формирования отчета
     * @param reportParams входные и выходные параметры формируемого отчета
     * @param xslfoCache   кеш отчетов в формате XSLFO
     * @param dataProvider провайдер данных
     */
    void make(
            OutputStream outputStream,
            MakeReportParams reportParams,
            XslfoCache xslfoCache,
            IReportDataProvider dataProvider
    );

    /**
     * Профиль отчета.
     *
     * @return профиль отчёта
     */
    ReportClass getReportProfile();

}
