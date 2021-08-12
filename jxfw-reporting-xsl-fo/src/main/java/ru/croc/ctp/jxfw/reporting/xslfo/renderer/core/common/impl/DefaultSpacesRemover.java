package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.impl;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.ISpacesRemover;

/**
 * Быстрая (скоростная) реализация удаления двойных пробелов.
 *
 * @author SMufazzalov
 * @since 1.6.3
 */
public class DefaultSpacesRemover implements ISpacesRemover {
    /**
     * Стоит с осторожностью использовать данную реализацию, т.к. в угоду скорости выполнения
     * съедает много ресурсов по памяти. Уже сталкивались с тем что на некоторых отчетах
     * 80% времени уходит на GC и анализ выявил что виновник такая реализация.
     *
     * @param value            - Исходная строка
     * @param isKeepFirstSpace - Сохранять ли начальный пробел
     * @param isKeepLastSpace  - Сохранять ли концевой пробел
     * @return результат
     */
    @Override
    public String removeDoubleSpaces(String value, boolean isKeepFirstSpace, boolean isKeepLastSpace) {
        return HelpFuncs.removeDoubleSpaces(value, isKeepFirstSpace, isKeepLastSpace);
    }
}
