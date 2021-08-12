package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportObjectFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ExpressionEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ReportFormatterData;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EncodingTypeClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.FragmentClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HeaderLayoutClass;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;

/**
 * Лэйаут для построения стандартного заголовка.
 * Created by vsavenkov on 25.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class HeaderLayout extends ReportAbstractLayout {

    /**
     * Компонент формирования заголовка отчета.
     * Поддерживает следующую функциональность:
     * 1. Возможность построчного формирования заголовка в виде подзаголовков
     * 2. Возможность контекстной подстановки в заголовок данных из параметров отчета
     * 3. Возможность контекстной подстановки в подзаголовки данных из подключаемых источников данных
     * 4. Возможность подключения к обработке и форматированию данных каждого подзаголовка форматтеров
     * Заголовок строится построчно (тег профиля sub-header), в каждой строчке
     * пофрагментно (fragment), который может содержать произвольное количество
     * форматтеров для форматирования и обработки значений каждого фрагмента
     * Пример профиля:
     * <r:header-layout r:n="header" r:use-data-sources="DataSourceName">
     *     <r:fragment r:value="Приложение: {@AppName}">
     *         <r:formatters>
     *             <r:string-evaluator
     *                  r:append-before="Заголовок главный: "/>
     *         </r:formatters>
     *     </r:fragment>
     *     <r:sub-header>
     *         <r:fragment r:value="Наименование типа территории: {@AreaTypeName}">
     *             <r:formatters>
     *                 <r:string-evaluator r:append-after=", вот так!"/>
     *             </r:formatters>
     *         </r:fragment>
     *     </r:sub-header>
     *     <r:sub-header>
     *         r:fragment r:value="ИД типа территории: {#ObjectID}"/>
     *     </r:sub-header>
     * </r:header-layout>
     * @param layoutProfile - Профиль
     * @param layoutData    - Параметры
     */
    @Override
    protected void doMake(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) throws XMLStreamException {

        HeaderLayoutClass profile = (HeaderLayoutClass) layoutProfile;
        ReportFormatterData formatterData = null;

        // Проверяем есть ли источник данных
        List<String> useDataSources = profile.getUseDataSources();
        if (useDataSources != null && useDataSources.size() != 0) {
        /* TODO: ждём готовности слоя доступа к данным
            try
            {
                // получаем ридер с данными
                DataTable LayoutDataTable = LayoutData.DataProvider.GetDataTable(useDataSources, null);
                if (LayoutDataTable.Rows.Count > 0)
                {
                    FormatterData = new ReportFormatterData(
                            LayoutData,
                            null,
                            null,
                            LayoutDataTable.Rows[0],
                            -1,
                            -1);
                }
            }
            catch (Exception oException)
            {
                throw new ReportException("Ошибка при получении данных для заголовка отчета", oException);
            }
        */
        }

        if (formatterData == null) {
            formatterData = new ReportFormatterData(layoutData, null);
        }

        // Получаем строку заголовка
        writeHeader(profile, formatterData);
        // Получаем подзаголовки
        writeSubHeaders(profile, formatterData);
    }

    /**
     * Формирует главный заголовок.
     * @param layoutProfile     - Профиль
     * @param formatterData     - Набор для выполнения форматирования
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    protected void writeHeader(HeaderLayoutClass layoutProfile, ReportFormatterData formatterData)
            throws XMLStreamException {
        String header = writeFragments(formatterData, layoutProfile.getFragment());
        if (StringUtils.isNotEmpty(header)) {
            formatterData.getRepGen().header(header);
        }
    }

    /**
     * Формирует один (под)заголовок.
     * @param formatterData     - Набор для выполнения форматирования
     * @param fragments         - Массив фрагментов (под)заголовка
     * @return String   - возвращает строку сформированного подзаголовка
     */
    protected String writeFragments(ReportFormatterData formatterData,  List<FragmentClass> fragments) {
        if (fragments == null) {
            return null;
        }

        StringBuilder resultStr = new StringBuilder();  // текст
        MacroProcessor processor = new MacroProcessor(formatterData);

        for (FragmentClass fragment : fragments) {
            String header = processor.setAndProcess(fragment.getValue(), null);
            if (StringUtils.isNotEmpty(fragment.getHideIf())) {
                // вычисляем значение признака того, что фрагмент показывать не надо
                boolean ifHide = Converter.toBoolean(
                        ExpressionEvaluator.evaluate(fragment.getHideIf(), formatterData).toString());

                if (ifHide) {
                    continue;
                }
            }

            // Если указано, что внутри текст (или ничего не указано)
            if (/* TODO: пока не понял где это может задаваться
                !Fragment.encodingSpecified || */ fragment.getEncoding() == EncodingTypeClass.TEXT) {
                header = StringEscapeUtils.escapeHtml4(header);
            }

            if (fragment.getFormatters() != null) {
                formatterData.setCurrentValue(header);
                for (JAXBElement<? extends AbstractFormatterClass> formatterNode
                        : fragment.getFormatters().getAbstractFormatter()) {
                    // в цикле по каждому форматтеру
                    AbstractFormatterClass formatterClass = formatterNode.getValue();
                    ReportObjectFactory.getFormatter(formatterClass).execute(formatterClass, formatterData);
                    header = formatterData.getCurrentValue().toString();
                }
            }
            resultStr.append(header);
        }

        return resultStr.toString();
    }

    /**
     * Формирует все под-заголовки.
     * @param layoutProfile     - Профиль
     * @param formatterData     - Набор для выполнения форматирования
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    protected void writeSubHeaders(HeaderLayoutClass layoutProfile, ReportFormatterData formatterData) throws
            XMLStreamException {

        if (layoutProfile.getSubHeader() != null) {
            // Получаем строки подзаголовков
            for (HeaderLayoutClass.SubHeaderClass subHeaderProfile : layoutProfile.getSubHeader()) {
                String subHeader = writeFragments(formatterData, subHeaderProfile.getFragment());
                if (StringUtils.isNotEmpty(subHeader)) {
                    formatterData.getRepGen().addSubHeader(subHeader);
                }
            }
        }
    }

}
