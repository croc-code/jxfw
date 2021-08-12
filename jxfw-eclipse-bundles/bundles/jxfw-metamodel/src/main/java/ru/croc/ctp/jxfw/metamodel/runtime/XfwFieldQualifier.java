package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.Set;

/**
 * Методы, осуществляющие поиск различных свойств в метаданных.
 */
public interface XfwFieldQualifier {

    /**
     * Получить все скалярные свойста, тип которых наследует от заданного класса.
     *
     * @param clazz класс
     * @return набор свойств.
     */
    Set<XfwStructuralFeature> getScalarFieldsOfType(Class<?> clazz);


    /**
     * Получить все массивные свойста, тип которых наследует от заданного класса.
     *
     * @param clazz класс
     * @return набор свойств.
     */
    Set<XfwStructuralFeature> getMassiveFieldsOfType(Class<?> clazz);


    /**
     * Получить все комплексные свойста.
     *
     * @return набор свойств.
     */
    Set<XfwStructuralFeature> getComplexFields();

    /**
     * Получить все свойста типа перечисление.
     *
     * @return набор свойств.
     */
    Set<XfwStructuralFeature> getEnumFields();

    /**
     * Получить все свойста типа перечисление - флаги.
     *
     * @return набор свойств.
     */
    Set<XfwStructuralFeature> getEnumFlagsFields();


    /**
     * Получить свойство - идентификатор доменного объекта.
     *
     * @return свойство
     */
    XfwAttribute getIdField();



    /**
     * Получить свойство - версию доменного объекта.
     *
     * @return свойство
     */
    XfwAttribute getVersionField();

}
