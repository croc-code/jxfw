package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table;

import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;

import java.util.Arrays;
import java.util.Map;

/**
 * Абстрактный базовый класс задающий поведение контейнера ячеек в виде матрицы ячеек.
 * Применим для элементов &lt;fo:table-header&gt;, &lt;fo:table-body&gt;, &lt;fo:table-footer&gt;
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public abstract class CellGridContainer extends GenericArea {

    /**
     * Кол-во строк, выделяемых при увеличении длины массива.
     */
    private final int chunkSize = 1024;

    /**
     * Текущая позиция ячейки в контейнере.
     */
    private int currentCellPosition;

    /**
     * Ячейки контейнера.
     */
    private TableCellArea[] cells;

    /**
     * Количество колонок таблицы.
     */
    private final int totalColumnNumber;

    /**
     * Максимальный номер ряда.
     */
    private int maxRowNumber;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     * @throws XslFoException - генерирует в случае нарушения структуры
     */
    protected CellGridContainer(IArea parentArea, Map<String, String> attributeList) throws XslFoException {
        super(parentArea, attributeList);

        if (parentArea.getAreaType() != AreaType.TABLE) {
            throw new XslFoException("Родительская область не является таблицей!",
                    "CellGridContainer.CellGridContainer");
        }

        totalColumnNumber = getParentTableArea().getColumnList().size();
        if (totalColumnNumber == 0) {
            throw new XslFoException("Таблица не имеет колонок!", "CellGridContainer.CellGridContainer");
        }

        cells = new TableCellArea[totalColumnNumber * chunkSize];
    }

    /**
     * Свойство - Родительская область-Таблица.
     * @return TableArea    - возвращает родительскую область-таблицу
     */
    public TableArea getParentTableArea() {
        return (TableArea) parentArea;
    }

    /**
     * Начало обработки вложенных ячеек контейнера.
     */
    public void beginCellProcessing() {
        currentCellPosition = 0;
    }

    /**
     * Установка занятости ячейки элементом &lt;fo:table-cell&gt;.
     * @param row    - Номер строки контейнера ячеек
     * @param column - Номер колонки контейнера ячеек
     * @param cell   - элемент &lt;fo:table-cell&gt;
     * @throws XslFoException - генерирует в случае нарушения структуры
     */
    private void setCell(int row, int column, TableCellArea cell) throws XslFoException {

        if (column < totalColumnNumber) {
            int position = row * totalColumnNumber + column;
            if (position >= cells.length) {
                cells = Arrays.copyOf(cells, cells.length + chunkSize * totalColumnNumber);
            }

            if (cells[position] == null) {
                cells[position] = cell;
                if (row > maxRowNumber) {
                    maxRowNumber = row;
                }
            } else {
                throw new XslFoException("Неправильная разметка таблицы! "
                        + "Найдена область таблицы которая уже покрыта элементом <fo:table-cell>: "
                        + String.format(GlobalData.getCultureInfo(), "Ряд: %1$d Строка: %2$d", row + 1, column + 1),
                        "CellGridContainer.SetCellFlag");
            }
        } else {
            throw new XslFoException(String.format(GlobalData.getCultureInfo(),
                    "Неправильная разметка таблицы! Для ячейки ряда = %1$d, колонки = %2$d",
                    row + 1, column + 1)
                    + " задано неправильное значение number-columns-spanned. "
                    + "Ширина ряда превышает ширину таблицы.",
                    "AreaTreeBuilder.ProcessCellSpan");
        }
    }

    /**
     * Проверка того, что нет ячеек контейнера, не охваченных элементами &lt;fo:table-cell&gt;.
     * @throws XslFoException - генерирует в случае нарушения структуры
     */
    private void checkCellCoverageIntegrity() throws XslFoException {

        // Количество рядов в контейнере ячеек
        int totalRowNumber = getChildrenList().size();

        if (maxRowNumber >= totalRowNumber) {
            throw new XslFoException("Неправильная разметка таблицы!"
                    + " Найдена ячейка, которая вышла за границы контейнера по высоте!",
                    "CellGridContainer.checkCellCoverageIntegrity");
        }

        for (int position = totalRowNumber * totalColumnNumber - 1; position >= 0; position--) {
            if (cells[position] == null) {
                throw new XslFoException("Неправильная разметка таблицы!"
                        + " Найдена область таблицы которая не покрыта элементом <fo:table-cell>!",
                        "CellGridContainer.checkCellCoverageIntegrity");
            }
        }
    }

    /**
     * Получение номера строки по позиции ячейки.
     * @param position - Позиция ячейки
     * @return int - возвращает номер строки
     */
    public int getRowNumber(int position) {
        return position / totalColumnNumber;
    }

    /**
     * Получение номера колонки по позиции ячейки.
     * @param position - Позиция ячейки
     * @return int - возвращает номер колонки
     */
    public int getColumnNumber(int position) {
        return position % totalColumnNumber;
    }

    /**
     * Возвращает область ячейки по указанной позиции.
     * @param position - позиция ячейки
     * @return TableCellArea    - возвращает область ячейки по указанной позиции или null
     */
    public TableCellArea cellAt(int position) {
        return position < cells.length ? cells[position] : null;
    }

    /**
     * Возвращает область ячейки по указанной позиции.
     * @param row - строка ячейки
     * @param col - колонка ячейки
     * @return TableCellArea    - возвращает область ячейки по указанной позиции или null
     */
    public TableCellArea cellAt(int row, int col) {
        return cellAt(row * totalColumnNumber + col);
    }

    /**
     * Обработка свойств распространения ячейки.
     * @param tableCellArea - Область ячейки
     * @throws XslFoException - генерирует в случае нарушения структуры
     */
    public void processCell(TableCellArea tableCellArea) throws XslFoException {

        Assert.isTrue(tableCellArea.getGridContainer() == this, "oTableCellArea.GridContainer == this");
        int cellColumn = getColumnNumber(currentCellPosition);
        int cellRow = getRowNumber(currentCellPosition);
        int numberColumnsSpanned = tableCellArea.getNumberColumnsSpanned();
        int numberRowsSpanned = tableCellArea.getNumberRowsSpanned();

        // Устанавливаем начальную позицию ячейки
        tableCellArea.setPosition(currentCellPosition);

        // Устанавливаем флаг - ячейка занята элементом <fo:table-cell> - для всех ячеек,
        // покрытых элементом <fo:table-cell>
        for (int i = cellRow; i < cellRow + numberRowsSpanned; i++) {
            for (int j = cellColumn; j < cellColumn + numberColumnsSpanned; j++) {
                setCell(i, j, tableCellArea);
            }
        }

        // Теперь устанавливаем корректную позицию ячейки для продолжения обработки
        // других ячеек
        while (cellAt(++currentCellPosition) != null) {
        }
    }

    /**
     * Заканчиваем обработку вложенных ячеек контейнера.
     * @throws XslFoException - генерирует в случае нарушения структуры
     */
    public void endCellProcessing() throws XslFoException {
        // Проверяем - не осталось ли ячеек контейнера, не охваченных элементами <fo:table-cell>
        checkCellCoverageIntegrity();
    }
}
