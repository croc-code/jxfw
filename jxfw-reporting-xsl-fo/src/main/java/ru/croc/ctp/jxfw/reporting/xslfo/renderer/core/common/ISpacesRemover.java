package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

/**
 * Контракт на удаления двойных пробелов..
 *
 * @author SMufazzalov
 * @since 1.6.3
 */
public interface ISpacesRemover {

    /**
     * Удаление из строки всех пробельных символов, встречающихся более 1 подряд.
     *
     * @param value            - Исходная строка
     * @param isKeepFirstSpace - Сохранять ли начальный пробел
     * @param isKeepLastSpace  - Сохранять ли концевой пробел
     * @return String   - возвращает обработанную строку
     */
    String removeDoubleSpaces(String value, boolean isKeepFirstSpace, boolean isKeepLastSpace);

}
