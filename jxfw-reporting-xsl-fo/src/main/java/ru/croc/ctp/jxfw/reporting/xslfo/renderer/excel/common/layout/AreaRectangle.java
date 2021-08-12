package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout;

import java.awt.Rectangle;

/**
 * Класс, инкапсулирующий геометрическое расположение области и ее размеры в пикселах.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class AreaRectangle {

    /**
     * Прямоугольник.
     */
    private Rectangle rectangle = new Rectangle(-1, -1, -1, -1);

    /**
     * Минимальная ширина области.
     */
    private int minWidth;

    /**
     * Максимальная ширина области.
     */
    private int maxWidth = Integer.MAX_VALUE >> 2;

    /**
     * Минимальная высота области.
     */
    private int minHeight;

    /**
     * Максимальныая высота области.
     */
    private int maxHeight = Integer.MAX_VALUE >> 2;

    /**
     * Свойство - горизонтальная позиция начала области.
     * @return int  - возвращает горизонтальную позицию начала области
     */
    public int getX() {
        return rectangle.x;
    }

    /**
     * Свойство - горизонтальная позиция начала области.
     * @param startX - горизонтальная позиция начала области
     */
    public void setX(int startX) {
        rectangle.x = startX;
    }

    /**
     * Свойство - вертикальная позиция начала области.
     * @return int  - возвращает вертикальную позицию начала области
     */
    public int getY() {
        return rectangle.y;
    }

    /**
     * Свойство - вертикальная позиция начала области.
     * @param startY - вертикальная позиция начала области
     */
    public void setY(int startY) {
        rectangle.y = startY;
    }

    /**
     * Свойство - ширина области.
     * @return int  - возвращает ширину области
     */
    public int getWidth() {
        return rectangle.width;
    }

    /**
     * Свойство - ширина области.
     * @param width - ширина области
     */
    public void setWidth(int width) {
        // Устанавливаем ширину в рамках пределов
        rectangle.width = width <= maxWidth ? (width >= minWidth ? width : minWidth) : maxWidth;
    }

    /**
     * Свойство - задана ли ширина области.
     * @return boolean  - возвращает true, если задана ширина области и false в противном случае
     */
    public boolean isWidthDefined() {
        return rectangle.width > -1;
    }

    /**
     * Свойство - высота области.
     * @return int  - возвращает высоту области
     */
    public int getHeight() {
        return rectangle.height;
    }

    /**
     * Свойство - высота области.
     * @param height - высота области
     */
    public void setHeight(int height) {
        // Устанавливаем высоту в рамках пределов
        rectangle.height = height <= maxHeight ? (height >= minHeight ? height : minHeight) : maxHeight;
    }

    /**
     * Свойство - задана ли высота области.
     * @return boolean  - возвращает true, если задана высота области и false в противном случае
     */
    public boolean isHeightDefined() {
        return rectangle.height > -1;
    }

    /**
     * Свойство - минимальная ширина области.
     * @return int  - возвращает минимальную ширину области
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     *  Свойство - минимальная ширина области.
     * @param minWidth - минимальная ширина области
     */
    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * Свойство - максимальная ширина области.
     * @return int  - возвращает максимальную ширину области
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Свойство - максимальная ширина области.
     * @param maxWidth - максимальная ширина области
     */
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * Признак того, что ширина достигла максимального размера, заданного для области.
     * @return boolean  - возвращает true, если ширина достигла максимального размера, заданного для области и false
     *      в противном случае
     */
    public boolean isMaximumWidth() {
        return rectangle.width == maxWidth;
    }

    /**
     * Свойство - минимальная высота области.
     * @return int  - возвращает минимальную высоту области
     */
    public int getMinHeight() {
        return minHeight;
    }

    /**
     * Свойство - минимальная высота области.
     * @param minHeight - минимальная высота области
     */
    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * Свойство - максимальная высота области.
     * @return int  - возвращает максимальную высоту области
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * Свойство - максимальная высота области.
     * @param maxHeight - максимальная высота области
     */
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * Признак того, что высота достигла максимального размера, заданного для области.
     * @return boolean  - возвращает true, если высота достигла максимального размера и false в противном случае
     */
    public boolean isMaximumHeight() {
        return rectangle.height == maxHeight;
    }

    /**
     * Конструктор по умолчанию.
     */
    public AreaRectangle() {
    }

    /**
     * Инициализирующий конструктор.
     * @param startX        - Горизонтальная позиция начала области
     * @param startY        - Вертикальная позиция начала области
     * @param width    - Ширина области
     * @param height   - Высота области
     */
    public AreaRectangle(int startX, int startY, int width, int height) {
        
        rectangle.x = startX;
        rectangle.y = startY;
        rectangle.width = width;
        rectangle.height = height;
    }

    /**
     * Создание копии AreaRectangle.
     * @return AreaRectangle    - возвращает копию AreaRectangle
     */
    public AreaRectangle cloneMe() {
        
        AreaRectangle result = new AreaRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        result.setMaxWidth(getMaxWidth());
        result.setMaxHeight(getMaxHeight());
        return result;
    }

    /**
     * Установка фиксированной (неизменяемой) ширины.
     * @param width - Ширина
     */
    public void setFixedWidth(int width) {
        
        setMinWidth(width);
        setMaxWidth(width);
        setWidth(width);
    }

    /**
     * Установка фиксированной (неизменяемой) высоты.
     * @param height - Высота
     */
    public void setFixedHeight(int height) {
        
        setMinHeight(height);
        setMaxHeight(height);
        setHeight(height);
    }
}
