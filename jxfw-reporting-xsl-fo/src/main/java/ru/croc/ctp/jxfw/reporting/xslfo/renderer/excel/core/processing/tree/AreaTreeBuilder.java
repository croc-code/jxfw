package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.tree;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.BASIC_LINK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.BLOCK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.INLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.LAYOUT_MASTER_SET;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.LIST_ITEM_BODY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.LIST_ITEM_LABEL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.PAGE_SEQUENCE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.ROOT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.SIMPLE_PAGE_MASTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.SPACER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.TABLE_CELL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.CHARACTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.COLUMN_NUMBER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.CONTENT_TYPE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.HEIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.INTERNAL_DESTINATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MAX_HEIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MAX_WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MIN_HEIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MIN_WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.NUMBER_COLUMNS_REPEATED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REF_ID;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.SRC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.EXCEPTION_STRING;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.EXCEPTION_STRING_SIMPLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.CONTENT_TYPE_SCRIPT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.PADDING_SPACE_THRESHOLD;

import de.vandermeer.svg2vector.applications.base.SvgTargets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.XmlUtil;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.ISpacesRemover;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.RenderType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.Svg2Image;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.impl.DefaultSpacesRemover;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.catchword.Catchword;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.catchword.CatchwordSection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.MarginProps;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.image.ExternalGraphicArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list.ListBlockArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list.ListItemArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list.ListItemBodyArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list.ListItemLabelArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc.CommonArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc.PageNumberArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc.SpacerArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.CellGridContainer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableBodyArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableCellArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableColumnArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableFooterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableHeaderArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableRowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text.BlockArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text.InlineArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region.RegionAfter;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region.RegionBefore;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region.RegionBody;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.FlowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.PageSequenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.StaticContentArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.border.FoBorder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

/**
 * Класс, инкапсулирующий логику построения дерева областей.
 * Created by vsavenkov on 11.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("deprecation")
public class AreaTreeBuilder {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(AreaTreeBuilder.class);

    /**
     * кэш текстов.
     */
    private Hashtable<String, String> textCache = new Hashtable<>();

    /**
     * Объект для чтения XML файла.
     */
    private final XmlTextReader reader;

    /**
     * Корневая область.
     */
    private final RootArea rootArea;

    /**
     * Реализация удаления двойных пробелов.
     */
    private ISpacesRemover spacesRemover = new DefaultSpacesRemover();

    /**
     * Конструктор.
     * @param xmlReader - Объект для чтения XML файла. Должен быть проинициализирован и указатель поставлен на контент
     * @param rootArea  - Корневая область, должна быть проинициализирована
     * @throws XslFoException генерирует исключение, если не задан объект для чтения XML файла
     */
    public AreaTreeBuilder(XmlTextReader xmlReader, RootArea rootArea) throws XslFoException {

        if (xmlReader == null) {
            throw new XslFoException("Объект чтения XML файла не задан", "AreaTreeBuilder.buildAreaTree");
        }
        this.reader = xmlReader;
        this.rootArea = rootArea;
    }

    /**
     * Построение дерева областей - возвращается область со всеми вложенными областями.
     * @throws Exception генерирует XMLEventReader или, если передан null в качестве параметра
     */
    public void buildAreaTree() throws Exception {

        try {
            reader.moveToElement(ROOT.getFoName());

            // Рекурсивно обрабатываем дочерние элементы если они есть
            if (!reader.isEndElement()) {
                parseChildrenElement(rootArea);
            }

            // Пройдем по всему дереву и сгенерируем пустые области в случае необходимости
            generateSpaceAreas(rootArea);

            // Пройдем по всему дереву и выставим корректные типы направления расположения
            // дочерних областей - progress direction
            checkAreaProgressionDirection(rootArea);
        } catch (XMLStreamException | XslFoException exc) {
            // Получение чистого сообщения
            int endPosition = exc.getMessage().indexOf("Line");
            String message = exc.getMessage().substring(0, 0 < endPosition ? endPosition : exc.getMessage().length());

            // Получение значения позиции ошибки
            //int index = exc.getMessage().lastIndexOf("position") + 9;
            //String position = exc.getMessage().substring(index, exc.getMessage().length() - index - 1);
            throw new XslFoException(
                    String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING, message,
                            reader.getLineNumber(), reader.getCharacterOffset()),
                    "AreaTreeBuilder.buildAreaTree", exc);
        }

        // Обработка и проверка областей <fo:flow> & <fo:static-content>
        processFlowWithStaticContentAreas(rootArea);
    }

    /**
     * Рекурсивный метод для обработки элементов текущего уровня вложенности XML файла и получения областей.
     * Объекты чтения XML файла и родительская область должны быть проинициализированы.
     * Объекты чтения XML файла должен иметь указатель установленным на первый дочерний элемент
     * элемента родительской области
     * @param parentArea - Родительская область - ОБЛАСТЬ КОНТЕНТА!!!
     * @throws Exception генерирует XMLEventReader или, если передан null в качестве параметра
     */
    private void parseChildrenElement(IArea parentArea) throws Exception {

        IArea childArea;                               // Дочерняя область
        Map<String, String> attributeList;             // Список атрибутов узла
        CellGridContainer cellGridContainer = null;    // Обрабатывается секция таблицы

        // Обрабатываем все узлы одного уровня
        while (!reader.isEndElement()/* TODO: м.б. это ни к чему && reader.NodeType != XmlNodeType.None*/) {
            // Если встретили текстовый элемент - блок #PCDATA - обрабатываем его
            if (reader.getEventType() == XMLStreamConstants.CHARACTERS
                    || reader.getEventType() == XMLStreamConstants.CDATA) {
                parsePcDataElement(parentArea);
                continue;
            }

            // Обрабатываем все остальные виды элементов

            // Получаем список атрибутов элемента
            attributeList = reader.getAttributeList();

            AreaType areaType = AreaType.parseValue(reader.getPrefixedName(), RenderType.EXCEL);

            // Проверка, принадлежит ли область верному родителю
            validateAreaType(areaType, parentArea);

            // Рекурсивная обработка вложенных элементов в зависимости от типа
            switch (areaType) {
                case ROOT:
                    processRootArea();
                    continue;

                case SIMPLE_PAGE_MASTER:
                    childArea = new SimplePageMasterArea(parentArea, attributeList);
                    processSimplePageMaster(parentArea, childArea);
                    continue;

                case PAGE_SEQUENCE:
                    childArea = new PageSequenceArea(parentArea, attributeList);
                    break;

                case STATIC_CONTENT:
                    childArea = new StaticContentArea(parentArea, attributeList);
                    break;

                case PAGE_NUMBER:
                    childArea = new PageNumberArea(rootArea, parentArea, attributeList);
                    break;

                case FLOW:
                    childArea = new FlowArea(parentArea, attributeList);
                    break;

                case LIST_BLOCK:
                    childArea = new ListBlockArea(parentArea, attributeList);
                    break;

                case LIST_ITEM:
                    childArea = new ListItemArea(parentArea, attributeList);
                    break;

                case LIST_ITEM_LABEL:
                    childArea = new ListItemLabelArea(parentArea, attributeList);
                    break;

                case LIST_ITEM_BODY:
                    childArea = new ListItemBodyArea(parentArea, attributeList);
                    break;

                case PAGE_NUMBER_CITATION:
                    processPageNumberCitation(attributeList);
                    childArea = new InlineArea(parentArea, attributeList);
                    break;

                case CHARACTER:
                    childArea = new InlineArea(processCharacter(attributeList), parentArea, attributeList);
                    break;

                case BASIC_LINK:
                    childArea = new InlineArea(parentArea, attributeList);
                    break;

                case BLOCK:
                    childArea = new BlockArea(parentArea, attributeList);
                    break;

                case INLINE:
                    childArea = new InlineArea(parentArea, attributeList);
                    break;

                case TABLE:
                    childArea = new TableArea(parentArea, attributeList);
                    break;

                case TABLE_COLUMN:
                    // Обрабатываем элемент колонки таблицы
                    processTableColumn((TableArea)parentArea, attributeList);

                    // Переходим на следующий элемент
                    reader.skipToNextElementWithComment();
                    continue;

                case TABLE_BODY:
                    cellGridContainer = new TableBodyArea(parentArea, attributeList);
                    childArea = cellGridContainer;
                    // Начинаем обработку вложенных ячеек контейнера
                    cellGridContainer.beginCellProcessing();
                    break;

                case TABLE_ROW:
                    childArea = new TableRowArea(parentArea, attributeList);
                    break;

                case TABLE_CELL:
                    TableCellArea cell = new TableCellArea(parentArea, attributeList);
                    childArea = cell;
                    // Обрабатываем свойства распространения ячейки
                    cell.getGridContainer().processCell(cell);
                    break;

                case TABLE_HEADER:
                    cellGridContainer = new TableHeaderArea(parentArea, attributeList);
                    childArea = cellGridContainer;
                    // Начинаем обработку вложенных ячеек контейнера
                    cellGridContainer.beginCellProcessing();
                    break;

                case TABLE_FOOTER:
                    cellGridContainer = new TableFooterArea(parentArea, attributeList);
                    childArea = cellGridContainer;
                    // Начинаем обработку вложенных ячеек контейнера
                    cellGridContainer.beginCellProcessing();
                    break;

                case EXTERNAL_GRAPHIC:
                    childArea = new ExternalGraphicArea(parentArea, attributeList);
                    break;

                case INSTREAM_FOREIGN_OBJECT:
                    processInstreamForeignObject(parentArea, attributeList);

                    // Переходим на следующий элемент
                    reader.skip();
                    continue;

                default:
                    // По умолчанию - переходим на следующий элемент
                    reader.skipToNextElementWithComment();
                    continue;
            }

            // Добавляем дочернюю область в список областей родительской области
            parentArea.getChildrenList().add(childArea);

            // Проверяем - если непустой элемент - то обрабатываем его дочерние элементы
            if (!reader.isEmptyElement()) {
                // Переходим на первый дочерний элемент
                reader.readStartElement();

                // Если есть вложенные элементы - обрабатываем их рекурсивно
                if (!reader.isEndElement()) {
                    parseChildrenElement(childArea);

                    // Заканчиваем процесс обработки ячеек контейнера если был начат
                    if (cellGridContainer != null) {
                        cellGridContainer.endCellProcessing();
                        cellGridContainer = null;
                    }
                } else {
                    // Если пустой элемент - пропускаем его
                    reader.skip();
                }
            } else {
                // Если пустой элемент - пропускаем его
                reader.skipToNextElementWithComment();
            }

            // Пост-обработка свойств
            childArea.postProcessProperties();
        }

        // Вышли из цикла - переходим на следующий элемент
        try {
            reader.skip();
        } catch (Exception exc) {
            throw new XslFoException("Один из элементов имеет отсутствующий закрывающий тэг",
                    "AreaTreeBuilder.parseChildrenElement", exc);
        }
    }

    /**
     * Преобразование области &lt;fo:character&gt; в область &lt;fo:inline&gt;.
     * @param attributeList - Список атрибутов
     * @return String   - Строка символа
     */
    private static String processCharacter(Map<String, String> attributeList) {

        if (attributeList == null) {
            return "";
        }

        // Получаем значение атрибута character
        String character = attributeList.get(CHARACTER.getPropertyName());

        // Удаляем этот атрибут из списка
        attributeList.remove(CHARACTER.getPropertyName());

        // Возвращаем значение атрибута
        return character;
    }

    /**
     * Преобразование области &lt;fo:page-number-citation&gt; в свойство internal-destination.
     * @param attributeList - Список атрибутов
     */
    private static void processPageNumberCitation(Map<String, String> attributeList) {

        if (attributeList == null) {
            return;
        }

        // Получаем значение ссылки
        String refId = attributeList.get(REF_ID.getPropertyName());

        // Удаляем свойство из списка атрибутов
        attributeList.remove(REF_ID.getPropertyName());

        // Добавляем свойство internal-destination
        attributeList.put(INTERNAL_DESTINATION.getPropertyName(), refId);
    }

    /**
     * Обработка областей &lt;fo:static-content&gt;.
     * @param rootArea - Корневая область
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     */
    public static void processFlowWithStaticContentAreas(RootArea rootArea) throws XslFoException {

        List<IArea> staticContent = new ArrayList<>();
        StaticContentArea staticContentArea;
        boolean itIsFirstBeforeRegion = true;

        // Проходим по всем <fo:page-sequence>
        for (int i = 0; i < rootArea.getChildrenList().size(); i++) {
            PageSequenceArea pageSequenceArea = (PageSequenceArea)rootArea.getChildrenList().get(i);
            SimplePageMasterArea simplePageMasterArea = getSimplePageMaster(rootArea, pageSequenceArea);
            for (int j = 0; j < pageSequenceArea.getChildrenList().size(); j++) {
                if (pageSequenceArea.getChildrenList().get(j) instanceof StaticContentArea) {
                    // Получаем ссылку на данную область
                    staticContentArea = (StaticContentArea)pageSequenceArea.getChildrenList().get(j);
                    // Имя области
                    String flowName = (String)staticContentArea.getPropertyValue(FoPropertyType.FLOW_NAME);
                    // Проверяем является ли данная область нижним колонтитулом
                    if (simplePageMasterArea.getRegionAfter() != null
                            && flowName.equals(simplePageMasterArea.getRegionAfter().getRegionName()) ) {
                        // Обработка для нижних колонтитулов
                        processStaticContentArea(pageSequenceArea, staticContentArea);
                        // Добавление данной области для последующего удаления
                        staticContent.add(staticContentArea);
                    } else if (simplePageMasterArea.getRegionBefore() != null
                            && flowName.equals(simplePageMasterArea.getRegionBefore().getRegionName())) {
                        // Если область является верхним колонтитулом
                        // Обрабатываем только первый верхний колонтитул, для всех page-sequence
                        if (!itIsFirstBeforeRegion) {
                            // Добавление данной области для последующего удаления
                            staticContent.add(staticContentArea);
                        }
                        // Устанавливаем флаг того, что один из верхних колонтитулов была уже обработана,
                        // остальные игнорируются
                        itIsFirstBeforeRegion = false;
                    }
                } else if (pageSequenceArea.getChildrenList().get(j) instanceof FlowArea) {
                    processFlowArea(simplePageMasterArea, pageSequenceArea,
                            (FlowArea)pageSequenceArea.getChildrenList().get(j));
                }
            }

            rootArea.setColumnCount((int)simplePageMasterArea.getRegionBody()
                    .getPropertyValue(FoPropertyType.COLUMN_COUNT));
            Dimension columnGap = simplePageMasterArea.getRegionBody().getDimensionValue(FoPropertyType.COLUMN_GAP);
            rootArea.setColumnGap(!columnGap.isPercentage()
                    ? (int)columnGap.getValue()
                    : simplePageMasterArea.getPageContentWidth() * (int)columnGap.getValue());

            // Удаление всех входящих <fo:static-content>
            for (int j = 0; j < staticContent.size(); j++) {
                pageSequenceArea.getChildrenList().remove(staticContent.get(j));
            }

            // Очищаем массив
            staticContent.clear();
        }
    }

    /**
     * Получение объекта параметров страницы.
     * @param rootArea              - Ссылка на главную область
     * @param pageSequenceArea      - Ссылка на объект страницы
     * @return SimplePageMasterArea - Ссылка на объект параметров страницы
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     */
    private static SimplePageMasterArea getSimplePageMaster(RootArea rootArea, PageSequenceArea pageSequenceArea)
            throws XslFoException {

        // Получаеми ссылку на объект параметров листа
        SimplePageMasterArea simplePageMasterArea;
        String masterReference = (String)pageSequenceArea.getPropertyValue(FoPropertyType.MASTER_REFERENCE);
        if (masterReference == null) {
            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    "Не удалось обнаружить атрибут <master-reference> для элемента <fo:page-sequence>"),
                    "AreaTreeBuilder.processFlowArea");
        }


        // Пытаемся получить объект SimplePageMasterArea
        simplePageMasterArea = (SimplePageMasterArea)rootArea.getLayoutMasterSet().get(masterReference);
        if (simplePageMasterArea == null) {
            String exceptionString = String.format(GlobalData.getCultureInfo(),
                    "Не удалось обнаружить <fo:simple-page-master> с именем '%1$s'", masterReference);
            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    exceptionString), "AreaTreeBuilder.processFlowArea");
        }
        return simplePageMasterArea;
    }

    /**
     * Обработка элемента &lt;fo:static-content&gt;.
     * @param pageSequenceArea  - Родительская область
     * @param staticContentArea - Данная область
     */
    private static void processStaticContentArea(PageSequenceArea pageSequenceArea,
                                                 StaticContentArea staticContentArea) {

        IArea area = staticContentArea;
        CatchwordSection enSection;
        while (area.getProgressionDirection() != AreaProgressionDirection.INLINE && area.isHasChildren()
                && area.getChildrenList().size() == 1) {
            area = area.getChildrenList().get(0);
        }
        Catchword catchword;
        switch (area.getProgressionDirection()) {
            case INLINE:
                catchword = new Catchword(area);
                enSection = catchword.getSection() != CatchwordSection.UNDEFINED
                        ? catchword.getSection()
                        : Catchword.getCatchwordSection(Integer.parseInt(
                        area.getInheritedPropertyValue(FoPropertyType.TEXT_ALIGN).toString()));

                pageSequenceArea.addFooterCatchword(catchword.getScript(), enSection);
                break;

            case BLOCK:
                if (!area.isHasChildren()) {
                    return;
                }
                enSection = Catchword.getCatchwordSection(Integer.parseInt(
                        area.getInheritedPropertyValue(FoPropertyType.TEXT_ALIGN).toString()));
                for (IArea childArea : area.getChildrenList()) {
                    if (childArea.getProgressionDirection() != AreaProgressionDirection.INLINE) {
                        continue;
                    }
                    catchword = new Catchword(childArea);
                    pageSequenceArea.addFooterCatchword(catchword.getScript(),
                            catchword.getSection() != CatchwordSection.UNDEFINED ? catchword.getSection() : enSection);
                }
                break;

            case ROW:
                // Если нет детей или их больше 3, то выходим
                if (!area.isHasChildren() || area.getChildrenList().size() > 3) {
                    return;
                }
                enSection = CatchwordSection.LEFT;
                for (IArea childArea : area.getChildrenList()) {
                    // Если у нас всего 2 ячейки, то распределяем как левый и правый колонитул
                    if (enSection == CatchwordSection.CENTER && area.getChildrenList().size() == 2) {
                        enSection = CatchwordSection.RIGHT;
                    }

                    switch (childArea.getProgressionDirection()) {
                        case INLINE:
                            catchword = new Catchword(childArea);
                            pageSequenceArea.addFooterCatchword(catchword.getScript(), enSection);
                            break;
                        case BLOCK:
                            if (childArea.isHasChildren()) {
                                for (IArea subChildArea : childArea.getChildrenList()) {
                                    if (subChildArea.getProgressionDirection() != AreaProgressionDirection.INLINE) {
                                        continue;
                                    }
                                    catchword = new Catchword(subChildArea);
                                    pageSequenceArea.addFooterCatchword(catchword.getScript(), enSection);
                                }
                            }
                            break;

                        default:
                            // В импортруемом коде ничего не было
                    }
                    enSection = enSection.next();
                }
                break;

            default:
                // В импортруемом коде ничего не было
        }
    }

    /**
     * Обработка области &lt;fo:flow&gt;.
     * @param simplePageMaster - Объект SimplePageMasterArea
     * @param pageSequenceArea - Родительская область
     * @param flowArea         - Данная область
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     */
    private static void processFlowArea(SimplePageMasterArea simplePageMaster, PageSequenceArea pageSequenceArea,
                                        FlowArea flowArea) throws XslFoException {

        String flowName = (String)flowArea.getPropertyValue(FoPropertyType.FLOW_NAME);

        // Проверяем, существует ли регион вообще или такое название региона
        if (simplePageMaster.getRegionBody() == null) {
            String exceptionString = String.format(GlobalData.getCultureInfo(),
                    "Регион <fo:region-body> не задан для <fo:-simple-page-master> с именем '%1$s'",
                    pageSequenceArea.getPropertyValue(FoPropertyType.MASTER_REFERENCE));

            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    exceptionString), "AreaTreeBuilder.processFlowArea");
        }

        if (!flowName.equals(simplePageMaster.getRegionBody().getRegionName())) {
            String exceptionString = String.format(GlobalData.getCultureInfo(),
                    "Не удалось обнаружить регион '%1$s' для <fo:simple-page-master> с именем '%2$s'",
                    flowName, pageSequenceArea.getPropertyValue(FoPropertyType.MASTER_REFERENCE));

            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    exceptionString), "AreaTreeBuilder.processFlowArea");
        }
    }

    /**
     * Обработка области SimplePageMaster.
     * @param parentArea - Родительская область
     * @param childArea  - Дочерняя область
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     * @throws XMLStreamException генерирует XMLEventReader
     */
    @SuppressWarnings("unchecked")
    private void processSimplePageMaster(IArea parentArea, IArea childArea) throws XslFoException, XMLStreamException {

        // Находимся в <fo:simple-page-master>
        SimplePageMasterArea simplePageMasterArea = (SimplePageMasterArea)childArea;

        String simplePageMasterName = (String)childArea.getPropertyValue(FoPropertyType.MASTER_NAME);
        if (simplePageMasterName == null) {
            String exceptionString = String.format(GlobalData.getCultureInfo(),
                    "Для элемента <fo:-simple-page-master> не задано имя!");
            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    exceptionString), "AreaTreeBuilder.processSimplePageMaster");
        }

        reader.readStartElement();

        while (!SIMPLE_PAGE_MASTER.getFoName().equals(reader.getPrefixedName())) {
            // Получаем список атрибутов элемента
            Map<String, String> attributeList = reader.getAttributeList();

            switch (AreaType.parseValue(reader.getPrefixedName(), RenderType.EXCEL)) {
                case REGION_BEFORE:
                    simplePageMasterArea.setRegionBefore(new RegionBefore(attributeList));
                    break;

                case REGION_AFTER:
                    simplePageMasterArea.setRegionAfter(new RegionAfter(attributeList));
                    break;

                case REGION_BODY:
                    simplePageMasterArea.setRegionBody(new RegionBody(attributeList));
                    break;

                default:
                    // В импортруемом коде ничего не было
            }

            reader.readStartElement(SIMPLE_PAGE_MASTER.getFoName());
        }

        // Добавляем объект oSimplePageMasterArea в коллекцию
        ((RootArea)parentArea).getLayoutMasterSet().put(simplePageMasterName, simplePageMasterArea);

        reader.skip();
        reader.moveToElement();
        if (reader.isEndElement()) {
            reader.moveToElement(PAGE_SEQUENCE.getFoName());
        }
    }

    /**
     * Обработка главной родительской области.
     * @throws XslFoException генерирует в случае некорректности FO-файла
     * @throws XMLStreamException генерирует XMLEventReader
     */
    private void processRootArea() throws XslFoException, XMLStreamException {

        // Пытаемся перейти на элемент <fo:layout-master-set>
        reader.moveToElement(LAYOUT_MASTER_SET.getFoName());
        if (!LAYOUT_MASTER_SET.getFoName().equals(reader.getPrefixedName())) {
            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    "Не удалось обнаружить элемент <fo:layout-master-set>"), "AreaTreeBuilder.processRootArea");
        }

        // Пытаемся перейти на <fo:simple-page-master>
        reader.moveToElement(SIMPLE_PAGE_MASTER.getFoName());
        if (!SIMPLE_PAGE_MASTER.getFoName().equals(reader.getPrefixedName())) {
            throw new XslFoException(String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING_SIMPLE,
                    "Не удалось обнаружить элементы <fo:simple-page-master>"), "AreaTreeBuilder.processRootArea");
        }
    }

    /**
     * Проверка, родительский тип области соответствует заданному типу области.
     * @param areaType   - Заданный тип
     * @param parentArea - Родительская область
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     */
    private void validateAreaType(AreaType areaType, IArea parentArea) throws XslFoException {

        switch (areaType) {
            case INLINE:
            case BASIC_LINK:
            case EXTERNAL_GRAPHIC:
            case INSTREAM_FOREIGN_OBJECT:
            case PAGE_NUMBER:
                checkAreaType(areaType, parentArea, BLOCK, INLINE);
                break;

            case LIST_ITEM:
                checkAreaType(areaType, parentArea, AreaType.LIST_BLOCK);
                break;

            case LIST_ITEM_LABEL:
            case LIST_ITEM_BODY:
                checkAreaType(areaType, parentArea, AreaType.LIST_ITEM);
                break;

            case TABLE_CELL:
                checkAreaType(areaType, parentArea, AreaType.TABLE_ROW);
                break;

            case TABLE_ROW:
                checkAreaType(areaType, parentArea, AreaType.TABLE_BODY, AreaType.TABLE_FOOTER,
                        AreaType.TABLE_HEADER);
                break;

            case TABLE_BODY:
            case TABLE_FOOTER:
            case TABLE_HEADER:
                checkAreaType(areaType, parentArea, AreaType.TABLE);
                break;

            default:
                // В импортруемом коде ничего не было
        }
    }

    /**
     * Проверка, родительский тип области соответствует одному из заданных типов области
     * Вспомогательный метод для ValidateArea.
     * @param areaType     - Заданный тип
     * @param parentArea   - Родительская область
     * @param checkedTypes - Массив типов
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     */
    private void checkAreaType(AreaType areaType, IArea parentArea, AreaType... checkedTypes)
            throws XslFoException {

        AreaType parentAreaType = parentArea.getAreaType();
        // Если родитель - служебная обобощенная область, ищем настоящего родителя
        while (AreaType.COMMON == parentAreaType && null != parentArea.getParentArea()) {
            parentArea = parentArea.getParentArea();
            parentAreaType = parentArea.getAreaType();
        }

        // Если родительская область является данной выходим
        if (Arrays.asList(checkedTypes).indexOf(parentAreaType) >= 0) {
            return;
        }

        StringBuilder areas = new StringBuilder();   // Используется для сохранения областей в кот. не вошла область
        for (AreaType checkedType : checkedTypes) {
            // Добавляем тип области
            areas.append(String.format(GlobalData.getCultureInfo(), "<%1$s> или ",
                    checkedType.getFoName()));
        }

        // Формируем строку описания исключения
        String exceptionString = String.format(GlobalData.getCultureInfo(), EXCEPTION_STRING,
                " Элемент <" + areaType.getFoName() + "> может находиться внутри"
                        + " элемента " + areas.substring(0, areas.length() - 5) + ",\nно не элемента"
                        + " <" + parentAreaType.getFoName() + "> ",
                reader.getLineNumber(), reader.getColumnNumber());

        // Выбрасываем исключение
        throw new XslFoException(String.format(GlobalData.getCultureInfo(), exceptionString, ""),
                "AreaTreeBuilder.checkAreaType");
    }

    /**
     * Обрабатываем текстовый элемент - #PCDATA.
     * @param parentArea - Родительская область
     * @throws XMLStreamException генерирует XMLEventReader
     */
    private void parsePcDataElement(IArea parentArea) throws XMLStreamException {

        String text = reader.getText(); // Текст
        // Переходим на следующий элемент текущего уровня
        reader.skip();
        // Если это не один из объектов, могущих содержать текст, то выходим
        if (!Arrays.asList(BLOCK, INLINE, BASIC_LINK).contains(parentArea.getAreaType())) {
            return;
        }
        //Чистка от лишних пробельных символов
        text = spacesRemover.removeDoubleSpaces(text, parentArea.isHasChildren(), !reader.isEndElement());
        if (StringUtils.isBlank(text)) {
            return;
        }
        // Вначале проверяем если ли данный текст в кэше
        if (textCache.containsKey(text)) {
            // Есть, используем его
            text = textCache.get(text);
        } else {
            textCache.put(text, text);
        }

        InlineArea inlineArea = parentArea instanceof InlineArea ? (InlineArea)parentArea : null;
        if (inlineArea != null && !inlineArea.isHasChildren() && inlineArea.getText().length() == 0) {
            inlineArea.setText(text);
        } else {
            IArea childArea = new InlineArea(text, parentArea, null);

            // Добавляем дочернюю область в список областей родительской области
            parentArea.getChildrenList().add(childArea);
        }
    }

    /**
     * Обрабатываем элемент колонки таблицы.
     * @param tableArea     - Родительская таблица
     * @param attributeList - Список атрибутов
     */
    private static void processTableColumn(TableArea tableArea, Map<String, String> attributeList) {

        List<IArea> columnList = tableArea.getColumnList();                 // Список колонок
        String key = NUMBER_COLUMNS_REPEATED.getPropertyName();        // Ключ
        int columnNumber = tableArea.getColumnList().size() + 1;              // Номер колонки

        if (attributeList == null) {
            attributeList = new HashMap<>();
        }

        // Если задан атрибут number-columns-repeated - создаем дополнительное количество колонок
        if (attributeList.containsKey(key)) {
            // Получаем количество повторяющихся колонок - которые нужно добавить
            int numberColumnsToCreate = (int)HelpFuncs.getValueFromString(attributeList.get(key));

            // Удаляем атрибут количества колонок из списка
            attributeList.remove(key);

            // Создаем и добавляем новые колонки в список колонок родительской таблицы
            for (int i = 0; i < numberColumnsToCreate; i++) {
                attributeList.put(COLUMN_NUMBER.getPropertyName(),
                        NumberFormat.getInstance(GlobalData.getCultureInfo()).format(columnNumber));
                TableColumnArea column = new TableColumnArea(tableArea, attributeList);
                columnList.add(column);
                columnNumber++;
            }
        } else {
            // Если номер колонки не был задан - генерируем его
            if (tableArea.getPropertyValue(COLUMN_NUMBER) == null) {
                if (attributeList.get(COLUMN_NUMBER.getPropertyName()) == null) {
                    attributeList.put(COLUMN_NUMBER.getPropertyName(),
                            NumberFormat.getInstance(GlobalData.getCultureInfo()).format(columnNumber));
                } else {
                    attributeList.put(COLUMN_NUMBER.getPropertyName(),
                            NumberFormat.getInstance(GlobalData.getCultureInfo()).format(columnNumber));
                }
            }

            TableColumnArea column = new TableColumnArea(tableArea, attributeList);
            columnList.add(column);
        }

        // Устанавливаем колонки таблицы
        tableArea.setColumnList(columnList);
    }

    /**
     * Обрабатываем элемент &lt;fo:instream-foreign-Object&gt;.
     * @param parentArea    - Родительская область
     * @param attributeList - Список атрибутов
     * @throws Exception генерируется XMLEventReader`ом или классами, связанными с DocumentBuilder`ом
     */
    private void processInstreamForeignObject(IArea parentArea, Map<String, String> attributeList)
            throws Exception {

        // Если это скрипт или пустой элемент, то ничего не делаем
        if ((attributeList != null
                && CONTENT_TYPE_SCRIPT.equals(attributeList.get(CONTENT_TYPE.getPropertyName())))
                || reader.isEmptyElement()) {
            return;
        }

        // Переходим на узел <svg>
        reader.readStartElement();

        // Вычитываем содержимое встроенного SVG объекта
        String svgXml = reader.readOuterXml();

        // Если строка пустая, выходим
        if (svgXml.length() == 0) {
            return;
        }

        // Получаем значения высоты и ширины картинки
        svgXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + svgXml;
        Document doc = XmlUtil.createXmlDocument(svgXml);
        Element element = doc.getDocumentElement();

        // Определяем высоту и ширину
        String widthAttr = element.getAttribute(WIDTH.getPropertyName());
        String heightAttr = element.getAttribute(HEIGHT.getPropertyName());

        // Если размеры не заданы, то по умолчанию картинка будет 100 х 100
        int width = widthAttr != null
                ? HelpFuncs.getSizeInPixels(widthAttr)
                : attributeList.containsKey(WIDTH.getPropertyName())
                ? HelpFuncs.getSizeInPixels(attributeList.get(WIDTH.getPropertyName()))
                : attributeList.containsKey(MAX_WIDTH.getPropertyName())
                ? HelpFuncs.getSizeInPixels(attributeList.get(MAX_WIDTH.getPropertyName()))
                : attributeList.containsKey(MIN_WIDTH.getPropertyName())
                ? HelpFuncs.getSizeInPixels(attributeList.get(MIN_WIDTH.getPropertyName())) : 100;

        int height = heightAttr != null
                ? HelpFuncs.getSizeInPixels(heightAttr)
                : attributeList.containsKey(HEIGHT.getPropertyName())
                ? HelpFuncs.getSizeInPixels(attributeList.get(HEIGHT.getPropertyName()))
                : attributeList.containsKey(MAX_HEIGHT.getPropertyName())
                ? HelpFuncs.getSizeInPixels(attributeList.get(MAX_HEIGHT.getPropertyName()))
                : attributeList.containsKey(MIN_HEIGHT.getPropertyName())
                ? HelpFuncs.getSizeInPixels(attributeList.get(MIN_HEIGHT.getPropertyName())) : 100;


        // Создаем конвертер
        Svg2Image svg2Image = new Svg2Image(width, height);

        // Устанавливаем источник
        try {
            svg2Image.setSource(svgXml);
        } catch (Exception e) {
            throw new XslFoException("Входящий SVG документ имеет некорректный формат",
                    "AreaTreeBuilder.processInstreamForeignObject", e);
        }

        if (attributeList == null) {
            attributeList = new HashMap<>();
        }

        // Добавляем новый атрибут src
        attributeList.put(SRC.getPropertyName(), svg2Image.getImageFileName(SvgTargets.wmf));

        // Имитируем область fo:external-graphic
        // Дочерняя область
        IArea childArea = new ExternalGraphicArea(parentArea, attributeList);
        parentArea.getChildrenList().add(childArea);
        // Пост-обработка свойств
        childArea.postProcessProperties();
    }

    /**
     * Вставка областей пустого пространства, если необходимо:
     * CommonArea и SpacerArea (см. описание классов).
     * @param area - Обрабатываемая область
     */
    public static void generateSpaceAreas(GenericArea area) {

        if (area.isHasChildren()) {
            // Использование IEnumerator не подходит, так как выполняется модификация списка в процессе обработки
            List<IArea> childrenList = area.getChildrenList();
            for (int i = 0; i < childrenList.size(); i++) {
                generateSpaceAreas((GenericArea)childrenList.get(i));
            }
        }
        // Обрабатываем атрибуты padding
        processPaddingAttributes(area);
        // Обрабатываем атрибуты margin
        processMarginAttributes(area);
    }

    /**
     * Обрабатываем атрибуты padding.
     * @param area - Исходная область
     */
    private static void processPaddingAttributes(GenericArea area) {

        // Также учитываем величину пустого пространства по высоте и ширине -
        // т.к. слишком маленькие области генерировать в Excel бессмысленно
        MarginProps margins = area.getMargins();
        if (null != margins) {
            if (margins.getPaddingLeft() < PADDING_SPACE_THRESHOLD) {
                margins.setPaddingLeft(0);
            }
            if (margins.getPaddingRight() < PADDING_SPACE_THRESHOLD) {
                margins.setPaddingRight(0);
            }
            if (margins.getPaddingTop() < PADDING_SPACE_THRESHOLD) {
                margins.setPaddingTop(0);
            }
            if (margins.getPaddingBottom() < PADDING_SPACE_THRESHOLD) {
                margins.setPaddingBottom(0);
            }
            // У самой области значения сбрасываем, чтобы не зациклиться
            area.getMargins().setPaddingTop(0);
            area.getMargins().setPaddingBottom(0);
            area.getMargins().setPaddingLeft(0);
            area.getMargins().setPaddingRight(0);
            handleLeftRightPaddingAttributes(area, margins.getPaddingLeft(), margins.getPaddingRight());
            handleTopBottomPaddingAttributes(area, margins.getPaddingTop(), margins.getPaddingBottom());
        }
    }

    /**
     * Если заданы атрибуты left/right - помещаем все необходимые области в область контейнер.
     * @param area         - Область контента
     * @param paddingLeft  - Значение свойства исходной области
     * @param paddingRight - Значение свойства исходной области
     */
    private static void handleLeftRightPaddingAttributes(IArea area, int paddingLeft, int paddingRight) {

        // Вспомогательная область
        IArea hlpArea;
        if (paddingLeft == 0 && paddingRight == 0) {
            return;
        }
        AreaRectangle borderRectangle = area.getBorderRectangle();
        // Если сумма ширин атрибутов padding больше ширины исходной области - не генерируем SPACER области
        if (borderRectangle.isWidthDefined()
                && borderRectangle.getWidth() <= paddingLeft + paddingRight) {
            return;
        }

        // Дочерние области будут располагаться в направлении расположения row

        switch (area.getProgressionDirection()) {
            case INLINE:
                return;
            case BLOCK:
                hlpArea = new CommonArea(area, null);
                hlpArea.setProgressionDirection(AreaProgressionDirection.ROW);
                hlpArea.setBorderRectangle(borderRectangle.cloneMe());
                if (area.isHasChildren()) {
                    IArea hlpArea2 = new CommonArea(hlpArea, null);
                    hlpArea2.setProgressionDirection(AreaProgressionDirection.BLOCK);
                    hlpArea2.setChildrenList(area.getChildrenList());
                    hlpArea.getChildrenList().add(hlpArea2);
                    area.setChildrenList(null);
                }
                area.getChildrenList().add(hlpArea);
                area = hlpArea;
                break;

            default:
                // В импортруемом коде ничего не было
        }

        // Добавляем область пустого пространства слева
        if (paddingLeft > 0) {
            hlpArea = new SpacerArea(area, null);
            hlpArea.getBorderRectangle().setFixedWidth(paddingLeft);
            area.getChildrenList().add(0, hlpArea);
        }

        // Добавляем область пустого пространства справа
        if (paddingRight > 0) {
            hlpArea = new SpacerArea(area, null);
            hlpArea.getBorderRectangle().setFixedWidth(paddingRight);
            area.getChildrenList().add(hlpArea);
        }

        // Корректируем размеры контент-области, если известны размеры исходной
        area.getBorderRectangle().setMaxWidth(borderRectangle.getMaxWidth() - paddingLeft - paddingRight);
        if (borderRectangle.isWidthDefined()) {
            area.getBorderRectangle().setWidth(borderRectangle.getWidth() - paddingLeft - paddingRight);
        }
    }

    /**
     * Если заданы атрибуты top/bottom - помещаем все необходимые области в область контейнер.
     * @param area          - Область контента
     * @param paddingTop    - Значение свойства исходной области
     * @param paddingBottom - Значение свойства исходной области
     */
    private static void handleTopBottomPaddingAttributes(IArea area, int paddingTop, int paddingBottom) {

        // Вспомогательная область
        IArea hlpArea;
        if (paddingTop == 0 && paddingBottom == 0) {
            return;
        }
        AreaRectangle borderRectangle = area.getBorderRectangle();
        // Если сумма высот атрибутов padding больше высоты исходной области - не генерируем SPACER области
        if (borderRectangle.isHeightDefined()
                && borderRectangle.getHeight() <= paddingTop + paddingBottom) {
            return;
        }

        // Дочерние области будут располагаться в направлении расположения block

        switch (area.getProgressionDirection()) {
            case INLINE:
                return;
            case ROW:
                hlpArea = new CommonArea(area, null);
                hlpArea.setProgressionDirection(AreaProgressionDirection.BLOCK);
                hlpArea.setBorderRectangle(borderRectangle.cloneMe());
                if (area.isHasChildren()) {
                    IArea hlpArea2 = new CommonArea(hlpArea, null);
                    hlpArea2.setProgressionDirection(AreaProgressionDirection.ROW);
                    hlpArea2.setChildrenList(area.getChildrenList());
                    hlpArea.getChildrenList().add(hlpArea2);
                    area.setChildrenList(null);
                }
                area.getChildrenList().add(hlpArea);
                area = hlpArea;
                break;

            default:
                // В импортруемом коде ничего не было
        }

        // Добавляем область пустого пространства сверху
        if (paddingTop > 0) {
            hlpArea = new SpacerArea(area, null);
            hlpArea.getBorderRectangle().setFixedHeight(paddingTop);
            area.getChildrenList().add(0, hlpArea);
        }

        // Добавляем область пустого пространства снизу
        if (paddingBottom > 0) {
            hlpArea = new SpacerArea(area, null);
            hlpArea.getBorderRectangle().setFixedHeight(paddingBottom);
            area.getChildrenList().add(hlpArea);
        }

        // Корректируем размеры контент-области, если известны размеры исходной
        area.getBorderRectangle().setMaxHeight(borderRectangle.getMaxHeight() - paddingTop - paddingBottom);
        if (borderRectangle.isHeightDefined()) {
            area.getBorderRectangle().setHeight(borderRectangle.getHeight() - paddingTop - paddingBottom);
        }
    }

    /**
     * Обрабатываем атрибуты margin.
     * @param area - Исходная область
     */
    private static void processMarginAttributes(GenericArea area) {

        // Также учитываем величину пустого пространства по высоте и ширине -
        // т.к. слишком маленькие области генерировать в Excel бессмысленно
        MarginProps margins = area.getMargins();
        if (null != margins) {
            if (margins.getMarginLeft() < PADDING_SPACE_THRESHOLD) {
                margins.setMarginLeft(0);
            }
            if (margins.getMarginRight() < PADDING_SPACE_THRESHOLD) {
                margins.setMarginRight(0);
            }
            if (margins.getMarginTop() < PADDING_SPACE_THRESHOLD) {
                margins.setMarginTop(0);
            }
            if (margins.getMarginBottom() < PADDING_SPACE_THRESHOLD) {
                margins.setMarginBottom(0);
            }

            // У самой области значения сбрасываем, чтобы не зациклиться
            area.getMargins().setMarginTop(0);
            area.getMargins().setMarginBottom(0);
            area.getMargins().setMarginLeft(0);
            area.getMargins().setMarginRight(0);

            area = handleTopBottomMarginAttributes(area, margins.getMarginTop(), margins.getMarginBottom());
            handleLeftRightMarginAttributes(area, margins.getMarginLeft(), margins.getMarginRight());
        }
    }

    /**
     * Заданы атрибуты start/end space - помещаем все необходимые области в область контейнер.
     * @param area        - Область контента
     * @param marginLeft  - Значение свойства исходной области
     * @param marginRight - Значение свойства исходной области
     * @return GenericArea - Область - контент
     */
    private static GenericArea handleLeftRightMarginAttributes(GenericArea area, int marginLeft, int marginRight) {

        if (marginLeft == 0 && marginRight == 0) {
            return area;
        }

        GenericArea hlpArea;
        IArea parentArea = area.getParentArea();
        int ind = parentArea.getChildrenList().indexOf(area);
        Assert.isTrue(ind >= 0);

        // Дочерние области будут располагаться в направлении расположения ROW

        switch (parentArea.getProgressionDirection()) {
            case INLINE:
                return area;
            case BLOCK:
                hlpArea = new CommonArea(parentArea, null);
                hlpArea.setProgressionDirection(AreaProgressionDirection.ROW);
                parentArea.getChildrenList().add(ind, hlpArea);
                area.setParentArea(hlpArea);
                ind = hlpArea.getChildrenList().size();
                hlpArea.getChildrenList().add(area);
                parentArea = hlpArea;
                area = hlpArea;
                break;

            default:
                // В импортруемом коде ничего не было
        }

        // Cперва добавляем СПРАВА, затем СЛЕВА, чтобы индекс не слетел
        // Добавляем область пустого пространства справа
        if (marginRight > 0) {
            hlpArea = new SpacerArea(parentArea, null);
            hlpArea.getBorderRectangle().setFixedWidth(marginRight);
            parentArea.getChildrenList().add(ind + 1, hlpArea);
        }
        // Добавляем область пустого пространства слева
        if (marginLeft > 0) {
            hlpArea = new SpacerArea(parentArea, null);
            hlpArea.getBorderRectangle().setFixedWidth(marginLeft);
            parentArea.getChildrenList().add(ind, hlpArea);
        }

        return area;
    }

    /**
     * Если заданы атрибуты before/after space - помещаем все необходимые области в область контейнер.
     * @param area         - Область контента
     * @param marginTop    - Значение свойства исходной области
     * @param marginBottom - Значение свойства исходной области
     * @return GenericArea - Область контента
     */
    private static GenericArea handleTopBottomMarginAttributes(GenericArea area, int marginTop, int marginBottom) {

        if (marginTop == 0 && marginBottom == 0) {
            return area;
        }

        GenericArea hlpArea;
        IArea parentArea = area.getParentArea();
        int ind = parentArea.getChildrenList().indexOf(area);
        Assert.isTrue(ind >= 0);

        // Дочерние области будут располагаться в направлении расположения block

        switch (parentArea.getProgressionDirection()) {
            case INLINE:
                return area;
            case ROW:
                hlpArea = new CommonArea(parentArea, null);
                hlpArea.setProgressionDirection(AreaProgressionDirection.BLOCK);
                parentArea.getChildrenList().add(ind, hlpArea);
                area.setParentArea(hlpArea);
                ind = hlpArea.getChildrenList().size();
                hlpArea.getChildrenList().add(area);
                parentArea = hlpArea;
                area = hlpArea;
                break;

            default:
                // В импортруемом коде ничего не было
        }

        // Сперва добавляем СНИЗУ, затем СВЕРХУ, чтобы индекс не слетел
        // Добавляем область пустого пространства снизу
        if (marginBottom > 0) {
            hlpArea = new SpacerArea(parentArea, null);
            hlpArea.getBorderRectangle().setFixedHeight(marginBottom);
            parentArea.getChildrenList().add(ind + 1, hlpArea);
        }
        // Добавляем область пустого пространства сверху
        if (marginTop > 0) {
            hlpArea = new SpacerArea(parentArea, null);
            hlpArea.getBorderRectangle().setFixedHeight(marginTop);
            parentArea.getChildrenList().add(ind, hlpArea);
        }

        return area;
    }

    /**
     * Возвращает признак того, что у области указанное свойство отличается от родительского (включая наследуемое
     * значение).
     * @param area         - Область
     * @param propertyType - Свойство
     * @return boolean    - возвращает признак того, что у области указанное свойство отличается от родительского
     */
    private static boolean isInheritablePropertyChanged(IArea area, FoPropertyType propertyType) {

        Object value = area.getProperty(propertyType);
        return null != value && value != area.getParentArea().getInheritedPropertyValue(propertyType);
    }

    /**
     * Проверка на то, что у области установлено/изменилось одно из свойств, влияющих на необходимость выделения оной
     * отдельной ячейки Excel.
     * @param area - Область
     * @return boolean  - возвращает true, если у области установлено/изменилось одно из свойств и false в противном
     *                      случае
     */
    private static boolean isImportantBlockPropertiesChanged(IArea area) {

        if (area.getProperties() == null || area.getProperties().size() == 0) {
            return false;
        }
        // Сперва проверяем свойства, которые дают необъектные значения:
        // ориентация текста, признак переноса, горизонтальное выравнивание, вертикальное выравнивание, фон, границы
        if (isInheritablePropertyChanged(area, FoPropertyType.TEXT_ALIGN)
                || isInheritablePropertyChanged(area, FoPropertyType.VERTICAL_ALIGN)
                || isInheritablePropertyChanged(area, FoPropertyType.REFERENCE_ORIENTATION)
                || isInheritablePropertyChanged(area, FoPropertyType.BACKGROUND_COLOR)
                || isInheritablePropertyChanged(area, FoPropertyType.WRAP_OPTION)
                || ((FoBorder)area.getPropertyValue(FoPropertyType.BORDER_TOP)).isDefined()
                || ((FoBorder)area.getPropertyValue(FoPropertyType.BORDER_BOTTOM)).isDefined()
                || ((FoBorder)area.getPropertyValue(FoPropertyType.BORDER_LEFT)).isDefined()
                || ((FoBorder)area.getPropertyValue(FoPropertyType.BORDER_RIGHT)).isDefined()) {
            return true;
        }
        // Если у области есть красная строка
        Dimension textIndent = (Dimension)area.getPropertyValue(FoPropertyType.TEXT_INDENT);
        if (!textIndent.isPercentage() && textIndent.hasValue()) {
            return true;
        }

        return false;
    }

    /**
     * Рекурсивный метод для прохода по всему дереву.
     * Это нужно для того, чтобы в случае совокупности дочерних &lt;fo:inline&gt; и иже с ними элементов
     * для родительского элемента &lt;fo:block&gt; выставить тоже inline.
     * @param parentArea - Текущий корневой узел поддерева областей
     * @return boolean  - Наличие прямоугольных областей
     */
    private static boolean checkAreaProgressionDirection(IArea parentArea) {

        boolean onlyInlineDirectionFound = true;

        // Проходим по всем дочерним узлам текущего узла
        if (parentArea.isHasChildren()) {
            for (IArea childArea : parentArea.getChildrenList()) {
                // Используем форму записи логического И, запрещающую сокращать вычисления
                onlyInlineDirectionFound = checkAreaProgressionDirection(childArea) & onlyInlineDirectionFound;
            }
        }

        if (parentArea.getProgressionDirection() == AreaProgressionDirection.INLINE) {
            if (!onlyInlineDirectionFound) {
                // внутри тега inline вложили явно не inline блоки!
                parentArea.setProgressionDirection(AreaProgressionDirection.BLOCK);
            }
        } else if (onlyInlineDirectionFound) {
            // Родитель был не инлайн, но все дети типа инлайн
            AreaType areaType = parentArea.getAreaType();
            if (areaType != BLOCK) {
                if (parentArea.isHasChildren()
                        && !Arrays.asList(SPACER, TABLE_CELL, LIST_ITEM_LABEL, LIST_ITEM_BODY).contains(areaType)) {
                    parentArea.setProgressionDirection(AreaProgressionDirection.INLINE);
                }

                onlyInlineDirectionFound = false;
            } else {
                parentArea.setProgressionDirection(AreaProgressionDirection.INLINE);
                // Мы не можем распространиться наверх, если у блока не совпадает с его родителем одно из важных
                // свойств
                onlyInlineDirectionFound = !isImportantBlockPropertiesChanged(parentArea);
            }
        }
        return onlyInlineDirectionFound;
    }

    public ISpacesRemover getSpacesRemover() {
        return spacesRemover;
    }

    public void setSpacesRemover(ISpacesRemover spacesRemover) {
        this.spacesRemover = spacesRemover;
    }
}
