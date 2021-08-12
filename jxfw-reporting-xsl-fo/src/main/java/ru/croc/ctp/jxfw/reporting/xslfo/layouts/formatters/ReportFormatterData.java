package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataRow;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;

import java.util.Dictionary;

/**
 * Набор данных передаваемых форматеру для обработки.
 * Created by vsavenkov on 06.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ReportFormatterData {

    /**
     * Данные лэйаута.
     */
    protected ReportLayoutData layoutData;

    /**
     * Текущее значение.
     */
    protected Object currentValue;

    /**
     * Чистое текущее значение.
     */
    protected Object rawCurrentValue;

    /**
     * Имя класса стиля.
     */
    protected String className;

    /**
     * Строчка данных.
     */
    protected IDataRow dataRow;

    /**
     * Номер текущей строки.
     */
    protected int currentRowNumber;

    /**
     * Номер текущей колонки.
     */
    protected int currentColumnNumber;

    /**
     * Данные лэйаута.
     * @return данные лэйаута.
     */
    public ReportLayoutData getLayoutData() {
        return layoutData;
    }

    /**
     * Текущее значение.
     * @return текущее значение
     */
    public Object getCurrentValue() {
        return currentValue;
    }

    /**
     * Текущее значение.
     * @param currentValue текущее значение
     */
    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Текущее значение.
     * @return чистое текущее значение
     */
    public Object getRawCurrentValue() {
        return rawCurrentValue;
    }

    public void setRawCurrentValue(Object rawCurrentValue) {
        this.rawCurrentValue = rawCurrentValue;
    }

    /**
     * Имя класса стиля.
     * @return имя класса стиля
     */
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Номер строки.
     * @return номер строки
     */
    public int getRowNum() {
        return currentRowNumber;
    }

    /**
     * Номер столбца.
     * @return номер столбца
     */
    public int getColNum() {
        return currentColumnNumber;
    }

    /**
     * Кастомный объект, пользовательские данные.
     * @return пользовательские данне
     */
    public Object getCustomData() {
        return layoutData.getCustomData();
    }

    /**
     * Параметры отчета.
     * @return параметры отчёта
     */
    public ReportParams getParams() {
        return layoutData.getParams();
    }

    /**
     * Провайдер данных.
     * @return провайдер данных
     */
    public IReportDataProvider getDataProvider() {
        return layoutData.getDataProvider();
    }

    /**
     * Объект-отрисовщик XSL-FO профиля.
     * @return отрисовщик XSL-FO профиля
     */
    public XslFoProfileWriter getRepGen() {
        return layoutData.getRepGen();
    }

    /**
     * Коллекция переменных лэйаута.
     * @return коллекцию переменных лэйоута
     */
    @SuppressWarnings("rawtypes")
    public Dictionary getVars() {
        return layoutData.getVars();
    }

    /**
     * Признак того что значнение CurrentValue непустое.
     * @return true если текущее знаечние пустое
     */
    public boolean isEmptyValue() {
        return Converter.isNull(currentValue);
    }

    /**
     * Конструктор объекта.
     * @param layoutData    - Данные лэйаута
     * @param currentValue  - Текущее значение
     * @param className     - Имя класса стиля
     * @param currentRow    - Объект текущей строки таблицы отчета
     * @param rowNum        - Номер строки
     * @param columnNum     - Номер столбца
     */
    public ReportFormatterData(ReportLayoutData layoutData, Object currentValue, String className, IDataRow currentRow,
            int rowNum, int columnNum) {

        this.layoutData = layoutData;
        this.currentValue = currentValue;
        this.rawCurrentValue = currentValue;
        this.className = className;
        this.dataRow = currentRow;
        this.currentRowNumber = rowNum;
        this.currentColumnNumber = columnNum;
    }

    /**
     * Конструктор объекта.
     * @param layoutData   - Данные лэйаута
     * @param currentValue - Текущее значение
     */
    public ReportFormatterData(ReportLayoutData layoutData, Object currentValue) {
        this.layoutData = layoutData;
        this.currentValue = currentValue;
        this.currentRowNumber = -1;
        this.currentColumnNumber = -1;
    }

    /**
     * Делает копию форматтера.
     * @return ReportFormatterData  - клон форматтера
     */
    public ReportFormatterData clone() {

        return new ReportFormatterData(layoutData, currentValue, className, dataRow, currentRowNumber,
                currentColumnNumber);
    }
}