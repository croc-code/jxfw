package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common;

/**
 * Класс хранящий глобальные данные, типы, перечисления и т.д.
 * Created by vsavenkov on 13.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class GlobalData extends ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData {

    //region Константы
    // *************** Константы ******************

    /**
     * управление алгоритмом, используемым для структурирования ячеек, рядов и столбцов таблицы.
     */
    public static final String TABLE_LAYOUT_AUTO = "auto";

    //region  вертикальное выравнивание бокса.
    /**
     * Тип выравнивания - Выравнивание верхнего края элемента по верху самого высокого элемента строки.
     */
    public static final String VERTICAL_ALIGN_TOP = "top";
    /**
     * Тип выравнивания - Выравнивает основание текущего элемента по нижней части элемента строки, расположенного ниже
     * всех.
     */
    public static final String VERTICAL_ALIGN_BOTTOM = "bottom";
    /**
     * Тип выравнивания - Выравнивание средней точки элемента по базовой линии родителя плюс половина высоты
     * родительского элемента.
     */
    public static final String VERTICAL_ALIGN_MIDDLE = "middle";
    /**
     * Тип выравнивания - Верхняя граница элемента выравнивается по самому высокому текстовому элементу текущей строки.
     */
    public static final String VERTICAL_ALIGN_TEXT_TOP = "text-top";
    /**
     * Тип выравнивания - Нижняя граница элемента выравнивается по самому нижнему краю текущей строки.
     */
    public static final String VERTICAL_ALIGN_TEXT_BOTTOM = "text-bottom";
    //endregion

    /**
     * дефолтное предварительное расстояние между начальными...
     */
    public static final float DEFAULT_PROVISIONAL_DISTANCE_BETWEEN_STARTS = 24F;
    //endregion

    /**
     * Название атрибута, содержащего ширину картинки.
     */
    public static final String WIDTH_PROPERTY = "width";

    /**
     * Название атрибута, содержащего высоту картинки.
     */
    public static final String HEIGHT_PROPERTY = "height";

}
