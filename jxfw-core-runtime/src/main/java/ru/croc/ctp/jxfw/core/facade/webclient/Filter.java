package ru.croc.ctp.jxfw.core.facade.webclient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Условия фильтрации.
 *
 * @author SMufazzalov
 * @see <a href="https://dev.rnd.croc.ru/webclient/docs/latest/docs/objectfilter.html">
 * Наименование ограничений фильтра в документации WC</a>
 * @since 1.5
 */
public class Filter {

    /**
     * == (Режим по умолчанию).
     */
    public static final String EQ = "eq";
    /**
     * != .
     */
    public static final String NE = "ne";

    /**
     * <= .
     */
    public static final String LE = "le";
    /**
     * >= .
     */
    public static final String GE = "ge";
    /**
     * < .
     */
    public static final String LT = "lt";
    /**
     * > .
     */
    public static final String GT = "gt";

    /**
     * Вхождение строки.
     */
    public static final String CONTAINS = "contains";
    /**
     * Вхождение строки case-sensitive.
     */
    public static final String CONTAINS_CS = "contains-cs";
    /**
     * Вхождение строки case-insensitive.
     */
    public static final String CONTAINS_CI = "contains-ci";
    /**
     * Начало строки.
     */
    public static final String STARTS = "starts";
    /**
     * Начало строки case-sensitive.
     */
    public static final String STARTS_CS = "starts-cs";
    /**
     * Начало строки case-insensitive.
     */
    public static final String STARTS_CI = "starts-ci";
    /**
     * Окончание строки.
     */
    public static final String ENDS = "ends";
    /**
     * Окончание строки case-sensitive.
     */
    public static final String ENDS_CS = "ends-cs";
    /**
     * Окончание строки case-insensitive.
     */
    public static final String ENDS_CI = "ends-ci";
    /**
     * Не содержит строки (null не включается).
     */
    public static final String NOT_CONTAINS = "not-contains";
    /**
     * Не содержит строки (null не включается) case-sensitive.
     */
    public static final String NOT_CONTAINS_CS = "not-contains-cs";
    /**
     * Не содержит строки (null не включается) case-insensitive.
     */
    public static final String NOT_CONTAINS_CI = "not-contains-ci";
    /**
     * Не начинается со строки (null не включается).
     */
    public static final String NOT_STARTS = "not-starts";
    /**
     * Не начинается со строки (null не включается) case-sensitive.
     */
    public static final String NOT_STARTS_CS = "not-starts-cs";
    /**
     * Не начинается со строки (null не включается) case-insensitive.
     */
    public static final String NOT_STARTS_CI = "not-starts-ci";
    /**
     * Не оканчивается на строку (null не включается).
     */
    public static final String NOT_ENDS = "not-ends";
    /**
     * Не оканчивается на строку (null не включается) case-sensitive.
     */
    public static final String NOT_ENDS_CS = "not-ends-cs";
    /**
     * Не оканчивается на строку (null не включается) case-insensitive.
     */
    public static final String NOT_ENDS_CI = "not-ends-ci";

    /**
     * Содержит хотя бы один из переданных флагов.
     */
    public static final String SOME = "some";
    /**
     * Содержит хотя бы один из переданных флагов.
     */
    public static final String ANY = "any";
    /**
     * Содержит все переданные флаги.
     */
    public static final String ALL = "all";
    /**
     * Не содержит хотя бы один из переданных флагов (null не включается).
     */
    public static final String NOT_SOME = "not-some";
    /**
     * Не содержит хотя бы один из переданных флагов (null не включается).
     */
    public static final String NOT_ANY = "not-any";
    /**
     * Не содержит все переданные флаги (null не включается).
     */
    public static final String NOT_ALL = "not-all";

    /**
     * Равенство строки case-insensitive.
     */
    public static final String EQ_CI = "eq-ci";

    /**
     * Равенство строки case-sensitive.
     */
    public static final String EQ_CS = "eq-cs";


    /**
     * Пустая коллекция.
     */
    public static final String COL_IS_EMPTY = "null";

    /**
     * Вхождение в коллекцию.
     */
    public static final String IN = "IN";


    /**
     * Применимость: все свойства.
     */
    public static final List<String> ALL_PROPS = Collections.unmodifiableList(Arrays.asList(EQ, NE));
    /**
     * Применимость: типы поддерживающие сравнение больше/меньше.
     */
    public static final List<String> COMPARABLES = Collections.unmodifiableList(Arrays.asList(LE, GE, LT, GT));
    /**
     * Применимость: только для строковых свойств.
     */
    public static final List<String> STRINGS = Collections.unmodifiableList(
            Arrays.asList(
                    CONTAINS, CONTAINS_CS, CONTAINS_CI,
                    STARTS, STARTS_CS, STARTS_CI,
                    ENDS, ENDS_CS, ENDS_CI,
                    NOT_CONTAINS, NOT_CONTAINS_CS, NOT_CONTAINS_CI,
                    NOT_STARTS, NOT_STARTS_CS, NOT_STARTS_CI,
                    NOT_ENDS, NOT_ENDS_CS, NOT_ENDS_CI,
                    EQ_CI, EQ_CS

            )
    );
    /**
     * Применимость: только для флагов.
     */
    public static final List<String> FLAGS = Collections.unmodifiableList(
            Arrays.asList(SOME, ANY, ALL, NOT_SOME, NOT_ANY, NOT_ALL)
    );
}
