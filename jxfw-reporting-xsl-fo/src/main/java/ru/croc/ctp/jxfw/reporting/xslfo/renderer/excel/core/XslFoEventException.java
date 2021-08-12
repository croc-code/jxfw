package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.XslFoEventArgs;

/**
 * Интерфейс для генерации события, возникающего при исключении внутри компонентов.
 * Created by vsavenkov on 10.08.2017.
 */
public interface XslFoEventException {

    /**
     * Событие, возникающее при исключении внутри компонентов.
     * @param sender - инициатор события
     * @param args   - аргументы события
     */
    void eventException(Object sender, XslFoEventArgs args);
}
