package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.ISpacesRemover;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.RenderType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.impl.DefaultSpacesRemover;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.PageSequenceMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.RegionAfter;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.RegionBefore;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.RegionBody;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.SinglePageMasterReferenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.FlowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.PageSequenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.StaticContentArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.BasicLinkArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.BlockArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.CharacterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.ExternalGraphicArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.InlineArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.InstreamForeignObjectArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.PageNumberArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.PageNumberCitationArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.listblock.ListBlockArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.listblock.ListItemArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.listblock.ListItemBodyArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.listblock.ListItemLabelArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableBodyArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableCellArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableColumnArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableFooterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableHeaderArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableRowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.RootArea;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Map;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.PAGE_SEQUENCE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.PAGE_SEQUENCE_MASTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.ROOT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.SIMPLE_PAGE_MASTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.CONTENT_TYPE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MASTER_NAME;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.CONTENT_TYPE_SCRIPT;

/**
 * Класс, хранящий дерево объектов.
 * Created by vsavenkov on 23.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("unchecked")
@Component
public class XslFoTreeBuilder {

    //region Поля для внешнего использования и свойства доступа к этим полям

    /**
     * Область RootArea.
     */
    private RootArea rootArea;

    /**
     * Реализация удаления двойных пробелов.
     */
    private ISpacesRemover spacesRemover = new DefaultSpacesRemover();

    /**
     * Свойство - область RootArea.
     *
     * @return GenericArea - возвращает область RootArea
     */
    public GenericArea getArea() {
        return rootArea;
    }
    //endregion

    //region Методы для внешнего использования

    /**
     * Запуск процесса построения дерева XSL-FO объектов.
     *
     * @param reader - Объект чтения Xml-файла
     * @throws XMLStreamException - генерится XMLStreamReader`ом
     */
    public void buildTree(XmlTextReader reader) throws XMLStreamException {

        //Создаем корневую область
        rootArea = new RootArea();

        //Переводим курсор на тег XslFo Root
        reader.moveToElement(ROOT.getFoName());

        //Рекурсивно обрабатываем все дочерние элементы
        parseChildrenElement(reader, rootArea);
    }

    private static String tab = "";

    /**
     * Рекурсивный метод для обработки элементов текущего уровня вложенности XML файла и получения областей.
     * Объекты чтения XML файла и родительская область должны быть проинициализированы.
     * Объекты чтения XML файла должен иметь указатель установленным на первый дочерний элемент
     * элемента родительской области
     *
     * @param reader     - Объект чтения XML файла
     * @param parentArea - Родительская область - ОБЛАСТЬ КОНТЕНТА!!!
     * @throws XMLStreamException генерирует XMLEventReader
     */
    private void parseChildrenElement(XmlTextReader reader, GenericArea parentArea) throws XMLStreamException {
        SimplePageMasterArea pageMaster = null;//Ссылка на последний созданный объект SimplePageMaster
        PageSequenceMasterArea pageSequenceMaster = null;//Ссылка на последний созданный объект PageSequenceMaster
        GenericArea childArea;             // Дочерняя область

        // Обрабатываем все узлы одного уровня. Но из корневого цикла выпускаем только, когда закроется корневой тег
        while (getPassCondition(reader, parentArea)) {
            if (reader.isEndElement()) {
                reader.moveToNextElementOrText();
                continue;
            }

            // Если встретили текстовый элемент - обрабатываем его
            if (XMLStreamConstants.CHARACTERS == reader.getEventType()
                    || XMLStreamConstants.CDATA == reader.getEventType()) {
                parseTextData(reader, parentArea);
                continue;
            }

            // Создаем список дочерних областей если необходимо
            if (parentArea.getChildrenList() == null) {
                parentArea.setChildrenList(new ArrayList<>());
            }

            //Определяем тип области
            AreaType areaType = AreaType.parseValue(reader.getPrefixedName(), RenderType.WORD);

            // Получаем список атрибутов элемента
            Map<String, String> attributeList = reader.getAttributeList();

            // Рекурсивная обработка вложенных элементов в зависимости от типа
            switch (areaType) {
                case ROOT:
                    reader.moveToNextElement();
                    continue;

                case LAYOUT_MASTER_SET:
                    reader.moveToNextElement();
                    continue;

                    //region Simple Page master

                case SIMPLE_PAGE_MASTER:
                    if (parentArea.getType() != ROOT) {
                        throw new RuntimeException("Не правильная последовательность для тега <simple-page-master>");
                    }
                    SimplePageMasterArea simplePageMaster = new SimplePageMasterArea(parentArea, attributeList);
                    pageMaster = simplePageMaster;
                    ((RootArea) parentArea).getLayoutMasterSet()
                            .put(simplePageMaster.getPropertyValue(MASTER_NAME), simplePageMaster);
                    reader.moveToNextElement();
                    continue;

                case REGION_BEFORE:
                    if ((parentArea.getType() != ROOT)
                            && (pageMaster.getType() != SIMPLE_PAGE_MASTER)) {
                        throw new RuntimeException("Не правильная последовательность для тега <region-before>");
                    }
                    RegionBefore regionBefore = new RegionBefore(parentArea, attributeList);
                    pageMaster.setRegionBefore(regionBefore);
                    reader.moveToNextElement();
                    continue;

                case REGION_BODY:
                    if ((parentArea.getType() != ROOT)
                            && (pageMaster.getType() != SIMPLE_PAGE_MASTER)) {
                        throw new RuntimeException("Не правильная последовательность для тега <region-body>");
                    }
                    RegionBody regionBody = new RegionBody(parentArea, attributeList);
                    pageMaster.setRegionBody(regionBody);
                    reader.moveToNextElement();
                    continue;

                case REGION_AFTER:
                    if ((parentArea.getType() != ROOT) && (pageMaster.getType() != SIMPLE_PAGE_MASTER)) {
                        throw new RuntimeException("Не правильная последовательность для тега <region-after>");
                    }
                    RegionAfter regionAfter = new RegionAfter(parentArea, attributeList);
                    pageMaster.setRegionAfter(regionAfter);
                    reader.moveToNextElement();
                    continue;

                case PAGE_SEQUENCE_MASTER:
                    if (parentArea.getType() != ROOT) {
                        throw new RuntimeException("Не правильная последовательность для тега <page-sequence-master>");
                    }
                    PageSequenceMasterArea pageSequenceMasterArea = new PageSequenceMasterArea(parentArea,
                            attributeList);
                    pageSequenceMaster = pageSequenceMasterArea;
                    ((RootArea) parentArea).getLayoutMasterSet().put(
                            pageSequenceMasterArea.getPropertyValue(MASTER_NAME), pageSequenceMaster);
                    reader.moveToNextElement();
                    continue;

                case SINGLE_PAGE_MASTER_REFERENCE:
                    if (parentArea.getType() != ROOT) {
                        throw new RuntimeException(
                                "Не правильная последовательность для тега <single-page-master-reference>");
                    }
                    SinglePageMasterReferenceArea singlePageMasterReference =
                            new SinglePageMasterReferenceArea(parentArea, attributeList);
                    pageSequenceMaster.getSinglePageMasterReferences().add(singlePageMasterReference);
                    reader.moveToNextElement();
                    continue;

                    //endregion

                case PAGE_SEQUENCE:
                    if (parentArea.getType() != ROOT) {
                        throw new RuntimeException("Не правильная последовательность для тега <page-sequence>");
                    }
                    childArea = new PageSequenceArea(parentArea, attributeList);
                    break;

                case STATIC_CONTENT:
                    if ((parentArea.getType() != PAGE_SEQUENCE) && (parentArea.getType() != PAGE_SEQUENCE_MASTER)) {
                        throw new RuntimeException("Не правильная последовательность для тега <static-content>");
                    }
                    childArea = new StaticContentArea(parentArea, attributeList);
                    break;

                case FLOW:
                    if ((parentArea.getType() != PAGE_SEQUENCE) && (parentArea.getType() != PAGE_SEQUENCE_MASTER)) {
                        throw new RuntimeException("Не правильная последовательность для тега <flow>");
                    }
                    childArea = new FlowArea(parentArea, attributeList);
                    break;

                //region List Block
                case LIST_BLOCK:
                    childArea = new ListBlockArea(parentArea, attributeList);
                    break;


                case LIST_ITEM:
                    childArea = new ListItemArea(parentArea, attributeList);
                    break;


                case LIST_ITEM_LABEL:
                    ListItemLabelArea listItemLabel = new ListItemLabelArea(parentArea, attributeList);
                    childArea = listItemLabel;
                    break;


                case LIST_ITEM_BODY:
                    ListItemBodyArea listItemBody = new ListItemBodyArea(parentArea, attributeList);
                    childArea = listItemBody;
                    break;

                //endregion

                case BASIC_LINK:
                    if ((parentArea.getType() != AreaType.BLOCK) && (parentArea.getType() != AreaType.INLINE)) {
                        throw new RuntimeException("Не правильная последовательность для тега <basic-link>");
                    }
                    childArea = new BasicLinkArea(parentArea, attributeList);
                    break;

                case BLOCK:
                    childArea = new BlockArea(parentArea, attributeList);
                    break;

                case CHARACTER:
                    childArea = new CharacterArea(parentArea, attributeList);
                    break;

                case INLINE:
                    if ((parentArea.getType() != AreaType.BLOCK) && (parentArea.getType() != AreaType.BASIC_LINK)) {
                        throw new RuntimeException("Не правильная последовательность для тега <inline>");
                    }
                    childArea = new InlineArea(parentArea, attributeList);
                    break;

                case PAGE_NUMBER:
                    childArea = new PageNumberArea(parentArea, attributeList);
                    break;

                case PAGE_NUMBER_CITATION:
                    childArea = new PageNumberCitationArea(parentArea, attributeList);
                    break;

                //region Table
                case TABLE:
                    childArea = new TableArea(parentArea, attributeList);
                    break;

                case TABLE_COLUMN:
                    if ((parentArea.getType() != AreaType.TABLE)) {
                        throw new RuntimeException("Не правильная последовательность для тега <table-column>");
                    }
                    // Обрабатываем элемент колонки таблицы
                    TableColumnArea.createTableColumns((TableArea) parentArea, attributeList);
                    reader.skipToNextElement();
                    continue;

                case TABLE_HEADER:
                    if ((parentArea.getType() != AreaType.TABLE)) {
                        throw new RuntimeException("Не правильная последовательность для тега <table-header>");
                    }
                    childArea = new TableHeaderArea(parentArea, attributeList);
                    break;

                case TABLE_BODY:
                    if ((parentArea.getType() != AreaType.TABLE)) {
                        throw new RuntimeException("Не правильная последовательность для тега <table-body>");
                    }
                    childArea = new TableBodyArea(parentArea, attributeList);
                    break;

                case TABLE_FOOTER:
                    if ((parentArea.getType() != AreaType.TABLE)) {
                        throw new RuntimeException("Не правильная последовательность для тега <table-footer>");
                    }
                    childArea = new TableFooterArea(parentArea, attributeList);
                    break;

                case TABLE_ROW:
                    if ((parentArea.getType() != AreaType.TABLE_HEADER)
                            && (parentArea.getType() != AreaType.TABLE_BODY)
                            && (parentArea.getType() != AreaType.TABLE_FOOTER)) {
                        throw new RuntimeException("Не правильная последовательность для тега <table-row>");
                    }
                    childArea = new TableRowArea(parentArea, attributeList);
                    break;

                case TABLE_CELL:
                    if (parentArea.getType() != AreaType.TABLE_ROW) {
                        throw new RuntimeException("Не правильная последовательность для тега <table-cell>");
                    }
                    childArea = new TableCellArea(parentArea, attributeList);
                    break;
                //endregion

                case EXTERNAL_GRAPHIC:
                    childArea = new ExternalGraphicArea(parentArea, attributeList);
                    break;

                case INSTREAM_FOREIGN_OBJECT:
                    if (attributeList != null && attributeList.containsKey(CONTENT_TYPE.getPropertyName())
                            && CONTENT_TYPE_SCRIPT.equals(attributeList.get(CONTENT_TYPE.getPropertyName()))) {
                        reader.skipToNextElement();
                        continue;
                    }
                    childArea = new InstreamForeignObjectArea(reader, parentArea, attributeList);
                    break;


                default:
                    // По умолчанию - переходим на следующий элемент
                    reader.skipToNextElement();
                    continue;
            }


            // Добавляем дочернюю область в список областей родительской области
            parentArea.getChildrenList().add(childArea);

            boolean isFlag = true;
            //Переходим на следующий тег или текстовый элемент в теге
            if (!reader.isEmptyElement() && !reader.isEndElement()) {
                reader.moveToNextElementOrText();
                isFlag = false;
            }

            if ((XMLStreamConstants.END_ELEMENT != reader.getEventType()) && (!isFlag)) {
                // Если есть вложенные элементы - обрабатываем их рекурсивно
                parseChildrenElement(reader, childArea);
            } else {
                // Если пустой элемент - переходим на следующий
                reader.moveToNextElementOrText();
            }
            // Пост-обработка свойств
            childArea.postProcessProperties();
        }
        //Вышли из основного цикла

        // Вышли из цикла - Переходим на следующий тег или текстовый элемент в теге
        reader.moveToNextElementOrText();
    }

    private static boolean getPassCondition(XmlTextReader reader, GenericArea parentArea) throws XMLStreamException {
        return reader.hasNext()
                && (!reader.isEndElement()
                || (reader.isEndElement()
                && parentArea.getType() != AreaType.parseValue(reader.getPrefixedName(), RenderType.WORD)));
    }

    //endregion

    //region Методы для внутреннего использования

    /**
     * Считывает текстовые данные.
     *
     * @param reader     - Объект чтения Xml-файла
     * @param parentArea - родительская область
     * @throws XMLStreamException - генерирует XMLStreamReader
     */
    private void parseTextData(XmlTextReader reader, GenericArea parentArea) throws XMLStreamException {

        String text = reader.getText(); // Текст
        // Переходим на следующий элемент
        reader.moveToNextElementOrText();
        switch (parentArea.getType()) {
            case BLOCK:
                //Чистка от лишних пробельных символов
                text = spacesRemover.removeDoubleSpaces(text, parentArea.hasChildren(),
                        XMLStreamConstants.END_ELEMENT != reader.getEventType());

                //Если текст в блоке, представляем его как объект inline и передаем ему свойства блока
                if (parentArea.getChildrenList() == null) {
                    parentArea.setChildrenList(new ArrayList<>());
                }
                GenericArea childArea = new InlineArea(parentArea, null);
                childArea.setText(text);
                parentArea.getChildrenList().add(childArea);
                break;

            case INLINE:
            case BASIC_LINK:
                //Чистка от лишних пробельных символов
                text = spacesRemover.removeDoubleSpaces(text, parentArea.hasChildren(),
                        XMLStreamConstants.END_ELEMENT != reader.getEventType());
                if (!parentArea.hasChildren()) {
                    parentArea.setText(parentArea.getText() + text);
                }
                break;

            default:
                // В импортруемом коде ничего не было
        }
    }

    //endregion

    @Autowired(required = false)
    public void setSpacesRemover(ISpacesRemover spacesRemover) {
        this.spacesRemover = spacesRemover;
    }
}
