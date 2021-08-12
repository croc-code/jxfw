package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import java.io.ByteArrayOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Набор данных, используемых лайаутом для обработки.
 * Created by vsavenkov on 06.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings("rawtypes")
public class ReportLayoutData {

    /**
     * Кастомный объект, пользовательские данные.
     */
    private Object customData;

    /**
     * Параметры отчета.
     */
    private ReportParams params;

    /**
     * Провайдер данных.
     */
    private IReportDataProvider dataProvider;

    /**
     * Объект-отрисовщик XSL-FO профиля.
     */
    private XslFoProfileWriter repGen;


    /**
     * Десериализованный профиль отчета.
     */
    private ReportClass reportProfile;


    /**
     * Коллекция переменных лэйаута.
     */
    private Dictionary layoutVars;

    /**
     * Кастомный объект, пользовательские данные.
     * @return пользовательские данные
     */
    public Object getCustomData() {
        return customData;
    }

    /**
     * Параметры отчета.
     * @return параметры отчёта
     */
    public ReportParams getParams() {
        return params;
    }

    /**
     * Провайдер данных.
     * @return провайдер данных
     */
    public IReportDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Объект-отрисовщик XSL-FO профиля.
     * @return отрисовщик XSL-FO профиля
     */
    public XslFoProfileWriter getRepGen() {
        return repGen;
    }

    /**
     * Десериализованный объект профиля отчета.
     * @return десериализованный объект профиля отчета
     */
    public ReportClass getReportProfile() {
        return reportProfile;
    }

    /**
     * Коллекция переменных лэйаута.
     * @return коллекцию переменных лэйаута
     */
    public Dictionary getVars() {
        return layoutVars;
    }

    /**
     * Конструктор объекта.
     * @param repGen        - Генератор отчетов
     * @param params        - Параметры отчета
     * @param dataProvider  - Провайдер данных
     * @param reportProfile -
     * @param customData    - Пользовательские данные
     */
    public ReportLayoutData(XslFoProfileWriter repGen,
                            ReportParams params,
                            IReportDataProvider dataProvider,
                            ReportClass reportProfile,
                            Object customData) {
        this.customData = customData;
        this.params = params;
        this.dataProvider = dataProvider;
        this.repGen = repGen;
        this.reportProfile = reportProfile;
        this.layoutVars = new Hashtable<String, ByteArrayOutputStream>();
    }
}
