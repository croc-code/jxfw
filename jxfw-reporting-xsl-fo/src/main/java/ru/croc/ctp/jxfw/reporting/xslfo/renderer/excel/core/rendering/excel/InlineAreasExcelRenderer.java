package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.rendering.excel;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.Cell;
import com.aspose.cells.FontSetting;
import com.aspose.cells.FontUnderlineType;
import com.aspose.cells.Hyperlink;
import com.aspose.cells.Style;
import com.aspose.cells.Worksheet;
import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.utility.Utils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text.InlineArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.font.FoFont;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text.FoBaseLineShift;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.vt.ValueType;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Отображение INLINE областей в рамках BLOCK области.
 * Created by vsavenkov on 22.08.2017.Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class InlineAreasExcelRenderer {

    /**
     * Используется для хранения значений ref-id & id.
     */
    private final Hashtable<String, IArea> refAreas = new Hashtable<>();

    /**
     * список неразрешенных ссылок.
     */
    private final Hashtable<String, List<IArea>> unlinkedRefAreas = new Hashtable<>();

    /**
     * Объект Excel.
     */
    private final ExcelRenderer excelRenderer;

    /**
     * Объект листа Excel.
     */
    private final Worksheet worksheet;

    /**
     * ячейка.
     */
    private Cell cell;

    /**
     * область.
     */
    private IArea area;

    /**
     * Какой-то признак, вероятно наличия переноса строк.
     */
    private boolean isLineFeed;

    /**
     * отступ 1й строки.
     */
    private String textIndent;

    /**
     * параметры шрифта.
     */
    private String fontString;

    /**
     * Конструктор.
     * @param excelRenderer - Объект Excel
     * @param worksheet     - Объект листа Excel
     */
    public InlineAreasExcelRenderer(ExcelRenderer excelRenderer, Worksheet worksheet) {

        this.excelRenderer = excelRenderer;
        this.worksheet = worksheet;
    }

    /**
     * BLOCK-область.
     * @param area      - область
     * @param areaStyle - стиль области
     * @throws XslFoException генерирует в случае нарушения структуры
     */
    public void execute(IArea area, Style areaStyle) throws XslFoException {

        // Если область не Inline сама по себе и у нее внутри только одна подобласть, то обрабатываем подобласть
        // (блок с inline унутри)
        this.area = !(area instanceof InlineArea) && area.isHasChildren() && area.getChildrenList().size() == 1
                && !area.getChildrenList().get(0).isHasChildren()
                ? area.getChildrenList().get(0)
                : area;

        // Получаем ячейку
        cell = worksheet.getCells().get(area.getBorderRange().getY(), area.getBorderRange().getX());
        isLineFeed = false;

        // Запомним параметры шрифта
        fontString = getFontString(this.area);

        FoFont areaFont = (FoFont) this.area.getInheritedPropertyValue(FoPropertyType.FONT);
        // Получение отступа 1й строки
        // само значение свойства читаем у блока, содержащего в себе inline области
        Dimension textIndent = (Dimension)area.getPropertyValue(FoPropertyType.TEXT_INDENT);
        this.textIndent = !textIndent.isPercentage() && textIndent.hasValue()
                ? HelpFuncs.convertPixelsToSpaces(textIndent.getValue(), areaFont.getFont())
                : StringUtils.EMPTY;

        // Установка выравнивания, оборачивания и поворота текста
        setTextAttributes(this.area, areaStyle);

        // Установка границ
        excelRenderer.renderBorderToStyle(area, areaStyle);
        if (this.area != area) {
            excelRenderer.renderBorderToStyle(this.area, areaStyle);
        }

        if (!this.area.isHasChildren()) {
            handleValueTypeAndSetText(areaStyle);
        } else {
            renderInlineAreas(areaStyle);
        }
    }

    /**
     * Отображение INLINE областей в рамках BLOCK области.
     * @param style - Стиль
     */
    private void renderInlineAreas(Style style) {

        StringBuilder cellValue = new StringBuilder();
        // 1й вызов для опреления полного значения текста в ячейке
        doRenderInlineAreas(area, cellValue, false);
        if (cellValue.length() != 0) {
            // Устанавливаем стиль = General
            style.setNumber(0);
            // Устанавливаем значение ячейки
            cell.putValue(cellValue.toString());
            // 2й вызов для установки шрифта
            cellValue.setLength(0);
            doRenderInlineAreas(area, cellValue, true);
        }
    }

    /**
     * Отображение INLINE областей в рамках BLOCK области.
     * @param area      - Текущая область
     * @param cellValue - Сюда собирается значение ячейки
     * @param isSetFont - признак того, что можно установить шрифт inline области
     */
    private void doRenderInlineAreas(IArea area, StringBuilder cellValue, boolean isSetFont) {

        InlineArea inlineArea = area instanceof InlineArea ? (InlineArea)area : null;

        if (inlineArea != null && !StringUtils.isBlank(inlineArea.getText())) {
            if (cellValue.length() == 0) {
                cellValue.append(textIndent);
            }
            if (cellValue.length() > 0 && isLineFeed) {
                cellValue.append('\n');
            }

            isLineFeed = false;
            cellValue.append(inlineArea.getText());
            if (isSetFont && getFontString(inlineArea) != fontString) {
                // Выделяем добавленный фрагмент
                int length = inlineArea.getText().length();
                FontSetting characters = cell.characters(cellValue.length() - length, length);
                // Устанавливаем шрифт
                setFont(inlineArea, characters.getFont());
            }
        } else if (area.getAreaType() == AreaType.BLOCK) {
            isLineFeed = true;
        }

        if (area.isHasChildren()) {
            // Обходим поддерево дочерних областей
            for (IArea childArea : area.getChildrenList()) {
                doRenderInlineAreas(childArea, cellValue, isSetFont);
            }
        }

        if (area.getAreaType() == AreaType.BLOCK) {
            isLineFeed = true;
        }
    }

    /**
     * Обработка установки значения типа данных области и установка текста.
     * @param style - Стиль
     * @throws XslFoException генерирует в случае нарушения структуры
     */
    private void handleValueTypeAndSetText(Style style) throws XslFoException {

        InlineArea inlineArea = area instanceof InlineArea ? (InlineArea)area : null;
        if (inlineArea == null) {
            return;
        }

        // Получаем тип данных
        ValueType valueType = (ValueType) area.getProperty(FoPropertyType.VT);
        if (valueType == null && area.getParentArea() != null) {
            valueType = (ValueType) area.getParentArea().getProperty(FoPropertyType.VT);
        }

        String areaText = inlineArea.getText();

        // Исправляем ошибку Экселя. Если длина строки 256 символов и более, то тип ячейки надо установить General, а
        // не Text
        // Иначе будут отображаться "###".
        // Также поступим, если не задан тип данных ячейки
        if (valueType == null || areaText.length() >= 256) {
            style.setNumber(0);
            // Вставляем текст
            cell.putValue(textIndent + areaText);
            return;
        }

        // Устанавливаем тип данных
        style.setNumber(valueType.getNumber());

        // Если значение в ячейке пустое, то больше ничего не делаем
        // Изменение внесено заказчиком !!!
        if (areaText.length() == 0) {
            return;
        }

        if (valueType.isInteger()) {
            try {
                int value = NumberFormat.getInstance(GlobalData.getCultureInfo()).parse(areaText).intValue();
                cell.putValue(value);
            } catch (ParseException fe) {
                throw new XslFoException("Неверный формат целого значения: " + areaText,
                        "InlineAreasExcelRenderer.handleValueTypeAndSetText", fe);
            }
        } else if (valueType.isFloat()) {
            Double value = HelpFuncs.parseDoubleValue(areaText);
            if (value == null) {
                throw new XslFoException("Неверный формат значения с одинарной плавающей точкой: " + areaText,
                        "InlineAreasExcelRenderer.handleValueTypeAndSetText");
            }
            cell.putValue(value.doubleValue());
        } else if (valueType.isDouble()) {
            Double value = HelpFuncs.parseDoubleValue(areaText);
            if (value == null) {
                throw new XslFoException("Неверный формат значения с двойной плавающей точкой: " + areaText,
                        "InlineAreasExcelRenderer.handleValueTypeAndSetText");
            }
            cell.putValue(value.doubleValue());
        } else if (valueType.isDateTime()) {
            // Если тип данных это DateTime
            try {
                // Пытаемся получить
                DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, GlobalData.getCultureInfo());
                Date dateTime = format.parse(areaText);
                // Вставляем
                cell.putValue(dateTime);
            } catch (ParseException fe) {
                throw new XslFoException("Неверный формат даты: " + areaText,
                        "InlineAreasExcelRenderer.handleValueTypeAndSetText", fe);
            }
        } else {
            cell.putValue(textIndent + areaText); // Вставляем текст
        }
    }

    /**
     * Установка атрибутов текста.
     * @param area  - Область
     * @param style - Стиль
     */
    private void setTextAttributes(IArea area, Style style) {

        // Установка выравнивания, оборачивания и поворота текста
        setTextAlignmentWithWrapRotation(area, style);

        // Устанавливаем шрифт
        setFont(area, style.getFont());

        setBackgroundColor(area, style);
    }

    /**
     * Установка выравнивания, оборачивания и поворота текста.
     * @param area  - Область
     * @param style - Стиль
     */
    private static void setTextAlignmentWithWrapRotation(IArea area, Style style) {

        // Выставляем горизонтальное выравнивание текста области
        style.setHorizontalAlignment(
                Integer.parseInt(area.getInheritedPropertyValue(FoPropertyType.TEXT_ALIGN).toString()));

        // Выставляем вертикальное выравнивание текста области
        style.setVerticalAlignment(
                Integer.parseInt(area.getInheritedPropertyValue(FoPropertyType.VERTICAL_ALIGN).toString()));

        // Переносить строки в ячейке
        style.setTextWrapped((boolean)area.getInheritedPropertyValue(FoPropertyType.WRAP_OPTION));

        // Устанавливаем поворот текста (Устанавливаем в значение, обратное свойству, ибо в Excel вот так вот
        style.setRotationAngle(-(int)area.getInheritedPropertyValue(FoPropertyType.REFERENCE_ORIENTATION));
    }

    /**
     * Установка шрифта.
     * @param area - Область
     * @param font - Шрифт
     */
    private void setFont(IArea area, com.aspose.cells.Font font) {

        // Установка шрифта
        FoFont areaFont = (FoFont)area.getInheritedPropertyValue(FoPropertyType.FONT);

        font.setName(areaFont.getName());
        font.setSize((int)areaFont.getSize());
        font.setBold(areaFont.isBold());
        font.setItalic(areaFont.isItalic());
        font.setStrikeout(areaFont.isStrikeout());
        font.setUnderline(areaFont.isUnderline() ? FontUnderlineType.SINGLE : FontUnderlineType.NONE);

        // Обработка индексов шрифта
        FoBaseLineShift baseLineShift = (FoBaseLineShift)area.getInheritedPropertyValue(FoPropertyType.BASELINE_SHIFT);
        if (baseLineShift != null) {
            if (baseLineShift.isSubscript()) {
                font.setSubscript(true);
            } else if (baseLineShift.isSuperscript()) {
                // тут блок else обязателен, так как иначе происходит сброс значения
                font.setSuperscript(true);
            }
        }

        // Выставляем цвет текста
        Object object = area.getInheritedPropertyValue(FoPropertyType.COLOR);
        if (object != null) {
            font.setColor(Utils.decodeColor(excelRenderer.getColors().getColor((java.awt.Color)object)));
        }
    }

    /**
     * Получение строки, описывающей хар-ки шрифта области.
     * @param area - Область
     * @return String возвращает строку, описывающую хар-ки шрифта области.
     */
    private static String getFontString(IArea area) {

        StringBuilder result = new StringBuilder(area.getInheritedPropertyValue(FoPropertyType.FONT).toString());
        // Обработка индексов шрифта
        FoBaseLineShift baseLineShift = (FoBaseLineShift)area.getInheritedPropertyValue(FoPropertyType.BASELINE_SHIFT);
        if (baseLineShift != null) {
            result.append(", ").append(baseLineShift.toString());
        }

        // Обработка цвета текста
        Object object = area.getInheritedPropertyValue(FoPropertyType.COLOR);
        if (object != null) {
            result.append(", Color = ").append(object.toString());
        }

        return result.toString();
    }

    /**
     * Установка background цвета.
     * @param area  - Область
     * @param style - Стиль
     */
    private void setBackgroundColor(IArea area, Style style) {

        // Выставляем цвет фона
        Object object = area.getInheritedPropertyValue(FoPropertyType.BACKGROUND_COLOR);
        if (object != null) {
            style.setForegroundColor(
                    Utils.decodeColor(excelRenderer.getColors().getColor((java.awt.Color)object)));
            style.setPattern(BackgroundType.SOLID);
        }
    }

    /**
     * Обработка ссылок.
     * @param area - Область
     */
    public void renderDestination(IArea area) {

        this.area = area;
        // Получаем ячейку
        cell = worksheet.getCells().get(area.getBorderRange().getY(), area.getBorderRange().getX());
        doRenderDestination(area);
    }

    /**
     * Реализация обработки ссылок.
     * @param area - Область
     */
    private void doRenderDestination(IArea area) {

        // Обрабатывем ref-id
        processRefId(area);

        // Обрабатываем external-destination
        String externalDestination = (String)area.getProperty(FoPropertyType.EXTERNAL_DESTINATION);
        if (!StringUtils.isBlank(externalDestination)) {
            // Прямоугольник области
            Range borderRange = this.area.getBorderRange();
            worksheet.getHyperlinks().add(borderRange.getY(),
                    borderRange.getX(),
                    borderRange.getHeight(),
                    borderRange.getWidth(), externalDestination);
        }

        // Обрабатываем internal-destination
        String internalDestination = (String)area.getProperty(FoPropertyType.INTERNAL_DESTINATION);
        if (!StringUtils.isBlank(internalDestination)) {
            if (refAreas.containsKey(internalDestination)) {
                treatInternalReference(area, refAreas.get(internalDestination));
            } else {
                // Ссылка вперед, видимо. Добавим в список неразрешенных ссылок
                List<IArea> areasFrom;
                if (unlinkedRefAreas.containsKey(internalDestination)) {
                    areasFrom = unlinkedRefAreas.get(internalDestination);
                    areasFrom.add(area);
                } else {
                    areasFrom = Arrays.asList(area);
                    unlinkedRefAreas.put(internalDestination, areasFrom);
                }
            }
        }

        if (area.isHasChildren()) {
            // Обходим поддерево дочерних областей
            for (IArea childArea : area.getChildrenList()) {
                doRenderDestination(childArea);
            }
        }
    }

    /**
     * Получение области, которая рендерится через InlineAreasExcelRenderer (факт. области, которая отображается
     * на ячейку).
     * @param area - область
     * @return IArea возвращает область, которая рендерится через InlineAreasExcelRenderer
     */
    private static IArea getCellRenderingArea(IArea area) {

        while (area.getParentArea() != null
                && area.getParentArea().getProgressionDirection() == AreaProgressionDirection.INLINE) {
            area = area.getParentArea();
        }
        return area;
    }

    /**
     * Получение диапазона ячеек в эксель (с перестраховкой).
     * @param area - область
     * @return Range возвращает диапазон ячеек в эксель
     */
    private static Range getAreaBorderRange(IArea area) {

        return area.getBorderRange().getWidth() != 0 && area.getBorderRange().getHeight() != 0
                ? area.getBorderRange() : null;
    }

    /**
     * Простановка гиперссылки от AreaFrom на AreaTo.
     * @param areaFrom - Исходная область
     * @param areaTo   - Область гиперперехода
     */
    private void treatInternalReference(IArea areaFrom, IArea areaTo) {

        Range rangeFrom = getAreaBorderRange(getCellRenderingArea(areaFrom));
        Range rangeTo = getAreaBorderRange(getCellRenderingArea(areaTo));
        if (rangeFrom == null || rangeTo == null) {
            return;
        }
        // Так как при простановке гиперссылки автоматически присваивается синий цвет с подчеркиванием,
        // то нужно сохранить стиль ячейки ДО добавления гиперссылки и восстановить его после
        Cell cell = worksheet.getCells().get(rangeFrom.getY(), rangeFrom.getX());
        Style cellStyle = cell.getStyle();

        String address = worksheet.getCells().get(rangeTo.getY(), rangeTo.getX()).getName();
        Hyperlink hyperlink = worksheet.getHyperlinks().get(worksheet.getHyperlinks()
                .add(rangeFrom.getY(), rangeFrom.getX(), rangeFrom.getHeight(), rangeFrom.getWidth(), address));
        // В данном случае нельзя, чтобы текст области был пустой, иначе появляются проблемы со стилями шрифтов
        if (StringUtils.isBlank(hyperlink.getTextToDisplay())) {
            hyperlink.setTextToDisplay(GlobalData.SPACE);
        }
        // Восстанавливаем стиль
        cell.setStyle(cellStyle, false);
    }

    /**
     * Обработка ref-id.
     * @param area - Область
     */
    public void processRefId(IArea area) {

        String refId = (String)area.getProperty(FoPropertyType.REF_ID);
        if (StringUtils.isBlank(refId)) {
            return;
        }

        refAreas.put(refId, area);
        // Обрабатываем ссылки вперед на данную область.
        if (unlinkedRefAreas.containsKey(refId)) {
            List<IArea> areasFrom = unlinkedRefAreas.get(refId);
            if (areasFrom != null) {
                for (IArea areaFrom : areasFrom) {
                    treatInternalReference(areaFrom, area);
                }
                unlinkedRefAreas.remove(refId);
            }
        }
    }
}
