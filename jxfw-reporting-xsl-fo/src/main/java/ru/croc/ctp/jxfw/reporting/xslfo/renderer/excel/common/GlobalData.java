package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common;

import com.aspose.cells.Border;
import com.aspose.cells.BorderType;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.FontUnderlineType;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;

import java.util.Locale;

/**
 * Класс хранящий глобальные данные, типы, перечисления и т.д.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class GlobalData extends ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData {
    
    // *************** Константы ******************
    //region Типы разрывов страниц
    /**
     * Разрыв слева.
     */
    public static final String PAGE_BREAK_LEFT = "left";
    /**
     * Разрыв справа.
     */
    public static final String PAGE_BREAK_RIGHT = "right";
    /**
     * Разрыв всегда.
     */
    public static final String PAGE_BREAK_ALWAYS = "always";
    /**
     * Разрыв отменён.
     */
    public static final String PAGE_BREAK_AVOID = "avoid";
    /**
     * Разрыв колонки.
     */
    public static final String BREAK_COLUMN = "column";
    /**
     * Разрыв страницы.
     */
    public static final String BREAK_PAGE = "page";
    /**
     * Разрыв чётных страниц.
     */
    public static final String BREAK_EVEN_PAGE = "even-page";
    /**
     * Разрыв нечётных страниц.
     */
    public static final String BREAK_ODD_PAGE = "odd-page";
    //endregion

    //region Типы значений ячеек
    /**
     * целое.
     */
    public static final String VALUE_TYPE_I2 = "i2";

    /**
     * длинное целое.
     */
    public static final String VALUE_TYPE_I4 = "i4";

    /**
     * фиксированой длинны.
     */
    public static final String VALUE_TYPE_FIXED_14_4 = "fixed.14.4";

    /**
     * вещественное.
     */
    public static final String VALUE_TYPE_R4 = "r4";

    /**
     * длинное вещественное.
     */
    public static final String VALUE_TYPE_R8 = "r8";

    /**
     * дата и время с часовым поясом.
     */
    public static final String VALUE_TYPE_DATETIME_TZ = "dateTime.tz";

    /**
     * время с часовым поясом.
     */
    public static final String VALUE_TYPE_TIME_TZ = "time.tz";

    /**
     * дата.
     */
    public static final String VALUE_TYPE_DATE = "date";

    /**
     * строка.
     */
    public static final String VALUE_TYPE_STRING = "string";

    /**
     * уникальный идентификатор.
     */
    public static final String VALUE_TYPE_UUID = "uuid";
    //endregion

    /**
     * массив допустимых значений стилей границ.
     */
    public static String[] BORDER_STYLES = new String[] {
        BORDER_STYLE_NONE, BORDER_STYLE_HIDDEN, BORDER_STYLE_DOTTED, BORDER_STYLE_DASHED, BORDER_STYLE_SOLID,
        BORDER_STYLE_DOUBLE, BORDER_STYLE_GROOVE, BORDER_STYLE_RIDGE, BORDER_STYLE_INSET, BORDER_STYLE_OUTSET };

    //region Значения атрибута span
    /**
     * all.
     */
    public static final String SPAN_ALL = "all";
    //endregion

    /**
     * Имя культуры (локаль) по умолчанию.
     */
    public static final String DEFAULT_CULTURE_INFO_NAME = "en-US";

    //region Поправочные коэффициенты для ширины и высоты служат для более точного вычисления размеров 
    // ( полученных после печати ) содержимого сгенерированного документа.
    // Проблема в том, что эксель не позволяет точно задавать размеры. Плюс к этому
    // различные модели принтеров также изменяют значения ширин и высот.
    // Данные коэффициенты получены империческим путем. 
   
    /**
     * ширина.
     */
    public static final double EXCEL_WIDTH_RATIO = 0.94d;
    /**
     * высота.
     */
    public static final double EXCEL_HEIGHT_RATIO = 1.033d;
    //endregion

    /**
     * Сущность применяемая в Excel для выставления номера страницы.
     */
    public static final String PAGE_NUMBER_EXCEL_ENTITY = "&P";

    /**
     * Номер страницы по умолчанию.
     */
    public static final String DEFAULT_PAGE_NUMBER = "1";

    //region Символы
    /**
     * новая строка.
     */
    public static final char LINE_FEED_CHAR = '\n';
    //endregion

    /**
     * дефолтное значение ширины колонки.
     */
    public static final String DEFAULT_COLUMN_WIDTH = PROPORTIONAL_COLUMN_WIDTH + "(1)";

    /**
     * дефолтное предварительное расстояние между начальными...
     */
    public static final int DEFAULT_PROVISIONAL_DISTANCE_BETWEEN_STARTS = 24;

    /**
     * Excel 2003:
     *        Worksheet size: 65,536 rows by 256 columns
     *        Column width:   255 characters
     * Excel 2007: 
     *        Worksheet size: 1,048,576 rows by 16,384 columns
     *        Column width:   255 characters.
     */
    public static class Excel2003Limits {
        /**
         * Максимальное количество строк в Excel (byte.MaxValue).
         */
        public static final int MAX_EXCEL_COLUMNS_COUNT = 255;

        /**
         * Максимальное количество строк в Excel (UInt16.MaxValue).
         */
        public static final int MAX_EXCEL_ROWS_COUNT = 65535;
    }

    /**
     * Максимальная высота ряда Excel в пикселах.
     */
    public static final int MAX_EXCEL_ROW_HEIGHT = (int)(545 / EXCEL_HEIGHT_RATIO);

    /**
     * Дефолтная высота ряда Excel в пикселах.
     */
    public static final int DEFAULT_EXCEL_ROW_HEIGHT = (int)(10 / EXCEL_HEIGHT_RATIO);

    /**
     * Максимальная ширина ячейки в пикселях.
     */
    public static final int MAX_EXCEL_CELL_WIDTH_PIXELS = 1790;

    /**
     * Максимальная ширина ячейки в поинтах.
     */
    public static final int MAX_EXCEL_CELL_WIDTH = 255;

    /**
     * Минимальное значение в пикселах(процентах) при котором обрабатываются величины для генерации пустых пространств.
     * Примерно = 0.5 от стандартной высоты ячейки Excel
     ///////////////////////////////////////////////////////////
     // 12.10.2006 DKL
     // Предыдущее значение равнялось 10.
     // Данное значение минимального отступа более обосновано, чем предыдущее, т.к. это сказывается
     // на более точном рендеринге, как следствие убирает непонятные смещения из-за отступов.
     // Убирать совсем эту константу неэффективно, т.к. это приведет к созданию в больших количествах
     // Space областей, и как следствие приведет к резкому ухудшению производительности, если много
     // областей будут иметь атрибуты отступов.

     ///////////////////////////////////////////////////////////
     // 23.11.2006 DKL
     // Изменено по запросу заказчика. см. письмо от 20.11.2006
     */
    public static final int PADDING_SPACE_THRESHOLD = 7;

    /**
     * Значение распространения ячейки по горизонтали по умолчанию.
     */
    public static final int DEFAULT_COLUMNS_SPAN_VALUE = 1;

    /**
     * Значение распространения ячейки по вертикали по умолчанию.
     */
    public static final int DEFAULT_ROWS_SPAN_VALUE = 1;

    /**
     * Значение переноса.
     */
    public static final boolean DEFAULT_WRAP_OPTION = true;

    //region Ориентация страницы
    /**
     * На 90 градусов против часовой стрелки.
     */
    public static final int REFERENCE_ORIENTATION_MINUS_90_ANGLE = -90;
    /**
     * На 90 градусов по часовой стрелке.
     */
    public static final int REFERENCE_ORIENTATION_90_ANGLE = 90;
    /**
     * Без поворота.
     */
    public static final int REFERENCE_ORIENTATION_0_ANGLE = 0;
    /**
     * По умолчанию.
     */
    public static final int DEFAULT_REFERENCE_ORIENTATION = REFERENCE_ORIENTATION_0_ANGLE;
    //endregion

    /**
     * Высота листа по умолчанию 297 mm в пикселах.
     */
    public static final int DEFAULT_PAGE_HEIGHT = 1123;
    /**
     * Ширина листа по умолчанию 210 mm в пикселах.
     */
    public static final int DEFAULT_PAGE_WIDTH = 793;

    /**
     * Установка стиля ячейки по умолчанию.
     * @param style - стиль
     * @return Style возвращает стиль, заполненный по умолчанию
     */
    public static Style defaultExcelStyle(Style style) {
        style.setForegroundColor(com.aspose.cells.Color.getWhite());
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.getFont().setName(DEFAULT_FONT_NAME);
        style.getFont().setSize(DEFAULT_FONT_SIZE);
        style.getFont().setBold(false);
        style.getFont().setItalic(false);
        style.getFont().setStrikeout(false);
        style.getFont().setSubscript(false);
        style.getFont().setSuperscript(false);
        style.getFont().setUnderline(FontUnderlineType.NONE);
        style.setRotationAngle(0);
        Border border = style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER);
        border.setColor(com.aspose.cells.Color.getEmpty());
        border.setLineStyle(CellBorderType.NONE);
        border = style.getBorders().getByBorderType(BorderType.TOP_BORDER);
        border.setColor(com.aspose.cells.Color.getEmpty());
        border.setLineStyle(CellBorderType.NONE);
        border = style.getBorders().getByBorderType(BorderType.LEFT_BORDER);
        border.setColor(com.aspose.cells.Color.getEmpty());
        border.setLineStyle(CellBorderType.NONE);
        border = style.getBorders().getByBorderType(BorderType.RIGHT_BORDER);
        border.setColor(com.aspose.cells.Color.getEmpty());
        border.setLineStyle(CellBorderType.NONE);
        style.getFont().setColor(com.aspose.cells.Color.getBlack());
        return style;
    }

    /**
     * Объект, содержащий информацию о региональных настройках.
     * Специфичен для конкретного потока
     */
    private static ThreadLocal<Locale> threadLocalScope = new ThreadLocal<>();

    /**
     * Информация о региональных настройках.
     * @return Locale возвращает информацию о региональных настройках
     */
    public static Locale getCultureInfo() {
        return threadLocalScope.get();
    }

    /**
     * Устанавливает информацию о региональных настройках.
     * @param locale - региональные настройки
     */
    public static void setCultureInfo(Locale locale) {
        threadLocalScope.set(locale);
    }

    /**
     * Получение точного значения ширины колонки Excel.
     * @param width - Ширина
     * @return int возвращает точное значение ширины колонки Excel
     */
    public static int getPrecizeExcelColumnWidth(int width) {
        return Math.min(MAX_EXCEL_CELL_WIDTH_PIXELS, (int) Math.ceil(EXCEL_WIDTH_RATIO * width));
        //return (int)Math.Ceiling(EXCEL_WIDTH_RATIO * nWidth);
    }

    /**
     * Получение точного значения высоты ряда Excel.
     * @param height - Высота
     * @return int возвращает точное значение высоты ряда Excel
     */
    public static int getPrecizeExcelColumnHeight(int height) {
        
        return (int)Math.ceil(EXCEL_HEIGHT_RATIO * height);
    }
}
