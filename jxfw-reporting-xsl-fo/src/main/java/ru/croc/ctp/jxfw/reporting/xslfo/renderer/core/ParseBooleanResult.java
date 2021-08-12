package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

/**
 * Класс - ответ метода разбора логического значения.
 * Используется в FoWrapOption::parse, FoVisibility::parse
 * Created by vsavenkov on 07.08.2017.
 */
public class ParseBooleanResult {

    /**
     * Признак успешного разбора.
     */
    private boolean parsed;

    /**
     * Результат разбора.
     */
    private boolean parsedValue;

    /**
     * Конструктор.
     * @param parsed      - признак успешного разбора
     * @param parsedValue - результат разбора
     */
    public ParseBooleanResult(boolean parsed, boolean parsedValue) {
        this.parsed = parsed;
        this.parsedValue = parsedValue;
    }

    public boolean isParsed() {
        return parsed;
    }

    public boolean getParsedValue() {
        return parsedValue;
    }
}
