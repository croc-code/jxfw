package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

/**
 * Класс возврата для методов, возвращающих флаг и некое значение.
 * Используется для замены возвращаемого значения методов, принимавших в .Net ref параметр и возвращавших флаг
 * Created by vsavenkov on 23.08.2017.
 * @param <T> - тип возвращаемого значения
 */
public class ValueWithFlagResult<T> {

    /**
     * Возвращаемый признак.
     */
    private boolean flag;

    /**
     * Возвращаемое значение.
     */
    private T value;

    /**
     * Конструктор.
     * @param flag  - Возвращаемый признак.
     * @param value Возвращаемое значение.
     */
    public ValueWithFlagResult(boolean flag, T value) {
        this.flag = flag;
        this.value = value;
    }

    public boolean isFlag() {
        return flag;
    }

    public T getValue() {
        return value;
    }
}
