package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import com.aspose.words.Cell;
import com.aspose.words.CellFormat;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Row;
import com.aspose.words.Table;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.awt.Color;

/**
 * Вспомогательные методы форматирования.
 * Created by vsavenkov on 27.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FormatHelper {

    /**
     * Установка цвета фона ячейки для области.
     * @param cellTo   - Ячейка
     * @param areaFrom - Область
     */
    public static void assignBackgroundColorArea(Cell cellTo, GenericArea areaFrom) {
        
        if (areaFrom == null) {
            return;
        }
        Color backgroundColor = HelpFuncs.nvl2(areaFrom.getInheritablePropertyValue(FoPropertyType.BACKGROUND_COLOR),
                FoColor.DEFAULT_COLOR);
        if (!FoColor.isEmpty(backgroundColor)) {
            cellTo.getCellFormat().getShading().setBackgroundPatternColor(backgroundColor);
        }
    }

    /**
     * У таблиц в Ворде есть глюк. Она смещается влево, если в первой ячейки задан padding-left или левая граница.
     * @param table - Таблица Word
     * @throws Exception генерируют классы com.aspose.words
     */
    public static void fixWordTableBug(Table table) throws Exception {
        
        CellFormat firstCellFormat = table.getFirstRow().getFirstCell().getCellFormat();
        if (firstCellFormat.getLeftPadding() != 0d || firstCellFormat.getBorders().getLeft().getLineWidth() != 0d) {
            // Если отступ меньше 0.1d, то округляем вверх
            double dblWidth = Math.max(0.1d, firstCellFormat.getBorders().getLeft().getLineWidth() / 2d);
            for (Row row : table.getRows()) {
                Cell cellMarginLeft = new Cell(table.getDocument());
                BlockProperties.addNullParagraph(cellMarginLeft);
                cellMarginLeft.getCellFormat().setWidth(dblWidth);
                row.insertBefore(cellMarginLeft, row.getFirstCell());
            }
        }
    }

    /**
     * Если 2 параграфа в Word имеют фон и у них есть space-before/space-after, то Word визуально склеивает эти области.
     * @param paragraph - параграф
     */
    public static void fixWordParagraphBug(Paragraph paragraph) {
        if (!(paragraph.getPreviousSibling() instanceof Paragraph)) {
            return;
        }
        Paragraph previousParagraph = (Paragraph)paragraph.getPreviousSibling();
        if (previousParagraph == null || FoColor.isEmpty(paragraph.getParagraphFormat().getShading()
                .getBackgroundPatternColor())) {
            return;
        }
        ParagraphFormat prevFormat = previousParagraph.getParagraphFormat();
        if (FoColor.isEmpty(prevFormat.getShading().getBackgroundPatternColor())
                || (prevFormat.getSpaceAfter() == 0 && paragraph.getParagraphFormat().getSpaceBefore() == 0)) {
            return;
        }
        Paragraph dummy = new Paragraph(paragraph.getDocument());
        dummy.getParagraphBreakFont().setSize(0.1);
        paragraph.getParentNode().insertBefore(dummy, paragraph);
    }
}
