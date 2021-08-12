package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStylesCollection;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractPageRegionClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AlignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.LayoutMasterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.PageFormatClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.RegionBodyClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.RegionFooterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.RegionHeaderClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ValignClass;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 * Класс XslFOProfileWriter.
 * Служит для отрисовки XSL FO профиля отчета
 * Created by vsavenkov on 06.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class XslFoProfileWriter {

    private static final Logger logger = LoggerFactory.getLogger(XslFoProfileWriter.class);

    //region константы

    /**
     * XSL FO namespace.
     */
    protected static final String XSLFO_NAMESPACE = "http://www.w3.org/1999/XSL/Format";

    /**
     * префикс для XSL-FO элементов.
     */
    protected static final String XSLFO_PREFIX = "fo";

    /**
     * Корневой элемент отчета.
     */
    protected static final String XSLFO_ROOT_ELEMENT = "root";

    /**
     * Элемент заголовка отчета.
     */
    protected static final String XSLFO_TITLE_ELEMENT = "title";

    /**
     * Элемент ссылки.
     */
    protected static final String XSLFO_LINK_ELEMENT = "basic-link";

    /**
     * Блок данных.
     */
    protected static final String XML_BLOCK_IN = "block";

    /**
     * Внешний объект в fo-профиле, элемент instream-foreign-object.
     */
    protected static final String XML_INLINEOBJECT_IN = "instream-foreign-object";

    /**
     * Элемент layout-master-set.
     */
    protected static final String XML_LAYOUTMASTER_IN = "layout-master-set";

    /**
     * Элемент simple-page-master.
     */
    protected static final String XML_SIMPLEPAGEMASTER_IN = "simple-page-master";

    /**
     * Элемент region-before.
     */
    protected static final String XML_REGIONBEFORE_IN = "region-before";

    /**
     * Элемент region-body.
     */
    protected static final String XML_REGIONBODY_IN = "region-body";

    /**
     * Элемент region-after.
     */
    protected static final String XML_REGIONAFTER_IN = "region-after";

    /**
     * Элемент flow.
     */
    protected static final String XML_FLOW_IN = "flow";

    /**
     * Элемент static-content.
     */
    protected static final String XML_STATICCONTENT_IN = "static-content";

    /**
     * Элемент page-sequence.
     */
    protected static final String XML_PAGESEQUENCE_IN = "page-sequence";

    /**
     * Элемент - таблица отчета.
     */
    protected static final String XML_TABLE_IN = "table";

    /**
     * Элемент - блок описания колонок отчета.
     */
    protected static final String XML_TABLEHEADER_IN = "table-header";

    /**
     * Элемент - тело таблицы отчета.
     */
    protected static final String XML_TABLEBODY_IN = "table-body";

    /**
     * Элемент - блок подвала отчета.
     */
    protected static final String XML_TABLEFOOT_IN = "table-footer";

    /**
     * Элемент - описание колонки.
     */
    protected static final String XML_COL_IN = "table-column";

    /**
     * Элемент - описание сточки.
     */
    protected static final String XML_ROW_IN = "table-row";

    /**
     * Элемент - описание колонки.
     */
    protected static final String XML_TABLECELL_IN = "table-cell";

    /**
     * Аттрибут - имя региона в page-master'e.
     */
    protected static final String XML_ATTRIBUTE_FLOWNAME = "flow-name";

    /**
     * Аттрибут master-reference.
     */
    protected static final String XML_ATTRIBUTE_MASTERREFERENCE = "master-reference";

    /**
     * Атрибут - объединение нескольких ячеек.
     */
    protected static final String XML_ATTRIBUTE_COLSPAN = "number-columns-spanned";

    /**
     * Атрибут - объединение нескольких строк.
     */
    protected static final String XML_ATTRIBUTE_ROWSPAN = "number-rows-spanned";

    /**
     * Атрибут, содержащий ширину.
     */
    protected static final String XML_ATTRIBUTE_COL_WIDTH = "column-width";

    /**
     * Аттрибут - номер колонки.
     */
    protected static final String XML_ATTRIBUTE_COL_NUMBER = "column-number";

    /**
     * Аттрибут - тип данных в fo-блоке.
     */
    protected static final String XML_ATTRIBUTE_VT = "vt";

    /**
     * Значение атрибута content-type для пользовательских скриптов.
     */
    protected static final String SCRIPT_CONTENT_TYPE = "text/script";

    /**
     * Размер отступов по умолчанию.
     */
    private static final String DEFAULT_MARGIN = "20mm";

    /**
     * Имя автогенерируемого page-master'a.
     */
    private static final String DEFAULT_PAGE_MASTER_NAME = "default-page-master";

    /**
     * Имя региона, представляющего верхний колонтитул.
     */
    private static final String REGION_BEFORE_NAME = "PageHeader";

    /**
     * Имя региона, представляющего тело отчета.
     */
    private static final String REGION_BODY_NAME = "PageBody";

    /**
     * Имя региона, представляющего нижний колонтитул.
     */
    private static final String REGION_AFTER_NAME = "PageBottom";

    /**
     * Имя класса стиля для отображения таблицы.
     */
    protected static final String TABLE_STYLE_CLASS_NAME = "TABLE";

    /**
     * Имя класса стиля для отображения Header.
     */
    protected static final String HEADER_STYLE_CLASS_NAME = "HEADER";

    /**
     * Имя класса стиля для отображения SubHeader.
     */
    protected static final String TITLE_STYLE_CLASS_NAME = "TITLES";

    /**
     * Имя класса стиля для отображения emptyBody.
     */
    protected static final String EMPTYBODY_STYLE_CLASS_NAME = "EMPTY";

    /**
     * наименование класса заголовка таблица.
     */
    public static final String THEADER_CLASS_NAME = "TABLE_HEADER";

    /**
     * наименование класса тела таблица.
     */
    public static final String CELL_CLASS_NAME = "CELL_CLASS";

    /**
     * наименование класса строки подзаголовка в таблице.
     */
    public static final String SUBTITLE_CLASS_NAME = "SUBTITLE";

    /**
     * наименование класса строки подитогов в таблице.
     */
    public static final String SUBTOTAL_CLASS_NAME = "SUBTOTAL";

    /**
     * наименование класса стиля для вывода данных в master-data-header.
     */
    public static final String MASTER_DATA_CLASS_NAME = "MASTER-DATA-CLASS";

    //endregion

    /**
     * Writer для написания XSL FO профиля.
     */
    protected XMLStreamWriter xmlStreamWriter;

    /**
     * Поток, куда пишется профиль.
     */
    protected OutputStream profileStream;

    /**
     * Имя Page-master'a по умолчанию.
     */
    protected String defaultPageMaster;

    /**
     * Текущий page-master по которому рисуем отчет.
     */
    protected String currentPageMaster;

    /**
     * Список скриптов.
     */
    protected ArrayList scriptsList;

    /**
     * Коллекция "правильных" стилей.
     */
    private ReportStylesCollection reportStylesCollection = new ReportStylesCollection();

    /**
     * Коллекция "правильных" стилей.
     * @return коллекцию стилей
     */
    // TODO: пока пришлось public сделать - было internal
    public  ReportStylesCollection getStylesCollection() {
        return reportStylesCollection;
    }

    // TODO: пока пришлось public сделать - было internal
    public void setStylesCollection(ReportStylesCollection stylesCollection) {
        reportStylesCollection = null != stylesCollection ? stylesCollection : new ReportStylesCollection();
    }

    /**
     * Таблица.
     */
    private TablePresentation currentTable;

    /**
     * Конструктор.
     * @param outputStream Поток, куда предполагается писать xslfo профиль
     * @param reportClass объект {@link ReportClass}, полученный из профиля отчета
     */
    public XslFoProfileWriter(
            OutputStream outputStream,
            ReportClass reportClass
    ) {
        profileStream = outputStream;
        fillReportStyleCollections(reportClass);
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            xmlStreamWriter = factory.createXMLStreamWriter(profileStream, StandardCharsets.UTF_8.name());

            // TODO: надо будет вывод форматировать с помощью TransformerFactory
            //m_XmlWriter.Formatting = Formatting.Indented;
            scriptsList = new ArrayList();

            //Начинаем документ:
            xmlStreamWriter.writeStartDocument();
            //Открываем документ:
            xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XSLFO_ROOT_ELEMENT, XSLFO_NAMESPACE);
            xmlStreamWriter.writeNamespace(XSLFO_PREFIX, XSLFO_NAMESPACE);
        } catch (XMLStreamException e) {
            throw new ReportException("Error in constructor XslFoProfileWriter", e);
        }
    }

    /**
     * Метод создает объект fo:title, в который записывается заголовок последовательности страниц.
     * @param pageSequenceTitle    - Заголовок последовательности страниц
     * @throws XMLStreamException выбрасывается из методов {@link XslFoProfileWriter}
     */
    public void writeTitle(String pageSequenceTitle) throws XMLStreamException {
        if (pageSequenceTitle != null) {
            xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XSLFO_TITLE_ELEMENT, XSLFO_NAMESPACE);
            xmlStreamWriter.writeCharacters(StringEscapeUtils.escapeHtml4(pageSequenceTitle));
            xmlStreamWriter.writeEndElement();
        }
    }

    /**
     * Генерит объект layoutmaster с установками по умолчанию и
     * отрисовавает в XSL FO профиле элемент layout-master-set.
     * Используется в случае, если в профиле отчета не задан элемент layout-master.
     * @throws Exception выбрасывается {@link #writeLayoutMaster(LayoutMasterClass)}
     */
    public void writeLayoutMaster() throws Exception {
        LayoutMasterClass.PageMasterClass pm = new LayoutMasterClass.PageMasterClass();
        pm.setN(DEFAULT_PAGE_MASTER_NAME);
        pm.setPageFormat(PageFormatClass.A_4);
        pm.setRegionBody(new RegionBodyClass());
        pm.getRegionBody().setValign("before");
        pm.getRegionBody().setMargin(DEFAULT_MARGIN);

        LayoutMasterClass layoutMasterClass = new LayoutMasterClass();
        layoutMasterClass.setPageMaster(Arrays.asList(pm));

        writeLayoutMaster(layoutMasterClass);
    }

    /**
     * Отрисовавает в XSL FO профиле элемент layout-master-set.
     * @param layoutMasterClass объект layoutmaster, полученный из профиля отчета
     * @throws Exception при отсутсвии элемента page-master
     */
    public void writeLayoutMaster(LayoutMasterClass layoutMasterClass) throws Exception {
        if (layoutMasterClass.getPageMaster() != null) {
            // Рисую <fo:layout-master-set>
            try {
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_LAYOUTMASTER_IN, XSLFO_NAMESPACE);

                for (LayoutMasterClass.PageMasterClass pm : layoutMasterClass.getPageMaster()) {
                    //<fo:simple-page-master>
                    xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_SIMPLEPAGEMASTER_IN, XSLFO_NAMESPACE);
                    //master-name
                    xmlStreamWriter.writeAttribute("master-name", pm.getN());
                    //если defaultPageMaster не установлен, установлю
                    if (defaultPageMaster == null) {
                        defaultPageMaster = pm.getN();
                    }
                    //page-width
                    xmlStreamWriter.writeAttribute("page-width", getPageWidth(pm));
                    //page-height
                    xmlStreamWriter.writeAttribute("page-height", getPageHeight(pm));
                    //reference-orientation
                    xmlStreamWriter.writeAttribute("reference-orientation", getPageOrientation(pm));

                    //рисуем region-body
                    //!region-body Д.б. по-любому!
                    if (pm.getRegionBody() == null) {
                        RegionBodyClass regionBody = new RegionBodyClass();
                        regionBody.setValign("before");
                        regionBody.setMargin(DEFAULT_MARGIN);
                        pm.setRegionBody(regionBody);
                    }

                    //<fo:region-body>
                    xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_REGIONBODY_IN, XSLFO_NAMESPACE);
                    setRegionAttributes(pm.getRegionBody());
                    //</fo:region-body>
                    xmlStreamWriter.writeEndElement();


                    //если задан pageheader, рисую region-before
                    if (pm.getRegionHeader() != null) {
                        //<fo:region-before>
                        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_REGIONBEFORE_IN, XSLFO_NAMESPACE);

                        if (pm.getRegionHeader().getPaddingLeft() == null) {
                            pm.getRegionHeader().setPaddingLeft(null != pm.getRegionBody().getMargin()
                                    ? pm.getRegionBody().getMargin() : DEFAULT_MARGIN);
                        }

                        if (pm.getRegionHeader().getPaddingRight() == null) {
                            pm.getRegionHeader().setPaddingRight(null != pm.getRegionBody().getMargin()
                                    ? pm.getRegionBody().getMargin() : DEFAULT_MARGIN);
                        }

                        if (pm.getRegionHeader().getExtent() == null) {
                            pm.getRegionHeader().setExtent(DEFAULT_MARGIN);
                        }

                        setRegionAttributes(pm.getRegionHeader());
                        //</fo:region-before>
                        xmlStreamWriter.writeEndElement();
                    }

                    // если задан pagebottom, рисую region-after
                    if (pm.getRegionFooter() != null) {
                        //<fo:region-after>
                        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_REGIONAFTER_IN, XSLFO_NAMESPACE);

                        if (pm.getRegionFooter().getPaddingLeft() == null) {
                            pm.getRegionFooter().setPaddingLeft(null == pm.getRegionBody().getMargin()
                                    ? pm.getRegionBody().getMargin() : DEFAULT_MARGIN);
                        }

                        if (pm.getRegionFooter().getPaddingRight() == null) {
                            pm.getRegionFooter().setPaddingRight(null == pm.getRegionBody().getMargin()
                                    ? pm.getRegionBody().getMargin() : DEFAULT_MARGIN);
                        }

                        if (pm.getRegionFooter().getExtent() == null) {
                            pm.getRegionFooter().setExtent(DEFAULT_MARGIN);
                        }

                        setRegionAttributes(pm.getRegionFooter());
                        //</fo:region-after>
                        xmlStreamWriter.writeEndElement();
                    }

                    //<//fo:simple-page-master>
                    xmlStreamWriter.writeEndElement();
                }

                // Рисую </fo:layout-master-set>
                xmlStreamWriter.writeEndElement();
            } catch (XMLStreamException e) {
                logger.error("Error by writing xml", e);
            }
        } else {
            throw new Exception("Не задан элемент page-master, при заданном элементе layout-master");
        }
    }

    /**
     * Функция орисовавает в текущем(последнем) page-sequence'e элемент page-header(fo:static-content).
      * @throws XMLStreamException выбрасывается xmlStreamWriter
     */
    public void startPageHeader() throws XMLStreamException {
        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_STATICCONTENT_IN, XSLFO_NAMESPACE);
        xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_FLOWNAME, REGION_BEFORE_NAME);
    }

    /**
     * Закрывает элемент fo:static-content.
     * @throws XMLStreamException выбрасывается xmlStreamWriter
     */
    public void endPageHeader() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
    }

    /**
     * Функция отрисовавает в текущем(последнем) page-sequence'e элемент page-body(fo:flow).
     */
    public void startPageBody() {
        try {
            xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_FLOW_IN, XSLFO_NAMESPACE);
            xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_FLOWNAME, REGION_BODY_NAME);
        } catch (XMLStreamException e) {
            throw new ReportException("Error by write stratPageBody", e);
        }

    }

    /**
     * Закрывает элемент fo:flow.
     */
    public void endPageBody() {
        try {
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ReportException("Error by endPageBody", e);
        }
    }

    /**
     * Функция орисовавает в текущем(последнем) page-sequence'e элемент page-bottom(fo:static-content).
     * @throws XMLStreamException выбрасывается xmlStreamWriter
     */
    public void startPageBottom() throws XMLStreamException {
        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_STATICCONTENT_IN, XSLFO_NAMESPACE);
        xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_FLOWNAME, REGION_AFTER_NAME);
    }

    /**
     * Закрывает элемент fo:static-content.
     * @throws XMLStreamException выбрасывается xmlStreamWriter
     */
    public void endPageBottom() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
    }

    /**
     * Функция начинает элемент fo:page-sequence.
     * Используется в случае, если в профиле отчета не задан ни один элемент r:page-sequence
     * @throws XMLStreamException выбрасывается xmlStreamWriter
     */
    public void startPageSequence() {
        try {
            xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_PAGESEQUENCE_IN, XSLFO_NAMESPACE);
            // т.к. page-sequence не задан, используем ссылку на первый page-master.
            xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_MASTERREFERENCE, defaultPageMaster);
            currentPageMaster = defaultPageMaster;

            //Если есть скрипты, добавляем flow со скриптами
            if (scriptsList.size() > 0) {
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_FLOW_IN, XSLFO_NAMESPACE);
                xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_FLOWNAME, REGION_BODY_NAME);
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
                for (int i = 0; i < scriptsList.size(); i++) {
                    // TODO: незнаю, насколько полноцнная замена writeRaw на writeCharacters
                    xmlStreamWriter.writeCharacters(scriptsList.get(i).toString());
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndElement();
                //Добавив скрипты один раз, нет смысла добавлять их ещё раз, в другой page-sequence,
                //поэтому очищу список
                scriptsList.clear();
            }
        } catch (XMLStreamException e) {
            throw new ReportException("Error by write start pageSequence",e);
        }
    }

    /**
     * Процедура отрисовывает элемент fo:page-sequence XSL FO профиля.
     * @param pageSequence  - объект Pagesequence, полученный из профиля отчета
     * @throws XMLStreamException выбрасывается xmlStreamWriter
     */
    public void startPageSequence(ReportClass.PageSequenceClass pageSequence) throws XMLStreamException {
        //Рисую <fo:page-sequence>
        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_PAGESEQUENCE_IN, XSLFO_NAMESPACE);
        //TODO: проверка, на наличие page-master'a с таким именем?
        xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_MASTERREFERENCE, pageSequence.getMasterReference());
        currentPageMaster = pageSequence.getMasterReference();

        //Если есть скрипты, добавляем flow со скриптами
        if (scriptsList.size() > 0) {
            for (int i = 0; i < scriptsList.size(); i++) {
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_FLOW_IN, XSLFO_NAMESPACE);
                xmlStreamWriter.writeAttribute(XML_ATTRIBUTE_FLOWNAME, REGION_BODY_NAME);
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
                xmlStreamWriter.writeCharacters(scriptsList.get(i).toString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndElement();
            }
            //Добавив скрипты один раз, нет смысла добавлять их ещё раз, в другой page-sequence,
            //поэтому очищу список
            scriptsList.clear();
        }
    }

    /**
     * Закрывает элемент fo:page-sequence.
     */
    public void endPageSequence() {
        //Рисую </fo:page-sequence>
        try {
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ReportException("Error endPageSequence", e);
        }
    }

    /**
     * Добавляет текст клиентского сценария (script) в результирующий отчет.
     * Результат работы: в выходной отчет добавляется блок "VBScript" или "JavaScript", содержащий текст сценария
     * @param strScriptBody     - тело клиентского сценария
     * @param strScriptLanguage - Язык скрипта
     */
    public void addScript(String strScriptBody, String strScriptLanguage) {
        StringBuilder scriptObject = new StringBuilder();
        scriptObject.append("<");
        scriptObject.append(XSLFO_PREFIX);
        scriptObject.append(":");
        scriptObject.append(XML_INLINEOBJECT_IN);
        scriptObject.append(" content-type=\"");
        scriptObject.append(SCRIPT_CONTENT_TYPE);
        scriptObject.append("\" ");
        // TODO: тут встречались вызовы AppendLine(string value) - при наличии времени можно поковыряться.
        // Предполагаю, что это влияет исключительно на внутреннее форматирование файла
        scriptObject.append("language=\"").append(strScriptLanguage).append("\"><![CDATA[");
        scriptObject.append(strScriptBody).append("]]>");
        scriptObject.append("</" + XSLFO_PREFIX + ":" + XML_INLINEOBJECT_IN + ">");

        scriptsList.add(scriptObject.toString());
    }

    /**
     * Добавляет ссылку на клиентский сценарий (script) в результирующий отчет.
     * @param strScriptSource   - ссылка на клиентский сценарий
     * @param strScriptLanguage - Язык скрипта
     * @param strScriptEncoding - Кодировка файла сценария
     */
    public void addScript(String strScriptSource, String strScriptLanguage, String strScriptEncoding) {
        StringBuilder scriptObject = new StringBuilder();
        scriptObject.append("<");
        scriptObject.append(XSLFO_PREFIX);
        scriptObject.append(":");
        scriptObject.append(XML_INLINEOBJECT_IN);
        scriptObject.append(" content-type=\"");
        scriptObject.append(SCRIPT_CONTENT_TYPE);
        scriptObject.append("\" ");
        if (StringUtils.isNotEmpty(strScriptEncoding)) {
            scriptObject.append("charset=\"").append(strScriptEncoding).append("\" ");
        }
        scriptObject.append("language=\"").append(strScriptLanguage).append("\" ");
        scriptObject.append("src=\"").append(strScriptSource).append("\" />");

        scriptsList.add(scriptObject.toString());
    }

    /**
     * Добавление к отчету описания стиля.
     * @param appName   - Значение заголовка
     */
    public void header(String appName) {
        header(appName, StringUtils.EMPTY);
    }

    /**
     * Формирование заголовка отчета.
     * @param appName   - Заголовок - имя приложения
     * @param header    - Заголовок - основной заголовок отчета
     */
    public void header(String appName, String header) {
        try {
            //Выведем заголовок приложения:
            xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
            ReportStyle rs = reportStylesCollection.getStyles().get(HEADER_STYLE_CLASS_NAME);
            rs.writeToXml(xmlStreamWriter);
            xmlStreamWriter.writeCharacters(appName);
            xmlStreamWriter.writeEndElement();

            //Выведем заголовок отчета
            if (header.length() > 0) {
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
                //TODO:добавить стиль отображения заголовка
                //ReportStyle.WriteToXml(m_XmlWriter, rs);
                xmlStreamWriter.writeCharacters(header);
                xmlStreamWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Error by write header", e);
        }
    }

    /**
     * Формирование подзаголовка отчета.
     * @param subHeader - подзаголовок
     */
    public void addSubHeader(String subHeader) {
        if (StringUtils.isEmpty(subHeader)) {
            return;
        }
        try {
            xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
            ReportStyle.writeToXml(xmlStreamWriter, reportStylesCollection.getStyles().get(TITLE_STYLE_CLASS_NAME));
            xmlStreamWriter.writeCharacters(subHeader);
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            new ReportException("Error by writing subheader", e);
        }

    }

    /**
     * Открывает формирование таблицы отчета.
     */
    public void tableStart() {
        tableStart(true, null, false);
    }

    /**
     * Открывает формирование таблицы отчета.
     * @param isShowHeader  - Отображать заголовок таблицы
     */
    public void tableStart(boolean isShowHeader) {
        tableStart(isShowHeader, null, false);
    }

    /**
     * Открывает формирование таблицы отчета.
     * @param isShowHeader  - Отображать заголовок таблицы
     * @param classDesc     - Класс стиля, назначаемый таблице
     * @param isTransp      - Признак необходимости транспонирования таблицы
     * @return TablePresentation    - Класс таблицы
     */
    public TablePresentation tableStart(boolean isShowHeader, String classDesc, boolean isTransp) {
        if (classDesc == null) {
            classDesc = TABLE_STYLE_CLASS_NAME;
        }

        if (!isTransp) {
            currentTable = new CommonTable(isShowHeader, reportStylesCollection.getReportStyle(classDesc,
                    false), null, xmlStreamWriter);
        } else {
            currentTable = new TransponedTable(isShowHeader, reportStylesCollection.getReportStyle(classDesc, false),
                    null, xmlStreamWriter);
        }

        return currentTable;
    }

    /**
     * Завершить формирование таблицы отчета.
     */
    public void tableEnd() {
        try {
            currentTable.endTable();
            currentTable = null;
        } catch (XMLStreamException e) {
            new ReportException("Error by writing tableEnd", e);
        }

    }

    /**
     * Начать строку таблицы.
     * @param elementClass  - Класс стиля, назначаемый строке таблицы
     */
    public void tableRowStart(String elementClass) {
        try {
            currentTable.startRow(reportStylesCollection.getReportStyle(elementClass, false));
        } catch (XMLStreamException e) {
            throw new ReportException("Error by writing tableRowStart", e);
        }
    }

    /**
     * Начать строку таблицы.
     * @see #tableRowStart(String) />.
     */
    public void tableRowStart()  {
        tableRowStart(null);
    }

    /**
     * Закончить формирование строки таблицы.
     */
    public void tableRowEnd() {
        try {
            currentTable.endRow();
        } catch (XMLStreamException e) {
            throw new ReportException("Error by writing tableRowEnd");
        }
    }

    /**
     * Добавить элемент отчета - ячейку таблицы. Содержимым ячейки таблицы будет результат
     * вызова {@link Object#toString} для аргумента {@code data}
     * Если переданный объект поддерживает интерфейс {@link List} (массив, коллекция и т.п.), то выводится
     * последовательность вызовов ToString для каждого элемента множества.
     * @param rowCellBuilder {@link RowCellBuilder}
     */
    public void tableRowAddCell(RowCellBuilder rowCellBuilder) {
        if (rowCellBuilder.elementClass == null) {
            rowCellBuilder.elementClass = CELL_CLASS_NAME;
        }
        try {
            currentTable.addCell(rowCellBuilder.data,
                    rowCellBuilder.colSpan,
                    rowCellBuilder.rowSpan,
                    reportStylesCollection.getReportStyle(rowCellBuilder.elementClass, false),
                    rowCellBuilder.type);
        } catch (XMLStreamException e) {
            throw new ReportException("Error by writing tableRowAddCell");
        }
    }

    /**
     * Говорит объекту TablePresentation пропустить одну ячейку; другими словами увеличить текущий счетчик ячеек.
     */
    public void tableRowOmitCell() {
        currentTable.omitCell();
    }

    /**
     * Начать колонку верхнего уровня в таблице отчета.
     * @return идентификатор сформированной колонки.
     */
    public int tableAddColumn() {
        ColumnBuilder columnBuilder = ColumnBuilder.create();
        return tableAddColumn(columnBuilder);
    }


    /**
     * Начать колонку верхнего уровня в таблице отчета.
     * Используется в последующих вызовах {@link #tableAddSubColumn}.
     * @param columnBuilder построитель колонки {@link ColumnBuilder}
     * @return идентификатор сформированной колонки.
     */
    public int tableAddColumn(ColumnBuilder columnBuilder) {
        if (columnBuilder.headerCellClass == null) {
            columnBuilder.setHeaderCellClass(THEADER_CLASS_NAME);
        }

        return currentTable.addColumn(columnBuilder.caption,
                columnBuilder.align,
                columnBuilder.valign,
                reportStylesCollection.getReportStyle(columnBuilder.columnClass, false),
                reportStylesCollection.getReportStyle(columnBuilder.headerCellClass, false),
                columnBuilder.columnWidth,
                columnBuilder.headerAlign,
                columnBuilder.headerValign,
                0);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @return Идентификатор сформированной колонки. Используется в последующих вызовах.
    */
    public int tableAddSubColumn(int parentColumn) {
        return tableAddSubColumn(parentColumn, StringUtils.EMPTY);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * Используется в последующих вызовах {@link XslFoProfileWriter#tableAddSubColumn}.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @param strCaption Заголовок колонки
    * @return Идентификатор сформированной колонки.
    */
    public int tableAddSubColumn(int parentColumn, String strCaption) {
        return tableAddSubColumn(parentColumn,
                strCaption,
                AlignClass.ALIGN_NONE,
                ValignClass.VALIGN_NONE,
                null,
                StringUtils.EMPTY,
                AlignClass.ALIGN_NONE,
                ValignClass.VALIGN_NONE,
                null);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * Используется в последующих вызовах {@link XslFoProfileWriter#tableAddSubColumn}.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @param strCaption Заголовок колонки
    * @param enAlign Горизонтальное выравнивание содержимого колонки
    * @return Идентификатор сформированной колонки.
    */
    public int tableAddSubColumn(int parentColumn, String strCaption, AlignClass enAlign) {
        return tableAddSubColumn(parentColumn,
                strCaption,
                enAlign,
                ValignClass.VALIGN_NONE,
                null,
                StringUtils.EMPTY,
                AlignClass.ALIGN_NONE,
                ValignClass.VALIGN_NONE,
                null);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * Используется в последующих вызовах {@link XslFoProfileWriter#tableAddSubColumn}.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @param strCaption Заголовок колонки
    * @param enAlign Горизонтальное выравнивание содержимого колонки
    * @param enVAlign Вертикальное выравнивание содержимого колонки
    * @return Идентификатор сформированной колонки.
    */
    public int tableAddSubColumn(int parentColumn, String strCaption, AlignClass enAlign, ValignClass enVAlign) {
        return tableAddSubColumn(parentColumn,
                strCaption,
                enAlign,
                enVAlign,
                null,
                StringUtils.EMPTY,
                AlignClass.ALIGN_NONE,
                ValignClass.VALIGN_NONE,
                null);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * Используется в последующих вызовах {@link XslFoProfileWriter#tableAddSubColumn}.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @param strCaption Заголовок колонки
    * @param enAlign Горизонтальное выравнивание содержимого колонки
    * @param enVAlign Вертикальное выравнивание содержимого колонки
    * @param strColumnWidth Ширина колонки
    * @return Идентификатор сформированной колонки.
    */
    public int tableAddSubColumn(int parentColumn,
                                 String strCaption,
                                 AlignClass enAlign,
                                 ValignClass enVAlign,
                                 String strColumnWidth) {
        return tableAddSubColumn(parentColumn,
                strCaption,
                enAlign,
                enVAlign,
                null,
                strColumnWidth,
                AlignClass.ALIGN_NONE,
                ValignClass.VALIGN_NONE,
                null);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * Используется в последующих вызовах {@link XslFoProfileWriter#tableAddSubColumn}.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @param strCaption Заголовок колонки
    * @param enAlign Горизонтальное выравнивание содержимого колонки
    * @param enVAlign Вертикальное выравнивание содержимого колонки
    * @param strColumnWidth Ширина колонки
    * @param enHeaderAlign Горизонтальное выравнивание заголовка колонки
    * @return Идентификатор сформированной колонки.
    *
    */
    public int tableAddSubColumn(int parentColumn,
                                 String strCaption,
                                 AlignClass enAlign,
                                 ValignClass enVAlign,
                                 String strColumnWidth,
                                 AlignClass enHeaderAlign) {
        return tableAddSubColumn(parentColumn,
                strCaption,
                enAlign,
                enVAlign,
                null,
                strColumnWidth,
                enHeaderAlign,
                ValignClass.VALIGN_NONE,
                null);
    }

    /**
    * Начать подчиненную колонку таблицы в отчете.
    * Используется в последующих вызовах {@link XslFoProfileWriter#tableAddSubColumn}.
    * @param parentColumn Идентификатор сформированной ранее колонки
    * @param strCaption Заголовок колонки
    * @param enAlign Горизонтальное выравнивание содержимого колонки
    * @param enVAlign Вертикальное выравнивание содержимого колонки
    * @param strColumnClass Класс стиля, назначаемый колонке (не ячейкам!!!)
    * @param strColumnWidth Ширина колонки
    * @param enHeaderAlign Горизонтальное выравнивание заголовка колонки
    * @param enHeaderVAlign Вертикальное выравнивание заголовка колонки
    * @param strHeaderCellClass Класс стиля, назначаемый ячейкам заголовка колонки
    * @return Идентификатор сформированной колонки.
    */
    public int tableAddSubColumn(int parentColumn,
                                 String strCaption,
                                 AlignClass enAlign,
                                 ValignClass enVAlign,
                                 String strColumnClass,
                                 String strColumnWidth,
                                 AlignClass enHeaderAlign,
                                 ValignClass enHeaderVAlign,
                                 String strHeaderCellClass) {
        if (strHeaderCellClass == null) {
            strHeaderCellClass = THEADER_CLASS_NAME;
        }
        return currentTable.addColumn(strCaption,
                enAlign,
                enVAlign,
                reportStylesCollection.getReportStyle(strColumnClass, false),
                reportStylesCollection.getReportStyle(strHeaderCellClass, false),
                strColumnWidth,
                enHeaderAlign,
                enHeaderVAlign,
                parentColumn);
    }

    /**
     * Запись тела пустого отчета (например, на случай отсутствия данных).
     * @throws XMLStreamException выбрасывается emptyBody(String strMessage)
     */
    public void emptyBody() throws XMLStreamException {
        emptyBody(null);
    }

    /**
     * Запись тела пустого отчета (например, на случай отсутствия данных).
     * @param strMessage    - Сообщение, выводимое вместо отчета
     */
    public void emptyBody(String strMessage) {
        try {
            if (xmlStreamWriter == null) {
                throw new RuntimeException("Для данного документа должен быть задан шаблон!");
            }

            //Что-то дорисовываем, только если переданное сообщение не пусто.
            if (strMessage != null && strMessage.length() != 0) {
                //Выведем заголовок приложения:
                xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
                ReportStyle.writeToXml(xmlStreamWriter,
                        reportStylesCollection.getStyles().get(EMPTYBODY_STYLE_CLASS_NAME));

                xmlStreamWriter.writeCharacters(strMessage);
                xmlStreamWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new ReportException("Error by write emptyBody",e);
        }
    }

    /**
     * Сброс внутренних буферов формирователя отчета в переданный
     * через конструктор выходной поток. После вызова этого метода,
     * формирование отчета прекращается.
     * @throws XMLStreamException выбрасываестя xmlStreamWriter
     */
    public void flush() throws XMLStreamException {
        //Проверим: допустим ли вызов?
        if (xmlStreamWriter == null) {
            throw new RuntimeException("Необходимо задать шаблон для документа!");
        }

        //Закрываем корневой элемент
        xmlStreamWriter.writeEndElement();
        //Закрываем документ
        xmlStreamWriter.writeEndDocument();
        //Сбрасываем генерируемый документ в результирующий буффер
        xmlStreamWriter.flush();
    }

    /**
     * Вывод "сырых" данных в генерируемый поток.
     * @param data  - Выводимые данные, предствленые строкой
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public void rawOutput(String data) throws XMLStreamException {

        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
        xmlStreamWriter.writeCharacters(data);
        xmlStreamWriter.writeEndElement();
    }

    /**
     * Вывод "сырых" данных в генерируемый поток с указанием именованного стиля вывода этих данных.
     * @param data       - Выводимые данные, предствленые строкой
     * @param styleName  - Наименование стиля
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public void rawOutput(String data, String styleName) throws XMLStreamException {
        //Выведем данные:
        xmlStreamWriter.writeStartElement(XSLFO_PREFIX, XML_BLOCK_IN, XSLFO_NAMESPACE);
        ReportStyle.writeToXml(xmlStreamWriter, reportStylesCollection.getReportStyle(styleName, false));
        xmlStreamWriter.writeCharacters(data);
        xmlStreamWriter.writeEndElement();
    }

    //region Вспомогательные функции

    /**
     * Функция для определения ширины страницы.
     * @param pm    - объект Pagemaster
     * @return String   - ширина страницы
     */
    protected static String getPageWidth(LayoutMasterClass.PageMasterClass pm) {
        if (pm.getPageWidth() != null && pm.getPageWidth().length() != 0) {
            return pm.getPageWidth();
        }
        switch (pm.getPageFormat()) {
            case A_2:
                return "420mm";
            case A_3:
                return "297mm";
            case A_4:
                return "210mm";
            case A_5:
                return "148mm";
            case LETTER:
                return "215,9mm";
            default:
                return "210mm";     // по умолчанию A4
        }
    }

    /**
     * Функция для определения высоты страницы.
     * @param pm    - объект Pagemaster
     * @return String   - высота страницы
     */
    protected static String getPageHeight(LayoutMasterClass.PageMasterClass pm) {
        if (pm.getPageHeight() != null && pm.getPageHeight().length() != 0) {
            return pm.getPageHeight();
        }

        switch (pm.getPageFormat()) {
            case A_2:
                return "594mm";
            case A_3:
                return "420mm";
            case A_4:
                return "297mm";
            case A_5:
                return "210mm";
            case LETTER:
                return "279,4mm";
            default:
                return "297mm";     // по умолчанию A4
        }
    }

    /**
     * Фукнция для определения ориентации страницы.
     * @param pm    - объект Pagemaster
     * @return String   - ориентация страницы
     */
    protected static String getPageOrientation(LayoutMasterClass.PageMasterClass pm) {
        String orientation = "";
        String pageOrientation = pm.getPageOrientation();
        if (StringUtils.isNotEmpty(pageOrientation)) {
            orientation = pageOrientation;
        }

        return orientation;
    }

    /**
     * процедура добавления аттрибутов в текуще-отрисовываемый region.
     * @param region    - region - объект типа page-header | page-body | page-bottom
     * @throws XMLStreamException выбрасываестя xmlStreamWriter
     */
    protected void setRegionAttributes(AbstractPageRegionClass region) throws XMLStreamException {

        // Для начала добавим region-name. Дело в том, что каждый region внутри page-master'ов должен
        // иметь name. В Croc ReportService это сделано прозрачно для профиля отчета.
        if (region instanceof RegionHeaderClass) {
            RegionHeaderClass regionHeader =
                    (RegionHeaderClass)region;

            //extent
            if (regionHeader.getExtent() != null) {
                xmlStreamWriter.writeAttribute("extent", regionHeader.getExtent());
            }

            xmlStreamWriter.writeAttribute("region-name", "PageHeader");
        } else if (region instanceof RegionBodyClass) {
            RegionBodyClass regionBody =
                    (RegionBodyClass)region;

            //margin
            if (regionBody.getMargin() != null) {
                xmlStreamWriter.writeAttribute("margin", regionBody.getMargin());
            }

            //margin-left
            if (regionBody.getMarginLeft() != null) {
                xmlStreamWriter.writeAttribute("margin-left", regionBody.getMarginLeft());
            }

            //margin-right
            if (regionBody.getMarginRight() != null) {
                xmlStreamWriter.writeAttribute("margin-right", regionBody.getMarginRight());
            }

            //margin-top
            if (regionBody.getMarginTop() != null) {
                xmlStreamWriter.writeAttribute("margin-top", regionBody.getMarginTop());
            }

            //margin-bottom
            if (regionBody.getMarginBottom() != null) {
                xmlStreamWriter.writeAttribute("margin-bottom", regionBody.getMarginBottom());
            }

            xmlStreamWriter.writeAttribute("region-name", "PageBody");
        } else if (region instanceof RegionFooterClass) {
            RegionFooterClass regionFooter = (RegionFooterClass)region;

            //extent
            if (regionFooter.getExtent() != null) {
                xmlStreamWriter.writeAttribute("extent", regionFooter.getExtent());
            }

            xmlStreamWriter.writeAttribute("region-name", "PageBottom");
        }

        //display-align
        if (region.getValign() != null) {
            xmlStreamWriter.writeAttribute("display-align", region.getValign());
        }

        //padding
        if (region.getPadding() != null) {
            xmlStreamWriter.writeAttribute("padding", region.getPadding());
        }

        //padding-left
        if (region.getPaddingLeft() != null) {
            xmlStreamWriter.writeAttribute("padding-left", region.getPaddingLeft());
        }

        //padding-right
        if (region.getPaddingRight() != null) {
            xmlStreamWriter.writeAttribute("padding-right", region.getPaddingRight());
        }

        //padding-top
        if (region.getPaddingTop() != null) {
            xmlStreamWriter.writeAttribute("padding-top", region.getPaddingTop());
        }

        //padding-bottom
        if (region.getPaddingBottom() != null) {
            xmlStreamWriter.writeAttribute("padding-bottom", region.getPaddingBottom());
        }
    }

    //endregion

    /**
     * Запролняем общие стили.
     * @param reportClass профиль отчёта
     */
    private void fillReportStyleCollections(ReportClass reportClass) {

    }

    /**
     * Отрисовка ссылки.
     * @param linkBuilder построитель {@link LinkBuilder}
     */
    public void writeLink(LinkBuilder linkBuilder) {
        linkBuilder.writeLink(xmlStreamWriter);
    }
}
