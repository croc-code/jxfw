package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.EnumSet;

/**
 * Методы конвертации перечислений в целое число и обратно.
 */
public interface EnumConverter {

    /**
     * Конвертация перечисления в целое число.
     *
     * @param anEnum перечисление
     * @return целое
     */
    @SuppressWarnings("rawtypes")
	Integer convertToInt(Enum anEnum);

    /**
     * Конвертация набора перечислений в целое число.
     *
     * @param enumSet набор перечислений
     * @return целое
     */
    @SuppressWarnings("rawtypes")
	Integer convertToInt(EnumSet enumSet);

    /**
     * Конвертация целого цисла в перечисление.
     *
     * @param integer    целое
     * @param targetType тип перечисления
     * @param <T>        тип перечисления
     * @return перечисление
     */
    @SuppressWarnings("rawtypes")
	<T extends Enum> T convertToEnum(Integer integer, Class<T> targetType);

    /**
     * Конвертация целого цисла в набор перечислений.
     *
     * @param integer    целое
     * @param targetType тип перечисления
     * @param <T>        тип перечисления
     * @return набор перечислений
     */
    <T extends Enum<T>> EnumSet<T> convertToEnumSet(Integer integer, Class<T> targetType);

    /**
     * Конвертация строкового литерала в перечисление.
     *
     * @param value      литерал
     * @param targetType тип перечисления
     * @param <T>        тип перечисления
     * @return перечисление
     */
    @SuppressWarnings("rawtypes")
	<T extends Enum> T convertToEnum(String value, Class<T> targetType);

    /**
     * Конвертация строковых литералов в набор перечислений.
     *
     * @param values     литералы через запятую
     * @param targetType тип перечисления
     * @param <T>        тип перечисления
     * @return набор перечислений
     */
    <T extends Enum<T>> EnumSet<T> convertToEnumSet(String values, Class<T> targetType);


}
