package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.table;

import com.aspose.words.AutoFitBehavior;
import com.aspose.words.BorderCollection;
import com.aspose.words.Cell;
import com.aspose.words.CellFormat;
import com.aspose.words.CellMerge;
import com.aspose.words.Document;
import com.aspose.words.HeightRule;
import com.aspose.words.LineStyle;
import com.aspose.words.Row;
import com.aspose.words.Table;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.BlockProperties;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.BorderHelper;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties.TableHelper;

import java.awt.Color;
import java.util.List;

/**
 * Класс работы с таблицами.
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class XslfoTable extends Table {

    /**
     * Необходимость установки ширины таблицы на основании содержания ячеек.
     */
    private boolean needAutoFit;

    /**
     * Word Document.
     */
    private final Document wordDocument;

    /**
     * Заголовок таблицы.
     */
    private List<Row> header;

    /**
     * Тело таблицы.
     */
    private List<Row> body;

    /**
     * Нижняя часть таблицы.
     */
    private List<Row> footer;

    //region Границы областей

    /**
     * Таблица.
     */
    private BorderCollection tableBorders;

    /**
     * Header.
     */
    private BorderCollection headerBorders;

    /**
     * Body.
     */
    private BorderCollection bodyBorders;
    //endregion

    /**
     * Footer.
     */
    private BorderCollection footerBorders;

    //region Отступы
    /**
     * MarginTop.
     */
    private double marginTop;

    /**
     * MarginBottom.
     */
    private double marginBottom;

    /**
     * MarginLeft.
     */
    private double marginLeft;

    /**
     * MarginRight.
     */
    private double marginRight;

    /**
     * PaddingTop.
     */
    private double paddingTop;

    /**
     * PaddingBottom.
     */
    private double paddingBottom;

    /**
     * PaddingLeft.
     */
    private double paddingLeft;

    /**
     * PaddingRight.
     */
    private double paddingRight;
    //endregion

    //region Цвета фона

    /**
     * Цвет фона таблицы.
     */
    private Color tableBackground;

    /**
     * Цвет фона Header.
     */
    private Color headerBackground;

    /**
     * Цвет фона body.
     */
    private Color bodyBackground;

    /**
     * Цвет фона footer.
     */
    private Color footerBackground;

    //endregion
    
    //region Контейнеры header, body, footer
    public List<Row> getHeader() {
        return header;
    }

    public void setHeader(List<Row> header) {
        this.header = header;
    }

    public List<Row> getBody() {
        return body;
    }

    public void setBody(List<Row> body) {
        this.body = body;
    }

    public List<Row> getFooter() {
        return footer;
    }

    public void setFooter(List<Row> footer) {
        this.footer = footer;
    }
    //endregion

    //region Границы
    public BorderCollection getTableBorders() {
        return tableBorders;
    }

    public void setTableBorders(BorderCollection tableBorders) {
        this.tableBorders = tableBorders;
    }

    public BorderCollection getHeaderBorders() {
        return headerBorders;
    }

    public void setHeaderBorders(BorderCollection headerBorders) {
        this.headerBorders = headerBorders;
    }

    public BorderCollection getBodyBorders() {
        return bodyBorders;
    }

    public void setBodyBorders(BorderCollection bodyBorders) {
        this.bodyBorders = bodyBorders;
    }

    public BorderCollection getFooterBorders() {
        return footerBorders;
    }

    public void setFooterBorders(BorderCollection footerBorders) {
        this.footerBorders = footerBorders;
    }
    //endregion

    //region Отступы
    public double getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(double marginTop) {
        this.marginTop = marginTop;
    }

    public double getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(double marginBottom) {
        this.marginBottom = marginBottom;
    }

    public double getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(double marginLeft) {
        this.marginLeft = marginLeft;
    }

    public double getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(double marginRight) {
        this.marginRight = marginRight;
    }

    public double getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(double paddingTop) {
        this.paddingTop = paddingTop;
    }

    public double getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(double paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public double getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(double paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public double getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(double paddingRight) {
        this.paddingRight = paddingRight;
    }
    //endregion

    //region Цвет фона
    public Color getTableBackground() {
        return tableBackground;
    }

    public void setTableBackground(Color tableBackground) {
        this.tableBackground = tableBackground;
    }

    public Color getHeaderBackground() {
        return headerBackground;
    }

    public void setHeaderBackground(Color headerBackground) {
        this.headerBackground = headerBackground;
    }

    public Color getBodyBackground() {
        return bodyBackground;
    }

    public void setBodyBackground(Color bodyBackground) {
        this.bodyBackground = bodyBackground;
    }

    public Color getFooterBackground() {
        return footerBackground;
    }

    public void setFooterBackground(Color footerBackground) {
        this.footerBackground = footerBackground;
    }
    //endregion

    /**
     * Инициализирующий конструктор.
     * @param wordDocument - Word документ
     */
    public XslfoTable(Document wordDocument) {
        super(wordDocument);
        this.wordDocument = wordDocument;
    }

    /**
     * Функция генерит таблицу Aspose.Word.
     * @throws Exception генерируют классы com.aspose.words
     */
    public void genericXslfoTable() throws Exception {
        
        removeAllChildren();
        //region Формируем таблицу
        if (header != null) {
            for (Row tableRow : header) {
                tableRow.getRowFormat().setHeadingFormat(true);
                appendChild(tableRow);
            }
        }

        if (body != null) {
            for (Row tableRow : body) {
                appendChild(tableRow);
            }
        }

        if (footer != null) {
            for (Row tableRow : footer) {
                appendChild(tableRow);
            }
        }
        //endregion
        if (needAutoFit) {
            autoFit(AutoFitBehavior.AUTO_FIT_TO_CONTENTS);
        } else {
            // NOTE: начиная с Aspose.Word 10.5 таблицы создаются с autofix, игнорируя все наши вычисления ширин
            setAllowAutoFit(false);
            autoFit(AutoFitBehavior.FIXED_COLUMN_WIDTHS);
        }

        //Формируем границы Header body footer
        setHeaderBodyFooterBordersProperties();
        //Формируем отступы padding
        setPadding();
        //Формируем границы таблицы
        setTableBordersProperties();
        //Формируем отступы margin
        setMargin();


    }

    /**
     * Функция устанавливает свойства padding.
     */
    private void setPadding() {
        
        if (paddingTop > 0d) {
            Row rowTop = TableHelper.ceateRow(wordDocument);
            Row rowTopOld = getFirstRow();
            for (Cell cell : rowTopOld.getCells()) {
                Cell cellTop = new Cell(wordDocument);
                BlockProperties.addNullParagraph(cellTop);
                cellTop.getCellFormat().setWidth(cell.getCellFormat().getWidth());
                cellTop.getCellFormat().setHorizontalMerge(cell.getCellFormat().getHorizontalMerge());
                rowTop.appendChild(cellTop);
            }
            rowTop.getRowFormat().setHeightRule(HeightRule.EXACTLY);
            rowTop.getRowFormat().setHeight(paddingTop);
            insertBefore(rowTop, rowTopOld);
        }

        if (paddingBottom > 0d) {
            Row rowBottom = TableHelper.ceateRow(wordDocument);
            for (Cell cell : getFirstRow().getCells()) {
                Cell cellBottom = new Cell(wordDocument);
                BlockProperties.addNullParagraph(cellBottom);
                cellBottom.getCellFormat().setWidth(cell.getCellFormat().getWidth());
                cellBottom.getCellFormat().setHorizontalMerge(cell.getCellFormat().getHorizontalMerge());
                rowBottom.appendChild(cellBottom);
            }
            rowBottom.getRowFormat().setHeightRule(HeightRule.EXACTLY);
            rowBottom.getRowFormat().setHeight(paddingBottom);
            appendChild(rowBottom);
        }

        if (paddingLeft > 0d) {
            for (Row row : getRows()) {
                Cell left = new Cell(wordDocument);
                BlockProperties.addNullParagraph(left);
                left.getCellFormat().setWidth(paddingLeft);
                row.insertBefore(left, row.getFirstCell());
            }

        }

        if (paddingLeft > 0d) {
            for (Row row : getRows()) {
                Cell right = new Cell(wordDocument);
                BlockProperties.addNullParagraph(right);
                right.getCellFormat().setWidth(paddingRight);
                row.appendChild(right);
            }

        }
    }

    /**
     * Функция устанавливает свойства границ таблицы.
     * @throws Exception генерируют классы com.aspose.words
     */
    private void setTableBordersProperties() throws Exception {
        
        //region Установка границ таблицы

        //Top
        if (tableBorders.getTop().getLineStyle() != LineStyle.NONE && getFirstRow() != null) {
            for (Cell topCell : getFirstRow().getCells()) {
                BorderHelper.assign(topCell.getCellFormat().getBorders().getTop(), tableBorders.getTop());
            }
        }
        //Bottom
        if (tableBorders.getBottom().getLineStyle() != LineStyle.NONE && getLastRow() != null) {
            for (Cell bottomCell : getLastRow().getCells()) {
                BorderHelper.assign(bottomCell.getCellFormat().getBorders().getBottom(), tableBorders.getBottom());
            }
        }
        //Left
        if (tableBorders.getLeft().getLineStyle() != LineStyle.NONE) {
            for (Row row : getRows()) {
                if (row.getFirstCell() != null) {
                    BorderHelper.assign(row.getFirstCell().getCellFormat().getBorders().getLeft(),
                            tableBorders.getLeft());
                }
            }
        }
        //Right
        if (tableBorders.getRight().getLineStyle() != LineStyle.NONE) {
            for (Row rightRow : getRows()) {
                // Для границы справа приходится проходиться по всем смерженных по горизонтали ячейкам и всем
                // проставлять границу справа
                for (Cell cell = rightRow.getLastCell(); cell != null; cell = (Cell)cell.getPreviousSibling()) {
                    BorderHelper.assign(cell.getCellFormat().getBorders().getRight(), tableBorders.getRight());
                    if (cell.getCellFormat().getHorizontalMerge() != CellMerge.PREVIOUS) {
                        break;
                    }
                }
            }
        }
        //endregion
    }

    /**
     * Функция устанавливает свойства границ Header Body Footer.
     * @throws Exception генерируют классы com.aspose.words
     */
    private void setHeaderBodyFooterBordersProperties() throws Exception {
        
        //Установка границ областей Header Body Footer
        int numRowsInHeader = 0;
        int numRowsInBody = 0;
        int numRowsInFooter = 0;

        if (getHeader() != null) {
            numRowsInHeader = getHeader().size();
        }
        if (getBody() != null) {
            numRowsInBody = getBody().size();
        }
        if (getFooter() != null) {
            numRowsInFooter = getFooter().size();
        }


        //region Header

        if (numRowsInHeader != 0) {

            if (numRowsInFooter == 0 && numRowsInBody == 0
                    && tableBorders.getBottom().getLineStyle() != LineStyle.NONE && getPaddingBottom() == 0) {
                headerBorders.getBottom().setLineStyle(LineStyle.NONE);
            }

            //Top            
            if ((headerBorders.getTop().getLineStyle() != LineStyle.NONE
                    && tableBorders.getTop().getLineStyle() == LineStyle.NONE) || getPaddingTop() > 0) {
                for (Cell topCell : getFirstRow().getCells()) {
                    BorderHelper.assign(topCell.getCellFormat().getBorders().getTop(), headerBorders.getTop());
                }
            }
            //Bottom
            if ((headerBorders.getBottom().getLineStyle() != LineStyle.NONE) || getPaddingBottom() > 0) {
                for (Cell bottomCell : getRows().get(numRowsInHeader - 1).getCells()) {
                    BorderHelper.assign(bottomCell.getCellFormat().getBorders().getBottom(), headerBorders.getBottom());
                }
            }
            //Left
            if ((headerBorders.getLeft().getLineStyle() != LineStyle.NONE
                    && tableBorders.getLeft().getLineStyle() == LineStyle.NONE) || getPaddingLeft() > 0) {
                for (int i = 0; i < numRowsInHeader; i++) {
                    BorderHelper.assign(getRows().get(i).getFirstCell().getCellFormat().getBorders().getLeft(),
                            headerBorders.getLeft());
                }
            }
            //Right
            if ((headerBorders.getRight().getLineStyle() != LineStyle.NONE
                    && tableBorders.getRight().getLineStyle() == LineStyle.NONE) || getPaddingRight() > 0) {
                for (int i = 0; i < numRowsInHeader; i++) {
                    // Для границы справа приходится проходиться по всем смерженных по горизонтали ячейкам и всем
                    // проставлять границу справа
                    for (Cell cell = getRows().get(i).getLastCell(); cell != null;
                         cell = (Cell)cell.getPreviousSibling()) {
                        BorderHelper.assign(cell.getCellFormat().getBorders().getRight(), headerBorders.getRight());
                        if (cell.getCellFormat().getHorizontalMerge() != CellMerge.PREVIOUS) {
                            break;
                        }
                    }
                }
            }
        }
        //endregion

        //region Body
        if (numRowsInBody != 0) {

            if (numRowsInHeader == 0 && tableBorders.getTop().getLineStyle() != LineStyle.NONE
                    && getPaddingTop() == 0) {
                bodyBorders.getTop().setLineStyle(LineStyle.NONE);
            }

            if (numRowsInFooter == 0 && tableBorders.getBottom().getLineStyle() != LineStyle.NONE
                    && getPaddingBottom() == 0) {
                bodyBorders.getBottom().setLineStyle(LineStyle.NONE);
            }

            //Top            
            if ((bodyBorders.getTop().getLineStyle() != LineStyle.NONE) || getPaddingTop() > 0) {
                for (Cell topCell : getRows().get(numRowsInHeader).getCells()) {
                    BorderHelper.assign(topCell.getCellFormat().getBorders().getTop(), bodyBorders.getTop());
                }
            }
            //Bottom
            if ((bodyBorders.getBottom().getLineStyle() != LineStyle.NONE) || getPaddingBottom() > 0) {
                for (Cell bottomCell : getRows().get(numRowsInHeader + numRowsInBody - 1).getCells()) {
                    BorderHelper.assign(bottomCell.getCellFormat().getBorders().getBottom(), bodyBorders.getBottom());
                }
            }
            //Left
            if ((bodyBorders.getLeft().getLineStyle() != LineStyle.NONE
                    && tableBorders.getLeft().getLineStyle() == LineStyle.NONE) || getPaddingLeft() > 0) {
                for (int i = 0; i < numRowsInBody; i++) {
                    BorderHelper.assign(getRows().get(numRowsInHeader + i).getFirstCell().getCellFormat().getBorders()
                            .getLeft(), bodyBorders.getLeft());
                }
            }
            //Right
            if ((bodyBorders.getRight().getLineStyle() != LineStyle.NONE
                    && tableBorders.getRight().getLineStyle() == LineStyle.NONE) || getPaddingRight() > 0) {
                for (int i = 0; i < numRowsInBody; i++) {
                    // Для границы справа приходится проходиться по всем смерженных по горизонтали ячейкам и всем
                    // проставлять границу справа
                    for (Cell cell = getRows().get(numRowsInHeader + i).getLastCell(); cell != null;
                            cell = (Cell)cell.getPreviousSibling()) {
                        BorderHelper.assign(cell.getCellFormat().getBorders().getRight(), bodyBorders.getRight());
                        if (cell.getCellFormat().getHorizontalMerge() != CellMerge.PREVIOUS) {
                            break;
                        }
                    }
                }
            }
        }
        //endregion

        //region Footer
        if (numRowsInFooter != 0) {
            if (numRowsInHeader == 0 && numRowsInBody == 0
                    && tableBorders.getTop().getLineStyle() != LineStyle.NONE && getPaddingTop() == 0) {
                footerBorders.getTop().setLineStyle(LineStyle.NONE);
            }

            //Top            
            if ((footerBorders.getTop().getLineStyle() != LineStyle.NONE) || getPaddingTop() > 0) {
                for (Cell topCell : getRows().get(numRowsInHeader + numRowsInBody).getCells()) {
                    BorderHelper.assign(topCell.getCellFormat().getBorders().getTop(), footerBorders.getTop());
                }
            }
            //Bottom
            if ((footerBorders.getBottom().getLineStyle() != LineStyle.NONE
                    && tableBorders.getBottom().getLineStyle() == LineStyle.NONE) || getPaddingBottom() > 0) {
                for (Cell bottomCell : getLastRow().getCells()) {
                    BorderHelper.assign(bottomCell.getCellFormat().getBorders().getBottom(), footerBorders.getBottom());
                }
            }
            //Left
            if ((footerBorders.getLeft().getLineStyle() != LineStyle.NONE
                    && tableBorders.getLeft().getLineStyle() == LineStyle.NONE) || getPaddingLeft() > 0) {
                for (int i = numRowsInHeader + numRowsInBody; i < getRows().getCount(); i++) {
                    BorderHelper.assign(getRows().get(i).getFirstCell().getCellFormat().getBorders().getLeft(),
                            footerBorders.getLeft());
                }
            }
            //Right
            if ((footerBorders.getRight().getLineStyle() != LineStyle.NONE
                    && tableBorders.getRight().getLineStyle() == LineStyle.NONE) || getPaddingRight() > 0) {
                for (int i = numRowsInHeader + numRowsInBody; i < getRows().getCount(); i++) {
                    // Для границы справа приходится проходиться по всем смерженных по горизонтали ячейкам и всем
                    // проставлять границу справа
                    for (Cell cell = getRows().get(i).getLastCell(); cell != null;
                            cell = (Cell)cell.getPreviousSibling()) {
                        BorderHelper.assign(cell.getCellFormat().getBorders().getRight(), footerBorders.getRight());
                        if (cell.getCellFormat().getHorizontalMerge() != CellMerge.PREVIOUS) {
                            break;
                        }
                    }
                }
            }
        }
        //endregion
    }

    /**
     * Функция устанавливает свойства Margin.
     * @throws Exception генерируют классы com.aspose.words
     */
    private void setMargin() throws Exception {
        
        // NOTE: В качестве маржина мы используем невидимые ячейки.
        if (marginTop > 0d) {
            Row rowTop = TableHelper.ceateRow(wordDocument);
            Row rowTopOld = getFirstRow();
            for (Cell cell : rowTopOld.getCells()) {
                Cell cellTop = new Cell(wordDocument);
                BlockProperties.addNullParagraph(cellTop);
                cellTop.getCellFormat().setWidth(cell.getCellFormat().getWidth());
                cellTop.getCellFormat().setHorizontalMerge(cell.getCellFormat().getHorizontalMerge());
                rowTop.appendChild(cellTop);
            }
            rowTop.getRowFormat().setHeightRule(HeightRule.EXACTLY);
            rowTop.getRowFormat().setHeight(marginTop);
            rowTop.getRowFormat().getBorders().setLineStyle(LineStyle.NONE);
            rowTop.getRowFormat().getBorders().setLineWidth(0d);
            insertBefore(rowTop, rowTopOld);
        }

        if (marginBottom > 0d) {
            Row rowBottom = TableHelper.ceateRow(wordDocument);
            for (Cell cell : getFirstRow().getCells()) {
                Cell cellBottom = new Cell(wordDocument);
                BlockProperties.addNullParagraph(cellBottom);
                cellBottom.getCellFormat().setWidth(cell.getCellFormat().getWidth());
                cellBottom.getCellFormat().setHorizontalMerge(cell.getCellFormat().getHorizontalMerge());
                rowBottom.appendChild(cellBottom);
            }
            rowBottom.getRowFormat().setHeightRule(HeightRule.EXACTLY);
            rowBottom.getRowFormat().setHeight(marginBottom);
            rowBottom.getRowFormat().getBorders().setLineStyle(LineStyle.NONE);
            rowBottom.getRowFormat().getBorders().setLineWidth(0d);
            appendChild(rowBottom);
        }

        CellFormat firstCellFormat = getFirstRow().getFirstCell().getCellFormat();
        if (marginLeft > 0d || firstCellFormat.getLeftPadding() > 0d
                || firstCellFormat.getBorders().getLeft().getLineWidth() > 0d) {
            // У нас задан margin-left или исправляем глюк
            // У таблиц в Ворде есть глюк. Она смещается влево, если в первой ячейки задан padding-left или левая
            // граница
            // Если отступ меньше 0.1d, то округляем вверх
            double width = Math.max(0.1d, marginLeft > 0d ? marginLeft
                    : firstCellFormat.getBorders().getLeft().getLineWidth() / 2d);
            for (Row row : getRows()) {
                Cell left = new Cell(wordDocument);
                BlockProperties.addNullParagraph(left);
                left.getCellFormat().setWidth(width);
                left.getCellFormat().getBorders().clearFormatting();
                row.insertBefore(left, row.getFirstCell());
            }
        }

        if (marginRight > 0d) {
            for (Row row : getRows()) {
                Cell right = new Cell(wordDocument);
                BlockProperties.addNullParagraph(right);
                right.getCellFormat().setWidth(marginRight);
                right.getCellFormat().getBorders().clearFormatting();
                row.appendChild(right);
            }
        }
    }

    public boolean isNeedAutoFit() {
        return needAutoFit;
    }

    public void setNeedAutoFit(boolean needAutoFit) {
        this.needAutoFit = needAutoFit;
    }
}
