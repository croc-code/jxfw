package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Перечисление - типы свойств FO элемента.
 * Часть описаний взята с "http://xml.nsu.ru/extra/fo_1.xml" - "http://xml.nsu.ru/extra/fo_12.xml"
 * Created by vsavenkov on 13.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public enum FoPropertyType {

    /**
     * Не определено.
     */
    UNDEFINED(StringUtils.EMPTY, RenderType.EXCEL | RenderType.WORD),

    /**
     * Размер шрифта.
     */
    FONT_SIZE("font-size", RenderType.EXCEL | RenderType.WORD),
    /**
     * Семейство шрифта.
     */
    FONT_FAMILY("font-family", RenderType.EXCEL | RenderType.WORD),
    /**
     * Вес шрифта.
     */
    FONT_WEIGHT("font-weight", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль шрифта.
     */
    FONT_STYLE("font-style", RenderType.EXCEL | RenderType.WORD),

    /**
     * Цвет.
     */
    COLOR("color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет фона.
     */
    BACKGROUND_COLOR("background-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Фоновая картинка.
     */
    BACKGROUND_IMAGE("background-image", RenderType.EXCEL | RenderType.WORD),

    /**
     * Ширина.
     */
    WIDTH("width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Высота.
     */
    HEIGHT("height", RenderType.EXCEL | RenderType.WORD),

    /**
     * Минимальная ширина.
     */
    MIN_WIDTH("min-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Максимальная ширина.
     */
    MAX_WIDTH("max-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Минимальная высота.
     */
    MIN_HEIGHT("min-height", RenderType.EXCEL | RenderType.WORD),
    /**
     * Максимальная высота.
     */
    MAX_HEIGHT("max-height", RenderType.EXCEL | RenderType.WORD),

    /**
     * Номер колонки.
     */
    COLUMN_NUMBER("column-number", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина колонки.
     */
    COLUMN_WIDTH("column-width", RenderType.EXCEL | RenderType.WORD),

    /**
     * Повторение номера колонки.
     */
    NUMBER_COLUMNS_REPEATED("number-columns-repeated", RenderType.EXCEL | RenderType.WORD),
    /**
     * Кол-во смерженных столбцов.
     */
    NUMBER_COLUMNS_SPANNED("number-columns-spanned", RenderType.EXCEL | RenderType.WORD),
    /**
     * Кол-во смерженных строк.
     */
    NUMBER_ROWS_SPANNED("number-rows-spanned", RenderType.EXCEL | RenderType.WORD),
    /**
     * Область таблицы.
     */
    TABLE_LAYOUT("table-layout", RenderType.EXCEL | RenderType.WORD),

    /**
     * Выравнивание текста.
     */
    TEXT_ALIGN("text-align", RenderType.EXCEL | RenderType.WORD),
    /**
     * Выравнивание по вертикали.
     */
    VERTICAL_ALIGN("vertical-align", RenderType.EXCEL | RenderType.WORD),
    /**
     * Отображение выравнивания.
     */
    DISPLAY_ALIGN("display-align", RenderType.EXCEL | RenderType.WORD),

    /**
     * Разбиение.
     */
    WRAP_OPTION("wrap-option", RenderType.EXCEL | RenderType.WORD),

    /**
     * Верхняя рамка.
     */
    BORDER_TOP("border-top", RenderType.EXCEL | RenderType.WORD),
    /**
     * Нижняя рамка.
     */
    BORDER_BOTTOM("border-bottom", RenderType.EXCEL | RenderType.WORD),
    /**
     * Левая рамка.
     */
    BORDER_LEFT("border-left", RenderType.EXCEL | RenderType.WORD),
    /**
     * Правая рамка.
     */
    BORDER_RIGHT("border-right", RenderType.EXCEL | RenderType.WORD),

    /**
     * Цвет границы перед.
     */
    BORDER_BEFORE_COLOR("border-before-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * стиль границы перед.
     */
    BORDER_BEFORE_STYLE("border-before-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина границы перед.
     */
    BORDER_BEFORE_WIDTH("border-before-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет границы после.
     */
    BORDER_AFTER_COLOR("border-after-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль границы после.
     */
    BORDER_AFTER_STYLE("border-after-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина границы после.
     */
    BORDER_AFTER_WIDTH("border-after-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет начальной границы.
     */
    BORDER_START_COLOR("border-start-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль начальной границы.
     */
    BORDER_START_STYLE("border-start-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина начальной границы.
     */
    BORDER_START_WIDTH("border-start-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет завершающей границы.
     */
    BORDER_END_COLOR("border-end-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль завершающей границы.
     */
    BORDER_END_STYLE("border-end-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина завершающей границы.
     */
    BORDER_END_WIDTH("border-end-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет верхней рамки.
     */
    BORDER_TOP_COLOR("border-top-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль верхней рамки.
     */
    BORDER_TOP_STYLE("border-top-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина верхней рамки.
     */
    BORDER_TOP_WIDTH("border-top-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет нижней рамки.
     */
    BORDER_BOTTOM_COLOR("border-bottom-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль нижней рамки.
     */
    BORDER_BOTTOM_STYLE("border-bottom-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина нижней рамки.
     */
    BORDER_BOTTOM_WIDTH("border-bottom-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет левой рамки.
     */
    BORDER_LEFT_COLOR("border-left-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль левой рамки.
     */
    BORDER_LEFT_STYLE("border-left-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина левой рамки.
     */
    BORDER_LEFT_WIDTH("border-left-width", RenderType.EXCEL | RenderType.WORD),
    /**
     * Цвет правой рамки.
     */
    BORDER_RIGHT_COLOR("border-right-color", RenderType.EXCEL | RenderType.WORD),
    /**
     * Стиль правой рамки.
     */
    BORDER_RIGHT_STYLE("border-right-style", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина правой рамки.
     */
    BORDER_RIGHT_WIDTH("border-right-width", RenderType.EXCEL | RenderType.WORD),

    /**
     * Значение поля до содержимого элемента.
     */
    PADDING_BEFORE("padding-before", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля после содержимого элемента.
     */
    PADDING_AFTER("padding-after", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля в начале содержимого элемента.
     */
    PADDING_START("padding-start", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля в конце содержимого элемента.
     */
    PADDING_END("padding-end", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля сверху содержимого элемента.
     */
    PADDING_TOP("padding-top", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля снизу содержимого элемента.
     */
    PADDING_BOTTOM("padding-bottom", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля слева от содержимого элемента.
     */
    PADDING_LEFT("padding-left", RenderType.EXCEL | RenderType.WORD),
    /**
     * Значение поля справа от содержимого элемента.
     */
    PADDING_RIGHT("padding-right", RenderType.EXCEL | RenderType.WORD),

    /**
     * Величина отступа от верхнего края элемента.
     */
    MARGIN_TOP("margin-top", RenderType.EXCEL | RenderType.WORD),
    /**
     * Величина отступа от нижнего края элемента.
     */
    MARGIN_BOTTOM("margin-bottom", RenderType.EXCEL | RenderType.WORD),
    /**
     * Величина отступа от левого края элемента.
     */
    MARGIN_LEFT("margin-left", RenderType.EXCEL | RenderType.WORD),
    /**
     * Величина отступа от правого края элемента.
     */
    MARGIN_RIGHT("margin-right", RenderType.EXCEL | RenderType.WORD),

    /**
     * Степень.
     */
    EXTENT("extent", RenderType.EXCEL | RenderType.WORD),

    /**
     * Пробелы перед.
     */
    SPACE_BEFORE("space-before", RenderType.EXCEL | RenderType.WORD),
    /**
     * Пробелы после.
     */
    SPACE_AFTER("space-after", RenderType.EXCEL | RenderType.WORD),
    /**
     * Пробелы в начале.
     */
    SPACE_START("space-start", RenderType.EXCEL | RenderType.WORD),
    /**
     * Пробелы в конце.
     */
    SPACE_END("space-end", RenderType.EXCEL | RenderType.WORD),

    /**
     * Отступ в начале.
     */
    START_INDENT("start-indent", RenderType.EXCEL | RenderType.WORD),
    /**
     * Отступ в конце.
     */
    END_INDENT("end-indent", RenderType.EXCEL | RenderType.WORD),

    /**
     * Источник.
     */
    SRC("src", RenderType.EXCEL | RenderType.WORD),

    /**
     * Идентификатор.
     */
    ID("id", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ссылочный идентификатор.
     */
    REF_ID("ref-id", RenderType.EXCEL | RenderType.WORD),
    /**
     * Внутреннее назначение.
     */
    INTERNAL_DESTINATION("internal-destination", RenderType.EXCEL | RenderType.WORD),
    /**
     * Внешнее назначение.
     */
    EXTERNAL_DESTINATION("external-destination", RenderType.EXCEL | RenderType.WORD),

    /**
     * Предварительное расстояние между начальными...
     */
    PROVISIONAL_DISTANCE_BETWEEN_STARTS("provisional-distance-between-starts", RenderType.EXCEL | RenderType.WORD),
    /**
     * Предварительный разделитель меток.
     */
    PROVISIONAL_LABEL_SEPARATION("provisional-label-separation", RenderType.EXCEL | RenderType.WORD),

    /**
     * Кол-во колонок.
     */
    COLUMN_COUNT("column-count", RenderType.EXCEL | RenderType.WORD),
    /**
     * Разрыв между колонками.
     */
    COLUMN_GAP("column-gap", RenderType.EXCEL | RenderType.WORD),
    /**
     * Пролёт.
     */
    SPAN("span", RenderType.EXCEL | RenderType.WORD),

    /**
     * Украшение текста.
     */
    TEXT_DECORATION("text-decoration", RenderType.EXCEL | RenderType.WORD),

    /**
     * Отступ текста.
     */
    TEXT_INDENT("text-indent", RenderType.EXCEL | RenderType.WORD),

    /**
     * Тип.
     */
    VT("vt", RenderType.EXCEL | RenderType.WORD),

    /**
     * Мастер наименование.
     */
    MASTER_NAME("master-name", RenderType.EXCEL | RenderType.WORD),
    /**
     * Наименование региона.
     */
    REGION_NAME("region-name", RenderType.EXCEL | RenderType.WORD),
    /**
     * Наименование потока.
     */
    FLOW_NAME("flow-name", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ориентация ссылки.
     */
    REFERENCE_ORIENTATION("reference-orientation", RenderType.EXCEL | RenderType.WORD),
    /**
     * Мастер ссылка.
     */
    MASTER_REFERENCE("master-reference", RenderType.EXCEL | RenderType.WORD),

    /**
     * Высота страницы.
     */
    PAGE_HEIGHT("page-height", RenderType.EXCEL | RenderType.WORD),
    /**
     * Ширина страницы.
     */
    PAGE_WIDTH("page-width", RenderType.EXCEL | RenderType.WORD),

    /**
     * Разрыв перед.
     */
    BREAK_AFTER("break-after", RenderType.EXCEL | RenderType.WORD),
    /**
     * Разрыв после.
     */
    BREAK_BEFORE("break-before", RenderType.EXCEL | RenderType.WORD),

    /**
     * Разрыв страницы после.
     */
    PAGE_BREAK_AFTER("page-break-after", RenderType.EXCEL | RenderType.WORD),
    /**
     * Разрыв страницы перед.
     */
    PAGE_BREAK_BEFORE("page-break-before", RenderType.EXCEL | RenderType.WORD),

    /**
     * Смещение базовой линии.
     */
    BASELINE_SHIFT("baseline-shift", RenderType.EXCEL | RenderType.WORD),

    /**
     * Символ.
     */
    CHARACTER("character", RenderType.EXCEL | RenderType.WORD),

    /**
     * Видимость.
     */
    VISIBILITY("visibility", RenderType.EXCEL | RenderType.WORD),

    /**
     * Стартовый номер страниц.
     */
    INITIAL_PAGE_NUMBER("initial-page-number", RenderType.EXCEL | RenderType.WORD),

    /**
     * Масштабирование.
     */
    SCALING("scaling", RenderType.EXCEL | RenderType.WORD),
    
    //region специфичные значения из WORD рендерера
    /**
     * Фон.
     */
    BACKGROUND("background", RenderType.WORD),
    /**
     * Шрифт.
     */
    FONT("font", RenderType.WORD),
    /**
     * высота линии.
     */
    LINE_HEIGHT("line-height", RenderType.WORD),
    /**
     * рамки.
     */
    BORDER("border", RenderType.WORD),
    /**
     * стиль рамки.
     */
    BORDER_STYLE("border-style", RenderType.WORD),
    /**
     * цвет рамки.
     */
    BORDER_COLOR("border-color", RenderType.WORD),
    /**
     * ширина рамки.
     */
    BORDER_WIDTH("border-width", RenderType.WORD),
    /**
     * признак наложения рамок.
     */
    BORDER_COLLAPSE("border-collapse", RenderType.WORD),
    /**
     * Значения полей с боков от содержимого элемента.
     */
    PADDING("padding", RenderType.WORD),
    /**
     * Не нашёл, что это такое.
     */
    ENDS_ROW("ends-row", RenderType.WORD),
    /**
     * Не нашёл, что это такое.
     */
    STARTS_ROW("starts-row", RenderType.WORD),
    /**
     * Величина отступов от краёв элемента.
     */
    MARGIN("margin", RenderType.WORD),
    /**
     * определяет меру усилий, которые затратит форматер для того, чтобы сохранить содержание данного форматирующего 
     * объекта на одной странице.
     */
    KEEP_TOGETHER("keep-together", RenderType.WORD),
    /**
     *  определяет меру усилий, которые затратит форматер для того, чтобы сохранить данный форматирующий объект на той 
     * же самой странице, что и следующий форматирующий объект.
     */
    KEEP_WITH_NEXT("keep-with-next", RenderType.WORD),
    /**
     * определяет меру усилий, которые затратит форматер для того, чтобы оставить данный форматирующий объект на той 
     * же странице, что и предыдущий.
     */
    KEEP_WITH_PREVIOUS("keep-with-previous", RenderType.WORD),
    /**
     * используется для указания, что документ должен иметь четное или нечетное количество страниц или должен 
     * заканчиваться на четной или нечетной странице.
     */
    FORCE_PAGE_COUNT("force-page-count", RenderType.WORD),
    /**
     * задаёт вертикальный размер собственно изображения. Если этот размер не совпадает с атрибутом height, 
     * изображение соответствующим образом масштабируется.
     */
    CONTENT_HEIGHT("content-height", RenderType.WORD),
    /**
     * задаёт горизонтальный размер собственно изображения. Если этот размер не совпадает с атрибутом width, 
     * изображение соответствующим образом масштабируется.
     */
    CONTENT_WIDTH("content-width", RenderType.WORD),
    /**
     * определяет тип графики.
     */
    CONTENT_TYPE("content-type", RenderType.WORD),
    /**
     * дополнительное свойство разрыва страниц.
     */
    PAGE_BREAK_INSIDE("page-break-inside", RenderType.WORD),
    //endregion

    //region специфичные значения, которые не используются для идентификации
    /**
     * загружает изображение с заданного URL и отображает его как внутри-строчный элемент.
     */
    EXTERNAL_GRAPHIC("external-graphic", RenderType.NONE);
    //endregion

    /**
     * наименование св-ва.
     */
    private String propertyName;

    /**
     * тип рендеринга, для которого используется значение.
     */
    private int renderType;

    /**
     * Конструктор.
     * @param propertyName - наименование св-ва
     * @param renderType   - тип рендеринга
     */
    FoPropertyType(String propertyName, int renderType) {
        this.propertyName = propertyName;
        this.renderType = renderType;
    }

    /**
     * Получение типа свойства FO элемента.
     * @param propertyName - наименование свойства FO элемента
     * @param renderType   - тип рендеринга
     * @return FoPropertyType возвращает тип свойства FO элемента, соответствующий наименованию св-ва 
     *                          или FoPropertyType.UNDEFINED
     */
    public static FoPropertyType parseValue(String propertyName, int renderType) {
        return Arrays.stream(FoPropertyType.values())
                .filter(m -> m.propertyName.equals(propertyName) && (renderType == (m.renderType & renderType)))
                .findAny().orElse(FoPropertyType.UNDEFINED);
    }

    public String getPropertyName() {
        return propertyName;
    }
}
