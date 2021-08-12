package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.rendering.excel;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REFERENCE_ORIENTATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.TEXT_INDENT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.WRAP_OPTION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_REFERENCE_ORIENTATION;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.Border;
import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellsHelper;
import com.aspose.cells.Color;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.PageOrientationType;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Picture;
import com.aspose.cells.Range;
import com.aspose.cells.Style;
import com.aspose.cells.StyleFlag;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.ValueWithFlagResult;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.collection.IntArray;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.focolor.ExcelColors;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.foimage.FoImage;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.utility.Utils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.PageSequenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.border.FoBorder;

import java.io.InputStream;

/**
 * Класс, отображения заданной области в Excel Range.
 * Created by vsavenkov on 22.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("deprecation")
public class ExcelRenderer {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(ExcelRenderer.class);

    /**
     * Объект для отображения INLINE областей в рамках BLOCK области.
     */
    private InlineAreasExcelRenderer inlineAreasExcelRenderer;

    /**
     * Объект Excel.
     */
    protected final Workbook excel;

    /**
     * Текущая палитра Excel.
     */
    private final ExcelColors colors;

    protected ExcelColors getColors() {
        return colors;
    }

    /**
     * Конструктор класса.
     * @param excel  - Объект Excel
     * @param colors - Объект текущей политры
     */
    public ExcelRenderer(Workbook excel, ExcelColors colors) {

        this.excel = excel;
        this.colors = colors;
    }

    /**
     * Установка значения отступов для страницы и ориентации листа.
     * @param simplePageMasterArea - Область SimplePageMasterArea
     * @param worksheet            - Соответствующий лист Excel
     */
    public void setPageSetup(SimplePageMasterArea simplePageMasterArea, Worksheet worksheet) {

        // Устанавливаем отступы для страницы
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setHeaderMargin(0);
        pageSetup.setFooterMargin(0);
        pageSetup.setLeftMargin(Utils.pixelsToCm(simplePageMasterArea.getMarginLeft()));
        pageSetup.setRightMargin(Utils.pixelsToCm(simplePageMasterArea.getMarginRight()));

        pageSetup.setTopMargin(Utils.pixelsToCm(simplePageMasterArea.getMarginTop()));

        pageSetup.setBottomMargin(Utils.pixelsToCm(simplePageMasterArea.getMarginBottom()));

        // Устанавливаем ориентацию листа
        pageSetup.setOrientation((simplePageMasterArea.getReferenceOrientation() == 0)
                ? PageOrientationType.PORTRAIT : PageOrientationType.LANDSCAPE);
    }

    /**
     * Установка колонтитулов.
     * @param pageSequenceArea - Область PageSequence
     * @param worksheet        - Соответствующий лист Excel
     */
    public void setCatchword(PageSequenceArea pageSequenceArea, Worksheet worksheet) {

        PageSetup pageSetup = worksheet.getPageSetup();

        // Устанавливаем значение первого листа
        if (pageSequenceArea.isInitialPageNumberDefined()) {
            pageSetup.setFirstPageNumber(pageSequenceArea.getInitialPageNumber());
        }

        // Если нижний колонтитул определен
        if (pageSequenceArea.isFooterDefined()) {
            // Устанавливаем все что заданы
            for (int section = 0; section < pageSequenceArea.getFooter().length; section++) {
                pageSetup.setFooter(section, pageSequenceArea.getFooter()[section]);
            }
        }
    }

    /**
     * Создание диапазона ячеек Excel, если необходимо и он еще не создан.
     * @param worksheet  - Объект листа Excel
     * @param area       - Область
     * @param excelRange - Диапазон ячеек
     * @return {@link ValueWithFlagResult}  - возвращает c признаком false, если ширина и высота области == 1
     * или c признаком true в противном случае
     */
    protected static ValueWithFlagResult<Range> setRangeIfNeed(
            Worksheet worksheet,
            IArea area,
            Range excelRange
    ) {

        ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range borderRange = area.getBorderRange();
        if (borderRange.getWidth() == 1 && borderRange.getHeight() == 1) {
            return new ValueWithFlagResult<>(false, excelRange);
        }
        // Создаем диапазон ячеек, если еще не создан
        if (excelRange == null) {
            excelRange = worksheet.getCells().createRange(borderRange.getY(), area.getBorderRange().getX(),
                    borderRange.getHeight(), borderRange.getWidth());
        }
        return new ValueWithFlagResult<>(true, excelRange);
    }

    /**
     * Вывод областей на лист Excel.
     * @param worksheet              - Объект листа Excel
     * @param parentArea             - Родительская область
     * @param arrHorizontalCoordList - Массив координат по горизонтали
     * @param arrVerticalCoordList   - Массив координат по вертикали
     * @throws XslFoException генерирует в случае ограничений Excel
     */
    public void renderToExcel(Worksheet worksheet, IArea parentArea,
                              IntArray arrHorizontalCoordList, IntArray arrVerticalCoordList) throws XslFoException {

        inlineAreasExcelRenderer = new InlineAreasExcelRenderer(this, worksheet);
        Range excelRange = null;
        // Подготавливаем лист Excel
        prepareExcelWorksheet(worksheet, arrHorizontalCoordList, arrVerticalCoordList);

        // Выводим дочерние области
        for (IArea childArea : parentArea.getChildrenList()) {
            excelRange = renderAreaToCells(worksheet, childArea, excelRange).getValue();
        }
    }

    /**
     * Подготовка листа Excel.
     * Устанавливаем ширины вычисленных колонок и высоты рядов
     * @param worksheet              - Объект листа Excel
     * @param arrHorizontalCoordList - Массив координат по горизонтали
     * @param arrVerticalCoordList   - Массив координат по вертикали
     * @throws XslFoException генерирует в случае ограничений Excel
     */
    private static void prepareExcelWorksheet(Worksheet worksheet, IntArray arrHorizontalCoordList,
                                              IntArray arrVerticalCoordList) throws XslFoException {

        int temp;

        if (worksheet.getWorkbook().getFileFormat() == FileFormatType.EXCEL_97_TO_2003) {
            if (arrHorizontalCoordList.getSize() > GlobalData.Excel2003Limits.MAX_EXCEL_COLUMNS_COUNT
                    || arrVerticalCoordList.getSize() > GlobalData.Excel2003Limits.MAX_EXCEL_ROWS_COUNT) {
                throw new XslFoException(String.format(GlobalData.getCultureInfo(),
                        "Ограничение Excel - невозможно вывести: Рядов - %1$d Колонок - %2$d",
                        arrVerticalCoordList.getSize(), arrHorizontalCoordList.getSize()),
                        "ExcelRenderer.prepareExcelWorksheet");
            }
        }

        // Создаем область вывода
        // Устанавливаем ширину колонок
        for (byte i = 0; i < arrHorizontalCoordList.getSize() - 1; i++) {
            temp = arrHorizontalCoordList.getValue(i + 1) - arrHorizontalCoordList.getValue(i);

            // Получаем прецизионно точное значение ширины колонки Excel, т.к.
            // не всегда точно устанавливается ширина
            worksheet.getCells().setColumnWidthPixel(i, GlobalData.getPrecizeExcelColumnWidth(temp));
        }

        // Устанавливаем высоту рядов
        for (int i = 0; i < arrVerticalCoordList.getSize() - 1; i++) {
            temp = arrVerticalCoordList.getValue(i + 1) - arrVerticalCoordList.getValue(i);

            // Проверка на максимальную высоту ряда в Excel - если есть превышение, устанавливаем максимальную
            if (temp > GlobalData.MAX_EXCEL_ROW_HEIGHT) {
                temp = GlobalData.MAX_EXCEL_ROW_HEIGHT;
            }
            worksheet.getCells().setRowHeightPixel(i, GlobalData.getPrecizeExcelColumnHeight(temp));
        }
    }

    /**
     * Функция отображает заданную область в ячейки листа Excel.
     * @param worksheet        - Объект листа Excel
     * @param area             - Текущая область
     * @param parentExcelRange - Диапазон ячеек Excel родительской области
     * @return ValueWithFlagResult&lt;Range&gt;  - возвращаем признак того, что размер области изменялся.
     *                      Однажды установленный, этот признак вверх по иерархии остается установленным
     * @throws XslFoException генерирует в случае нарушения структуры
     */
    private ValueWithFlagResult<Range> renderAreaToCells(Worksheet worksheet, IArea area,
                                                         Range parentExcelRange) throws XslFoException {

        // Признак того, что у детей область не совпадает с текущей
        boolean childrenRectsChanged = false;
        ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range borderRange = area.getBorderRange();
        // Проверяем нужно ли рендерить эту область
        if (Boolean.valueOf(false).equals(area.getPropertyValue(FoPropertyType.VISIBILITY))) {
            return new ValueWithFlagResult<>(false, parentExcelRange);
        }

        if (borderRange.getWidth() == 0 || borderRange.getHeight() == 0) {
            // Вроде бы нонсенс, что пустая область может иметь непустых детей. Тем не менее, встречается
            // Чтобы не переписывать еще и расчет размеров областей, сделано вот такое безобразие.
            if (area.isHasChildren()) {
                // Рекурсивно вызываем эту же ф-ю для дочерних областей
                for (IArea childArea : area.getChildrenList()) {
                    ValueWithFlagResult<Range> result = renderAreaToCells(worksheet, childArea,
                            parentExcelRange);
                    parentExcelRange = result.getValue();
                    childrenRectsChanged = result.isFlag() | childrenRectsChanged;
                }
            }
            return new ValueWithFlagResult<>(childrenRectsChanged, parentExcelRange);
        }

        // Признак того, что область не совпадает с родительской
        boolean isRectChanged = parentExcelRange == null
                || !borderRange.isEquals(area.getParentArea().getBorderRange());
        // Стиль области: наследуется родительский при совпадении размеров
        Style areaStyle = isRectChanged ? GlobalData.defaultExcelStyle(excel.getStyles().get(0))
                : excel.getStyles().get(0);
        // получу и проставлю признак разбиения на строки
        boolean canwrap = (boolean)area.getInheritedPropertyValue(WRAP_OPTION);
        areaStyle.setTextWrapped(canwrap);
        Dimension textIndentDimension = (Dimension)area.getPropertyValue(TEXT_INDENT);
        int textIndent = 0;
        if (!textIndentDimension.isPercentage() && textIndentDimension.hasValue()) {
            textIndent = Float.valueOf(textIndentDimension.getValue()).intValue();
        }
        if (0 != textIndent) {
            areaStyle.setIndentLevel(textIndent);
        }
        Integer rotation = (Integer)area.getInheritedPropertyValue(REFERENCE_ORIENTATION);
        if (DEFAULT_REFERENCE_ORIENTATION != rotation) {
            areaStyle.setRotationAngle(rotation);
        }
        // Диапазон ячеек области: наследуется родительский при совпадении размеров
        Range excelRange = isRectChanged ? null : parentExcelRange;

        // Выводим INLINE-области в рамках BLOCK-области
        if (area.getProgressionDirection() == AreaProgressionDirection.INLINE) {
            excelRange = setRangeIfNeed(worksheet, area, excelRange).getValue();
            // Объединяем ячейки Excel
            if (excelRange != null) {
                excelRange.merge();
            }

            // Обработка ссылок
            inlineAreasExcelRenderer.renderDestination(area);
            // Рендеринг текста
            inlineAreasExcelRenderer.execute(area, areaStyle);
        } else {
            // Обрабатывем ref-id
            inlineAreasExcelRenderer.processRefId(area);

            // Устанавливаем цвет фона
            setBackgroundColorRange(worksheet, area);

            if (!area.isHasChildren()) {
                childrenRectsChanged = true;
            } else {
                // Рекурсивно вызываем эту же ф-ю для дочерних областей
                for (IArea childArea : area.getChildrenList()) {
                    ValueWithFlagResult<Range> result = renderAreaToCells(worksheet, childArea,
                            excelRange);
                    excelRange = result.getValue();
                    childrenRectsChanged = result.isFlag() | childrenRectsChanged;
                }

                // Вывод границ области в стиль, если дети занимают такой же диапазон ячеек
                if (!childrenRectsChanged) {
                    renderBorderToStyle(area, areaStyle);
                }
            }
        }

        // Если у родителя другой диапазон ячеек, а у детей диапазон совпадает с текущим, пора сохранять стиль!
        if (isRectChanged && !childrenRectsChanged) {
            ValueWithFlagResult<Range> result = setRangeIfNeed(worksheet, area, excelRange);
            excelRange = result.getValue();
            if (result.isFlag()) {
                StyleFlag styleFlag = new StyleFlag();
                styleFlag.setAll(true);
                excelRange.applyStyle(areaStyle, styleFlag);
            } else {
                worksheet.getCells().get(borderRange.getY(), borderRange.getX()).setStyle(areaStyle, true);
            }
        }

        if (area.isNeedRendering()) {
            if (childrenRectsChanged) {
                excelRange = renderBorder(worksheet, area, excelRange);
            }

            // Выводим фоновую картинку
            renderBackgroundImage(worksheet, area);

            // Выводим картинку
            renderImage(worksheet, area);

            // Собираем мусор для области TABLE_BODY
            clearTableBody(area);

            // Установка PageBreak после каждого page-sequence
            setPageBreak(worksheet, area, borderRange);

            // Устанавливаем верхний колонтитул
            setUpperRegionStaticContent(worksheet, area);
        }

        if (parentExcelRange == null && excelRange != null
                && borderRange.isEquals(area.getParentArea().getBorderRange())) {
            parentExcelRange = excelRange;
        }

        // Возвращаем признак того, что размер области изменялся. Однажды установленный, этот признак вверх по иерархии
        // остается установленным
        return new ValueWithFlagResult<>(isRectChanged | childrenRectsChanged, parentExcelRange);
    }

    /**
     * Установка цвета фона.
     * @param worksheet - Объект листа Excel
     * @param area      - Область
     */
    private void setBackgroundColorRange(Worksheet worksheet, IArea area) {

        Object backgroundColor = area.getPropertyValue(FoPropertyType.BACKGROUND_COLOR);
        if (backgroundColor == null) {
            return;
        }
        // Выбираем подходящий стиль
        java.awt.Color color = colors.getColor((java.awt.Color)backgroundColor);
        ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range borderRange = area.getBorderRange();
        for (int y = borderRange.getY(), height = borderRange.getHeight(); height > 0; y++, height--) {
            for (int x = borderRange.getX(), width = borderRange.getWidth(); width > 0; x++, width--) {
                Cell cell = worksheet.getCells().get(y, x);
                Style style = cell.getStyle();
                style.setForegroundColor(Color.fromArgb(color.getRGB()));
                style.setPattern(BackgroundType.SOLID);
                cell.setStyle(style, false);
            }
        }
    }

    /**
     * Отображение фоновой картинки.
     * @param worksheet - Объект листа Excel
     * @param area      - Область
     */
    private static void renderBackgroundImage(Worksheet worksheet, IArea area) {

        FoImage foImage = (FoImage)area.getPropertyValue(FoPropertyType.BACKGROUND_IMAGE);
        if (foImage != null) {
            worksheet.getPictures().add(area.getBorderRange().getY(),
                    area.getBorderRange().getX(),
                    area.getBorderRange().getY() + area.getBorderRange().getHeight(),
                    area.getBorderRange().getX() + area.getBorderRange().getWidth(),
                    (InputStream) foImage.getStream());
        }
    }

    /**
     * Отображение картинки.
     * @param worksheet - Объект листа Excel
     * @param area      - Область
     */
    private static void renderImage(Worksheet worksheet, IArea area) {

        FoImage foImage = (FoImage)area.getPropertyValue(FoPropertyType.EXTERNAL_GRAPHIC);
        if (foImage != null) {
            // Значение коэффициента масштабирования по умолчанию
            float scalingWidth = 100F;
            float scalingHeight = 100F;
            // Определяем значение масштабирования
            String scaling = (String)area.getPropertyValue(FoPropertyType.SCALING);
            if (scaling != null) {
                float picWidth = foImage.getWidth();
                float picHeight = foImage.getHeight();
                float areaWidth = GlobalData.getPrecizeExcelColumnWidth(area.getBorderRectangle().getWidth());
                float areaHeight = GlobalData.getPrecizeExcelColumnHeight(area.getBorderRectangle().getHeight());

                float diffHeight;
                float diffWidth;

                // Значение данного атрибута означает, что картинку нужно изменять пропорционально
                if (scaling == GlobalData.SCALING_UNIFORM) {
                    // Если картинка меньше области или если картинка больше области
                    if ((picWidth < areaWidth && picHeight < areaHeight) || (picWidth > areaWidth
                            && picHeight > areaHeight)) {
                        diffHeight = areaHeight / picHeight * 100;
                        diffWidth = areaWidth / picWidth * 100;

                        // Если разница между высотами больше чем между широтами
                        if (diffHeight < diffWidth) {
                            scalingHeight = diffHeight;
                            scalingWidth = diffHeight;
                        } else {
                            scalingWidth = diffWidth;
                            scalingHeight = diffWidth;
                        }
                    } else if (picWidth <= areaWidth && picHeight > areaHeight) {
                        // Если ширина картинки меньше или равна ширине области, а высота больше
                        scalingHeight = areaHeight / picHeight * 100;
                        scalingWidth = scalingHeight;
                    } else {
                        // Если высота картинки меньше или равна высоте области, а ширина больше
                        // Вычисляем коэффициент увеличения ширины
                        scalingWidth = areaWidth / picWidth * 100;
                        scalingHeight = scalingWidth;
                    }
                } else if (scaling == GlobalData.SCALING_NON_UNIFORM) {
                    // Значение данного атрибута означает, что картинку не нужно изменять пропорционально
                    scalingHeight = areaHeight / picHeight * 100;
                    scalingWidth = areaWidth / picWidth * 100;
                }
            }

            // Если размер ячейки совпадает с размером картинки
            int index = worksheet.getPictures().add(area.getBorderRange().getY(), area.getBorderRange().getX(),
                    (InputStream) foImage.getStream(), Math.round(scalingWidth), Math.round(scalingHeight));

            Picture picture = worksheet.getPictures().get(index);
            picture.setLeft(1);
            picture.setTop(1);
            // Устанавливаем рамку, если все границы одинаковые
            FoBorder borderTop = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_TOP);
            FoBorder borderRight = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_RIGHT);
            FoBorder borderLeft = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_LEFT);
            FoBorder borderBottom = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_BOTTOM);
            if (borderTop != null && borderTop.getStyle() == GlobalData.BORDER_STYLE_SOLID
                    && borderTop.equals(borderRight) && borderTop.equals(borderLeft)
                    && borderTop.equals(borderBottom)) {
                picture.setBorderLineColor(Color.fromArgb(borderTop.getColor().getRGB()));
                picture.setBorderWeight(borderTop.getWidth());
            }
        }
    }

    /**
     * Сборка мусора для области TABLE_BODY.
     * @param area - Область
     */
    private static void clearTableBody(IArea area) {

        if (area.getAreaType() == AreaType.TABLE_BODY) {
            ///////////////////////////////////////////////////////////
            // 03.05.2006 DKL
            // Добавлена проверка на нуль.

            if (area.isHasChildren()) {
                area.getChildrenList().clear();
                if (logger.isDebugEnabled()) {
                    Runtime.getRuntime().gc();
                }
            }

            //
            ///////////////////////////////////////////////////////////
        }
    }

    /**
     * Установка PageBreak после каждого page-sequence.
     * @param worksheet   - Объект листа Excel
     * @param area        - Область
     * @param borderRange - Прямоугольник Excel в ячейках Excel
     */
    private static void setPageBreak(Worksheet worksheet, IArea area,
                                     ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range borderRange) {

        AreaType areaType = area.getAreaType();
        if ((areaType == AreaType.PAGE_SEQUENCE) && (borderRange.getY() > 0)) {
            worksheet.getHorizontalPageBreaks().add(borderRange.getY(), borderRange.getX());
        }
        if ((areaType == AreaType.BLOCK || areaType == AreaType.TABLE || areaType == AreaType.TABLE_ROW
                || areaType == AreaType.LIST_BLOCK || areaType == AreaType.LIST_ITEM)) {
            if (area.getPropertyValue(FoPropertyType.BREAK_BEFORE) != null) {
                String breakBefore = (String)area.getPropertyValue(FoPropertyType.BREAK_BEFORE);
                if (breakBefore == GlobalData.BREAK_PAGE || breakBefore == GlobalData.BREAK_EVEN_PAGE
                        || breakBefore == GlobalData.BREAK_ODD_PAGE) {
                    worksheet.getHorizontalPageBreaks().add(borderRange.getY(), borderRange.getX());
                } else if (breakBefore == GlobalData.BREAK_COLUMN) {
                    worksheet.getVerticalPageBreaks().add(borderRange.getY(), borderRange.getX());
                }
            }
            if (area.getPropertyValue(FoPropertyType.BREAK_AFTER) != null) {
                String breakAfter = (String)area.getPropertyValue(FoPropertyType.BREAK_AFTER);
                if (breakAfter == GlobalData.BREAK_PAGE || breakAfter == GlobalData.BREAK_EVEN_PAGE
                        || breakAfter == GlobalData.BREAK_ODD_PAGE) {
                    worksheet.getHorizontalPageBreaks().add(borderRange.getY() + borderRange.getHeight(),
                            borderRange.getX() + borderRange.getWidth());
                } else if (breakAfter == GlobalData.BREAK_COLUMN) {
                    worksheet.getVerticalPageBreaks().add(borderRange.getY() + borderRange.getHeight(),
                            borderRange.getX() + borderRange.getWidth());
                }
            }
        }
    }

    /**
     * Устанавливаем верхний колонтитул.
     * @param worksheet - Лист Excel
     * @param area      - Область
     */
    private static void setUpperRegionStaticContent(Worksheet worksheet, IArea area) {

        // Если область - это область верхнего псевдоколонтитула
        if (area.getAreaType() == AreaType.STATIC_CONTENT) {
            PageSetup pageSetup = worksheet.getPageSetup();

            String titleColumns = String.format("$%1$s:$%2$s",
                    CellsHelper.columnIndexToName(area.getBorderRange().getX()),
                    CellsHelper.columnIndexToName(area.getBorderRange().getX() + area.getBorderRange().getWidth() - 1));
            pageSetup.setPrintTitleColumns(titleColumns);

            String titleRow = String.format("$%1$d:$%2$d", area.getBorderRange().getY() + 1,
                    area.getBorderRange().getY() + area.getBorderRange().getHeight());
            pageSetup.setPrintTitleRows(titleRow);
        }
    }

    /**
     * Вывод границ области.
     * @param worksheet  - Объект листа Excel
     * @param area       - Область
     * @param excelRange - Диапазон ячеек листа Excel.
     * @return Range   - возвращает границы области
     */
    private Range renderBorder(Worksheet worksheet, IArea area, Range excelRange) {

        // Если область занимает только одну ячейку
        if (area.getBorderRange().getHeight() == 1 && area.getBorderRange().getWidth() == 1) {
            // Получаем ячейку
            Cell cell = worksheet.getCells().get(area.getBorderRange().getY(), area.getBorderRange().getX());
            Style cellStyle = cell.getStyle();
            if (renderBorderToStyle(area, cellStyle)) {
                cell.setStyle(cellStyle, true);
            }
        } else {
            // Выводим заданные границы для неcкольких ячеек
            excelRange = renderBorderRange(worksheet, area, excelRange);
        }

        return excelRange;
    }

    /**
     * Вывод заданных границ в стиль.
     * @param area  - Область
     * @param style - Объект стиля Excel
     * @return boolean  - возвращает признак установки границ ячейки
     */
    protected boolean renderBorderToStyle(IArea area, Style style) {

        FoBorder borderTop = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_TOP);
        FoBorder borderBottom = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_BOTTOM);
        FoBorder borderLeft = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_LEFT);
        FoBorder borderRight = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_RIGHT);


        // Флаги наличия установленных атрибутов
        boolean borderTopIsDefined = borderTop.isDefined();
        boolean borderBottomIsDefined = borderBottom.isDefined();
        boolean borderLeftIsDefined = borderLeft.isDefined();
        boolean borderRightIsDefined = borderRight.isDefined();


        // Если границы не установлены - выходим
        if (!borderTopIsDefined && !borderBottomIsDefined
                && !borderLeftIsDefined && !borderRightIsDefined) {
            return false;
        }

        // Установка границ
        // Верхняя
        if (borderTopIsDefined) {
            Border border = style.getBorders().getByBorderType(BorderType.TOP_BORDER);
            border.setLineStyle(borderTop.getBorderType());
            border.setColor(Color.fromArgb(colors.getColor(borderTop.getColor()).getRGB()));
        }

        // Нижняя
        if (borderBottomIsDefined) {
            Border border = style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER);
            border.setLineStyle(borderBottom.getBorderType());
            border.setColor(Color.fromArgb(colors.getColor(borderBottom.getColor()).getRGB()));
        }

        // Левая
        if (borderLeftIsDefined) {
            Border border = style.getBorders().getByBorderType(BorderType.LEFT_BORDER);
            border.setLineStyle(borderLeft.getBorderType());
            border.setColor(Color.fromArgb(colors.getColor(borderLeft.getColor()).getRGB()));
        }

        // Правая
        if (borderRightIsDefined) {
            Border border = style.getBorders().getByBorderType(BorderType.RIGHT_BORDER);
            border.setLineStyle(borderRight.getBorderType());
            border.setColor(Color.fromArgb(colors.getColor(borderRight.getColor()).getRGB()));
        }

        return true;
    }

    /**
     * Вывод заданных границ.
     * @param worksheet  - Лист Excel
     * @param area       - Область
     * @param excelRange - Диапазон ячеек листа Excel. Если не задан, то создается соотв. указанной области
     * @return Range   - возвращает границы области
     */
    private Range renderBorderRange(Worksheet worksheet, IArea area,
                                    Range excelRange) {

        FoBorder borderTop = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_TOP);
        FoBorder borderBottom = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_BOTTOM);
        FoBorder borderLeft = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_LEFT);
        FoBorder borderRight = (FoBorder)area.getPropertyValue(FoPropertyType.BORDER_RIGHT);

        // Флаги наличия установленных атрибутов
        boolean borderTopIsDefined = borderTop.isDefined();
        boolean borderBottomIsDefined = borderBottom.isDefined();
        boolean borderLeftIsDefined = borderLeft.isDefined();
        boolean borderRightIsDefined = borderRight.isDefined();


        // Если границы не установлены - выходим
        if (!borderTopIsDefined && !borderBottomIsDefined
                && !borderLeftIsDefined && !borderRightIsDefined) {
            return excelRange;
        }

        excelRange = setRangeIfNeed(worksheet, area, excelRange).getValue();

        // Установка границ
        // Верхняя
        if (borderTopIsDefined) {
            excelRange.setOutlineBorder(BorderType.TOP_BORDER, borderTop.getBorderType(),
                    Color.fromArgb(colors.getColor(borderTop.getColor()).getRGB()));
        }

        // Нижняя
        if (borderBottomIsDefined) {
            excelRange.setOutlineBorder(BorderType.BOTTOM_BORDER, borderBottom.getBorderType(),
                    Color.fromArgb(colors.getColor(borderBottom.getColor()).getRGB()));
        }

        // Левая
        if (borderLeftIsDefined) {
            excelRange.setOutlineBorder(BorderType.LEFT_BORDER, borderLeft.getBorderType(),
                    Color.fromArgb(colors.getColor(borderLeft.getColor()).getRGB()));
        }

        // Правая
        if (borderRightIsDefined) {
            excelRange.setOutlineBorder(BorderType.RIGHT_BORDER, borderRight.getBorderType(),
                    Color.fromArgb(colors.getColor(borderRight.getColor()).getRGB()));
        }

        return excelRange;
    }
}
