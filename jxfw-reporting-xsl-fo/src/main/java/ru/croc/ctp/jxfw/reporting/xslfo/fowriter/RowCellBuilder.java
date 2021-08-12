package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

/**
 * Строитель строки row.
 * @author PaNovikov
 * @since 10.07.2017.
 */
public class RowCellBuilder {
    /**
     * Данные.
     */
    protected Object data;
    /**
     * Тип.
     */
    protected String type;
    /**
     * Число объединенных ячеек.
     */
    protected int colSpan = 1;
    /**
     * Число объединенных рядов.
     */
    protected int rowSpan = 1;
    /**
     * Стиль элемента.
     */
    protected String elementClass;

    private RowCellBuilder(){}

    /**
     * Создать объект.
     * @param data данные
     * @return созданный объект.
     */
    public static RowCellBuilder create(Object data) {
        RowCellBuilder rcb = new RowCellBuilder();
        rcb.data = data;
        return rcb;
    }

    /**
     * Установить данные.
     * @param data данные.
     * @return this
     */
    public RowCellBuilder setData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * Установть тип.
     * @param type тип
     * @return this
     */
    public RowCellBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Установить colSpan.
     * @param colSpan число объединенных колонок
     * @return this
     */
    public RowCellBuilder setColSpan(int colSpan) {
        this.colSpan = colSpan;
        return this;
    }

    /**
     * Установить rowSpan.
     * @param rowSpan число объединенных рядов.
     * @return this
     */
    public RowCellBuilder setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
        return this;
    }

    /**
     * Установить класс ячейки.
     * @param elementClass класс ячейки
     * @return this
     */
    public RowCellBuilder setElementClass(String elementClass) {
        this.elementClass = elementClass;
        return this;
    }
}