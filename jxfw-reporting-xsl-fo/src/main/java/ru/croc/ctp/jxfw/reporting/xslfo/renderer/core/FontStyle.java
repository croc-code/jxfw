package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

/**
 * Перечисление стилей шрифта.
 * Аналог System.Drawing.FontStyle в .Net
 * Created by vsavenkov on 28.06.2017.
 */
public enum FontStyle {

    /**
     * Normal text.
     */
    Regular(0),

    /**
     * Bold text.
     */
    Bold(1),

    /**
     * Italic text.
      */
    Italic(2),

    /**
     * Underlined text.
      */
    Underline(4),

    /**
     * Text with a line through the middle.
      */
    Strikeout(8);

    /**
     * значение.
     */
    private final int value;

    /**
     * Конструктор.
     * @param initialValue - значение
     */
    FontStyle(int initialValue) {
        value = initialValue;
    }

    /**
     * Возвращает значение.
     * @return int  - Возвращает значение.
     */
    public int value() {
        return value;
    }
}
