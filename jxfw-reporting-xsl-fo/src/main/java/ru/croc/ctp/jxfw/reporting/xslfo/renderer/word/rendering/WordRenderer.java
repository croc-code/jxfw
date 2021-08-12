package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering;

import static com.aspose.words.HeaderFooterType.FOOTER_PRIMARY;
import static com.aspose.words.HeaderFooterType.HEADER_PRIMARY;
import static com.aspose.words.HeightRule.EXACTLY;
import static com.aspose.words.NodeType.SECTION;
import static com.aspose.words.Orientation.LANDSCAPE;
import static com.aspose.words.RelativeHorizontalPosition.CHARACTER;
import static com.aspose.words.RelativeVerticalPosition.PARAGRAPH;
import static com.aspose.words.ShapeType.IMAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.ROOT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.TABLE_BODY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.TABLE_FOOTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.TABLE_HEADER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND_COLOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND_IMAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.EXTERNAL_DESTINATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.FLOW_NAME;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.INITIAL_PAGE_NUMBER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MASTER_REFERENCE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PAGE_HEIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PAGE_WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REFERENCE_ORIENTATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.SRC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs.ZERO;

import com.aspose.words.Body;
import com.aspose.words.Border;
import com.aspose.words.BorderCollection;
import com.aspose.words.Cell;
import com.aspose.words.CellFormat;
import com.aspose.words.CellMerge;
import com.aspose.words.CompositeNode;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.HeaderFooter;
import com.aspose.words.HeightRule;
import com.aspose.words.Paragraph;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.Shape;
import com.aspose.words.Style;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.Table;
import com.aspose.words.TextOrientation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.SideValues;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute.FoBorder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute.FoFont;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.FlowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.PageSequenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.StaticContentArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block.BasicLinkArea;
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
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.BlockProperties;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.BorderHelper;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.FontHelper;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.FormatHelper;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.InlineProperties;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.TableHelper;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.TableProperties;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.table.XslfoTable;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Класс, реализующий рендеринг дерева объектов XSL-FO в объектную модель Word.
 * Created by vsavenkov on 23.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings({"deprecation", "rawtypes"})
@Component
public class WordRenderer {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(WordRenderer.class);

    private final XfwReportProfileManager profileManager;


    /**
     * Конструктор.
     * @param profileManager {@link XfwReportProfileManager}
     */
    @Autowired
    public WordRenderer(XfwReportProfileManager profileManager) {
        this.profileManager = profileManager;

        try {
            document = new Document();
        } catch (Exception e) {
            logger.error("Error create aspose word document.", e);
        }
    }

    /**
     * Хранит дерево объектов Word.
     */
    private Document document;

    /**
     * Возвращает дерево объектов Word.
     * @return Document - Возвращает дерево объектов Word.
     */
    public Document getWordDocument() {
        return document;
    }

    /**
     * Метод формирует документ Word из дерева объектов.
     * @param area - Корневая область дерева XslFo
     * @throws Exception генерируют классы com.aspose.words
     */
    public void createDocument(GenericArea area) throws Exception {
        
        if (area.getType() != ROOT) {
            throw new IllegalArgumentException("область не является корневой");
        }

        getWordDocument().removeAllChildren();
        // Устанавливаем в стиле по умолчанию наш шрифт по умолчанию
        Style defaultStyle = getWordDocument().getStyles().get(StyleIdentifier.NORMAL);
        FontHelper.assign(defaultStyle.getFont(), FoFont.getDefaultFont());

        CompositeNode parentNode = getWordDocument();
        //Рекурсивная обработка дочерних элементов корневой области и создание дерева объектов aspose
        treeRenderer(area, parentNode);
        getWordDocument().updateFields();
        getWordDocument().updatePageLayout();
    }


    /**
     * Рекурсивный метод для построения дерева объетов aspose.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    public void treeRenderer(GenericArea area, CompositeNode nodeParent) throws Exception {
        CompositeNode currentNode = nodeParent;
        switch (area.getType()) {
            case PAGE_SEQUENCE:
                currentNode = addPage((PageSequenceArea)area, nodeParent);
                break;

            case FLOW:
                currentNode = addFlow((FlowArea)area, nodeParent);
                break;

            case STATIC_CONTENT:
                currentNode = addRegion((StaticContentArea)area, nodeParent);
                break;

            case BLOCK:
                currentNode = BlockProperties.addBlock(area, nodeParent);
                break;

            case INLINE:
                currentNode = addInline((InlineArea)area, nodeParent);
                break;

            case PAGE_NUMBER:
                currentNode = addPageNumber((PageNumberArea)area, nodeParent);
                break;

            case PAGE_NUMBER_CITATION:
                currentNode = addPageNumberCitation((PageNumberCitationArea)area, nodeParent);
                break;

            case BASIC_LINK:
                currentNode = addBasicLink((BasicLinkArea)area, nodeParent);
                break;

            case CHARACTER:
                currentNode = addCharacter((CharacterArea)area, nodeParent);
                break;

            case EXTERNAL_GRAPHIC:
                currentNode = addGraphic((ExternalGraphicArea)area, nodeParent);
                break;

            case TABLE:
                currentNode = addTable((TableArea)area, nodeParent);
                break;

            case TABLE_HEADER:
                currentNode = addTableHeader((TableHeaderArea)area, nodeParent);
                break;

            case TABLE_BODY:
                currentNode = addTableBody((TableBodyArea)area, nodeParent);
                break;

            case TABLE_FOOTER:
                currentNode = addTableFooter((TableFooterArea)area, nodeParent);
                break;

            case TABLE_ROW:
                currentNode = addTableRow((TableRowArea)area, nodeParent);
                break;

            case TABLE_CELL:
                currentNode = addTableCell((TableCellArea)area, nodeParent);
                break;

            case INSTREAM_FOREIGN_OBJECT:
                currentNode = addInstreamForeignObject((InstreamForeignObjectArea)area, nodeParent);
                break;

            case LIST_BLOCK:
                currentNode = addListBlock((ListBlockArea)area, nodeParent);
                break;

            case LIST_ITEM:
                currentNode = addListItem((ListItemArea)area, nodeParent);
                break;

            case LIST_ITEM_LABEL:
                currentNode = addListItemLabel((ListItemLabelArea)area, nodeParent);
                break;

            case LIST_ITEM_BODY:
                currentNode = addListItemBody((ListItemBodyArea)area, nodeParent);
                break;

            default:
                break;
        }
        // Сохраняем признак того, что при обработке был сформирован новый объект в Aspose
        area.setOutputObjectCreated(currentNode != nodeParent);

        //Обрабатываем дочерние элементы если они есть
        if (area.hasChildren()) {
            for (GenericArea areaChild : area.getChildrenList()) {
                treeRenderer(areaChild, currentNode);
            }
        }

        if (area.isOutputObjectCreated()) {
            switch (area.getType()) {
                case FLOW:
                    // Завершение формирования потока
                    completeFlow(currentNode);
                    break;
                case TABLE:
                    // Завершение формирования таблицы
                    completeTable((XslfoTable)currentNode);
                    break;
                case LIST_BLOCK:
                    // Завершение формирования списка
                    completeListBlock((Table)currentNode);
                    break;
                case LIST_ITEM:
                    // Завершение формирование элемента списка
                    completeListItem((ListItemArea)area, (Row)currentNode);
                    break;
                default:
                    // Завершение формирования абзаца
                    if (currentNode instanceof Paragraph) {
                        BlockProperties.completeBlock(area, (Paragraph) currentNode);
                    }
                    break;
            }
        }
    }

    //region WordRenderer.BasicLink.cs
    /**
     * Метод добавляет параграф в Aspose и устанавливает все параметры.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addBasicLink(BasicLinkArea area, CompositeNode nodeParent) throws Exception {

        Paragraph paragraph = InlineProperties.getParentForInline(area, nodeParent);
        DocumentBuilder builder = new DocumentBuilder(getWordDocument());
        builder.moveTo(paragraph);

        //Установка свойств шрифта
        FontHelper.assignFontArea(builder.getFont(), area,
                paragraph.getParagraphFormat().getShading().getBackgroundPatternColor());

        String urlText = area.getText();
        //Если родитель параграфа - ячейка и у нее есть свойство reference orientation, то выставляем высоту строки
        InlineProperties.setHeightForParentCell(paragraph.getParentNode(), area, builder.getFont().getSize(),
                urlText.length());

        String stringUrl = HelpFuncs.getImageSource((String)area.getPropertyValue(EXTERNAL_DESTINATION));

        writeLink(builder, stringUrl, urlText, paragraph);

        return paragraph;
    }

    private void writeLink(DocumentBuilder builder, String stringUrl, String urlText, Paragraph paragraph) {
        Run run = new Run(getWordDocument(), urlText);
        if (StringUtils.isBlank(stringUrl)) {
            paragraph.appendChild(run);
            return;
        }
        try {
            URL url = new URL(stringUrl);
            builder.insertHyperlink(urlText, url.getPath(), false);
        } catch (MalformedURLException e) {
            paragraph.appendChild(run);
        }
    }
    //endregion
    
    //region WordRenderer.Character.cs
    /**
     * Метод добавляет inline в документ.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addCharacter(CharacterArea area, CompositeNode nodeParent) throws Exception {
        
        String propertyValue = (String)area.getPropertyValue(FoPropertyType.CHARACTER);
        if (StringUtils.isBlank(propertyValue)) {
            return nodeParent;
        }

        //Выставляем родителя для элемента
        Paragraph paragraph = InlineProperties.getParentForInline(area, nodeParent);
        Run run = new Run(getWordDocument(), propertyValue);
        paragraph.appendChild(run);

        //Установка свойств шрифта
        FontHelper.assignFontArea(run.getFont(), area,
                paragraph.getParagraphFormat().getShading().getBackgroundPatternColor());

        //Если родитель параграфа - ячейка и у нее есть свойство reference orientation, то выставляем высоту строки
        InlineProperties.setHeightForParentCell(paragraph.getParentNode(), area, run.getFont().getSize(),
                propertyValue.length());

        return paragraph;
    }
    //endregion
    
    //region WordRenderer.ExternalGraphic.cs
    /**
     * Метод добавляет графическое изображение и устанавливает все параметры.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addGraphic(ExternalGraphicArea area, CompositeNode nodeParent) throws Exception {
        
        if (!area.hasProperties()) {
            return nodeParent;
        }
        Shape shape = new Shape(getWordDocument(), IMAGE);
        String source = HelpFuncs.getImageSource((String)area.getPropertyValue(SRC));
        if (StringUtils.isBlank(source)) {
            return nodeParent;
        }

        if (!loadImage(shape, source)) {
            return nodeParent;
        }

        //oShape.ImageData.SetImage(sSource);
        shape.setRelativeHorizontalPosition(CHARACTER);
        shape.setRelativeVerticalPosition(PARAGRAPH);
        shape.setBehindText(false);

        Paragraph shapeParagraph = BlockProperties.addBlock(area, nodeParent);
        Cell shapeCell = (Cell)shapeParagraph.getParentNode();
        Assert.isNull(shapeCell, "oShapeCell");
        calcShapeSize(area, shape, shapeCell);
        shapeParagraph.appendChild(shape);
        return shapeParagraph;
    }

    /**
     * Расчет размеров картинки.
     * @param area      - Область Xsl-fo
     * @param shape     - Картинка
     * @param shapeCell - Ячейка, в которую вписываем картинку
     * @throws Exception генерируют классы com.aspose.words
     */
    private static void calcShapeSize(GenericArea area, Shape shape, Cell shapeCell) throws Exception {
        
        double width = 0;
        double height = 0;
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    case CONTENT_HEIGHT:
                        Double heightFromEntry = HelpFuncs.getSizeInPointsEx((String)value);
                        height = "auto".equals(value)
                              ? shape.getImageData().getImageSize().getHeightPoints()
                              : null != heightFromEntry ? heightFromEntry : height;
                        break;
                    case CONTENT_WIDTH:
                        Double widthFromEntry = HelpFuncs.getSizeInPointsEx((String)value);
                        width = "auto".equals(value)
                             ? Math.min(shape.getImageData().getImageSize().getWidthPoints(),
                                HelpFuncs.getWidthForFill(shapeCell))
                             : null != widthFromEntry ? widthFromEntry : width;
                        break;
                    case HEIGHT:
                        height = (double)value;
                        break;
                    case WIDTH:
                        width = (double)value;
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
            }
        }
        if (height > 0) {
            shape.setHeight(height);
        } else {
            shape.setHeight(shape.getImageData().getImageSize().getHeightPoints());
            height = shape.getHeight();
        }
        if (width > 0) {
            shape.setWidth(width);
        } else {
            shape.setWidth(Math.min(shape.getImageData().getImageSize().getWidthPoints(),
                    HelpFuncs.getWidthForFill(shapeCell)));
            width = shape.getWidth();
        }

        CellFormat cellFormat = shapeCell.getCellFormat();
        width += Math.max(cellFormat.getLeftPadding(), cellFormat.getBorders().getLeft().getLineWidth() / 2)
                + Math.max(cellFormat.getRightPadding(), cellFormat.getBorders().getRight().getLineWidth() / 2);
        height += Math.max(cellFormat.getTopPadding(), cellFormat.getBorders().getTop().getLineWidth() / 2)
                + Math.max(cellFormat.getBottomPadding(), cellFormat.getBorders().getBottom().getLineWidth() / 2);
        shapeCell.getParentRow().getRowFormat().setHeight(height);
        shapeCell.getParentRow().getRowFormat().setHeightRule(EXACTLY);
        // При наличии отступа сверху или снизу могли добавиться строки. Проставим правильную ширину во всех строках
        // таблицы
        int index = shapeCell.getParentRow().indexOf(shapeCell);
        Assert.isTrue(index >= 0, "nInd >= 0");
        for (Row row : shapeCell.getParentRow().getParentTable().getRows()) {
            row.getCells().get(index).getCellFormat().setWidth(width);
        }
    }


    /**
     * загрузка картинки в фигуру.
     * @param shape     - фигура
     * @param imagePath - путь до изображения
     * @return признак успешности загрузки
     */
    private boolean loadImage(Shape shape, String imagePath) {
        
        URI imageUri = getUriFromPath(imagePath);
        if (imageUri == null) { 
            return false;
        }

        try {
            shape.getImageData().setImage(imageUri.getPath());
        } catch (Exception e) {
            logger.debug("Ошибка, установки изображения: " + imagePath, e);
            return false;
        }

        return true;
    }

    
    /**
     * загрузка потока в массив байт.
     * @param input - входной поток
     * @return массив байт из потока
     * @throws IOException генерирует java.io.InputStream
     */
    private static byte[] readFully(InputStream input) throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[16 * 1024];
        int read;
        while ((read = input.read(buffer, 0, buffer.length)) > 0) {
            result.write(buffer, 0, read);
        }
        return result.toByteArray();
    }
    
    /**
     * Преобразует путь к Uri.
     * @param imagePath - Путь к картинке
     * @return Путь в формате Uri
     */
    private URI getUriFromPath(String imagePath) {
        try {
            return profileManager.getResource(imagePath).getURI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //endregion
    
    //region WordRenderer.Flow.cs
    /**
     * Метод обрабатывает тег fo:Flow.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addFlow(FlowArea area, CompositeNode nodeParent) throws Exception {
        //создаем макет отображения тега в Aspose
        if (getWordDocument().getLastSection().getBody() != null) {
            nodeParent = getWordDocument().getLastSection().getBody();
        } else {
            Body body = new Body(getWordDocument());
            getWordDocument().getLastSection().appendChild(body);
            BlockProperties.addNullParagraph(nodeParent);
            nodeParent = body;
        }

        String flowName = (String)area.getPropertyValue(FLOW_NAME);
        //Заполняем аттрибутами
        if (!StringUtils.isBlank(flowName)) {

            String masterName = (String)area.getParentArea().getPropertyValue(MASTER_REFERENCE);
            SimplePageMasterArea simplePage = HelpFuncs.getSimplePageMaster(
                    ((RootArea)area.getParentArea().getParentArea()).getLayoutMasterSet(), masterName);
            if (simplePage.getRegionBefore() != null) {
                if (flowName.equals(simplePage.getRegionBefore().getRegionName())) {
                    nodeParent = getWordDocument().getLastSection();
                    nodeParent = addCatchWord(area, nodeParent);
                }
            }
            if (simplePage.getRegionAfter() != null) {
                if (flowName.equals(simplePage.getRegionAfter().getRegionName())) {
                    nodeParent = getWordDocument().getLastSection();
                    nodeParent = addCatchWord(area, nodeParent);
                }
            }
            if (simplePage.getRegionBody() != null) {
                if (flowName.equals(simplePage.getRegionBody().getRegionName()) || flowName == "xsl-region-body") {
                    nodeParent = getWordDocument().getLastSection().getBody();
                    nodeParent = addRegionBody(area, nodeParent);
                }
            }
        }
        return nodeParent;
    }

    /**
     * Заверешение формирования потока.
     * @param currentNode - Узел потока
     */
    public static void completeFlow(CompositeNode currentNode) {
        
        if (currentNode.getCount() > 1 && BlockProperties.isDummyParagraph(currentNode.getChildNodes().get(0))) {
            currentNode.getChildNodes().get(0).remove();
        }
    }
    //endregion
    
    //region WordRenderer.Inline.cs
    /**
     * Метод добавляет  inline в документ.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addInline(InlineArea area, CompositeNode nodeParent) throws Exception {
        
        if (StringUtils.isBlank(area.getText())) {
            return nodeParent;
        }

        Paragraph paragraph = InlineProperties.getParentForInline(area, nodeParent);
        Run run = new Run(getWordDocument(), area.getText());
        paragraph.appendChild(run);

        //Установка свойств шрифта                    
        FontHelper.assignFontArea(run.getFont(), area,
                paragraph.getParagraphFormat().getShading().getBackgroundPatternColor());

        //Если родитель параграфа - ячейка и у нее есть свойство reference orientation, то выставляем высоту строки
        InlineProperties.setHeightForParentCell(paragraph.getParentNode(), area, run.getFont().getSize(),
                area.getText().length());

        return paragraph;
    }
    //endregion
    
    //region WordRenderer.InstreamForeignObject.cs
    /**
     * Метод добавляет в документ svg объект.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addInstreamForeignObject(InstreamForeignObjectArea area, CompositeNode nodeParent) throws
            Exception {
        
        if (StringUtils.isBlank(area.getPictureResource())) {
            return nodeParent;
        }

        Shape shape = new Shape(getWordDocument(), IMAGE);
        shape.getImageData().setImage(area.getPictureResource());
        shape.setRelativeHorizontalPosition(CHARACTER);
        shape.setRelativeVerticalPosition(PARAGRAPH);
        shape.setBehindText(true);

        Paragraph shapeParagraph = BlockProperties.addBlock(area, nodeParent);
        Cell shapeCell = (Cell)shapeParagraph.getParentNode();
        Assert.isNull(shapeCell, "oShapeCell");
        calcShapeSize(area, shape, shapeCell);
        shapeParagraph.appendChild(shape);
        return shapeParagraph;
    }
    //endregion
    
    //region WordRenderer.ListBlock.cs
    /**
     * Метод добавляет список.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addListBlock(ListBlockArea area, CompositeNode nodeParent) throws Exception {
        
        //Свойства margin
        SideValues margins = new SideValues();
        //Свойства padding
        SideValues paddings = new SideValues();

        //Создаем макет отображения тэга в Aspose
        Table maketTable = new Table(getWordDocument());
        final Row maketRow = TableHelper.ceateRow(getWordDocument());
        final Cell maketCell = new Cell(getWordDocument());
        //MaketRow.RowFormat.AllowAutoFit = true;
        nodeParent = HelpFuncs.getNodeForBlock(nodeParent);

        BlockProperties.addNullParagraph(nodeParent);
        nodeParent.appendChild(maketTable);
        BlockProperties.addNullParagraph(nodeParent);

        maketTable.appendChild(maketRow);
        maketRow.appendChild(maketCell);

        if (!area.hasProperties()) {
            //Установка ширины
            maketCell.getCellFormat().setWidth(HelpFuncs.getWidthForFill(nodeParent));
        } else {
            //Заполняем аттрибутами
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    //height
                    case HEIGHT:
                        maketRow.getRowFormat().setHeight((double)value);
                        break;
                    //width
                    case WIDTH:
                        maketCell.getCellFormat().setWidth((double)value);
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считываем padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
                //Считываем Margin
                margins = TableProperties.setMarginProperties(margins, key, value);
            }
            //Установка бордюров
            BorderHelper.assign(maketCell.getCellFormat().getBorders(), area);

            //Установка ширины
            if (maketCell.getCellFormat().getWidth() == 0) {
                double width = HelpFuncs.getWidthForFill(nodeParent);
                if (width <= margins.getLeft() + margins.getRight()) {
                    margins.setLeft(0d);
                    margins.setRight(0d);
                }
                maketCell.getCellFormat().setWidth(width - margins.getLeft() - margins.getRight());
            }

            //region Установка padding
            maketCell.getCellFormat().setTopPadding(paddings.getTop());
            maketCell.getCellFormat().setBottomPadding(paddings.getBottom());
            maketCell.getCellFormat().setLeftPadding(paddings.getLeft());
            maketCell.getCellFormat().setRightPadding(paddings.getRight());
            //endregion

            //region Установка margin
            //TopMargin
            if (margins.getTop() > 0) {
                Row rowMarginTop = TableHelper.ceateRow(getWordDocument());
                Cell cellMarginTop = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(cellMarginTop);
                cellMarginTop.getCellFormat().setWidth(maketCell.getCellFormat().getWidth());
                rowMarginTop.appendChild(cellMarginTop);
                rowMarginTop.getRowFormat().setHeightRule(EXACTLY);
                rowMarginTop.getRowFormat().setHeight(margins.getTop());
                maketTable.insertBefore(rowMarginTop, maketRow);
            }
            if (margins.getBottom() > 0) {
                Row rowMarginBottom = TableHelper.ceateRow(getWordDocument());
                Cell cellMarginBottom = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(cellMarginBottom);
                cellMarginBottom.getCellFormat().setWidth(maketCell.getCellFormat().getWidth());
                rowMarginBottom.appendChild(cellMarginBottom);
                rowMarginBottom.getRowFormat().setHeightRule(EXACTLY);
                rowMarginBottom.getRowFormat().setHeight(margins.getBottom());
                maketTable.insertAfter(rowMarginBottom, maketRow);
            }

            CellFormat firstCellFormat = maketTable.getFirstRow().getFirstCell().getCellFormat();
            if (margins.getLeft() > 0 || firstCellFormat.getLeftPadding() > 0d
                    || firstCellFormat.getBorders().getLeft().getLineWidth() > 0d) {
                // У нас задан margin-left или исправляем глюк
                // У таблиц в Ворде есть глюк. Она смещается влево, если в первой ячейки задан padding-left или левая
                // граница
                // Если отступ меньше 0.1d, то округляем вверх
                double dblWidth = Math.max(0.1d, margins.getLeft() > 0d
                                                      ? margins.getLeft()
                                                      : firstCellFormat.getBorders().getLeft().getLineWidth() / 2d);
                for (Row row : maketTable.getRows()) {
                    Cell cellMarginLeft = new Cell(getWordDocument());
                    BlockProperties.addNullParagraph(cellMarginLeft);
                    cellMarginLeft.getCellFormat().setWidth(dblWidth);
                    row.insertBefore(cellMarginLeft, row.getFirstCell());
                }
            }

            if (margins.getRight() > 0) {
                for (Row row : maketTable.getRows()) {
                    Cell cellMarginRight = new Cell(getWordDocument());
                    BlockProperties.addNullParagraph(cellMarginRight);
                    cellMarginRight.getCellFormat().setWidth(margins.getRight());
                    row.appendChild(cellMarginRight);
                }
            }

            //endregion
        }

        //Установка Background-color
        FormatHelper.assignBackgroundColorArea(maketCell, area);

        //Обрабатываем свойства перехода на следующую страницу
        BlockProperties.addPageBreak(maketTable, area);

        // Устанавливаем свойство BACKGROUND_IMAGE
        String backgroundImage = (String)area.getPropertyValue(BACKGROUND_IMAGE);
        BlockProperties.addBackgroundImage(maketCell, backgroundImage);

        // Вкладываем таблицу для отработки элементов списка
        Table table = new Table(getWordDocument());
        maketCell.appendChild(table);
        //Добавляем параграф в конец
        BlockProperties.addNullParagraph(maketCell);
        return table;
    }

    /**
     * Завершение формирования списка.
     * @param tableNode - Область таблицы
     * @throws Exception генерируют классы com.aspose.words
     */
    private static void completeListBlock(Table tableNode) throws Exception {
        
        FormatHelper.fixWordTableBug(tableNode);
        // Удаляем нами созданные параграфы вокруг таблицы, если они не пригодились
        if (BlockProperties.isDummyParagraph(tableNode.getNextSibling())
                && tableNode.getNextSibling().getNextSibling() == null) {
            tableNode.getNextSibling().remove();
        }
        if (BlockProperties.isDummyParagraph(tableNode.getPreviousSibling())
                && tableNode.getPreviousSibling().getPreviousSibling() == null) {
            tableNode.getPreviousSibling().remove();
        }
    }
    //endregion
    
    //region WordRenderer.ListItem.cs
    /**
     * Метод добавляет строку в список.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     */
    private CompositeNode addListItem(ListItemArea area, CompositeNode nodeParent) {
        
        Assert.isNull(nodeParent);
        //Создаем макет отображения тэга в Aspose
        Row row = TableHelper.ceateRow(getWordDocument());
        row.getRowFormat().setAllowAutoFit(true);
        nodeParent.appendChild(row);
        return row;
    }

    /**
     * Завершение добавления строки списка.
     * @param area         - Область Xsl-fo
     * @param listItemNode - Ссылка на область Aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private void completeListItem(ListItemArea area, Row listItemNode) throws Exception {
        
        //Свойства margin
        SideValues margins = new SideValues();
        //Свойства padding
        SideValues paddings = new SideValues();

        // Бордюры
        FoBorder borderTop = null;
        FoBorder borderBottom = null;
        FoBorder borderLeft = null;
        FoBorder borderRight = null;

        //Заполняем аттрибутами
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    case BORDER_TOP:
                        borderTop = (FoBorder)value;
                        break;
                    case BORDER_BOTTOM:
                        borderBottom = (FoBorder)value;
                        break;
                    case BORDER_LEFT:
                        borderLeft = (FoBorder)value;
                        break;
                    case BORDER_RIGHT:
                        borderRight = (FoBorder)value;
                        break;
                    //height
                    case HEIGHT:
                        listItemNode.getRowFormat().setHeight((double)value);
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считываем padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
                //Считываем Margin
                margins = TableProperties.setMarginProperties(margins, key, value);
            }

            //region Установка padding

            Row paddingTopRow = listItemNode;
            Row paddingBottomRow = listItemNode;
            if (paddings.getTop() > 0) {
                paddingTopRow = TableHelper.ceateRow(getWordDocument());
                Cell paddingTopCell = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(paddingTopCell);
                paddingTopRow.appendChild(paddingTopCell);
                paddingTopRow.getRowFormat().setHeightRule(EXACTLY);
                paddingTopRow.getRowFormat().setHeight(paddings.getTop());
                listItemNode.getParentNode().insertBefore(paddingTopRow, listItemNode);
            }
            if (paddings.getBottom() > 0) {
                paddingBottomRow = TableHelper.ceateRow(getWordDocument());
                Cell paddingBottomCell = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(paddingBottomCell);
                paddingBottomRow.appendChild(paddingBottomCell);
                paddingBottomRow.getRowFormat().setHeightRule(EXACTLY);
                paddingBottomRow.getRowFormat().setHeight(paddings.getBottom());
                listItemNode.getParentNode().insertAfter(paddingBottomRow, listItemNode);
            }
            if (paddings.getLeft() > 0) {
                Cell cellPaddingLeft = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(cellPaddingLeft);
                cellPaddingLeft.getCellFormat().setWidth(paddings.getLeft());
                listItemNode.insertBefore(cellPaddingLeft, listItemNode.getFirstCell());
            }
            if (paddings.getRight() > 0) {
                Cell cellPaddingRight = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(cellPaddingRight);
                cellPaddingRight.getCellFormat().setWidth(paddings.getRight());
                listItemNode.appendChild(cellPaddingRight);
            }
            //endregion

            //region Установка бордюров
            if (borderTop != null && borderTop.isVisible()) {
                for (Cell cell : paddingTopRow.getCells()) {
                    Border border = cell.getCellFormat().getBorders().getTop();
                    if (!border.isVisible()) {
                        BorderHelper.assign(border, borderTop);
                    }
                }
            }
            if (borderBottom != null && borderBottom.isVisible()) {
                for (Cell cell : paddingBottomRow.getCells()) {
                    Border border = cell.getCellFormat().getBorders().getBottom();
                    if (!border.isVisible()) {
                        BorderHelper.assign(border, borderBottom);
                    }
                }
            }
            if (borderLeft != null && borderLeft.isVisible()) {
                for (Row row = paddingTopRow, stopRow = (Row)paddingBottomRow.getNextSibling();
                        row != stopRow; row = (Row)row.getNextSibling()) {
                    Border border = row.getFirstCell().getCellFormat().getBorders().getLeft();
                    if (!border.isVisible()) {
                        BorderHelper.assign(border, borderLeft);
                    }
                }
            }
            if (borderRight != null && borderRight.isVisible()) {
                for (Row row = paddingTopRow, stopRow = (Row)paddingBottomRow.getNextSibling();
                        row != stopRow; row = (Row)row.getNextSibling()) {
                    Border border = row.getLastCell().getCellFormat().getBorders().getRight();
                    if (!border.isVisible()) {
                        BorderHelper.assign(border, borderRight);
                    }
                }
            }
            //endregion

            //region Установка margin
            if (margins.getTop() > 0) {
                Row marginTopRow = TableHelper.ceateRow(getWordDocument());
                Cell marginTopCell = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(marginTopCell);
                marginTopRow.appendChild(marginTopCell);
                marginTopRow.getRowFormat().setHeightRule(EXACTLY);
                marginTopRow.getRowFormat().setHeight(margins.getTop());
                listItemNode.getParentNode().insertBefore(marginTopRow, paddingTopRow);
            }
            if (margins.getBottom() > 0) {
                Row marginBottomRow = TableHelper.ceateRow(getWordDocument());
                Cell marginBottomCell = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(marginBottomCell);
                marginBottomRow.appendChild(marginBottomCell);
                marginBottomRow.getRowFormat().setHeightRule(EXACTLY);
                marginBottomRow.getRowFormat().setHeight(margins.getBottom());
                listItemNode.getParentNode().insertAfter(marginBottomRow, paddingBottomRow);
            }
            if (margins.getLeft() > 0) {
                Cell cellMarginLeft = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(cellMarginLeft);
                cellMarginLeft.getCellFormat().setWidth(margins.getLeft());
                listItemNode.insertBefore(cellMarginLeft, listItemNode.getFirstCell());
            }
            if (margins.getRight() > 0) {
                Cell cellMarginRight = new Cell(getWordDocument());
                BlockProperties.addNullParagraph(cellMarginRight);
                cellMarginRight.getCellFormat().setWidth(margins.getRight());
                listItemNode.appendChild(cellMarginRight);
            }
            //endregion
        }
    }
    //endregion
    
    //region WordRenderer.ListItemBody.cs
    /**
     * Метод добавляет содержимое в список.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addListItemBody(ListItemBodyArea area, CompositeNode nodeParent) throws Exception {
        
        //Свойства padding
        SideValues paddings = new SideValues();
        // Вычисляем ширину родительской области LIST-ITEM за вычетом отступов (они еще не созданы), поэтому учитывать
        // надо вручную
        double calcWidth = HelpFuncs.getWidthForFill(nodeParent)
                - HelpFuncs.nvl2(area.getParentArea().getPropertyValue(MARGIN_LEFT), ZERO)
                - HelpFuncs.nvl2(area.getParentArea().getPropertyValue(MARGIN_RIGHT), ZERO)
                - HelpFuncs.nvl2(area.getParentArea().getPropertyValue(PADDING_LEFT), ZERO)
                - HelpFuncs.nvl2(area.getParentArea().getPropertyValue(PADDING_RIGHT), ZERO);

        //Создаем макет отображения тэга в Aspose
        Row listItemRow = (Row)nodeParent;
        Assert.isNull(listItemRow);
        Cell cell = new Cell(getWordDocument());
        cell.getCellFormat().setWidth(0);
        listItemRow.appendChild(cell);

        //Заполняем аттрибутами
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    //height
                    case HEIGHT:
                        listItemRow.getRowFormat().setHeight(
                                Math.max((double)value, listItemRow.getRowFormat().getHeight()));
                        break;
                    //width
                    case WIDTH:
                        cell.getCellFormat().setWidth((double)value);
                        break;
                    case MARGIN_LEFT:
                        // отступ слева
                        if (0 < (double)value) {
                            Cell leftMarginCell = new Cell(getWordDocument());
                            BlockProperties.addNullParagraph(leftMarginCell);
                            leftMarginCell.getCellFormat().setWidth((double)value);
                            listItemRow.insertBefore(leftMarginCell, cell);
                        }
                        break;
                    case MARGIN_RIGHT:
                        // Отступ права не создаем, но вычитаем его размер из ширины
                        calcWidth -= (double)value;
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считываем padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
            }

            //Установка бордюров
            BorderHelper.assign(cell.getCellFormat().getBorders(), area);
            //region Установка padding
            cell.getCellFormat().setTopPadding(paddings.getTop());
            cell.getCellFormat().setBottomPadding(paddings.getBottom());
            cell.getCellFormat().setLeftPadding(paddings.getLeft());
            cell.getCellFormat().setRightPadding(paddings.getRight());
            //endregion
            //Установка свойства background-color
            FormatHelper.assignBackgroundColorArea(cell, area);
        }

        if (cell.getCellFormat().getWidth() == 0) {
            // Вычитаем ширину метки и прочие отступы
            for (Cell currentCell : listItemRow.getCells()) {
                calcWidth -= currentCell.getCellFormat().getWidth()
                        - currentCell.getCellFormat().getBorders().getLeft().getLineWidth() / 2
                        - currentCell.getCellFormat().getBorders().getRight().getLineWidth() / 2;
            }
            if (calcWidth > 0d) {
                cell.getCellFormat().setWidth(calcWidth);
            }
        }

        return cell;
    }
    //endregion

    //region WordRenderer.ListItemLabel.cs
    /**
     * Метод добавляет лэйбл в список.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addListItemLabel(ListItemLabelArea area, CompositeNode nodeParent) throws Exception {
        
        //Свойства padding
        SideValues paddings = new SideValues();

        //Создаем макет отображения тэга в Aspose
        Row listItemRow = (Row)nodeParent;
        Assert.isNull(listItemRow);
        Cell cell = new Cell(getWordDocument());
        listItemRow.appendChild(cell);

        //Заполняем аттрибутами
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    //height
                    case HEIGHT:
                        listItemRow.getRowFormat().setHeight(Math.max((double)value,
                                listItemRow.getRowFormat().getHeight()));
                        break;
                    //width
                    case WIDTH:
                        cell.getCellFormat().setWidth((double)value);
                        break;
                    case MARGIN_LEFT:
                        // отступ слева
                        Cell leftMarginCell = new Cell(getWordDocument());
                        BlockProperties.addNullParagraph(leftMarginCell);
                        leftMarginCell.getCellFormat().setWidth((double)value);
                        listItemRow.insertBefore(leftMarginCell, cell);
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считываем padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
            }

            //Установка бордюров
            BorderHelper.assign(cell.getCellFormat().getBorders(), area);
            //region Установка padding
            cell.getCellFormat().setTopPadding(paddings.getTop());
            cell.getCellFormat().setBottomPadding(paddings.getBottom());
            cell.getCellFormat().setLeftPadding(paddings.getLeft());
            cell.getCellFormat().setRightPadding(paddings.getRight());
            //endregion
            //Установка свойства background-color
            FormatHelper.assignBackgroundColorArea(cell, area);
        }
        return cell;
    }
    //endregion

    //region WordRenderer.PageNumber.cs
    /**
     * Метод добавляет номер текущей страницы в Aspose и устанавливает все параметры.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addPageNumber(PageNumberArea area, CompositeNode nodeParent) throws Exception {
        
        Paragraph paragraph = InlineProperties.getParentForInline(area, nodeParent);
        DocumentBuilder builder = new DocumentBuilder(getWordDocument());
        builder.moveTo(paragraph);

        //Установка свойств шрифта                    
        FontHelper.assignFontArea(builder.getFont(), area,
                paragraph.getParagraphFormat().getShading().getBackgroundPatternColor());

        //Если родитель параграфа - ячейка и у нее есть свойство reference orientation, то выставляем высоту строки
        InlineProperties.setHeightForParentCell(paragraph.getParentNode(), area, builder.getFont().getSize(),
                area.getText().length());

        builder.insertField("PAGE", StringUtils.EMPTY);
        return paragraph;
    }
    //endregion

    //region WordRenderer.PageNumberCitation.cs
    /**
     * Метод добавляет количество страниц в документе в Aspose и устанавливает все параметры.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addPageNumberCitation(PageNumberCitationArea area, CompositeNode nodeParent) throws
            Exception {
        
        Paragraph paragraph = InlineProperties.getParentForInline(area, nodeParent);
        DocumentBuilder builder = new DocumentBuilder(getWordDocument());
        builder.moveTo(paragraph);

        //Установка свойств шрифта                    
        FontHelper.assignFontArea(builder.getFont(), area,
                paragraph.getParagraphFormat().getShading().getBackgroundPatternColor());

        //Если родитель параграфа - ячейка и у нее есть свойство reference orientation, то выставляем высоту строки
        InlineProperties.setHeightForParentCell(paragraph.getParentNode(), area, builder.getFont().getSize(),
                area.getText().length());

        builder.insertField("NUMPAGES", StringUtils.EMPTY);
        return paragraph;
    }
    //endregion

    //region WordRenderer.PageSequence.cs
    /**
     * Метод добавляет страницу и устанавливает ее параметры (Section в Aspose).
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     */
    private CompositeNode addPage(PageSequenceArea area, CompositeNode nodeParent) {
        
        //создаем макет отображения тега в Aspose
        nodeParent = getWordDocument();
        Section section = new Section(getWordDocument());
        nodeParent.appendChild(section);
        Body body = new Body(getWordDocument());
        section.appendChild(body);
        BlockProperties.addNullParagraph(body);

        //Установка параметров страницы по умолчанию
        section.getPageSetup().setPageHeight(ConvertUtil.millimeterToPoint(297));
        section.getPageSetup().setPageWidth(ConvertUtil.millimeterToPoint(210));
        //Установка свойств margin
        section.getPageSetup().setRightMargin(0);
        section.getPageSetup().setLeftMargin(0);
        section.getPageSetup().setTopMargin(0);
        section.getPageSetup().setBottomMargin(0);

        //Заполняем аттрибутами
        if (area.hasProperties()) {
            //master-reference
            String masterName = (String)area.getPropertyValue(MASTER_REFERENCE);  // имя мастер страницы
            SimplePageMasterArea simplePage = HelpFuncs.getSimplePageMaster(
                    ((RootArea)area.getParentArea()).getLayoutMasterSet(), masterName);

            if (simplePage.hasProperties()) {
                //page-width
                String pageWidth = (String)simplePage.getPropertyValue(PAGE_WIDTH);
                if (pageWidth != null) {
                    Double width = HelpFuncs.getSizeInPointsEx(pageWidth);
                    section.getPageSetup().setPageWidth(null != width ? width
                            : section.getPageSetup().getPageWidth());
                }
                //page-height
                String pageHeight = (String)simplePage.getPropertyValue(PAGE_HEIGHT);
                if (pageHeight != null) {
                    Double height = HelpFuncs.getSizeInPointsEx(pageHeight);
                    section.getPageSetup().setPageHeight(null != height ? height
                            : section.getPageSetup().getPageHeight());
                }
                //orientation
                Object orientation = simplePage.getPropertyValue(REFERENCE_ORIENTATION);
                if (orientation != null) {
                    int referenceOrientation = Math.abs((int)orientation);
                    if (referenceOrientation == 90) {
                        section.getPageSetup().setOrientation(LANDSCAPE);
                    }
                }

                //region Установка margin
                double extent = simplePage.getRegionBeforeExtent();
                double margin = simplePage.getMarginTop();
                if (extent == 0) {
                    extent = margin;
                }
                section.getPageSetup().setHeaderDistance(margin - extent);
                section.getPageSetup().setTopMargin(extent);

                extent = simplePage.getRegionAfterExtent();
                margin = simplePage.getMarginBottom();
                if (extent == 0) {
                    extent = margin;
                }
                section.getPageSetup().setFooterDistance(margin - extent);
                section.getPageSetup().setBottomMargin(extent);
                section.getPageSetup().setLeftMargin(simplePage.getMarginLeft());
                section.getPageSetup().setRightMargin(simplePage.getMarginRight());
                //endregion
            }

            //region Установка свойства inital-page-number
            String initalPageNumberString = (String)area.getPropertyValue(INITIAL_PAGE_NUMBER);
            if (!StringUtils.isBlank(initalPageNumberString)) {
                try {
                    int initalPageNumber = Integer.parseInt(initalPageNumberString);
                    section.getPageSetup().setPageStartingNumber(initalPageNumber);
                    section.getPageSetup().setRestartPageNumbering(true);
                } catch (NumberFormatException e) {
                    logger.error("Error parse initial page number:" + initalPageNumberString, e);
                }
            }
            //endregion
        }
        return section;
    }
    //endregion
    
    //region WordRenderer.RegionBefore.cs
    /**
     * Метод добавляет колонтитулы со всеми параметрами.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addCatchWord(GenericArea area, CompositeNode nodeParent) throws Exception {
        
        //Свойства padding
        SideValues paddings = new SideValues();
        // Получаем параметры страницы и устанавливаем параметры страницы
        // имя мастер страницы
        String masterName = (String)area.getParentArea().getPropertyValue(MASTER_REFERENCE);
        SimplePageMasterArea simplePage = HelpFuncs.getSimplePageMaster(
                ((RootArea)area.getParentArea().getParentArea()).getLayoutMasterSet(), masterName);
        double leftMargin = simplePage.getMarginLeft();

        //вставляем таблицу в region before и region after
        Table table = new Table(getWordDocument());
        Row row = TableHelper.ceateRow(getWordDocument());
        table.appendChild(row);
        //Добавляем пустую ячейку в строку для исправления бага padding
        Cell emptyCell = new Cell(getWordDocument());
        emptyCell.getCellFormat().setWidth(0.1);
        row.appendChild(emptyCell);

        Cell cell = new Cell(getWordDocument());
        cell.getCellFormat().setWidth(getWordDocument().getLastSection().getPageSetup().getPageWidth());
        row.appendChild(cell);
        BlockProperties.addNullParagraph(cell);

        GenericArea regionArea = simplePage.getRegionBody();

        //считываем атрибуты
        if (simplePage.getRegionAfter() != null) {
            if ((String)area.getPropertyValue(FLOW_NAME)
                    == simplePage.getRegionAfter().getRegionName()) {
                HeaderFooter footer = new HeaderFooter(getWordDocument(), FOOTER_PRIMARY);
                getWordDocument().getLastSection().getHeadersFooters().add(footer);
                BlockProperties.addNullParagraph(footer);
                footer.appendChild(table);
                regionArea = simplePage.getRegionAfter();
                double extent = simplePage.getRegionAfterExtent();
                row.getRowFormat().setHeight(extent > 0 ? extent : simplePage.getMarginBottom());
                row.getRowFormat().setHeightRule(EXACTLY);
            }
        }
        if (simplePage.getRegionBefore() != null) {
            if ((String)area.getPropertyValue(FLOW_NAME)
                    == simplePage.getRegionBefore().getRegionName()) {
                HeaderFooter header = new HeaderFooter(getWordDocument(), HEADER_PRIMARY);
                getWordDocument().getLastSection().getHeadersFooters().add(header);
                BlockProperties.addNullParagraph(header);
                header.appendChild(table);
                regionArea = simplePage.getRegionBefore();
                double extent = simplePage.getRegionBeforeExtent();
                row.getRowFormat().setHeight(extent > 0 ? extent : simplePage.getMarginTop());
                row.getRowFormat().setHeightRule(EXACTLY);
            }
        }

        //устанавливаем атрибуты
        if (regionArea.hasProperties()) {
            double dblWidth = 0;
            for (FoPropertyType key : regionArea.getProperties().keySet()) {
                Object value = regionArea.getProperties().get(key);
                switch (key) {
                    //width
                    case WIDTH:
                        dblWidth = (double)value;
                        break;
                    //display-align
                    case DISPLAY_ALIGN:
                        cell.getCellFormat().setVerticalAlignment((int)value);
                        break;
                    //orientation
                    case REFERENCE_ORIENTATION:
                        int referenceOrientation = (int)value;
                        if (referenceOrientation == 90) {
                            cell.getCellFormat().setOrientation(TextOrientation.DOWNWARD);
                        } else if (referenceOrientation == -90) {
                            cell.getCellFormat().setOrientation(TextOrientation.UPWARD);
                        }
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считывае padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
            }
            //Установка бордюров
            BorderHelper.assign(cell.getCellFormat().getBorders(), regionArea);

            if (!cell.getCellFormat().getBorders().getLeft().isVisible()) {
                if (dblWidth <= 0) {
                    dblWidth -= paddings.getLeft();
                }
                leftMargin -= paddings.getLeft();
                paddings.setLeft(0);
            }
            if (!cell.getCellFormat().getBorders().getRight().isVisible()) {
                if (dblWidth <= 0) {
                    dblWidth -= paddings.getRight();
                }
                paddings.setRight(0);
            }

            // Если мы принудительно задали ширину через WIDTH, то установим её, 
            // иначе уменьшим текущую (равную ширине страницы) на величину отступов
            if (dblWidth > 0) {
                cell.getCellFormat().setWidth(dblWidth);
            } else {
                cell.getCellFormat().setWidth(cell.getCellFormat().getWidth() + dblWidth);
            }

            //region Установка padding
            cell.getCellFormat().setTopPadding(paddings.getTop());
            cell.getCellFormat().setBottomPadding(paddings.getBottom());
            cell.getCellFormat().setLeftPadding(paddings.getLeft());
            cell.getCellFormat().setRightPadding(paddings.getRight());
            //endregion
            //Установка свойства background-color
            FormatHelper.assignBackgroundColorArea(cell, regionArea);
        }
        // Саму таблицу сместим влево на ширину маргина, 
        // ибо в ворде по умолчанию на колонтитул распространяется отступы страницы
        table.setLeftIndent(table.getLeftIndent() - leftMargin);

        //region Устанавливаем свойство BACKGROUND_IMAGE
        String backgroundImage = (String)regionArea.getPropertyValue(BACKGROUND_IMAGE);
        if (!StringUtils.isBlank(backgroundImage)) {
            BlockProperties.addBackgroundImage(cell, backgroundImage);
        }
        //endregion

        //возвращаем параметр 
        nodeParent = cell;
        return nodeParent;
    }
    //endregion

    //region WordRenderer.RegionBody.cs
    /**
     * Метод добавляет колонтитулы со всеми параметрами.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addRegionBody(GenericArea area, CompositeNode nodeParent) throws Exception {
        
        // Получаем параметры страницы и устанавливаем параметры страницы            
        // имя мастер страницы
        String masterName = (String)area.getParentArea().getPropertyValue(MASTER_REFERENCE);
        SimplePageMasterArea simplePage = HelpFuncs.getSimplePageMaster(
                ((RootArea)area.getParentArea().getParentArea()).getLayoutMasterSet(), masterName);
        GenericArea regionArea = simplePage.getRegionBody();
        if (nodeParent.getNodeType() != SECTION) {
            while (nodeParent.getNodeType() != SECTION) {
                nodeParent = nodeParent.getParentNode();
            }
        }
        if (nodeParent.getNodeType() == SECTION) {
            Section section = (Section)nodeParent;
            //устанавливаем атрибуты
            if (regionArea.hasProperties()) {
                for (FoPropertyType key : regionArea.getProperties().keySet()) {
                    Object value = regionArea.getProperties().get(key);
                    switch (key) {
                        case COLUMN_COUNT:
                            int count;
                            try {
                                count = Integer.parseInt((String)value);
                                section.getPageSetup().getTextColumns().setCount(count);
                            } catch (NumberFormatException e) {
                                // Так я заменил int.TryParse
                            }
                            break;
                        case COLUMN_GAP:
                            section.getPageSetup().getTextColumns().setSpacing((double)value);
                            break;

                        default:
                            // В импортруемом коде ничего не было
                    }
                }
                //Установка бордюров
                BorderHelper.assign(section.getPageSetup().getBorders(), regionArea);
            }
        }

        //возвращаем параметр 
        return getWordDocument().getLastSection().getBody();
    }
    //endregion

    //region WordRenderer.StaticContent.cs
    /**
     * Метод добавляет колонтитул в Aspose и устанавливает все параметры.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addRegion(StaticContentArea area, CompositeNode nodeParent) throws Exception {
        
        String flowName = (String)area.getPropertyValue(FLOW_NAME);
        if (!StringUtils.isBlank(flowName)) {
            String masterName = (String)area.getParentArea().getPropertyValue(MASTER_REFERENCE);
            SimplePageMasterArea simplePage = HelpFuncs.getSimplePageMaster(
                    ((RootArea)area.getParentArea().getParentArea()).getLayoutMasterSet(), masterName);
            if (simplePage.getRegionBefore() != null) {
                if (flowName.equals(simplePage.getRegionBefore().getRegionName())) {
                    nodeParent = getWordDocument().getLastSection();
                    nodeParent = addCatchWord(area, nodeParent);
                }
            }
            if (simplePage.getRegionAfter() != null) {
                if (flowName.equals(simplePage.getRegionAfter().getRegionName())) {
                    nodeParent = getWordDocument().getLastSection();
                    nodeParent = addCatchWord(area, nodeParent);
                }
            }
            if (simplePage.getRegionBody() != null) {
                if (flowName.equals(simplePage.getRegionBody().getRegionName())) {
                    nodeParent = getWordDocument().getLastSection().getBody();
                    nodeParent = addRegionBody(area, nodeParent);
                }
            }
        }
        return nodeParent;
    }
    //endregion

    //region WordRenderer.Table.cs
    /**
     * Метод формирует таблицу Aspose.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    @SuppressWarnings("unchecked")
    private CompositeNode addTable(TableArea area, CompositeNode nodeParent) throws Exception {
        
        double dblMaketTableWidth = 0d;
        //Свойства margin
        SideValues margins = new SideValues();

        //Свойства padding
        SideValues paddings = new SideValues();

        XslfoTable maketTable = new XslfoTable(getWordDocument());

        //Инициализация бордюров таблицы
        if (maketTable.getTableBorders() == null) {
            Cell cell = new Cell(getWordDocument());
            Cell cell1 = new Cell(getWordDocument());
            Cell cell2 = new Cell(getWordDocument());
            Cell cell3 = new Cell(getWordDocument());
            maketTable.setTableBorders(cell.getCellFormat().getBorders());
            maketTable.setHeaderBorders(cell1.getCellFormat().getBorders());
            maketTable.setBodyBorders(cell2.getCellFormat().getBorders());
            maketTable.setFooterBorders(cell3.getCellFormat().getBorders());
        }

        //Создаем макет отображения тэга в Aspose          
        nodeParent = HelpFuncs.getNodeForBlock(nodeParent);
        // Добавляются невидимые параграфы до и после таблицы для отработки свойста page-break-... и break-...
        BlockProperties.addNullParagraph(nodeParent);
        nodeParent.appendChild(maketTable);
        BlockProperties.addNullParagraph(nodeParent);

        //region Заполнение свойств

        maketTable.setTableBackground(HelpFuncs.nvl2(
            area.getInheritablePropertyValue(BACKGROUND_COLOR), FoColor.DEFAULT_COLOR));

        //Заполняем аттрибутами
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    //width
                    case WIDTH:
                        dblMaketTableWidth = (double)value;
                        break;
                    case TABLE_LAYOUT:
                        String tableLayout = (String) value;
                        if (StringUtils.equals(tableLayout, GlobalData.TABLE_LAYOUT_AUTO)) {
                            maketTable.setNeedAutoFit(true);
                        }
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считывае padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
                //Считывае Margin
                margins = TableProperties.setMarginProperties(margins, key, value);
            }
            //Установка бордюров
            BorderHelper.assign(maketTable.getTableBorders(), area);
            //region Установка padding
            maketTable.setPaddingTop(paddings.getTop());
            maketTable.setPaddingBottom(paddings.getBottom());
            maketTable.setPaddingLeft(paddings.getLeft());
            maketTable.setPaddingRight(paddings.getRight());
            //endregion
            //#region Установка margin
            maketTable.setMarginTop(margins.getTop());
            maketTable.setMarginBottom(margins.getBottom());
            // Чтобы половинки границ не вылезали за пределы родителей, выставляем их как отступы, если они их превышают
            maketTable.setMarginLeft(Math.max(margins.getLeft(),
                    maketTable.getTableBorders().getLeft().getLineWidth() / 2));
            maketTable.setMarginRight(Math.max(margins.getRight(),
                    maketTable.getTableBorders().getRight().getLineWidth() / 2));
            //endregion
        }
        //endregion

        BlockProperties.addPageBreak(maketTable, area);

        //region Установка ширин колонок и таблицы
        //Сортировка Колонок по свойству column-number
        TableProperties.setColumnNumber(area.getColumnList());

        //Ширина Таблицы
        if (dblMaketTableWidth == 0d) {
            dblMaketTableWidth = HelpFuncs.getWidthForFill(nodeParent);
            if (maketTable.getMarginLeft() + maketTable.getMarginRight() < dblMaketTableWidth) {
                dblMaketTableWidth -= maketTable.getMarginLeft() + maketTable.getMarginRight();
            } else {
                maketTable.setMarginLeft(0d);
                maketTable.setMarginRight(0d);
            }
        }
        if (maketTable.getPaddingLeft() + maketTable.getPaddingRight() < dblMaketTableWidth) {
            dblMaketTableWidth -= maketTable.getPaddingLeft() + maketTable.getPaddingRight();
        } else {
            maketTable.setPaddingLeft(0d);
            maketTable.setPaddingRight(0d);
        }

        //Рассчитываем для каждой колонки ее фиксированную ширину
        TableProperties.setColumnWidth(area.getColumnList(), dblMaketTableWidth);
        //endregion

        return maketTable;
    }

    /**
     * Завершение формирования таблицы.
     * @param tableNode - Область таблицы
     * @throws Exception генерируют классы com.aspose.words
     */
    private static void completeTable(XslfoTable tableNode) throws Exception {
        
        // Генерируем таблицу
        tableNode.genericXslfoTable();

        // Удаляем нами созданные параграфы вокруг таблицы, если они не пригодились
        if (BlockProperties.isDummyParagraph(tableNode.getNextSibling())
                && tableNode.getNextSibling().getNextSibling() == null) {
            tableNode.getNextSibling().remove();
        }
        if (BlockProperties.isDummyParagraph(tableNode.getPreviousSibling())
                && tableNode.getPreviousSibling().getPreviousSibling() == null) {
            tableNode.getPreviousSibling().remove();
        }
    }
    //endregion

    //region WordRenderer.TableBody.cs
    /**
     * Метод формирует таблицу Aspose для представления элемента table-body.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private static CompositeNode addTableBody(TableBodyArea area, CompositeNode nodeParent) throws Exception {
        
        //Установка свойств Spanned для TableCellArea
        TableProperties.setSpannedForCell(area);

        setBordersForHeaderBodyFooter(area, (XslfoTable)nodeParent, TABLE_BODY);

        return nodeParent;
    }

    /**
     * Добавление таблицы для установки элементов Header, Body, Footer.
     * @param area       - Область дерева XslFo
     * @param maketTable - Ссылка на таблицу aspose
     * @param typeTable  - Тип элемента HEADER/BODY/FOOTER)
     * @throws Exception генерируют классы com.aspose.words
     */
    private static void setBordersForHeaderBodyFooter(GenericArea area, XslfoTable maketTable, AreaType typeTable)
            throws Exception {
        
        BorderCollection bordersArea = null;
        switch (typeTable) {
            case TABLE_HEADER:
                bordersArea = maketTable.getHeaderBorders();
                break;
            case TABLE_BODY:
                bordersArea = maketTable.getBodyBorders();
                break;
            case TABLE_FOOTER:
                bordersArea = maketTable.getFooterBorders();
                break;

            default:
                // В импортруемом коде ничего не было
        }

        //region Заполнение свойств
        if (!area.hasProperties()) {
            return;
        }

        //Установка бордюров
        BorderHelper.assign(bordersArea, area);
        //region Установка фона
        //Установка свойства background-color
        Color backgroundColor = HelpFuncs.nvl2(area.getPropertyValue(BACKGROUND_COLOR), FoColor.DEFAULT_COLOR);
        if (!FoColor.isEmpty(backgroundColor)) {
            switch (typeTable) {
                case TABLE_HEADER:
                    maketTable.setHeaderBackground(backgroundColor);
                    break;
                case TABLE_BODY:
                    maketTable.setBodyBackground(backgroundColor);
                    break;
                case TABLE_FOOTER:
                    maketTable.setFooterBackground(backgroundColor);
                    break;

                default:
                    // В импортруемом коде ничего не было
            }
        }
        //endregion

        //endregion
    }
    //endregion

    //region WordRenderer.TableCell.cs
    /**
     * Метод формирует ячейку таблицы Aspose.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private CompositeNode addTableCell(TableCellArea area, CompositeNode nodeParent) throws Exception {

        // если это фейковая ячейка, то не добавляем её
        if (area.getHorizontalMerge() == CellMerge.PREVIOUS) {
            return nodeParent;
        }
        
        //Свойства padding
        SideValues paddings = new SideValues();

        //Создаем макет отображения тэга в Aspose
        Cell cell = new Cell(document);

        XslfoTable table = (XslfoTable)nodeParent.getParentNode();

        //Фон
        String backgroundImage = StringUtils.EMPTY;
        Color backgroundColor = table.getTableBackground();


        //Выставляем цвет фона Header body footer
        switch (area.getParentArea().getParentArea().getType()) {
            case TABLE_HEADER:
                if (!FoColor.isEmpty(table.getHeaderBackground())) {
                    backgroundColor = table.getHeaderBackground();
                }
                break;
            case TABLE_BODY:
                if (!FoColor.isEmpty(table.getBodyBackground())) {
                    backgroundColor = table.getBodyBackground();
                }
                break;
            case TABLE_FOOTER:
                if (!FoColor.isEmpty(table.getFooterBackground())) {
                    backgroundColor = table.getFooterBackground();
                }
                break;

            default:
                // В импортруемом коде ничего не было
        }

        CellFormat cellFormat = cell.getCellFormat();
        //Получаем ширину ячейки
        cellFormat.setWidth(area.getCellWidthInPoints());
        //Получаем колонку для данной ячейки
        TableColumnArea column = area.getColumn();
        if (column != null) {
            //Устанавливаем цвет колонки если он задан
            backgroundColor = HelpFuncs.nvl2(column.getPropertyValue(BACKGROUND_COLOR), backgroundColor);
        }

        //Выставляем цвет фона строки
        backgroundColor = HelpFuncs.nvl2(area.getParentArea().getPropertyValue(BACKGROUND_COLOR), backgroundColor);

        nodeParent.appendChild(cell);
        nodeParent = cell;


        //Заполняем аттрибутами
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    //orientation
                    case REFERENCE_ORIENTATION:
                        int referenceOrientation = (int)value;
                        if (referenceOrientation == 90) {
                            cellFormat.setOrientation(TextOrientation.DOWNWARD);
                        } else if (referenceOrientation == -90) {
                            cellFormat.setOrientation(TextOrientation.UPWARD);
                        }
                        break;

                    case DISPLAY_ALIGN:
                        cellFormat.setVerticalAlignment((int)value);
                        break;
                    case WRAP_OPTION:
                        //region Wrap option
                        switch ((String)value) {
                            case "no-wrap":
                                cellFormat.setWrapText(false);
                                break;
                            case "wrap":
                                cellFormat.setWrapText(true);
                                break;

                            default:
                                // В импортруемом коде ничего не было
                        }
                        //endregion
                        break;
                    case BACKGROUND_IMAGE:
                        backgroundImage = (String)value;
                        break;
                    case BACKGROUND_COLOR:
                        backgroundColor = (Color)value;
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }

                //Считываем padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
            }
            //Установка бордюров
            BorderHelper.assign(cellFormat.getBorders(), area);
            //region Установка padding
            cellFormat.setTopPadding(paddings.getTop());
            cellFormat.setBottomPadding(paddings.getBottom());
            cellFormat.setLeftPadding(paddings.getLeft());
            cellFormat.setRightPadding(paddings.getRight());
            //endregion
            // Устанавливаем свойство BACKGROUND_IMAGE
            if (!StringUtils.isBlank(backgroundImage)) {
                BlockProperties.addBackgroundImage(cell, backgroundImage);
            }
        }

        //Установка свойства background-color
        if (!FoColor.isEmpty(backgroundColor)) {
            cellFormat.getShading().setBackgroundPatternColor(backgroundColor);
        }

        //Обрабатываем свойства Spanned для ячейки
        cellFormat.setHorizontalMerge(area.getHorizontalMerge());
        cellFormat.setVerticalMerge(area.getVerticalMerge());

        if (!area.hasChildren()) {
            BlockProperties.addNullParagraph(cell);
        }

        return nodeParent;
    }
    //endregion

    //region WordRenderer.TableFooter.cs
    /**
     * Метод формирует таблицу Aspose для представления элемента table-footer.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private static CompositeNode addTableFooter(TableFooterArea area, CompositeNode nodeParent) throws Exception {
        
        //Установка свойств Spanned для TableCellArea
        TableProperties.setSpannedForCell(area);

        setBordersForHeaderBodyFooter(area, (XslfoTable)nodeParent, TABLE_FOOTER);
                          
        return nodeParent;
    }
    //endregion

    //region WordRenderer.TableHeader.cs
    /**
     * Метод формирует таблицу Aspose для представления элемента table-header.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     * @throws Exception генерируют классы com.aspose.words
     */
    private static CompositeNode addTableHeader(TableHeaderArea area, CompositeNode nodeParent) throws Exception {
        
        //Установка свойств Spanned для TableCellArea
        TableProperties.setSpannedForCell(area);

        setBordersForHeaderBodyFooter(area, (XslfoTable)nodeParent, TABLE_HEADER);
        
        return nodeParent;
    }
    //endregion

    //region WordRenderer.TableRow.cs
    /**
     * Метод формирует строку таблицы Aspose.
     * @param area       - Область дерева XslFo
     * @param nodeParent - Ссылка на родителя aspose
     * @return Новый родительский элемент aspose
     */
    private CompositeNode addTableRow(TableRowArea area, CompositeNode nodeParent) {
        
        //Создаем макет отображения тэга в Aspose
        Row row = TableHelper.ceateRow(document);
        row.getRowFormat().setAllowBreakAcrossPages(true);

        XslfoTable maketTable = (XslfoTable)nodeParent;
        maketTable.appendChild(row);

        switch (area.getParentArea().getType()) {
            case TABLE_HEADER:
                if (maketTable.getHeader() == null) {
                    maketTable.setHeader(new ArrayList<>());
                }
                row.getRowFormat().setHeadingFormat(true);
                maketTable.getHeader().add(row);
                break;
            case TABLE_BODY:
                if (maketTable.getBody() == null) {
                    maketTable.setBody(new ArrayList<>());
                }
                maketTable.getBody().add(row);
                break;
            case TABLE_FOOTER:
                if (maketTable.getFooter() == null) {
                    maketTable.setFooter(new ArrayList<>());
                }
                maketTable.getFooter().add(row);
                break;

            default:
                // В импортруемом коде ничего не было
        }

        //Заполняем аттрибутами
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                switch (key) {
                    case PAGE_BREAK_INSIDE:
                        if ("avoid".equals(value)) {
                            row.getRowFormat().setAllowBreakAcrossPages(false);
                        }
                        break;
                    case KEEP_TOGETHER:
                        if ("always".equals(value)) {
                            row.getRowFormat().setAllowBreakAcrossPages(false);
                        }
                        break;
                    case HEIGHT:
                        row.getRowFormat().setHeight((double)value);
                        row.getRowFormat().setHeightRule(EXACTLY);
                        break;
                    case MIN_HEIGHT:
                        row.getRowFormat().setHeight((double)value);
                        row.getRowFormat().setHeightRule(HeightRule.AT_LEAST);
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
            }
        }

        nodeParent = row;
        return nodeParent;
    }
    //endregion
}
