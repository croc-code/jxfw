package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import static com.aspose.words.NodeType.CELL;
import static com.aspose.words.NodeType.TABLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.EXTERNAL_GRAPHIC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.INSTREAM_FOREIGN_OBJECT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND_COLOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BREAK_AFTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BREAK_BEFORE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.DISPLAY_ALIGN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PAGE_BREAK_AFTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PAGE_BREAK_BEFORE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REFERENCE_ORIENTATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.TEXT_ALIGN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.TEXT_INDENT;

import com.aspose.words.Cell;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.CompositeNode;
import com.aspose.words.DocumentBase;
import com.aspose.words.HeightRule;
import com.aspose.words.Node;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.Row;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.aspose.words.Table;
import com.aspose.words.TextOrientation;
import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.SideValues;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Класс, хранящий функции для обработки свойств параграфа (fo:block).
 * Created by vsavenkov on 26.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class BlockProperties {

    /**
     * Обработка блочной области - добавление параграфа.
     * Если свойства области не позволяют обойтись добавлением параграфа, то добавляется таблица с ячейкой, внутри
     * нее - параграф
     * @param area       - Область Xsl-fo
     * @param parentNode - Родительская область Aspose
     * @return Сгенерированный параграф
     * @throws Exception генерируют классы com.aspose.words
     */
    public static Paragraph addBlock(GenericArea area, CompositeNode parentNode) throws Exception {
        DocumentBase wordDocument = parentNode.getDocument();
        Paragraph resultParagraph = new Paragraph(wordDocument);
        resultParagraph.getParagraphBreakFont().setSize(0.1);
        // Для областей-картинок всегда создаем ячейку
        boolean isNeedCell = Arrays.asList(EXTERNAL_GRAPHIC, INSTREAM_FOREIGN_OBJECT).contains(area.getType());

        if (parentNode instanceof Paragraph) {
            Paragraph parentNodeParagraph = (Paragraph) parentNode;
            if (!isNeedCell && !area.hasProperties() && !parentNodeParagraph.hasChildNodes()) {
                return parentNodeParagraph;
            }

            // Наследуем родительские свойства, которые имеет смысл наследовать
            resultParagraph.getParagraphFormat().setKeepTogether(parentNodeParagraph.getParagraphFormat()
                    .getKeepTogether());
            resultParagraph.getParagraphFormat().setKeepWithNext(parentNodeParagraph.getParagraphFormat()
                    .getKeepWithNext());
            resultParagraph.getParagraphFormat().setLeftIndent(parentNodeParagraph.getParagraphFormat()
                    .getLeftIndent());
            resultParagraph.getParagraphFormat().setRightIndent(parentNodeParagraph.getParagraphFormat()
                    .getRightIndent());
        }

        parentNode = HelpFuncs.getNodeForBlock(parentNode);

        Table helperTable = null;
        Row helperRow = null;
        Cell helperCell = null;

        int referenceOrientation = HelpFuncs.nvl2(
                area.getInheritablePropertyValue(REFERENCE_ORIENTATION), 0);
        int enTextOrientation = referenceOrientation == 90
                ? TextOrientation.DOWNWARD
                : referenceOrientation == -90
                ? TextOrientation.UPWARD
                : TextOrientation.HORIZONTAL;

        int enVerticalAlignment =
                HelpFuncs.nvl2(area.getInheritablePropertyValue(DISPLAY_ALIGN),
                        enTextOrientation != TextOrientation.DOWNWARD ? CellVerticalAlignment.TOP
                                : CellVerticalAlignment.BOTTOM);

        Color backgroundColor = HelpFuncs.nvl2(area.getInheritablePropertyValue(BACKGROUND_COLOR),
                FoColor.DEFAULT_COLOR);

        if (parentNode instanceof Cell) {
            Cell parentNodeCell = (Cell)parentNode;
            // Если выравнивание или ориентация не совпадает с родительскими, то обязательно нужно создать ячейку
            isNeedCell |= parentNodeCell.getCellFormat().getVerticalAlignment() != enVerticalAlignment
                    || parentNodeCell.getCellFormat().getOrientation() != enTextOrientation;
        } else {
            isNeedCell |= enVerticalAlignment != CellVerticalAlignment.TOP
                    && enTextOrientation != TextOrientation.HORIZONTAL;
        }

        if (isNeedCell || !HelpFuncs.onlyInlineAreasInside(area)) {
            helperTable = new Table(wordDocument);
            helperRow = TableHelper.ceateRow(wordDocument);
            helperRow.getRowFormat().setAllowBreakAcrossPages(true);
            helperTable.appendChild(helperRow);
            helperCell = new Cell(wordDocument);
            helperCell.getCellFormat().setVerticalAlignment(enVerticalAlignment);
            helperCell.getCellFormat().setOrientation(enTextOrientation);
            helperRow.appendChild(helperCell);
        }

        if (!FoColor.isEmpty(backgroundColor)) {
            if (helperCell != null) {
                isNeedCell = true;
                helperCell.getCellFormat().getShading().setBackgroundPatternColor(backgroundColor);
            }
            resultParagraph.getParagraphFormat().getShading().setBackgroundPatternColor(backgroundColor);
        }

        resultParagraph.getParagraphFormat().setAlignment(
                HelpFuncs.nvl2(area.getInheritablePropertyValue(TEXT_ALIGN), ParagraphAlignment.LEFT));

        resultParagraph.getParagraphFormat().setFirstLineIndent(
                HelpFuncs.nvl2(area.getInheritablePropertyValue(TEXT_INDENT), HelpFuncs.ZERO));

        // Если у нас нет свойств и ячейку мы создавать не собираемся, то досрочный выход
        if (!area.hasProperties() && !isNeedCell) {
            parentNode.appendChild(resultParagraph);
            return resultParagraph;
        }

        //Свойства margin
        SideValues margins = new SideValues();
        //Свойства padding
        SideValues paddings = new SideValues();

        double dblWidth = 0;

        //region Обработка свойств области
        if (area.hasProperties()) {
            for (FoPropertyType key : area.getProperties().keySet()) {
                Object value = area.getProperties().get(key);
                if (value == null) {
                    continue;
                }

                switch (key) {
                    case PAGE_BREAK_INSIDE:
                        if (value.equals("avoid")) {
                            resultParagraph.getParagraphFormat().setKeepTogether(true);
                            if (helperRow != null) {
                                helperRow.getRowFormat().setAllowBreakAcrossPages(false);
                            }
                        }
                        break;
                    case KEEP_TOGETHER:
                        if (value.equals("always")) {
                            resultParagraph.getParagraphFormat().setKeepTogether(true);
                            if (helperRow != null) {
                                helperRow.getRowFormat().setAllowBreakAcrossPages(false);
                            }
                        }
                        break;
                    case KEEP_WITH_NEXT:
                        if (value.equals("always")) {
                            resultParagraph.getParagraphFormat().setKeepWithNext(true);
                        }
                        break;

                    case BACKGROUND_IMAGE:
                        addBackgroundImageSimple(resultParagraph, (String) value);
                        break;

                    case WIDTH:
                        dblWidth = (double) value;
                        break;
                    case HEIGHT:
                        if (helperRow != null) {
                            helperRow.getRowFormat().setHeight((double) value);
                            isNeedCell = true;
                        }
                        break;

                    case PADDING_LEFT:
                    case PADDING_RIGHT:
                    case PADDING_TOP:
                    case PADDING_BOTTOM:
                        if (helperCell != null && (double) value != HelpFuncs.ZERO) {
                            isNeedCell = true;
                        }
                        break;
                    case BORDER_TOP:
                    case BORDER_LEFT:
                    case BORDER_RIGHT:
                    case BORDER_BOTTOM:
                        if (helperCell != null) {
                            isNeedCell = true;
                        }
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
                //Считываем Margin
                margins = TableProperties.setMarginProperties(margins, key, value);
                //Считываем Padding
                paddings = TableProperties.setPaddingProperties(paddings, key, value);
            }
        }
        //endregion

        if (dblWidth == HelpFuncs.ZERO) {
            // Если ширина не задана, то высчитываем по родительской
            dblWidth = HelpFuncs.getWidthForFill(parentNode);
            if (margins.getLeft() + margins.getRight() < dblWidth) {
                dblWidth -= margins.getLeft() + margins.getRight();
            } else {
                margins.setLeft(0d);
                margins.setRight(0d);
            }
        }

        if (isNeedCell) {
            BorderHelper.assign(helperCell.getCellFormat().getBorders(), area);
            helperCell.getCellFormat().setWidth(dblWidth);
            //region Установка отступов
            if (margins.getTop() > 0) {
                Row rowMargin = TableHelper.ceateRow(wordDocument);
                Cell cellMargin = new Cell(wordDocument);
                addNullParagraph(cellMargin);
                cellMargin.getCellFormat().setWidth(dblWidth);
                rowMargin.appendChild(cellMargin);
                rowMargin.getRowFormat().setHeightRule(HeightRule.EXACTLY);
                rowMargin.getRowFormat().setHeight(margins.getTop());
                helperTable.insertBefore(rowMargin, helperRow);
            }
            if (margins.getBottom() > 0) {
                Row rowMargin = TableHelper.ceateRow(wordDocument);
                Cell cellMargin = new Cell(wordDocument);
                addNullParagraph(cellMargin);
                cellMargin.getCellFormat().setWidth(dblWidth);
                rowMargin.appendChild(cellMargin);
                rowMargin.getRowFormat().setHeightRule(HeightRule.EXACTLY);
                rowMargin.getRowFormat().setHeight(margins.getBottom());
                helperTable.insertAfter(rowMargin, helperRow);
            }
            if (margins.getLeft() > 0) {
                for (Row row : helperTable.getRows()) {
                    Cell cellMarginLeft = new Cell(wordDocument);
                    addNullParagraph(cellMarginLeft);
                    cellMarginLeft.getCellFormat().setWidth(margins.getLeft());
                    row.insertBefore(cellMarginLeft, row.getFirstCell());
                }
            }
            if (margins.getRight() > 0) {
                for (Row row : helperTable.getRows()) {
                    Cell cellMarginRight = new Cell(wordDocument);
                    addNullParagraph(cellMarginRight);
                    cellMarginRight.getCellFormat().setWidth(margins.getRight());
                    row.appendChild(cellMarginRight);
                }
            }
            helperCell.getCellFormat().setTopPadding(paddings.getTop());
            helperCell.getCellFormat().setBottomPadding(paddings.getBottom());
            helperCell.getCellFormat().setLeftPadding(paddings.getLeft());
            helperCell.getCellFormat().setRightPadding(paddings.getRight());
            //endregion
            helperCell.appendChild(resultParagraph);
            parentNode.appendChild(helperTable);
            FormatHelper.fixWordTableBug(helperTable);
        } else {
            BorderHelper.assign(resultParagraph.getParagraphFormat().getBorders(), area);
            // К сожалению, если у нас нет ячейки, то приходится смешивать в кучу (суммировать) отступы внешние и
            // внутренние

            // Значения "отступов до и после" параграфа просто присваиваем (и то "отступ после" возможно придется
            // перенести после обработки детей)
            // Причем, если нет внешних отступов, то на внутренние придется забить, если есть цвет фона, ибо иначе они
            // будут дырками смотреться
            if (!FoColor.isEmpty(backgroundColor)) {
                if (margins.getTop() == 0) {
                    paddings.setTop(0);
                }
                if (margins.getBottom() == 0) {
                    paddings.setBottom(0);
                }
            }
            ParagraphFormat paragraphFormat = resultParagraph.getParagraphFormat();
            paragraphFormat.setSpaceBefore(margins.getTop() + paddings.getTop());
            paragraphFormat.setSpaceAfter(margins.getBottom() + paddings.getBottom());
            // А значения "отступов слева и справа" складываем с унаследованными от родителя
            paragraphFormat.setLeftIndent(paragraphFormat.getLeftIndent() + margins.getLeft() + paddings.getRight());
            paragraphFormat.setRightIndent(paragraphFormat.getRightIndent() + margins.getRight() + paddings.getRight());
            parentNode.appendChild(resultParagraph);
            // Исправляем ошибку отображения ворда
            FormatHelper.fixWordParagraphBug(resultParagraph);
        }

        return resultParagraph;

    }

    /**
     * Завершение добавления блока (параграфа) в Aspose.
     * Вызывается после завершения обработки всех детей
     * @param area          - Область xsl-fo
     * @param paragraphNode - Параграф
     */
    public static void completeBlock(GenericArea area, Paragraph paragraphNode) {
        
        if (paragraphNode == null) {
            return;
        }
        // Если дети успели что-то надобавлять после родительского параграфа, а у родительского параграфа подразумевался
        // отступ после, то этот отступ надо перенести в конец
        if (paragraphNode.getNextSibling() != null && paragraphNode.getParagraphFormat().getSpaceAfter() > 0) {
            Paragraph paragraph = (Paragraph)paragraphNode.getParentNode().getLastChild();
            if (null == paragraph) {
                paragraph = addNullParagraph(paragraphNode.getParentNode());
            }
            paragraph.getParagraphFormat().setSpaceAfter(paragraph.getParagraphFormat().getSpaceAfter()
                    + paragraphNode.getParagraphFormat().getSpaceAfter());
            if (!paragraph.hasChildNodes()) {
                paragraph.getParagraphBreakFont().setSize(paragraph.getParagraphFormat().getSpaceAfter());
            }
            paragraphNode.getParagraphFormat().setSpaceAfter(0d);
        }
        //region Обработка свойств области, ответственных за разрыв страницы
        boolean pageBreakAfter = false;
        if (area.hasProperties()) {
            Map<FoPropertyType, Object> properties = area.getProperties();
            for (FoPropertyType key : properties.keySet()) {
                Object value = properties.get(key);
                if (value == null) {
                    continue;
                }
                switch (key) {
                    case PAGE_BREAK_BEFORE:
                        paragraphNode.getParagraphFormat().setPageBreakBefore(true);
                        break;
                    case BREAK_BEFORE:
                        String breakBefore = (String)value;
                        if (breakBefore.equals("even-page") || breakBefore.equals("page")) {
                            paragraphNode.getParagraphFormat().setPageBreakBefore(true);
                        }
                        break;
                    case PAGE_BREAK_AFTER:
                        pageBreakAfter = true;
                        break;
                    case BREAK_AFTER:
                        String breakAfter = (String)value;
                        if (breakAfter.equals("even-page") || breakAfter.equals("page")) {
                            pageBreakAfter = true;
                        }
                        break;

                    default:
                        // В импортруемом коде ничего не было
                }
            }
        }
        if (pageBreakAfter) {
            Paragraph paragraph = (Paragraph)paragraphNode.getParentNode().getLastChild();
            if (paragraph == null || paragraph == paragraphNode) {
                paragraph = addNullParagraph(paragraphNode.getParentNode());
            }
            paragraph.getParagraphFormat().setPageBreakBefore(true);
        }
        //endregion

        // Если наш собственный параграф не несёт никакой нагрузки, то его можно удалить
        if (isDummyParagraph(paragraphNode)) {
            paragraphNode.remove();
        }
    }

    /**
     * Проверка на то, что переданный узел является бесполезным параграфом-пустышкой.
     * @param node - Узел
     * @return boolean возвращает true, если переданный узел является бесполезным параграфом-пустышкой
     *                  и false в противном случае
     */
    public static boolean isDummyParagraph(Node node) {
        
        Paragraph paragraph = (Paragraph)node;
        if (paragraph == null) {
            return false;
        }
        ParagraphFormat paragraphFormat = paragraph.getParagraphFormat();
        return !paragraph.hasChildNodes() && !paragraphFormat.getPageBreakBefore()
                && paragraphFormat.getSpaceBefore() == 0d && paragraphFormat.getSpaceAfter() == 0d
                // это не единственный ребенок у родителя
                && (paragraph.getNextSibling() != null || paragraph.getPreviousSibling() != null);
    }

    /**
     * Добавляет пустой параграф и возвращает его.
     * @param nodeParent - Родительская область
     * @return Нулевой параграф
     */
    public static Paragraph addNullParagraph(CompositeNode nodeParent) {
        
        Paragraph parSpace = new Paragraph(nodeParent.getDocument());
        parSpace.getParagraphBreakFont().setSize(0.1);
        nodeParent.appendChild(parSpace);
        return parSpace;
    }

    /**
     * Функция добавляет background Image. Упрощенная версия.
     * @param nodeParent  - Родительская область
     * @param imageSource - Адрес картинки
     * @throws Exception генерируют классы com.aspose.words
     */
    public static void addBackgroundImageSimple(CompositeNode nodeParent, String imageSource) throws Exception {
        
        if (StringUtils.isBlank(imageSource)) {
            return;
        }
        String source = HelpFuncs.getImageSource(imageSource);
        if (!new File(source).exists()) {
            return;
        }
        Shape shape = new Shape(nodeParent.getDocument(), ShapeType.IMAGE);
        nodeParent.appendChild(shape);
        shape.getImageData().setImage(source);
        shape.setBehindText(true);
    }

    /**
     * Функция добавляет background Image.
     * @param nodeParent  - Родительская область
     * @param imageSource - Адрес картинки
     * @throws Exception генерируют классы com.aspose.words
     */
    public static void addBackgroundImage(CompositeNode nodeParent, String imageSource) throws Exception {
        
        if (StringUtils.isBlank(imageSource)) {
            return;
        }
        String source = HelpFuncs.getImageSource(imageSource);
        if (!new File(source).exists()) {
            return;
        }

        DocumentBase wordDocument = nodeParent.getDocument();
        Paragraph paragraph = new Paragraph(wordDocument);
        nodeParent.appendChild(paragraph);
        Shape shape = new Shape(wordDocument, ShapeType.IMAGE);
        paragraph.appendChild(shape);
        shape.getImageData().setImage(source);
        shape.setBehindText(true);
        shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.COLUMN);

        if (nodeParent.getNodeType() == CELL) {
            shape.setWidth(((Cell)nodeParent).getCellFormat().getWidth());
        }

        paragraph.getParagraphBreakFont().setSize(0.1);
    }

    /**
     * Обрабатываем свойство перехода на следующую страницу.
     * @param node - Элемент дерева Aspose
     * @param area - Элемент дерева внутреннего представления
     */
    public static void addPageBreak(CompositeNode node, GenericArea area) {
        
        if (node.getNodeType() != TABLE) {
            return;
        }

        //Сброс на следующую страницу
        //Page-break-before
        if (area.getPropertyValue(PAGE_BREAK_BEFORE) != null) {
            ((Paragraph)node.getPreviousSibling()).getParagraphFormat().setPageBreakBefore(true);
        }
        //Page-break-after
        if (area.getPropertyValue(PAGE_BREAK_AFTER) != null) {
            ((Paragraph)node.getNextSibling()).getParagraphFormat().setPageBreakBefore(true);
        }
        //Break-before
        if (area.getPropertyValue(BREAK_BEFORE) != null) {
            String breakBefore = (String)area.getPropertyValue(BREAK_BEFORE);
            if (breakBefore.equals("even-page") || breakBefore.equals("page")) {
                ((Paragraph)node.getPreviousSibling()).getParagraphFormat().setPageBreakBefore(true);
            }
        }
        //Break-after
        if (area.getPropertyValue(BREAK_AFTER) != null) {
            String breakAfter = (String)area.getPropertyValue(BREAK_AFTER);
            if (breakAfter.equals("even-page") || breakAfter.equals("page")) {
                ((Paragraph)node.getNextSibling()).getParagraphFormat().setPageBreakBefore(true);
            }
        }
    }
}
