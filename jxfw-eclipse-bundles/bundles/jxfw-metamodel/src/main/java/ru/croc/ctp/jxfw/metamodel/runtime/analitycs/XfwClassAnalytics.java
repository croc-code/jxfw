package ru.croc.ctp.jxfw.metamodel.runtime.analitycs;

import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

/**
 * Аналитика метаданных класса.
 *
 * @since 1.6
 * @author OKrutova
 */
public interface XfwClassAnalytics {

    /**
     * Провести аналитику.
     * @param xfwClass метаданные класса
     */
    void analyze(final XfwClass xfwClass);

}
