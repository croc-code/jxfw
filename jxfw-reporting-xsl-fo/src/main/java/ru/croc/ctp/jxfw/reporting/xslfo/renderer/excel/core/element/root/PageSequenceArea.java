package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.catchword.CatchwordSection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:page-sequence.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class PageSequenceArea extends GenericArea {

    /**
     * Массив нижних колонтитулов.
     */
    private final String[] footer = new String[3];

    /**
     * Номер первой страницы.
     */
    private int initialPageNumber = -1;

    /**
     * Конструктор с параметрами.
     * @param parentArea    - Родительская область
     * @param attributeList - Список атрибутов
     */
    public PageSequenceArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);

        preProcessProperties();
    }

    /**
     * Свойство - тип области.
     * @return AreaType - возвращает тип области
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.PAGE_SEQUENCE;
    }

    /**
     * Свойство - массив нижних колонтитулов.
     * @return String[] - возвращает массив нижних колонтитулов
     */
    public String[] getFooter() {
        return footer;
    }

    /**
     * Свойство - хоть один нижний колонтитул определен ?.
     * @return boolean - возвращает true, если хоть один нижний колонтитул определен и false в противном случае
     */
    public boolean isFooterDefined() {

        int length = 0;
        for (int i = footer.length - 1; i >= 0; i--) {
            if (footer[i] != null) {
                length += footer[i].length();
            }
        }

        // Эксель поддерживает колонтитулы только до 255 символов в сумме!!!
        return length > 0 && length <= Byte.MAX_VALUE;
    }

    /**
     * Свойство - номер первой страницы.
     * @return int  - возвращает номер первой страницы
     */
    public int getInitialPageNumber() {
        return initialPageNumber;
    }

    /**
     * Свойство - номер первой страницы.
     * @param initialPageNumber - номер первой страницы
     */
    public void setInitialPageNumber(int initialPageNumber) {
        this.initialPageNumber = initialPageNumber;
    }

    /**
     * Свойство - номер первой страницы определен ?.
     * @return boolean - возвращает true, если номер первой страницы определен и false в противном случае
     */
    public boolean isInitialPageNumberDefined() {
        return initialPageNumber != -1;
    }

    /**
     * Добавление нижнего колонтитула.
     * @param script           - Текст колонтитула
     * @param catchwordSection - Секция колонтитула
     */
    public void addFooterCatchword(String script, CatchwordSection catchwordSection) {

        if (StringUtils.isBlank(script)) {
            return;
        }
        // По умолчанию выравнивание влево
        int section = (catchwordSection != CatchwordSection.UNDEFINED) ? catchwordSection.value()
                : CatchwordSection.LEFT.value();
        // Устанавливаем колонтитул
        String oldValue = footer[section];
        footer[section] = StringUtils.isBlank(oldValue) ? script : String.join(StringUtils.EMPTY,
                oldValue, String.valueOf(GlobalData.LINE_FEED_CHAR), script);

        // Остальные секции, если уже заданы, надо сместить вверх на такое же кол-во строк
        int lineFeedCount = 1;
        for (int i = script.length() - 1; i >= 0; i--) {
            if (script.charAt(i) == GlobalData.LINE_FEED_CHAR) {
                lineFeedCount++;
            }
        }

        script = StringUtils.repeat(String.valueOf(GlobalData.LINE_FEED_CHAR), lineFeedCount);
        for (int i = footer.length - 1; i >= 0; i--) {
            if (i != section && !StringUtils.isBlank(footer[i])) {
                footer[i] += script;
            }
        }
    }

    /**
     * Обработка свойств области. Все свойства должны быть установлены.
     * Общая реализация.
     */
    private void preProcessProperties() {

        // Обработка свойства начальной страницы
        String initialPageNumber = (String) getPropertyValue(FoPropertyType.INITIAL_PAGE_NUMBER);

        if (StringUtils.isBlank(initialPageNumber)) {
            return;
        }

        // Если значение атрибута - "auto"
        if (GlobalData.INITIAL_PAGE_NUMBER_AUTO.equals(initialPageNumber)) {
            setInitialPageNumber(GlobalData.DEFAULT_INITIAL_PAGE_NUMBER);
        } else {
            Integer parsedPageNumber = Integer.parseInt(initialPageNumber);
            if (null != parsedPageNumber) {
                setInitialPageNumber(parsedPageNumber);
            }
        }
    }
}
