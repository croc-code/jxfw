package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

/**
 * Specifies the unit of measure for the given data.
 * Аналог System.Drawing.GraphicsUnit в .Net
 * Created by vsavenkov on 24.07.2017.
 */
public enum GraphicsUnit {
    
    /**
     * Specifies the world coordinate system unit as the unit of measure.
     */
    World(0),

    /**
     * Specifies the unit of measure of the display device. Typically pixels for
     * video displays, and 1/100 inch for printers.
     */
    Display(1),

    /**
     * Specifies a device pixel as the unit of measure.
     */
    Pixel(2),

    /**
     * Specifies a printer's point (1/72 inch) as the unit of measure.
     */
    Point(3),

    /**
     * Specifies the inch as the unit of measure.
     */
    Inch(4),

    /**
     * Specifies the document unit (1/300 inch) as the unit of measure.
     */
    Document(5),

    /**
     * Specifies the millimeter as the unit of measure.
     */
    Millimeter(6);

    /**
     * значение.
     */
    private final int value;

    /**
     * Конструктор.
     * @param initialValue - значение
     */
    GraphicsUnit(int initialValue) {
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
