package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

/**
 * Класс общих констант.
 * Буду постепенно выносить сюда дубли из одноимённых классов пакетов 
 * ru.croc.ctp.jxfw.reporting.renderer.excel и ru.croc.ctp.jxfw.reporting.renderer.word.
 * Created by vsavenkov on 28.07.2017.
 */
public class GlobalData {

    // *************** Константы ******************

    /**
     * Строка, с которой начинается представление цвета в виде rgb(0,0,0) или rgb(0%,0%,0%).
     */
    public static final String RGB = "rgb";

    /**
     * Символ, с которого начинается представление цвета в виде #rgb либо #rrggbb.
     */
    public static final char NUMBER_CHAR = '#';
    /**
     * Символ - запятая.
     */
    public static final char COMMA_CHAR = ',';

    //region Типы выравнивания.
    
    /**
     * Тип выравнивания - по верхнему краю.
     */
    public static final String ALIGNMENT_TYPE_TOP = "top";

    /**
     * Тип выравнивания - по нижнему краю.
     */
    public static final String ALIGNMENT_TYPE_BOTTOM = "bottom";

    /**
     * Тип выравнивания - по левому краю.
     */
    public static final String ALIGNMENT_TYPE_LEFT = "left";

    /**
     * Тип выравнивания - аналогично значению left, если текст идёт слева направо и right, когда 
     * текст идёт справа налево.
     */
    public static final String ALIGNMENT_TYPE_START = "start";

    /**
     * Тип выравнивания - по правому краю.
     */
    public static final String ALIGNMENT_TYPE_RIGHT = "right";

    /**
     * Тип выравнивания - аналогично значению right, если текст идёт слева направо и left, когда 
     * текст идёт справа налево.
     */
    public static final String ALIGNMENT_TYPE_END = "end";

    /**
     * Тип выравнивания - по центру.
     */
    public static final String ALIGNMENT_TYPE_CENTER = "center";

    /**
     * Тип выравнивания - по середине.
     */
    public static final String ALIGNMENT_TYPE_MIDDLE = "middle";

    /**
     * Тип выравнивания - по ширине (одновременно по правому и левому краю).
     */
    public static final String ALIGNMENT_TYPE_JUSTIFY = "justify";

    /**
     * Тип выравнивания.
     */
    public static final String ALIGNMENT_TYPE_BEFORE = "before";

    /**
     * Тип выравнивания.
     */
    public static final String ALIGNMENT_TYPE_AFTER = "after";
    
    //endregion

    /**
     * Основное сообщение исключения с указанием позиции.
     */
    public static final String EXCEPTION_STRING = "Ошибка в XML документе: %1$s\nСтрока:%2$d\nПозиция:%3$d";

    /**
     * Основное сообщение исключения.
     */
    public static final String EXCEPTION_STRING_SIMPLE = "Ошибка в XML документе: %1$s";

    //region Единицы измерений
    /**
     * дюймы.
     */
    public static final String UNIT_IN = "in";
    /**
     * сантиметры.
     */
    public static final String UNIT_CM = "cm";
    /**
     * миллиметры.
     */
    public static final String UNIT_MM = "mm";
    /**
     * точки.
     */
    public static final String UNIT_PT = "pt";
    /**
     * пики.
     */
    public static final String UNIT_PC = "pc";
    /**
     * пиксели.
     */
    public static final String UNIT_PX = "px";
    /**
     * относительная единица измерения.
     */
    public static final String UNIT_EM = "em";
    //endregion

    //region Опция - обертка слов текста
    /**
     * заворачивать.
     */
    public static final String WRAP_OPTION = "wrap";
    /**
     * не заворачивать.
     */
    public static final String NO_WRAP_OPTION = "no-wrap";
    //endregion

    //region Украшение текста
    /**
     * Отменяет все эффекты, в том числе и подчеркивания у ссылок, которое задано по умолчанию.
     */
    public static final String TEXT_DECORATION_NONE = "none";
    /**
     * подчеркнутый текст.
     */
    public static final String TEXT_DECORATION_UNDERLINE = "underline";
    /**
     * линия проходит над текстом.
     */
    public static final String TEXT_DECORATION_OVERLINE = "overline";
    /**
     * перечеркнутый текст.
     */
    public static final String TEXT_DECORATION_LINE_THROUGH = "line-through";
    /**
     * мигание текста.
     */
    public static final String TEXT_DECORATION_BLINK = "blink";
    /**
     * отменяет подчеркнутый текст.
     */
    public static final String TEXT_DECORATION_NO_UNDERLINE = "no-underline";
    /**
     * отменяет линию, проходящую над текстом.
     */
    public static final String TEXT_DECORATION_NO_OVERLINE = "no-overline";
    /**
     * отменяет перечеркнутый текст.
     */
    public static final String TEXT_DECORATION_NO_LINE_THROUGH = "no-line-through";
    /**
     * отменяет мигание текста.
     */
    public static final String TEXT_DECORATION_NO_BLINK = "no-blink";
    //endregion

    /**
     * Класс FoImage.
     */
    public static final String IMAGE_NONE = "none";

    //region Стандартные семейства наименований шрифта
    /**
     * шрифты с засечками (антиквенные), типа Times.
     */
    public static final String FONT_FAMILY_SERIF = "serif";
    /**
     * рубленные шрифты (шрифты без засечек или гротески), типичный представитель Arial.
     */
    public static final String FONT_FAMILY_SANS_SERIF = "sans-serif";
    /**
     * sans serif.
     */
    public static final String FONT_FAMILY_SANS_SERIF2 = "sans serif";
    /**
     * моноширинные шрифты, ширина каждого символа в таком семействе одинакова (шрифт Courier).
     */
    public static final String FONT_FAMILY_SANS_MONOSPACE = "monospace";
    /**
     * курсивные шрифты.
     */
    public static final String FONT_FAMILY_SANS_CURSIVE = "cursive";
    /**
     * декоративные шрифты.
     */
    public static final String FONT_FAMILY_SANS_FANTASY = "fantasy";
    //endregion

    //region Размер шрифта
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_XX_SMALL = "xx-small";
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_X_SMALL = "x-small";
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_SMALL = "small";
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_MEDIUM = "medium";
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_LARGE = "large";
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_X_LARGE = "x-large";
    /**
     * абсолютный размер.
     */
    public static final String FONT_SIZE_XX_LARGE = "xx-large";
    /**
     * Относительный размер шрифта - меньше родительского.
     */
    public static final String FONT_SIZE_SMALER = "smaller";
    /**
     * Относительный размер шрифта - больше родительского.
     */
    public static final String FONT_SIZE_LARGER = "larger";
    //endregion

    //region Вес шрифта
    /**
     * нормальное начертание. Эквивалентно условной единице = 400
     */
    public static final String FONT_WEIGHT_NORMAL = "normal";
    /**
     * полужирное начертание. Эквивалентно условной единице = 700
     */
    public static final String FONT_WEIGHT_BOLD = "bold";
    /**
     * изменяют жирность относительно насыщенности родителя в большую сторону.
     */
    public static final String FONT_WEIGHT_BOLDER = "bolder";
    /**
     * изменяют жирность относительно насыщенности родителя в меньшую сторону.
     */
    public static final String FONT_WEIGHT_LIGHTER = "lighter";
    /**
     * условная единица 100 - сверхсветлое начертание, которое может отобразить браузер.
     */
    public static final String FONT_WEIGHT_100 = "100";
    /**
     * условная единица 200.
     */
    public static final String FONT_WEIGHT_200 = "200";
    /**
     * условная единица 300.
     */
    public static final String FONT_WEIGHT_300 = "300";
    /**
     * условная единица 400.
     */
    public static final String FONT_WEIGHT_400 = "400";
    /**
     * условная единица 500.
     */
    public static final String FONT_WEIGHT_500 = "500";
    /**
     * условная единица 600.
     */
    public static final String FONT_WEIGHT_600 = "600";
    /**
     * условная единица 700.
     */
    public static final String FONT_WEIGHT_700 = "700";
    /**
     * условная единица 800.
     */
    public static final String FONT_WEIGHT_800 = "800";
    /**
     * условная единица 900 - сверхжирное начертание, которое может отобразить браузер.
     */
    public static final String FONT_WEIGHT_900 = "900";
    //endregion

    //region Стиль шрифта
    /**
     * Обычное начертание текста.
     */
    public static final String FONT_STYLE_NORMAL = "normal";
    /**
     * Курсивное начертание.
     */
    public static final String FONT_STYLE_ITALIC = "italic";
    /**
     * Наклонное начертание. Курсив и наклонный шрифт при всей их похожести не одно и то же. 
     * Курсив это специальный шрифт имитирующий рукописный, наклонный же образуется путем наклона обычных знаков вправо.
     */
    public static final String FONT_STYLE_OBLIQUE = "oblique";
    /**
     * backslant.
     */
    public static final String FONT_STYLE_BACKSLANT = "backslant";
    //endregion
    
    //region Ширина границ
    /**
     * толщина границы - 2 пикселя.
     */
    public static final String BORDER_STYLE_THIN = "thin";
    /**
     * толщина границы - 4 пикселя.
     */
    public static final String BORDER_STYLE_MEDIUM = "medium";
    /**
     * толщина границы - 6 пикселя.
     */
    public static final String BORDER_STYLE_THICK = "thick";
    //endregion
    //region Стили границ
    /**
     *  вид границы - dotted.
     */
    public static final String BORDER_STYLE_DOTTED = "dotted";
    /**
     * вид границы - dashed.
     */
    public static final String BORDER_STYLE_DASHED = "dashed";
    /**
     * вид границы - solid.
     */
    public static final String BORDER_STYLE_SOLID = "solid";
    /**
     * вид границы - double.
     */
    public static final String BORDER_STYLE_DOUBLE = "double";
    /**
     * Не отображает границу и её толщина (border-width) задаётся нулевой.
     */
    public static final String BORDER_STYLE_NONE = "none";
    /**
     * Имеет тот же эффект, что и none за исключением применения border-style к ячейкам таблицы, у которой значение 
     * свойства border-collapse установлено как collapse. В этом случае вокруг ячейки граница не будет отображаться 
     * вообще.
     */
    public static final String BORDER_STYLE_HIDDEN = "hidden";
    /**
     * вид границы - groove.
     */
    public static final String BORDER_STYLE_GROOVE = "groove";
    /**
     * вид границы - ridge.
     */
    public static final String BORDER_STYLE_RIDGE = "ridge";
    /**
     * вид границы - inset.
     */
    public static final String BORDER_STYLE_INSET = "inset";
    /**
     * вид границы - outset.
     */
    public static final String BORDER_STYLE_OUTSET = "outset";
    //endregion

    //region Индекс шрифта
    /**
     * Выравнивает базовую линию блока по базовой линии родителя. Если у блока нет базовой линии, то за неё принимается
     * нижняя граница.
     */
    public static final String BASELINE_SHIFT_BASELINE = "baseline";
    /**
     * Опускает базовую линию блока вниз для создания нижнего индекса. Не оказывает влияние на размер текста.
     */
    public static final String BASELINE_SHIFT_SUB = "sub";
    /**
     * Поднимает базовую линию блока вверх для создания верхнего индекса. Не оказывает влияние на размер текста.
     */
    public static final String BASELINE_SHIFT_SUPER = "super";
    //endregion
    //region Видимость областей
    /**
     * Отображает элемент как видимый.
     */
    public static final String VISIBILITY_VISIBLE = "visible";
    /**
     * Элемент становится невидимым или правильней сказать, полностью прозрачным, поскольку он продолжает участвовать
     * в форматировании страницы.
     */
    public static final String VISIBILITY_HIDDEN = "hidden";
    /**
     * Если это значение применяется не к строкам или колонкам таблицы, то результат его использования будет таким же, 
     * как hidden. В случае использования collapse для содержимого ячеек таблиц, то они реагируют, словно к ним было 
     * добавлено display: none. Иными словами, заданные строки и колонки убираются, а таблица перестраивается по новой.
     */
    public static final String VISIBILITY_COLLAPSE = "collapse";
    //endregion

    //region Номер первого листа Excel по умолчанию
    /**
     * автоматически.
     */
    public static final String INITIAL_PAGE_NUMBER_AUTO = "auto";
    /**
     * 1.
     */
    public static final int DEFAULT_INITIAL_PAGE_NUMBER = 1;

    /**
     * Значение данного атрибута означает, что картинку нужно изменять пропорционально.
     */
    public static final String SCALING_UNIFORM = "uniform";
    /**
     * Значение данного атрибута означает, что картинку не нужно изменять пропорционально.
     */
    public static final String SCALING_NON_UNIFORM = "non-uniform";
    //endregion

    //region Коэффициенты пересчета из единиц измерения в пиксели
    ////////////////////////////////////////////////////////////////////////
    // 2007.01.23 DKL
    // Из справки Microsoft Excel 2003. "Measurement units and rulers in Excel"
    // An approximate conversion of points and pixels to inches is shown in the following table.
    // Points  Pixels Inches
    //   72     96      1
    //           
    // 96 pixels == 1 inche => 1 mm == 3.78 pixels

    /**
     * в пиксели.
     */
    public static final double IN_PIXELS_CONVERT_RATIO = 96d;
    /**
     * в точки.
     */
    public static final double IN_POINTS_CONVERT_RATIO = 72d;
    /**
     * в мм.
     */
    public static final double IN_MM_CONVERT_RATIO = 25.4d;
    /**
     * в см.
     */
    public static final double IN_CM_CONVERT_RATIO = IN_MM_CONVERT_RATIO / 10d;
    /**
     * из см в пиксели.
     */
    public static final double CM_PIXELS_CONVERT_RATIO = IN_PIXELS_CONVERT_RATIO / IN_CM_CONVERT_RATIO;
    /**
     * из мм в пиксели.
     */
    public static final double MM_PIXELS_CONVERT_RATIO = IN_PIXELS_CONVERT_RATIO / IN_MM_CONVERT_RATIO;
    /**
     * из точек в пиксели.
     */
    public static final double PT_PIXELS_CONVERT_RATIO = IN_PIXELS_CONVERT_RATIO / IN_POINTS_CONVERT_RATIO;
    /**
     * из пикселей в пиксели.
     */
    public static final double PX_PIXELS_CONVERT_RATIO = IN_PIXELS_CONVERT_RATIO / IN_PIXELS_CONVERT_RATIO;
    /**
     * из пиков в пиксели.
     */
    public static final double PC_PIXELS_CONVERT_RATIO = 12d;
    //endregion
    
    //region Стандартные названия регионов
    /**
     * тело.
     */
    public static final String XSL_REGION_BODY = "xsl-region-body";
    /**
     * перед.
     */
    public static final String XSL_REGION_BEFORE = "xsl-region-before";
    /**
     * после.
     */
    public static final String XSL_REGION_AFTER = "xsl-region-after";
    //endregion

    //region Строки
    /**
     * пробел.
     */
    public static final String SPACE = " ";
    /**
     * табуляция.
     */
    public static final String TAB = "\t";
    /**
     * апостроф.
     */
    public static final String APOSTROPHE = "'";

    /**
     * Функции.
     */
    public static final String URL_START = "url(";
    //endregion

    //region Символы
    /**
     * минус.
     */
    public static final char MINUS_CHAR = '-';
    /**
     * одиночная кавычка.
     */
    public static final char QUOTE_CHAR = '\'';
    /**
     * двойная кавычка.
     */
    public static final char DQUOTE_CHAR = '"';

    /**
     * пробел.
     */
    public static final char SPACE_CHAR = ' ';

    /**
     * открывающая скобка.
     */
    public static final char LEFT_PARENTHESIS_CHAR = '(';
    /**
     * закрывающая скобка.
     */
    public static final char RIGHT_PARENTHESIS_CHAR = ')';

    /**
     * процент.
     */
    public static final char PERCENT_CHAR = '%';
    //endregion

    /**
     * Значение атрибута content-type для скрипта.
     */
    public static final String CONTENT_TYPE_SCRIPT = "text/script";

    /**
     * Значение transparent атрибута color.
     */
    public static final String TRANSPARENT_COLOR = "transparent";

    /**
     * Значение атрибута для пропорциональной ширины колонок.
     */
    public static final String PROPORTIONAL_COLUMN_WIDTH = "proportional-column-width";

    /**
     * дефолтное кол-во колонок.
     */
    public static final int DEFAULT_COLUMN_COUNT = 1;

    /**
     * Значение column-gap по умолчанию - 12pt(16 пикселей).
     */
    public static final int DEFAULT_COLUMN_GAP = 16;

    /**
     * Процент ширины таблицы для колонок заданных процорциональным значением в случае превышения суммы ширин колонок
     * над шириной таблицы.
     */
    public static final int PROPORTIONAL_COLUMN_TABLE_WIDTH_PERCENT = 50;

    /**
     * Минимальная длина значения размера - должна быть равна 2, т.к. указывается единица измерения.
     */
    public static final int MIN_SIZE_VALUE_LENGTH = 2;

    /**
     * Размер шрифта по умолчанию.
     */
    public static final int DEFAULT_FONT_SIZE = 10;

    /**
     * Масштабный коэффициент шрифта.
     */
    public static final float DEFAULT_FONT_SCALING_FACTOR = 1.2F;

    /**
     * Название шрифта по умолчанию.
     */
    public static final String DEFAULT_FONT_NAME = "Arial";

    /**
     * массив символов-пустышек.
     * Используется для удаления их в начале и конце строк.
     */
    public static final char[] WHITE_SPACES;

    static {
        // Инициализация массива символов-пустышек
        WHITE_SPACES = new char[1 + ' '];
        for (char ch = '\0'; ch <= ' '; ch++) {
            WHITE_SPACES[ch] = ch;
        }
    }
}
