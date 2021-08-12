package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common;

import java.util.Locale;

/**
 * Created by vsavenkov on 14.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class XslFoCulture {

    private static ThreadLocal<Locale> threadLocalScope = new ThreadLocal<>();

    /**
     * Получить локализацию.
     * @return локализацию (установленная для текущего потока)
     */
    public static final Locale getCultureInfo() {
        return threadLocalScope.get();
    }

    /**
     * Установить локализацию (для текущего потока).
     * @param locale локализация
     */
    public static final void setCultureInfo(Locale locale) {
        threadLocalScope.set(locale);
    }


    /**
     * экземпляр класса.
     */
    private XslFoCulture() { }
}
