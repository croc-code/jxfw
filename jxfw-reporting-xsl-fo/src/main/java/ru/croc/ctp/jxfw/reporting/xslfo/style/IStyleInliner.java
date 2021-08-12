package ru.croc.ctp.jxfw.reporting.xslfo.style;

/**
 * Умеет заинлайнить смешанные стили вида
 * class="x-report-empty" font-size="10pt" color="#FFFFFF"
 * распарсив имя класса из файла css.
 */
public interface IStyleInliner {
    /**
     * Стиль.
     *
     * @param styleString строка со стилями class="x-report-empty" font-size="10pt" color="#FFFFFF"
     * @return заинлайненный.
     */
    String inlined(String styleString);
}
