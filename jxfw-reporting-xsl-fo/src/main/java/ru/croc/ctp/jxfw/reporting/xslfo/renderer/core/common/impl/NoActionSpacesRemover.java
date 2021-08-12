package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.impl;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.ISpacesRemover;
/**
 * Возвращает исходное значение, прикладной проект зная свои данные может таким образом,
 * ускорить их обработку.
 *
 * @author SMufazzalov
 * @since 1.6.3
 */
public class NoActionSpacesRemover implements ISpacesRemover {

    @Override
    public String removeDoubleSpaces(String value, boolean isKeepFirstSpace, boolean isKeepLastSpace) {
        return value;
    }

}