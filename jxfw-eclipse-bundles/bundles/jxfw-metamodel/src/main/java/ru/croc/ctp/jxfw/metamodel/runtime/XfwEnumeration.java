package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.List;

/**
 * Перечисление в метамодели.
 */
public interface XfwEnumeration extends XfwLocalizable, XfwClassifier, EnumConverter {

    /* EEnum methods */

    /**
     * Все элементы перечисления.
     * @return список.
     */
    List<XfwEnumLiteral> getELiterals();

    /**
     * Найти элемент перечисления по имени.
     * @param var1 имя
     * @return элемент перечисления
     */
    XfwEnumLiteral getEEnumLiteral(String var1);

    /**
     * Найти элемент перечисления по значению из @XFWEnumId.
     * @param var1 значение
     * @return элемент перечисления
     */
    XfwEnumLiteral getEEnumLiteral(int var1);


    /**
     * Получить локализованное имя элемента перечисления.
     * @param anEnum элемент перечисления
     * @param lang код языка
     * @return локализованное имя.
     */
    @SuppressWarnings("rawtypes")
	String getLocalizedValue(Enum anEnum, String lang);


}
