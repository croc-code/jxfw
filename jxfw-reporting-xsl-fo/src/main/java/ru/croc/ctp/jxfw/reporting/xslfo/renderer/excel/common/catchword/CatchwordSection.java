package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.catchword;

/**
 * Секция колонтитула.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public enum CatchwordSection {
    
    /**
     * Не определёна.
     */
    UNDEFINED(-1),

    /**
     * Слева.
     */
    LEFT(0),

    /**
     * По центру.
     */
    CENTER(1),

    /**
     * Справа.
     */
    RIGHT(2);

    /**
     * значение.
     */
    private final int value;

    /**
     * Конструктор.
     * @param initialValue - значение
     */
    CatchwordSection(int initialValue) {
        value = initialValue;
    }

    /**
     * Возвращает значение.
     * @return int  - Возвращает значение.
     */
    public int value() {
        return value;
    }

    /**
     * Возвращает следующее значение перечисления по возрастанию значения (value).
     * @return int  - Возвращает следующее значение перечисления или null.
     */
    public CatchwordSection next() {

        // начинаем с текущего
        CatchwordSection returnValue = null;
        // перебираем значения
        for (CatchwordSection current : CatchwordSection.values()) {
            // если очередное значение больше текущего и меньше возвращаемого, то берём его.
            // значений в перечислении мало, поэтому не вижу смысла усложнять логику перебора
            // в борьбе за производительность. :)
            if (current.value() > value
                    && (null == returnValue || current.value() < returnValue.value())) {
                returnValue = current;
            }
        }

        return returnValue;
    }
}
