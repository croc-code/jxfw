package ru.croc.ctp.jxfw.core.datasource.impl;

import java.util.Arrays;
import java.util.List;

/**
 * Набор утилит для парсинга парметров специфичных XFW
 *
 * @author Nosov Alexander
 *         on 19.09.15.
 * @deprecated since 1.6
 */
@Deprecated
public class WebParamUtil {

    /**
     * Обертка для сплита входных данных об доп. подружаемых свойствах
     *
     * @param expandParam - строка вида "Comment,Survey.Question"
     * @return список свойств
     */
    public static List<String> parseExpandParam(String expandParam) {
        return Arrays.asList(expandParam.split(","));
    }
}
