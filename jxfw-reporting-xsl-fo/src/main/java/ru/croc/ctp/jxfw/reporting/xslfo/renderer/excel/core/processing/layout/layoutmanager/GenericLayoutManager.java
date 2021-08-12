package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_EXCEL_ROW_HEIGHT;

import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.collection.IntArray;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.ColumnWidth;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.CellGridContainer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableCellArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableColumnArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table.TableRowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.border.FoBorder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.logging.AreaLogHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс, инкапсулирующий общее поведение построения разметки контента - разбивка на страницы.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("deprecation")
public abstract class GenericLayoutManager implements ILayoutManager {

    /**
     * логгер.
     */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GenericLayoutManager.class);

    /**
     *  Ширина области контента страницы.
     */
    protected int pageContentWidth;

    /**
     * Высота области контента страницы.
     */
    protected int pageContentHeight;

    /**
     * Конструктор по умолчанию.
     * @param simplePageMasterArea - Экземпляр области SimplePageMasterArea
     */
    protected GenericLayoutManager(SimplePageMasterArea simplePageMasterArea) {

        pageContentWidth = simplePageMasterArea.getPageContentWidth();
        pageContentHeight = simplePageMasterArea.getPageContentHeight();
    }

    /**
     * Метод построения разметки контента корневой области.
     * @param rootArea               - корневая область
     * @param arrHorizontalCoordList - Массив координат по горизонтали
     * @param arrVerticalCoordList   - Массив координат по вертикали
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    public void doLayout(RootArea rootArea, IntArray arrHorizontalCoordList, IntArray arrVerticalCoordList)
            throws XslFoException {

        // Рассчитываем геометрическое расположение областей и всех вложенных подобластей
        // на плоскости и их размеров
        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "Before calculateAreaLayout : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));
        }
        calculateAreaLayout(rootArea);
        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "After calculateAreaLayout : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));
            logger.debug(AreaLogHelper.getAreaCoordinatesForLog(rootArea));
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "Before calculateAreaCellRange : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));
        }
        // Рассчитываем области ячеек, занимаемых ячейками на листе Excel
        calculateAreaCellRange(rootArea, arrHorizontalCoordList, arrVerticalCoordList);
        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "After calculateAreaCellRange : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));
            logger.debug(AreaLogHelper.getAreaRangesForLog(rootArea));
            Runtime.getRuntime().gc();
        }
    }

    /**
     * Расчет геометрического расположения областей и всех вложенных подобластей на плоскости и их размеров.
     * Рассчитываются расположение внешнего и внутреннего прямоугольников областей и их размеры.
     * @param rootArea - Корневая область
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    private void calculateAreaLayout(RootArea rootArea) throws XslFoException {

        if (rootArea == null) {
            throw new XslFoException("Родительская область не задана!", "GenericLayoutManager.calculateAreaLayout");
        }

        // Сначала рассчитываем размеры области и всех вложенных подобластей
        calculateAreaSize(rootArea);

        // Устанавливаем одинаковые размеры областей и вложенных подобластей - для формирования
        // полной прямоугольной разметки страницы, дочерние области полностью
        // своими размерами входят в родительскую
        makeEqualChildrenSizes(rootArea);

        // Рассчитываем расположение областей (координаты начала области по ширине и высоте - X и Y)
        calculateAreaCoordinates(rootArea);
    }

    /**
     * Рекурсивный метод для расчета размеров области по размерам дочерних областей.
     * @param parentArea - Родительская область
     */
    private void calculateAreaSize(IArea parentArea) {

        TableArea parentTableArea = parentArea instanceof TableArea ? (TableArea)parentArea : null;
        AreaRectangle parentRectangle = parentArea.getBorderRectangle();

        // Обрабатываем свойство span области
        if (parentArea.isColumnLayoutSpan()) {
            parentRectangle.setWidth(pageContentWidth);
        }

        // Если родительская область - область используемая для расположения левых и правых SPACER
        // областей при обработке padding и space атрибутов, то устанавливаем ширину контент-
        // области равной разности ширины родительской области и суммы ширин SPACER областей
        if (parentArea.getProgressionDirection() == AreaProgressionDirection.ROW
                && parentArea.getAreaType() != AreaType.TABLE_ROW) {
            setPaddingSpaceContentAreaWidth(parentArea);
        }

        // Обрабатываем свойства колонок таблицы
        if (parentTableArea != null) {
            processTableColumnsProperties(parentTableArea);
        }

        // Проходим по всем дочерним областям
        for (IArea childArea : parentArea.getChildrenList()) {
            TableCellArea cellArea = childArea instanceof TableCellArea ? (TableCellArea)childArea : null;
            AreaRectangle childRectangle = childArea.getBorderRectangle();
            // Находим признак распространения ячейки на несколько строк
            int rowSpanned = cellArea != null
                    ? cellArea.getNumberRowsSpanned()
                    : GlobalData.DEFAULT_ROWS_SPAN_VALUE;

            // Обрабатываем свойства ячейки
            if (cellArea != null) {
                setCellWidth(cellArea);
            }

            // Если ширина прямоугольника дочерней области неизвестна,
            // и родительская область имеет тип направления расположения block,
            // и ширина прямоугольника родительской области известна,
            // то эта ширина становится шириной прямоугольника дочерней области
            if (!childRectangle.isWidthDefined()
                    && parentArea.getProgressionDirection() == AreaProgressionDirection.BLOCK
                    && parentRectangle.isWidthDefined()) {
                childRectangle.setWidth(parentRectangle.getWidth());
            }

            // Если высота прямоугольника дочерней области неизвестна, и дочерняя область не имеет row-span,
            // родительская область имеет тип направления расположения row
            // и высота прямоугольника родительской области известна,
            // то эта высота становится высотой прямоугольника дочерней области
            if (!childRectangle.isHeightDefined()
                    && rowSpanned == GlobalData.DEFAULT_ROWS_SPAN_VALUE
                    && parentArea.getProgressionDirection() == AreaProgressionDirection.ROW
                    && parentRectangle.isHeightDefined()) {
                childRectangle.setHeight(parentRectangle.getHeight());
            }

            if (childArea.getProgressionDirection() == AreaProgressionDirection.INLINE) {
                // стараемся отойти от расчётов и заменить их стандартным методом aspose - autoFitRows, поэтому
                // задаём дефолтное значение
                childRectangle.setHeight(DEFAULT_EXCEL_ROW_HEIGHT);
            } else if (childArea.isHasChildren()) {
                // Рекурсивно определяем размеры всех непосредственных дочерних элементов
                calculateAreaSize(childArea);
            } else {
                if (!childRectangle.isWidthDefined()) {
                    childRectangle.setWidth(0);
                }
                if (!childRectangle.isHeightDefined()) {
                    childRectangle.setHeight(0);
                }
            }
        }

        if (parentTableArea != null) {
            // Расчет высоты/ширины таблицы по размерам отдельных ячеек
            calculateTableWidthFromCells(parentTableArea);
            calculateTableHeightFromCells(parentTableArea);
        } else if (!Arrays.asList(AreaType.TABLE_HEADER, AreaType.TABLE_BODY, AreaType.TABLE_FOOTER,
                AreaType.TABLE_FOOTER, AreaType.TABLE_ROW).contains(parentArea.getAreaType())) {
            // Табличные элементы рассчитываются уже при расчете самой таблицы (из-за row-span и column-span)
            switch (parentArea.getProgressionDirection()) {
                case BLOCK:
                    // Вычисляем ширину области как максимальную ширину у дочерних областей
                    setAreaMaximumWidth(parentArea);
                    // Вычисляем высоту области как сумму высот дочерних областей
                    enlargeAreaHeight(parentArea, getChildAreasTotalHeight(parentArea.getChildrenList()));
                    break;

                case ROW:
                    if (!parentArea.isColumnLayoutSpan()) {
                        // Вычисляем ширину области как сумму ширин дочерних областей
                        enlargeAreaWidth(parentArea, getChildAreasTotalWidth(parentArea.getChildrenList()));
                    }
                    // Вычисляем высоту области как максимальную высоту у дочерних областей
                    setAreaMaximumHeight(parentArea);
                    break;

                default:
                    // в оригинале ничего не было
            }
        }

        if (parentRectangle.getWidth() == 0) {
            FoBorder border = (FoBorder)parentArea.getProperty(FoPropertyType.BORDER_LEFT);
            if (border != null) {
                parentRectangle.setWidth(parentRectangle.getWidth() + (border.getWidth() + 1) >> 1);
            }
            border = (FoBorder)parentArea.getProperty(FoPropertyType.BORDER_RIGHT);
            if (border != null) {
                parentRectangle.setWidth(parentRectangle.getWidth() + (border.getWidth() + 1) >> 1);
            }
        }
        if (parentRectangle.getHeight() == 0) {
            FoBorder border = (FoBorder)parentArea.getProperty(FoPropertyType.BORDER_TOP);
            if (border != null) {
                parentRectangle.setHeight(parentRectangle.getHeight() + (border.getWidth() + 1) >> 1);
            }
            border = (FoBorder)parentArea.getProperty(FoPropertyType.BORDER_BOTTOM);
            if (border != null) {
                parentRectangle.setHeight(parentRectangle.getHeight() + (border.getWidth() + 1) >> 1);
            }
        }
    }

    /**
     * Рекурсивный метод для установки одинаковых размеров областей и вложенных подобластей -
     * для формирования полной прямоугольной разметки страницы, дочерние области полностью
     * своими размерами входят в родительскую.
     * @param area - Текущая область
     */
    private static void makeEqualChildrenSizes(IArea area) {

        // Обрабатываем дочерние области
        if (!area.isHasChildren()) {
            return;
        }
        AreaType areaType = area.getAreaType();
        AreaRectangle areaRect = area.getBorderRectangle();

        if (areaType == AreaType.TABLE) {
            distributeWidthGap(area, ((TableArea)area).getColumnList());
        }

        // Равномерно распределяем разницу размеров контейнера и дочерних областей
        switch (area.getProgressionDirection()) {
            case BLOCK:
                distributeHeightGap(area, area.getChildrenList());
                break;
            case ROW:
                if (areaType != AreaType.TABLE_ROW) {
                    distributeWidthGap(area, area.getChildrenList());
                }
                break;

            default:
                // в оригинале ничего не было
        }

        if (areaType == AreaType.TABLE_ROW) {
            // Для ячеек таблицы более сложный алгоритм выставления размеров из-за col-span и row-span
            for (IArea childArea : area.getChildrenList()) {
                TableCellArea currentTableCellArea = (TableCellArea)childArea;
                currentTableCellArea.recalculateWidthAndHeight();
                makeEqualChildrenSizes(currentTableCellArea);
            }
        } else {
            // Обрабатываем дочерние области
            for (IArea childArea : area.getChildrenList()) {
                // Устанавливаем одинаковую с родительской областью ширину/высоту в зависимости от
                // типа направления расположения родительской области
                if (!childArea.isColumnLayoutSpan()) {
                    switch (area.getProgressionDirection()) {
                        case BLOCK:
                            childArea.getBorderRectangle().setWidth(areaRect.getWidth());
                            break;
                        case ROW:
                            childArea.getBorderRectangle().setHeight(areaRect.getHeight());
                            break;
                        case INLINE:
                            childArea.getBorderRectangle().setWidth(areaRect.getWidth());
                            childArea.getBorderRectangle().setHeight(areaRect.getHeight());
                            break;

                        default:
                            // в оригинале ничего не было
                    }
                }

                makeEqualChildrenSizes(childArea);
            }
        }
    }

    /**
     * Равномерное распределение значения ширины между ширинами дочерних областей.
     * @param parentArea   - Родительская область
     * @param childrenList - Список дочерних областей
     */
    private static void distributeWidthGap(IArea parentArea, List<IArea> childrenList) {

        // Вычисляем разницу между шириной родительской области и суммы ширин дочерних областей
        int widthGap = parentArea.getBorderRectangle().getWidth();
        // Количество дочерних областей, которые можно расширить
        int redistributableChildrenCount = 0;
        for (IArea childArea : childrenList) {
            if (childArea.isColumnLayoutSpan()) {
                continue;
            }
            AreaRectangle rectangle = childArea.getBorderRectangle();
            widthGap -= rectangle.getWidth();
            if (!rectangle.isMaximumWidth()) {
                redistributableChildrenCount++;
            }
        }
        if (widthGap <= 0 || redistributableChildrenCount == 0) {
            return;
        }
        // Если ширина родительской области больше суммы ширин дочерних областей,
        // тогда равномерно распределяем разницу между ширинами дочерних областей

        // Базовая величина, которая будет прибавлена к ширине каждой дочерней области
        int baseWidthPart = widthGap / redistributableChildrenCount;
        // Остаток от разницы ширины при равномерном распределении
        int remainderWidth = widthGap % redistributableChildrenCount;

        widthGap = parentArea.getBorderRectangle().getWidth();
        // Равномерно распределяем разницу между ширинами дочерних областей
        for (IArea childArea : childrenList) {
            if (childArea.isColumnLayoutSpan()) {
                continue;
            }
            AreaRectangle rectangle = childArea.getBorderRectangle();
            if (!rectangle.isMaximumWidth()) {
                childArea.getBorderRectangle().setWidth(childArea.getBorderRectangle().getWidth() + baseWidthPart
                        + ((remainderWidth > 0) ? 1 : 0));
                remainderWidth--;
            }
            // Вычисляем заново, ибо из-за ограничения на макс-ширину мы теоретически можем не суметь нормально
            // распределиться
            widthGap -= rectangle.getWidth();
        }
        if (widthGap <= 0) {
            return;
        }
        // Опять осталась дырка, вызовем себя заново
        distributeWidthGap(parentArea, childrenList);
    }

    /**
     * Равномерное распределение значения высоты между высотами дочерних областей.
     * @param parentArea   - Родительская область
     * @param childrenList - Список дочерних областей
     */
    private static void distributeHeightGap(IArea parentArea, List<IArea> childrenList) {

        // Вычисляем разницу между высотой родительской области и суммы высот дочерних областей
        int heightGap = parentArea.getBorderRectangle().getHeight();
        // Количество дочерних областей, которые можно расширить
        int redistributableChildrenCount = 0;
        for (IArea childArea : childrenList) {
            if (childArea.isColumnLayoutSpan()) {
                continue;
            }
            AreaRectangle rectangle = childArea.getBorderRectangle();
            heightGap -= rectangle.getHeight();
            if (!rectangle.isMaximumHeight()) {
                redistributableChildrenCount++;
            }
        }
        if (heightGap <= 0 || redistributableChildrenCount == 0) {
            return;
        }
        // Если ширина родительской области больше суммы ширин дочерних областей,
        // тогда равномерно распределяем разницу между ширинами дочерних областей

        // Базовая величина, которая будет прибавлена к ширине каждой дочерней области
        int baseHeightPart = heightGap / redistributableChildrenCount;
        // Остаток от разницы ширины при равномерном распределении
        int remainderHeight = heightGap % redistributableChildrenCount;

        heightGap = parentArea.getBorderRectangle().getHeight();
        // Равномерно распределяем разницу между ширинами дочерних областей
        for (IArea childArea : childrenList) {
            if (childArea.isColumnLayoutSpan()) {
                continue;
            }
            AreaRectangle rectangle = childArea.getBorderRectangle();
            if (!rectangle.isMaximumHeight()) {
                childArea.getBorderRectangle().setHeight(childArea.getBorderRectangle().getHeight() + baseHeightPart
                        + ((remainderHeight > 0) ? 1 : 0));
                remainderHeight--;
            }
            // Вычисляем заново, ибо из-за ограничения на макс-ширину мы теоретически можем не суметь нормально
            // распределиться
            heightGap -= rectangle.getHeight();
        }
        if (heightGap <= 0) {
            return;
        }
        // Опять осталась дырка, вызовем себя заново
        distributeHeightGap(parentArea, childrenList);
    }

    /**
     * Установка ширины контент-области равной разности ширины родительской области и суммы ширин SPACER областей.
     * @param parentArea - Родительская область
     */
    private static void setPaddingSpaceContentAreaWidth(IArea parentArea) {

        if (!parentArea.isHasChildren() || !parentArea.getBorderRectangle().isWidthDefined()) {
            return;
        }

        IArea contentArea = null;
        int tmpSum = 0;

        for (IArea area : parentArea.getChildrenList()) {
            AreaRectangle rectangle = area.getBorderRectangle();
            if (rectangle.getWidth() == 0) {
                continue;
            }
            if (rectangle.isMaximumWidth()) {
                // Вычисляем сумму ширин SPACER областей
                tmpSum += rectangle.getWidth();
            } else if (contentArea == null) {
                contentArea = area;
            } else {
                return; // У нас больше одного кандидата на звание контент-области
            }
        }
        if (contentArea == null) {
            // Все дети фиксированной ширины. Присвоим их ширину родителю
            parentArea.getBorderRectangle().setMaxWidth(tmpSum);
            parentArea.getBorderRectangle().setWidth(tmpSum);
        } else {
            tmpSum = parentArea.getBorderRectangle().getWidth() - tmpSum;
            if (tmpSum > 0) {
                contentArea.getBorderRectangle().setWidth(tmpSum);
            }
        }
    }

    /**
     * Установка ширина ячейки таблицы по указанной ширине колонки(ок).
     * @param area - Область ячейки
     */
    private static void setCellWidth(TableCellArea area) {

        // Вычисляем сумму ширин колонок
        int width = 0;
        boolean isResizable = false;
        for (TableColumnArea column : area.getColumns()) {
            if (!column.getBorderRectangle().isWidthDefined()) {
                return;
            }
            isResizable |= column.getSizeCanBeRedistributed();
            width += column.getBorderRectangle().getWidth();
        }
        if (!isResizable || area.getBorderRectangle().getMaxWidth() < width) {
            area.getBorderRectangle().setMaxWidth(width);
        }
        // Устанавливаем значение ширины ячейки равной сумме ширин колонок
        area.getBorderRectangle().setWidth(width);
    }

    /**
     * Обработка свойств колонок таблицы. Вычисление ширин колонок.
     * @param tableArea - Область-Таблица
     */
    private static void processTableColumnsProperties(TableArea tableArea) {

        final float prc100 = 100f;
        float proportionalSum = 0f;
        float proportionalPercents = 0f;
        float percentsSum = 0f;
        int sumInPoints = 0;

        // Пройдем по всем колонкам таблицы
        for (IArea tableColumnArea : tableArea.getColumnList()) {
            TableColumnArea currentTableColumnArea = (TableColumnArea)tableColumnArea;
            ColumnWidth columnWidth = currentTableColumnArea.width;
            if (!columnWidth.hasValue()) {
                // Задаем фиксированную нулевую ширину, чтобы нельзя было перераспределить в ходе дальнейших
                // перетурбаций
                currentTableColumnArea.getBorderRectangle().setFixedWidth(0);
            } else if (columnWidth.isProportional()) {
                proportionalSum += columnWidth.getValue();
            } else if (columnWidth.isPercentage()) {
                percentsSum += columnWidth.getValue();
            } else {
                // Если абсолютное значение, то присваиваем значение в пикселах (причем фиксируем ширину столбца)
                currentTableColumnArea.getBorderRectangle().setFixedWidth((int)columnWidth.getValue());
                sumInPoints += currentTableColumnArea.getBorderRectangle().getWidth();
            }
        }
        // Считаем сколько процентов надо отдать пропорциональным столбцам.. Если сумма столбцов с процентами < 100,
        // то, соответственно, остальное пропорциональным столбцам.. иначе (дебильный случай) берем
        // значение по умолчанию
        if (proportionalSum > 0f) {
            proportionalPercents = (percentsSum < prc100)
                    ? prc100 - percentsSum
                    : GlobalData.PROPORTIONAL_COLUMN_TABLE_WIDTH_PERCENT;
        }

        // Добавляем в общие проценты проценты от пропорциональных столбцов
        percentsSum += proportionalPercents;

        // Если у нас только фиксированные столбцы, то их ширины нельзя пересчитать
        if (percentsSum == 0f) {
            tableArea.getBorderRectangle().setFixedWidth(sumInPoints);
            return;
        }

        // Если общая ширина таблицы неизвестна, то мы не можем рассчитать ширины пропорциональных и процентных колонок
        if (!tableArea.getBorderRectangle().isWidthDefined()) {
            return;
        }

        // Вычисляем размер, который мы распределяем между процентными и пропорциональными столбцами
        int widthGap = tableArea.getBorderRectangle().getWidth() - sumInPoints;

        // Обрабатываем ситуацию, когда сумма значений ширин фиксированных колонок превысила ширину таблицы
        if (widthGap <= 0) {
            // Резервируем для рассчитываемых колонок место, равное проценту от ширины таблицы, заданному константой
            widthGap = Float.valueOf(tableArea.getBorderRectangle().getWidth()
                    * GlobalData.PROPORTIONAL_COLUMN_TABLE_WIDTH_PERCENT / prc100).intValue();
            // Устанавливаем новую ширину таблицы
            tableArea.getBorderRectangle().setWidth(sumInPoints + widthGap);
        }

        // Размер, распределяемый между пропорциональными столбцами
        float proportionalsWidth = widthGap * proportionalPercents / percentsSum;

        int sum = 0;
        //Рассчитываем ширину пропорциональных и процентных столбцов
        for (IArea tableColumnArea : tableArea.getColumnList()) {
            TableColumnArea currentTableColumnArea = (TableColumnArea)tableColumnArea;
            ColumnWidth columnWidth = currentTableColumnArea.width;
            if (!columnWidth.hasValue()) {
                continue;
            }
            if (columnWidth.isProportional()) {
                currentTableColumnArea.getBorderRectangle().setWidth((int)Math.floor(columnWidth.getValue()
                        * proportionalsWidth / proportionalSum));
            } else if (columnWidth.isPercentage()) {
                currentTableColumnArea.getBorderRectangle().setWidth((int)Math.floor(columnWidth.getValue()
                        * widthGap / percentsSum));
            }
            sum += currentTableColumnArea.getBorderRectangle().getWidth();
        }

        // Т.к. использовались операции деления и округления -
        // общая ширина колонок может не совпадать с шириной таблицы,
        // перераспределяем ширины колонок
        if (sum < tableArea.getBorderRectangle().getWidth()) {
            distributeWidthGap(tableArea, tableArea.getColumnList());
        }
    }

    /**
     * Присваивает новую ширину области, если она больше текущей.
     * @param area  - Область
     * @param width - Ширина, до которой увеличиваем ширину области
     */
    private static void enlargeAreaWidth(IArea area, int width) {

        AreaRectangle rectangle = area.getBorderRectangle();
        if (rectangle.getWidth() < width) {
            if (rectangle.getMaxWidth() < width) {
                rectangle.setMaxWidth(width);
            }
            rectangle.setWidth(width);
        }
    }

    /**
     * Присваивает новую высоту области, если она больше текущей.
     * @param area   - Область
     * @param height - Высота, до которой увеличиваем высоту области
     */
    private static void enlargeAreaHeight(IArea area, int height) {

        AreaRectangle rectangle = area.getBorderRectangle();
        if (rectangle.getHeight() < height) {
            if (rectangle.getMaxHeight() < height) {
                rectangle.setMaxHeight(height);
            }
            rectangle.setHeight(height);
        }
    }

    /**
     * Вспомогательный метод - нахождение максимальной ширины из списка дочерних областей,
     * и выставление ширины родительской области.
     * @param parentArea - Родительская область
     */
    private static void setAreaMaximumWidth(IArea parentArea) {

        if (!parentArea.isHasChildren()) {
            return;
        }
        int maxWidth = 0;
        for (IArea childArea : parentArea.getChildrenList()) {
            if (childArea.isColumnLayoutSpan()) {
                continue;
            }
            int width = childArea.getBorderRectangle().getWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        enlargeAreaWidth(parentArea, maxWidth);
    }

    /**
     * Вспомогательный метод - нахождение максимальной высоты из списка дочерних областей,
     * и выставление высоты родительской области.
     * @param parentArea - Родительская область
     */
    private static void setAreaMaximumHeight(IArea parentArea) {

        if (!parentArea.isHasChildren()) {
            return;
        }
        int maxHeight = 0;
        for (IArea childArea : parentArea.getChildrenList()) {
            if (childArea.isColumnLayoutSpan()) {
                continue;
            }
            int height = childArea.getBorderRectangle().getHeight();
            if (height > maxHeight) {
                maxHeight = height;
            }
        }
        enlargeAreaHeight(parentArea, maxHeight);
    }

    /**
     * Вспомогательный метод - вычисление ширины области как суммы ширин дочерних областей.
     * @param areaList - Список областей
     * @return int  - возвращает ширину области как сумму ширин дочерних областей
     */
    private static int getChildAreasTotalWidth(List<IArea> areaList) {

        int totalWidth = 0;
        for (IArea childArea : areaList) {
            if (!childArea.isColumnLayoutSpan()) {
                totalWidth += childArea.getBorderRectangle().getWidth();
            }
        }

        return totalWidth;
    }

    /**
     * Вспомогательный метод - вычисление высоты области как суммы высот дочерних областей.
     * @param areaList - Список областей
     * @return int  - возвращает высоту области как сумму высот дочерних областей
     */
    private static int getChildAreasTotalHeight(List<IArea> areaList) {

        int totalHeight = 0;
        for (IArea childArea : areaList) {
            if (!childArea.isColumnLayoutSpan()) {
                totalHeight += childArea.getBorderRectangle().getHeight();
            }
        }

        return totalHeight;
    }

    /**
     * Подбор и расчет ширины таблицы (а также колонок) на основании ширин отдельных ячеек.
     * @param tableArea - Область-Таблица
     */
    private static void calculateTableWidthFromCells(TableArea tableArea) {

        final int columnCount = tableArea.getColumnList().size();
        boolean isResizable = false;
        for (IArea column : tableArea.getColumnList()) {
            TableColumnArea currentTableColumnArea = (TableColumnArea)column;
            isResizable = currentTableColumnArea.getSizeCanBeRedistributed();
            if (isResizable) {
                break;
            }
        }
        // Если все колонки имеют жестко заданную ширину, то ловить тут нечего
        if (!isResizable) {
            return;
        }

        int rowCount = 0;
        for (IArea tablePart : tableArea.getChildrenList()) {
            rowCount += tablePart.getChildrenList().size();
        }
        int[][] savedWidths = new int[rowCount][2];
        int prevColumnWidth = 0;

        for (int col = 0; col < columnCount; col++) {
            int startRow = 0;
            int maxWidth = 0;
            int minColSpanned = Integer.MAX_VALUE;
            for (IArea tablePart : tableArea.getChildrenList()) {
                CellGridContainer currentCellGridContainer = (CellGridContainer) tablePart;
                for (int row = 0; row < currentCellGridContainer.getChildrenList().size(); row++) {
                    int colSpanned;
                    int width;
                    if (savedWidths[startRow + row][0] > GlobalData.DEFAULT_COLUMNS_SPAN_VALUE) {
                        // Уменьшаем на единицу кол-во объединяемых столбцов
                        savedWidths[startRow + row][0]--;
                        // Вычитаем из ширины ячейки ширину предыдущего столбца
                        savedWidths[startRow + row][1] -= prevColumnWidth;
                        // Присвоим кол-во объединяемых столбцов
                        colSpanned = savedWidths[startRow + row][0];
                        // Присвоим ширину
                        width = savedWidths[startRow + row][1];
                    } else {
                        TableCellArea cell = currentCellGridContainer.cellAt(row, col);
                        colSpanned = cell.getNumberColumnsSpanned();
                        width = cell.getBorderRectangle().getWidth();
                        savedWidths[startRow + row][0] = colSpanned;
                        savedWidths[startRow + row][1] = width;
                    }
                    // Если данная ячейка объединяет меньше столбцов и ширина задана
                    if (colSpanned < minColSpanned && width > 0) {
                        minColSpanned = colSpanned;
                        maxWidth = width;
                    } else if (colSpanned == minColSpanned && width > maxWidth) {
                        // Если данная ячейка объединяет столько же столбцов, но она шире!
                        maxWidth = width;
                    }
                }
                startRow += currentCellGridContainer.getChildrenList().size();
            }

            // Если найденный минимум содержит сумму нескольких столбцов, то вычитаем текущую ширину следующих столбцов
            if (minColSpanned < Integer.MAX_VALUE) {
                while (--minColSpanned > 0) {
                    maxWidth -= tableArea.getColumnList().get(col + minColSpanned).getBorderRectangle().getWidth();
                }
            }

            // Найденную ширину присваиваем столбцу, если она больше заданной, и столбцу не задана ширина 0
            // (столбец скрыт)
            AreaRectangle columnRect = tableArea.getColumnList().get(col).getBorderRectangle();
            if (columnRect.getWidth() < maxWidth && columnRect.getWidth() != 0) {
                columnRect.setWidth(maxWidth);
            }
            prevColumnWidth = columnRect.getWidth();
        }

        // Найдем сумму всех колонок таблицы и присвоим ее таблице, если она больше установленной ширины таблицы
        enlargeAreaWidth(tableArea, getChildAreasTotalWidth(tableArea.getColumnList()));
        // Отдельным секциям и строкам присваивать не будем.. это будет проделано на следующем этапе
        // при присвоении дочерним областям размеров родительских
    }

    /**
     * Подбор и установка высоты таблицы (а также секций и строк) на основании высот отдельных ячеек.
     * @param tableArea - Область-Таблица
     */
    private static void calculateTableHeightFromCells(TableArea tableArea) {

        final int columnCount = tableArea.getColumnList().size();

        for (IArea tablePart : tableArea.getChildrenList()) {
            CellGridContainer currentCellGridContainer = (CellGridContainer) tablePart;
            int[][] savedHeights = new int[columnCount][2];
            int prevRowHeight = 0;
            for (int row = 0; row < currentCellGridContainer.getChildrenList().size(); row++) {
                int maxHeight = 0;
                int minRowSpanned = Integer.MAX_VALUE;
                for (int col = 0; col < columnCount; col++) {
                    int rowSpanned;
                    int height;
                    if (savedHeights[col][0] > GlobalData.DEFAULT_ROWS_SPAN_VALUE) {
                        // Уменьшаем на единицу кол-во объединяемых строк
                        savedHeights[col][0]--;
                        // Вычитаем из высоты ячейки высоту предыдущей строки
                        savedHeights[col][1] -= prevRowHeight;
                        // Присвоим кол-во объединяемых строк
                        rowSpanned = savedHeights[col][0];
                        // Присвоим высоту
                        height = savedHeights[col][1];
                    } else {
                        TableCellArea cell = currentCellGridContainer.cellAt(row, col);
                        rowSpanned = cell.getNumberRowsSpanned();
                        height = cell.getBorderRectangle().getHeight();
                        savedHeights[col][0] = rowSpanned;
                        savedHeights[col][1] = height;
                    }
                    // Если данная ячейка объединяет меньше строк и высота задана
                    if (rowSpanned < minRowSpanned && height > 0) {
                        minRowSpanned = rowSpanned;
                        maxHeight = height;
                    } else if (rowSpanned == minRowSpanned && height > maxHeight) {
                        // Если данная ячейка объединяет столько же строк, но она длинее!
                        maxHeight = height;
                    }
                }

                // Если найденный минимум содержит сумму нескольких строк, то вычитаем текущую высоту следующих строк
                if (minRowSpanned < Integer.MAX_VALUE) {
                    while (--minRowSpanned > 0) {
                        maxHeight -= currentCellGridContainer.getChildrenList().get(row + minRowSpanned)
                                .getBorderRectangle().getHeight();
                    }
                }

                // Найденную высоту присваиваем строке, если она больше заданной, и высота строки не равна 0
                // (строка скрыта)
                AreaRectangle rowRect = currentCellGridContainer.getChildrenList().get(row).getBorderRectangle();
                if (rowRect.getHeight() < maxHeight && rowRect.getHeight() != 0) {
                    rowRect.setHeight(maxHeight);
                }
                prevRowHeight = rowRect.getHeight();
            }
            // Расчет высоты секции таблицы как сумма высот строк
            enlargeAreaHeight(currentCellGridContainer,
                    getChildAreasTotalHeight(currentCellGridContainer.getChildrenList()));
        }
        // Расчет высоты таблицы как сумма секций таблицы
        enlargeAreaHeight(tableArea, getChildAreasTotalHeight(tableArea.getChildrenList()));
    }

    /**
     * Метод для расчета расположения областей.
     * (координаты начала области по ширине и высоте - X и Y)
     * При вызове метода для родительской области координаты должны быть заданы.
     * @param parentArea - Родительская область
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    protected void calculateAreaCoordinates(IArea parentArea) throws XslFoException {
        doCalculateAreaCoordinates(parentArea);
    }

    /**
     * Рекурсивный метод для расчета расположения областей.
     * (координаты начала области по ширине и высоте - X и Y)
     * При вызове метода для родительской области координаты должны быть заданы.
     * @param parentArea - Родительская область
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    private static void doCalculateAreaCoordinates(IArea parentArea) throws XslFoException {

        if (!parentArea.isHasChildren()) {
            return;
        }

        int coordX = parentArea.getBorderRectangle().getX();     // Текущая координата X
        int coordY = parentArea.getBorderRectangle().getY();     // Текущая координата Y
        AreaType enAreaType = parentArea.getAreaType();
        List<IArea> tableColumns = enAreaType == AreaType.TABLE_ROW
                ? ((TableRowArea)parentArea).getGridContainer().getParentTableArea().getColumnList()
                : null;
        int prevColumn = 0;

        // Проходим по всем дочерним областям
        for (IArea childArea : parentArea.getChildrenList()) {
            if (!childArea.getBorderRectangle().isWidthDefined() || !childArea.getBorderRectangle().isHeightDefined()) {
                throwUndefinedSizeException(childArea);
            }

            // Выставляем координаты текущей области
            if (childArea.isColumnLayoutSpan()) {
                childArea.getBorderRectangle().setX(0);
            } else {
                // Из-за column-span ячейки таблицы могут идти с разрывами, нужно добавить соотв. пропуск
                if (enAreaType == AreaType.TABLE_ROW) {
                    int column = ((TableCellArea)childArea).getColumn();
                    for (int i = prevColumn; i < column; i++) {
                        coordX += tableColumns.get(i).getBorderRectangle().getWidth();
                    }
                    prevColumn = column;
                }
                childArea.getBorderRectangle().setX(coordX);
            }

            childArea.getBorderRectangle().setY(coordY);

            // Рекурсивно выставляем координаты дочерних областей
            doCalculateAreaCoordinates(childArea);

            switch (parentArea.getProgressionDirection()) {
                case BLOCK:
                    coordY += childArea.getBorderRectangle().getHeight();
                    break;
                case ROW:
                    if (enAreaType != AreaType.TABLE_ROW) {
                        coordX += childArea.getBorderRectangle().getWidth();
                    }
                    break;

                default:
                    // в оригинале ничего не было
            }
        }
    }

    /**
     * Выброс исключения о том, что не все размеры области были заданы.
     * @param area - Область
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    protected static void throwUndefinedSizeException(IArea area) throws XslFoException {
        // Если не заданы ширина или высота дочерней области - прекращаем обработку -
        // размеры должны были быть заданы на предыдущих шагах
        throw new XslFoException("Для области <"
                + area.getAreaType().getFoName()
                + "> не были определены размеры! Дальнейшая обработка невозможна!",
                "GenericLayoutManager.throwUndefinedSizeException");
    }

    /**
     * Расчет областей ячеек, занимаемых областями на листе Excel.
     * Используем информацию о расположении прямоугольников и их размеров для расчета областей ячеек
     * расположения областей на листе Excel - отображение прямоугольников на лист Excel.
     * @param rootArea            - Корневая область
     * @param horizontalCoordList - Массив координат по горизонтали
     * @param verticalCoordList   - Массив координат по вертикали
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    private void calculateAreaCellRange(RootArea rootArea, IntArray horizontalCoordList, IntArray verticalCoordList)
            throws XslFoException {

        if (rootArea == null) {
            throw new XslFoException("Корневая область не задана!", "GenericLayoutManager.calculateAreaCellRange");
        }

        // Заполняем массивы координат по горизонтали и вертикали,
        // т.о. логически разбиваем плоскость горизонтальными и вертикальными линиями.
        // В результате получаем сетку, которая ложится на лист Excel.
        startBuildCoordinatesArrays(rootArea, horizontalCoordList, verticalCoordList);

        // Рассчитываем координаты областей ячеек и их размеры для каждой области
        // и заносим эту информацию в соответствующие поля каждой области
        setAreaCellRange(rootArea, horizontalCoordList, verticalCoordList);
    }

    /**
     * Вспомогательный рекурсивный метод для обхода дерева и заполнения массивов координат по горизонтали и вертикали.
     * @param area                - Текущая область
     * @param horizontalCoordList - Массив координат по горизонтали
     * @param verticalCoordList   - Массив координат по вертикали
     */
    private static void setAreaCellRange(IArea area, IntArray horizontalCoordList, IntArray verticalCoordList) {

        // Прямоугольник области
        AreaRectangle areaRect = area.getBorderRectangle();

        // Индекс элемента координаты X начала области в массиве координат по горизонтали
        int indexBeginX = horizontalCoordList.indexOfByBinarySearch(areaRect.getX());

        // Индекс элемента координаты Y начала области в массиве координат по вертикали
        int indexBeginY = verticalCoordList.indexOfByBinarySearch(areaRect.getY());

        // Индекс элемента координаты X конца области в массиве координат по горизонтали
        int indexEndX = horizontalCoordList.indexOfByBinarySearch(areaRect.getX() + areaRect.getWidth()) - 1;

        // Индекс элемента координаты Y конца области в массиве координат по вертикали
        int indexEndY = verticalCoordList.indexOfByBinarySearch(areaRect.getY() + areaRect.getHeight()) - 1;

        // Обрабатываем координаты родительской области
        if (indexBeginX > -1 && indexBeginY > -1 && indexEndX > -1 && indexEndY > -1) {
            //Заносим координаты областей и их размеры
            area.getBorderRange().setX(indexBeginX);
            area.getBorderRange().setY(indexBeginY);
            area.getBorderRange().setWidth(indexEndX - indexBeginX + 1);
            area.getBorderRange().setHeight(indexEndY - indexBeginY + 1);
        } else {
            if (area.isNeedRendering()) {
                // Область нулевого размера. Выбросим ее из списка на рендеринг
                area.setPropertyValue(FoPropertyType.VISIBILITY, false);
                return;
            }
        }

        if (!area.isHasChildren() || area.getProgressionDirection() == AreaProgressionDirection.INLINE) {
            return;
        }

        // Обрабатываем дочерние области
        for (IArea childArea : area.getChildrenList()) {
            setAreaCellRange(childArea, horizontalCoordList, verticalCoordList);
        }
        // Удаляем объекты, они больше не нужны
        area.setBorderRectangle(null);
    }

    /**
     * Заполняем массивы координат по горизонтали и вертикали,
     * т.о. логически разбиваем плоскость горизонтальными и вертикальными линиями.
     * В результате получаем сетку, которая ложится на лист Excel.
     * @param rootArea         - Корневая область
     * @param horizontalCoords - Массив координат по горизонтали
     * @param verticalCoords   - Массив координат по вертикали
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    private void startBuildCoordinatesArrays(RootArea rootArea, IntArray horizontalCoords, IntArray verticalCoords)
            throws XslFoException {

        // Вспомогательные списки координат
        List<Integer> horizontalCoordList = new ArrayList<>();
        List<Integer> verticalCoordList = new ArrayList<>();

        // Обходим все дочерние области
        buildCoordinatesArrays(rootArea, horizontalCoordList, verticalCoordList);

        horizontalCoords.setValues(horizontalCoordList);
        verticalCoords.setValues(verticalCoordList);

        // Оставляем в массивах только уникальные значения
        horizontalCoords.makeDistinctValues();
        verticalCoords.makeDistinctValues();
    }

    /**
     * Вспомогательный рекурсивный метод для обхода дерева и заполнения массивов координат по горизонтали и вертикали.
     * @param area                - Текущая область
     * @param horizontalCoordList - Массив координат по горизонтали
     * @param verticalCoordList   - Массив координат по вертикали
     * @throws XslFoException - выбрасывает исключения о том, что не все размеры области были заданы
     */
    private static void buildCoordinatesArrays(IArea area, List<Integer> horizontalCoordList,
                                               List<Integer> verticalCoordList) throws XslFoException {

        // Прямоугольник области
        AreaRectangle areaRect = area.getBorderRectangle();

        // Ряды таблиц не обрабатываем - т.к. возможны различные комбинации при
        // наличии span ячеек рядов(контейнера)
        if (area.isNeedRendering()) {
            if (areaRect.getX() != -1 && areaRect.getY() != -1
                    && areaRect.isWidthDefined() && areaRect.isHeightDefined()) {

                // Обрабатываем координаты текущей области
                horizontalCoordList.add(areaRect.getX());

                verticalCoordList.add(areaRect.getY());

                horizontalCoordList.add(areaRect.getX() + areaRect.getWidth());

                verticalCoordList.add(areaRect.getY() + areaRect.getHeight());
            } else {
                throw new XslFoException("Для области <"
                        + area.getAreaType().getFoName()
                        + "> не были определены координаты областей! Дальнейшая обработка невозможна!",
                        "GenericLayoutManager.buildCoordinatesArrays");
            }
        }

        if (!area.isHasChildren() || area.getProgressionDirection() == AreaProgressionDirection.INLINE) {
            return;
        }

        // Обрабатываем все дочерние области
        for (IArea childArea : area.getChildrenList()) {
            buildCoordinatesArrays(childArea, horizontalCoordList, verticalCoordList);
        }
    }
    
    /**
     * Логирование записи c форматом '[ВРЕМЯ]   :ЗНАЧЕНИЕ'.
     *
     * @param value - логируемая запись
     */
    private void logWithTimestamp(String value) {
        value = AreaLogHelper.getValueWithTimestampForLog(value);
        logger.debug(value);
    }
}
