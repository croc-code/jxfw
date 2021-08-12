package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.IReportFormatter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportObjectFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataReader;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataRow;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataTable;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ExpressionEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ReportFormatterData;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.DetailDataClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDataClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDataFragmentClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDetailLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;

/**
 * Класс бизнес-логики для master-detail-layout'a.
 * Created by vsavenkov on 04.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class MasterDetailLayout extends ReportAbstractLayout {

    /**
     * Реализация абстрактного метода DoMake из базового класса.
     * @param layoutProfile - десереализованный объект лейата
     * @param layoutData    - Данные, используемые при генерации лейута
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    protected void doMake(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) throws XMLStreamException {

        // Получаю объект нужного типа
        MasterDetailLayoutClass layout = (MasterDetailLayoutClass) layoutProfile;

        // ридер по данным лэйаута
        IDataReader layoutDataReader;

        // получение данных
        // на самом деле LayoutDataReader - Это TableDataReader
        layoutDataReader = getData(layout, layoutData);

        // проверка данных на пустоту
        if (isNoData(layoutDataReader)) {
            // выводим сообщение об отсутствии данных
            writeNoDataMessage(layoutData, layout.getNoDataMessage());

            // закрывем ридер (одновременно закрывается коннекшн)
            layoutDataReader.close();

            return;
        }

        try {
            // рисуем лэйаут по полученным данным
            writeLayout(layout, layoutDataReader, layoutData);
        } finally {
            // закрывем ридер (одновременно закрывается коннекшн)
            layoutDataReader.close();
        }
    }

    /**
     * Непосредственно отрисовка лейата.
     * @param layout        - десереализованный объект лейата
     * @param reader        - ридер, используемый для генерации XSL-FO
     * @param layoutData    - Данные, используемые при генерации лейута
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private static void writeLayout(MasterDetailLayoutClass layout, IDataReader reader, ReportLayoutData layoutData)
            throws XMLStreamException {
        IDataRow currentRow;    // текущий обрабатываемый ряд

        // ДатаТейбл с данными
        // !!! Reader уже на позиции not prior to the first record !!!
        IDataTable table = layoutData.getDataProvider().convertIDataReaderToDataTable(reader);

        // Скопирую начальный набор параметров. Это параметры отчета. Они не должны быть переопределены.
        ReportParams globalParams = new ReportParams(layoutData.getParams());

        for (int i = 0; i < table.getRows().size(); i++) {
            currentRow = table.getRows().get(i);

            // проходим по всем данным мастера
            for (Object item : layout.getMasterDataOrDetailData()) {
                if (item instanceof MasterDataClass) {
                    // Рисую  master-data часть
                    writeMasterData(currentRow, layoutData, layout, (MasterDataClass) item, i);
                } else if (item instanceof DetailDataClass) {
                    DetailDataClass detailData = (DetailDataClass)item;

                    // Проверка на то, что глобальные параметры отчета не переопределены
                    if (detailData.getParamColumn() != null) {
                        for (DetailDataClass.ParamColumnClass paramColumn : detailData.getParamColumn()) {
                            if (globalParams.isParamExists(paramColumn.getName())) {
                                // Решили, что правильнее выбрасывать эксепшн и не давать разработчику отчетов
                                // переопределять имена параметров отчета.
                                throw new ArgumentException("Имя параметра " + paramColumn.getName()
                                        + " элемента detail-data в лейауте " + layout.getN()
                                        + " совпадает с существующим параметром отчета.");
                            }
                        }
                    }

                    // Нужно нарисовать вложенный лейаут.
                    writeDetailData(currentRow, layoutData, layout, detailData);

                    // Удаление из LayoutData.Params добавленных для данного вложенного лейаута параметров
                    if (detailData.getParamColumn() != null) {
                        for (DetailDataClass.ParamColumnClass paramColumn : detailData.getParamColumn()) {
                            // Удалим параметр...
                            layoutData.getParams().removeParam(paramColumn.getName());
                        }
                    }
                }
            }
            // Добавляю пробел для разделения элементов
            layoutData.getRepGen().rawOutput(StringUtils.EMPTY);
        }
    }

    /**
     * Предназначен для вывода в поток с XSL-FO текущего master-data элемента.
     * @param currentRow            - Текущий DataRow
     * @param layoutData            - Данные, используемые при построении лейаута
     * @param masterDetailLayout    - профиль лейаута
     * @param masterdata            - masterdata
     * @param currentRowIdx         - индекс текущей строки
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private static void writeMasterData(IDataRow currentRow, ReportLayoutData layoutData,
            MasterDetailLayoutClass masterDetailLayout, MasterDataClass masterdata, int currentRowIdx)
            throws XMLStreamException {

        String fragmentValue;
        StringBuilder value = new StringBuilder();
        MacroProcessor processor;                   // Процессор выражений

        // получаем объект, с которым работают форматтеры и эвалуаторы
        ReportFormatterData formatterData = new ReportFormatterData(layoutData, null, null, currentRow, currentRowIdx,
                -1);

        // если есть атрибут hide-if
        String hideIf = masterdata.getHideIf();
        if (StringUtils.isNotEmpty(hideIf)) {
            // вычисляем значение признака того, что фрагмент показывать не надо
            Object expressionValue = ExpressionEvaluator.evaluate(hideIf, formatterData);
            if (expressionValue != null && Converter.toBoolean(expressionValue.toString())) {
                return;
            }
        }

        // имя класса стиля для данного мастер-дата элемента
        String className = StringUtils.isEmpty(masterdata.getStyleClass()) ? XslFoProfileWriter.MASTER_DATA_CLASS_NAME
                : masterdata.getStyleClass();

        // Получаю процессор выражений
        processor = new MacroProcessor(formatterData);

        // Рисую фрагменты текущего мастер-дата элемента
        if (masterdata.getMasterDataFragment() != null) {
            // Для каждого фрагмента рисую его значение
            for (MasterDataFragmentClass item : masterdata.getMasterDataFragment()) {
                if (item.getValue() != null) {
                    // Определим инкодинг
                    /* TODO: пока не понял где это может задаваться
                    if (oItem.encodingSpecified)
                    {
                        // Если указано, что внутри текст
                            if (oItem.encoding == encodingtype.text)
                                sFragmentValue = StringEscapeUtils.escapeHtml4(oItem.getValue());
                            else
                                sFragmentValue = oItem.value;
                    }
                    else
                    */
                    fragmentValue = StringEscapeUtils.escapeHtml4(item.getValue());

                    // Отпроцессим значение
                    fragmentValue = processor.setAndProcess(fragmentValue, null);

                    // Отформаттим значение
                    if (item.getFormatters() != null) {
                        formatterData.setCurrentValue(fragmentValue);

                        // в цикле по каждому форматтеру
                        for (JAXBElement<? extends AbstractFormatterClass> formatterNode
                                : item.getFormatters().getAbstractFormatter()) {
                            AbstractFormatterClass formatterClass = formatterNode.getValue();
                            IReportFormatter formatter = ReportObjectFactory.getFormatter(formatterClass);
                            formatter.execute(formatterClass, formatterData);
                            fragmentValue = formatterData.getCurrentValue().toString();
                        }
                    }

                    value.append(fragmentValue);
                }
            }

            // Рисую текущий мастер-дата элемент
            layoutData.getRepGen().rawOutput(value.toString(), className);

            value.setLength(0);
        }
    }

    /**
     * Производит отрисовку детализирующего лейаута.
     * @param currentRow            - текущая строка
     * @param layoutData            - Данные, используемые при генерации лейута
     * @param masterDetailLayout    - профиль лейаута
     * @param detaildata            - detaildata
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private static void writeDetailData(IDataRow currentRow, ReportLayoutData layoutData,
            MasterDetailLayoutClass masterDetailLayout, DetailDataClass detaildata) throws XMLStreamException {

        // Проверка, чтобы не получилось рекурсии
        if (detaildata.getDetailLayoutName() == masterDetailLayout.getN()) {
            throw new ReportException("Нельзя в качестве вложенного лейаута устанавливать ссылку \"на себя\"!");
        }

        // получаем объект, с которым работают форматтеры и эвалуаторы
        ReportFormatterData formatterData = new ReportFormatterData(layoutData, null, null, currentRow, -1, -1);

        // если есть атрибут hide-if
        String hideIf = detaildata.getHideIf();
        if (StringUtils.isNotEmpty(hideIf)) {
            // вычисляем значение признака того, что фрагмент показывать не надо
            Object expressionValue = ExpressionEvaluator.evaluate(hideIf, formatterData);
            if (expressionValue != null && Converter.toBoolean(expressionValue.toString())) {
                return;
            }
        }

        AbstractLayoutClass layoutProfile = LayoutFinder.findLayout(layoutData.getReportProfile(),
                detaildata.getDetailLayoutName());
        if (layoutProfile == null) {
            throw new ReportException("Не найден в профиле отчета лейаут \"" + detaildata.getDetailLayoutName()
                    + "\".");
        }

        // Подготовлю данные для построения вложенного лейаута
        ReportLayoutData data = new ReportLayoutData(layoutData.getRepGen(), layoutData.getParams(),
                layoutData.getDataProvider(), layoutData.getReportProfile(), null);

        // Подготовка инициализирующих параметров
        if (detaildata.getParamColumn() != null) {
            // Получаю процессор выражений
            MacroProcessor processor = new MacroProcessor(formatterData);

            for (DetailDataClass.ParamColumnClass paramColumn : detaildata.getParamColumn()) {
                Object currentValue = processor.setAndProcess(wrapParamColumnName(paramColumn), null);

                if (data.getParams().isParamExists(paramColumn.getName())) {
                    data.getParams().getParam(paramColumn.getName()).setValue(currentValue);
                } else {
                    ReportParams.ReportParam newParam = initFakeParam(paramColumn.getName(), currentValue);
                    data.getParams().addParam(newParam);
                }
            }
        }

        // Рисую вложенный лейаут
        makeLayout(layoutProfile, data);

        layoutData.getRepGen().emptyBody();
    }

    private static String wrapParamColumnName(DetailDataClass.ParamColumnClass paramColumn) {
        return StringUtils.join("{#", paramColumn.getName(), "}");
    }

    /**
     * Инициализирует параметр для передачи его в детализирующий лейаут.
     * @param paramColumn   - Имя параметра
     * @param currentValue  - Наименование параметра
     * @return ReportParams.ReportParam - возвращает инициализированный параметр
     */
    private static ReportParams.ReportParam initFakeParam(String paramColumn, Object currentValue) {

        ReportClass.ParamsClass.ParamClass newParam = new ReportClass.ParamsClass.ParamClass();
        newParam.setN(paramColumn);
        newParam.setRequired(true);
        newParam.setProtected(false);
        switch (currentValue.getClass().getSimpleName()) {
            case "Date":
                newParam.setVt(VarTypesClass.DATE_TIME_TZ);
                break;
            case "Integer":
                newParam.setVt(VarTypesClass.I_4);
                break;
            case "Double":
                newParam.setVt(VarTypesClass.R_8);
                break;
            case "BigDecimal":
                newParam.setVt(VarTypesClass.FIXED_14_4);
                break;
            case "Boolean":
                newParam.setVt(VarTypesClass.BOOLEAN);
                break;
            case "UUID":
                newParam.setVt(VarTypesClass.UUID);
                break;
            default:
                newParam.setVt(VarTypesClass.STRING);
                break;
        }

        ReportParams.ReportParam param = new ReportParams.ReportParam(newParam);

        param.setValue(currentValue);

        return param;
    }

    /**
     * Рисует вложенный лейаут.
     * @param profile   - Десериализованный лейаут
     * @param data      - Данные, используемые при генерации лейута
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private static void makeLayout(AbstractLayoutClass profile, ReportLayoutData data) throws XMLStreamException {

        // Получим очередной Layout
        IReportLayout layout = ReportLayoutFactory.getInstance(profile);
        // И отрисуем его
        layout.make(profile, data);
    }

    /**
     * получает данные лэйаута в виде ридера.
     * @param layoutProfile     - профиль лэйаута
     * @param layoutData        - Данные лэйаута
     * @return IDataReader  - ридер с данными
     */
    protected IDataReader getData(MasterDetailLayoutClass layoutProfile, ReportLayoutData layoutData) {

        // наименование источника данных
        String dataSourceName = layoutProfile.getDataSourceName();
        // производим возможные подстановки из параметров
        dataSourceName = MacroProcessor.process(dataSourceName, layoutData);
        try {
            // получаем ридер от провайдера данных
            return layoutData.getDataProvider().getDataReader(dataSourceName, layoutData.getCustomData());
        } catch (Exception exc) {
            // генерим исключение
            throw new ReportException("Ошибка при получении данных для отображения лэйаута", exc);
        }
    }

    /**
     * проверяет наличие данных в ридере.
     * @param reader    - ридер
     * @return boolean  - возвращает true, в случае пустоты ридера. иначе false
     */
    protected boolean isNoData(IDataReader reader) {

        // попытка считать первую строку
        return (!reader.read());
    }
}
