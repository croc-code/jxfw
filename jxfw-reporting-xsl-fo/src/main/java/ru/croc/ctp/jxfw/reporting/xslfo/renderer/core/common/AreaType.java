package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Перечисление - тип области, соответствующий FO элементу.
 * описание элементов взято с "http://pyramidin.narod.ru/xsl/slice6.html"
 * Терминология:
 *  ОФ - Объекты Форматирования
 * Created by vsavenkov on 13.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public enum AreaType {

    /**
     * Не определена.
     */
    UNDEFINED(StringUtils.EMPTY, RenderType.EXCEL | RenderType.WORD),
    /**
     * область общего назначения.
     */
    COMMON("common", RenderType.EXCEL | RenderType.WORD),
    /**
     * пустое пространство.
     */
    SPACER("spacer", RenderType.EXCEL | RenderType.WORD),

    /**
     * корневой верхний узел результирующего дерева XSL. Это дерево состоит из ОФ.
     */
    ROOT("fo:root", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется для генерации страниц и специфицирует геометрию страницы. Страница может быть подразделена на 
     * несколько регионов (до5).
     */
    SIMPLE_PAGE_MASTER("fo:simple-page-master", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется для спецификации того, как создавать (суб-)последовательность страниц внутри документа; например, 
     * главу репортажа. Содержимое этих страниц получается из потомков поплавков от fo:page-sequence.
     */
    PAGE_SEQUENCE("fo:page-sequence", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется для ассоциирования заголовка с данной последовательностью страниц. Этот заголовок может 
     * использоваться интерактивным пользовательским агентом для идентификации страниц. Например, содержимое fo:title 
     * может быть отформатировано и отображено в окне "title" или в "tool tip".
     */
    TITLE("fo:title", RenderType.EXCEL | RenderType.WORD),
    /**
     * последовательность всплывающих объектов (поплавков), представляющая всплывающее текстовое
     * содержимое, распределённое по страницам.
     */
    FLOW("fo:flow", RenderType.EXCEL | RenderType.WORD),
    /**
     * содержит последовательность или дерево ОФ, которые существуют в одном регионе или повторяются в одинаково 
     * называющихся регионах на одной или более страницах в последовательности страниц. Чаще всего используется для 
     * создания повторяющихся или "бегущих" шапок и футеров.
     */
    STATIC_CONTENT("fo:static-content", RenderType.EXCEL | RenderType.WORD),

    /**
     * обычно используется при форматировании параграфов, заголовков, названий рисунков и таблиц и т.п.
     */
    BLOCK("fo:block", RenderType.EXCEL | RenderType.WORD),
    /**
     * обычно используется для форматирования участка текста с фоном или в рамке.
     */
    INLINE("fo:inline", RenderType.EXCEL | RenderType.WORD),
    /**
     * Объект потока - представляет символ, отображаемый в глиф для презентации.
     */
    CHARACTER("fo:character", RenderType.EXCEL | RenderType.WORD),

    /**
     * Объект потока - используется для форматирования табличного материала..
     */
    TABLE("fo:table", RenderType.EXCEL | RenderType.WORD),
    /**
     * имеет в качестве содержимого тело таблицы.
     */
    TABLE_BODY("fo:table-body", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется для группирования содержимого, размещая его в ячейке таблицы..
     */
    TABLE_CELL("fo:table-cell", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется для группирования ячеек таблицы в ряды.
     */
    TABLE_ROW("fo:table-row", RenderType.EXCEL | RenderType.WORD),
    /**
     * специфицирует характеристики ячеек таблицы, находящихся в одном столбце и имеющих общий захват/span.
     */
    TABLE_COLUMN("fo:table-column", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется как контейнер содержимого "шапки"/header таблицы.
     */
    TABLE_HEADER("fo:table-header", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется как контейнер содержимого футера таблицы.
     */
    TABLE_FOOTER("fo:table-footer", RenderType.EXCEL | RenderType.WORD),

    /**
     * Объект потока - используется для графики в тех случаях, когда графические данные находятся вне результирующего 
     * дерева XML в пространстве имён fo.
     */
    EXTERNAL_GRAPHIC("fo:external-graphic", RenderType.EXCEL | RenderType.WORD),
    /**
     * Объект-поплавок - используется для инлайн-графики или другого "общего/generic" объекта, где данные объекта 
     * расположены как потомки fo:instream-foreign-object.
     */
    INSTREAM_FOREIGN_OBJECT("fo:instream-foreign-object", RenderType.EXCEL | RenderType.WORD),

    /**
     * используется для представления стартового ресурса простой ссылки.
     */
    BASIC_LINK("fo:basic-link", RenderType.EXCEL | RenderType.WORD),

    /**
     * используется при форматировании списка.
     */
    LIST_BLOCK("fo:list-block", RenderType.EXCEL | RenderType.WORD),
    /**
     * содержит лэйбл и тело элемента списка.
     */
    LIST_ITEM("fo:list-item", RenderType.EXCEL | RenderType.WORD),
    /**
     * содержимое тела list-item.
     */
    LIST_ITEM_BODY("fo:list-item-body", RenderType.EXCEL | RenderType.WORD),
    /**
     * содержимое лэйбла для list-item; обычно используется для нумерации, идентификации или украшения тела 
     * list-item.
     */
    LIST_ITEM_LABEL("fo:list-item-label", RenderType.EXCEL | RenderType.WORD),

    /**
     * используется для представления номера текущей страницы.
     */
    PAGE_NUMBER("fo:page-number", RenderType.EXCEL | RenderType.WORD),
    /**
     * используется для ссылки на номер страницы для страницы, содержащей первую нормальную область, возвращаемую 
     * цитируемым ОФ.
     */
    PAGE_NUMBER_CITATION("fo:page-number-citation", RenderType.EXCEL | RenderType.WORD),

    /**
     * Этот регион определяет порт просмотра, размещённый на стороне "before" региона fo:region-body.
     */
    REGION_BEFORE("fo:region-before", RenderType.EXCEL | RenderType.WORD),
    /**
     * Этот регион определяет порт просмотра, размещённый на стороне "after" региона fo:region-body.
     */
    REGION_AFTER("fo:region-after", RenderType.EXCEL | RenderType.WORD),
    /**
     * Этот регион определяет пару порт просмотра/ссылка, размещённую "center" в fo:simple-page-master.
     */
    REGION_BODY("fo:region-body", RenderType.EXCEL | RenderType.WORD),
    /**
     * оболочка для всех мастеров, используемых в документе.
     */
    LAYOUT_MASTER_SET("fo:layout-master-set", RenderType.EXCEL | RenderType.WORD),

    //region специфичные значения из WORD рендерера
    /**
     * Атрибут master-reference этого элемента идентифицирует используемую мастер-страницу.
     */
    SINGLE_PAGE_MASTER_REFERENCE("fo:single-page-master-reference", RenderType.WORD),

    /**
     * позволяет определить, что сколько бы страниц не понадобилось для размещения всего содержимого документа, все 
     * страницы будут создаваться на основе одной мастер-страницы.
     */
    REPEATABLE_PAGE_MASTER_REFERENCE("fo:repeatable-page-master-reference", RenderType.WORD),

    /**
     * задает различные мастер-страницы для первой страницы, четных и нечетных страниц, пустых страниц, последних 
     * четных и последних нечетных страниц.
     */
    REPEATABLE_PAGE_MASTER_ALTERNATIVES("fo:repeatable-page-master-alternatives", RenderType.WORD),

    /**
     * элемент, в котором должны быть сгруппированы мастер-страницы, когда их задается несколько.
     */
    PAGE_SEQUENCE_MASTER("fo:page-sequence-master", RenderType.WORD);
    //endregion

    /**
     * наименование FO элемента.
     */
    private String foName;

    /**
     * тип рендеринга, для которого используется значение.
     */
    private int renderType;

    /**
     * Конструктор.
     * @param foName     - наименование FO элемента
     * @param renderType - тип рендеринга
     */
    AreaType(String foName, int renderType) {
        this.foName = foName;
        this.renderType = renderType;
    }

    /**
     * Получение типа области, соответствующего FO элементу.
     * @param foNodeName - наименование FO элемента
     * @param renderType - тип рендеринга
     * @return AreaType возвращает тип области, соответствующий FO элементу или AreaType.UNDEFINED
     */
    public static AreaType parseValue(String foNodeName, int renderType) {
        return Arrays.stream(AreaType.values())
                .filter(m -> m.foName.equals(foNodeName) && (renderType == (m.renderType & renderType))).findAny()
                .orElse(AreaType.UNDEFINED);
    }

    public String getFoName() {
        return foName;
    }
}
