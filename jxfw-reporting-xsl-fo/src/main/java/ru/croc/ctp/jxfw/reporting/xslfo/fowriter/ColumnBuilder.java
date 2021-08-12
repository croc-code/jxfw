package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AlignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ValignClass;

/**
 * Строитель колонок.
 *
 * @author PaNovikov
 * @since 09.06.2017.
 */
public class ColumnBuilder {

    /**
     * Заголовок.
     */
    protected String caption = StringUtils.EMPTY;
    /**
     * Выравнивание по горизонтали.
     */
    protected AlignClass align = AlignClass.ALIGN_NONE;
    /**
     * Выравнивание по вертикали.
     */
    protected ValignClass valign = ValignClass.VALIGN_NONE ;
    /**
     * Класс колонки.
     */
    protected String columnClass = null;
    /**
     * Ширина колокни.
     */
    protected String columnWidth = StringUtils.EMPTY;
    /**
     * Выравнивание заголовка оп горизонтали.
     */
    protected AlignClass headerAlign = AlignClass.ALIGN_NONE;
    /**
     * Выравнивание заголовка по вертикали.
     */
    protected ValignClass headerValign = ValignClass.VALIGN_NONE;
    /**
     * Класс заголовка.
     */
    protected String headerCellClass = null;

    /**
     * Получить объект класса.
     * @return объект класса
     */
    public static ColumnBuilder create() {
        ColumnBuilder columnBuilder = new ColumnBuilder();
        return columnBuilder;
    }

    private ColumnBuilder() {
    }

    /**
     * Установить наименование колонки.
     * @param caption наименование колонки.
     * @return this
     */
    public ColumnBuilder setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    /**
     * Установить горизонтальное выравнивание ячеек.
     * @param align горизонтальное выравнивае.
     * @return {@code this}
     */
    public ColumnBuilder setAlign(AlignClass align) {
        this.align = align;
        return this;
    }

    /**
     * Установить вертикальное выравнивание ячеек.
     * @param valign горизонтальное выравнивае.
     * @return {@code this}
     */
    public ColumnBuilder setValign(ValignClass valign) {
        this.valign = valign;
        return this;
    }

    /**
     * Установить класс колонки.
     * @param columnClass наименование класса.
     * @return {@code this}
     */
    public ColumnBuilder setColumnClass(String columnClass) {
        this.columnClass = columnClass;
        return this;
    }

    /**
     * Установить ширину колонки.
     * @param columnWidth ширина колонки
     * @return {@code this}
     */
    public ColumnBuilder setColumnWidth(String columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    /**
     * Установить горизонтальное выравнивание header.
     * @param headerAlign горизонтальное выравнивание header
     * @return {@code this}
     */
    public ColumnBuilder setHeaderAlign(AlignClass headerAlign) {
        this.headerAlign = headerAlign;
        return this;
    }

    /**
     * Установить горизонтальное выравнивание header.
     * @param headerValign вертикальное выравние headerValign
     * @return {@code this}
     */
    public ColumnBuilder setHeaderValign(ValignClass headerValign) {
        this.headerValign = headerValign;
        return this;
    }

    /**
     * Установить класс ячейки заголовка.
     * @param headerCellClass вертикальное выравние headerValign
     * @return {@code this}
     */
    public ColumnBuilder setHeaderCellClass(String headerCellClass) {
        this.headerCellClass = headerCellClass;
        return this;
    }

}