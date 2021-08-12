package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.reporting.xslfo.IReport;
import ru.croc.ctp.jxfw.reporting.xslfo.IReportService;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.data.impl.DataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractDataSourceClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Класс, предоставляющий доступ к отчетам.
 * Created by vsavenkov on 21.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@Service
public class ReportService implements IReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);


    /**
     * Кеш сформированный отчетов в формате XSLFO.
     */
    private final XslfoCache xslfoCache;

    /**
     * массив профилей отчетов.
     */
    private Map<String, XfwXslfoReport> reportsDictionary = new ConcurrentHashMap<>();


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public ReportService(XslfoCache xslfoCache) {
        this.xslfoCache = xslfoCache;
    }


    /** FIXME невыполненный кусок инициализации из .NET
     * TODO: буду ковырять по мере надобности

     if (m_oConfigurations.FullCachingRequired)
     CreateDirectoryListing();

     InitializeReportWatcher();
     */


    /// <summary>
    /// Генерирует отчет
    /// </summary>
    /// <param name="OutputStream">Поток для формирования отчета</param>
    /// <param name="ReportParams">Входные и выходные параметры формируемого отчета</param>

    /**
     * Генерирует отчет.
     *
     * @param outputStream Поток для формирования отчета
     * @param reportParams Входные и выходные параметры формируемого отчета
     */
    @Override
    public void makeReport(OutputStream outputStream, MakeReportParams reportParams) {
        /* TODO: это надо или достаточно ConcurrentHashMap
        lock (reportsDictionary.SyncRoot)
        {
            bool refresh = Utility.Converter.
                        ToBoolean(Utility.Converter.ToString(reportParams.ReportParamsCollection["Refresh"]), false);
            // Если отчета нет в кэше и отсутствует параметр Refresh, то добавим последний.
            // Причина: отчет мог быть удален из кэша из-за изменения профиля отчета
            if (!reportsDictionary.Contains(reportParams.ReportName) && !refresh)
            {
                reportParams.ReportParamsCollection.Set("Refresh", "true");
            }
        }
        */
        boolean refresh = Converter.toBoolean(Converter
                        .toString(reportParams.getReportParamsCollection().get("Refresh")),
                false);
        // Если отчета нет в кэше и отсутствует параметр Refresh, то добавим последний.
        // Причина: отчет мог быть удален из кэша из-за изменения профиля отчета
        if (!reportsDictionary.containsKey(reportParams.getReportName()) && !refresh) {
            reportParams.getReportParamsCollection().put("Refresh", "true");
        }

        XfwXslfoReport report = getReport(reportParams.getReportName());
        List<AbstractDataSourceClass> abstractDataSourceClasses = report.getListAbstactDataSourceClass();
        ReportParams params = report.createReportParams(reportParams);
        IReportDataProvider provider = new DataProvider(abstractDataSourceClasses, params);
        // вызов метода make у соответствующего отчета
        // если отчет не является потокобезопасным, то в критической секции
        if (report.getClass().isAnnotationPresent(ReportObjectThreadSafe.class)) {
            report.make(outputStream,
                    reportParams,
                    xslfoCache,
                    provider);
        } else {
            synchronized (report) {
                report.make(outputStream,
                        reportParams,
                        xslfoCache,
                        provider);
            }
        }
    }

    /**
     * Возвращает объект Report по имени отчета(имя в виде r-******.xml)
     *
     * @param reportName имя отчета
     * @return объект Report
     */
    private XfwXslfoReport getReport(String reportName) {
        XfwXslfoReport report;
        /* TODO: это надо или достаточно ConcurrentHashMap
        lock (reportsDictionary.SyncRoot)
        {
            // Ищем профиль отчета в кэше
            report = reportsDictionary[reportName];
        }
        */
        // Ищем профиль отчета в кэше
        report = reportsDictionary.get(reportName);

        if (report == null) {
            //Поднимаем профиль отчета из файла


            // Создам отчет
            try {
                report = applicationContext.getBean(reportName, XfwXslfoReport.class);
            } catch (Exception ex) {
                logger.error("{}", ex);
                /*FIXME если нет нужного бина, то создавать общий класс отчета,
                 но он пока абстрактный, поэтому будем выкидывать исключение

                 т.к. бины получаются из контекста, то reportsDictionary не нужен.
                 Но пусть пока полежит, до реализации общего класса отчетов, который
                 должен предоставлять несколько инстансов себя для разных профилей.
                 */
                throw new RuntimeException(ex);
            }
            // Добавим в кэш профилей
            /* TODO: это надо или достаточно ConcurrentHashMap
            lock (reportsDictionary.SyncRoot)
            {
                reportsDictionary[reportName] = report;
            }
            */
            reportsDictionary.put(reportName, report);
        }

        return report;
    }


    @Override
    public Iterable<OutputFormat> getFormats() {
        Set<OutputFormat> result = new HashSet<>();
        result.add(OutputFormat.HTML5);
        result.add(OutputFormat.EXCEL);
        result.add(OutputFormat.PDF);
        result.add(OutputFormat.WORD);

        return result;

    }


    /**
     * Параметры отчёта.
     *
     * @param reportName имя отчёта
     * @return параметры отчёта
     */
    @Override
    public List<ReportClass.ParamsClass.ParamClass> getReportParams(String reportName) {
        IReport report = getReport(reportName);
        return report.getReportProfile().getParams().getParam();
    }


}
