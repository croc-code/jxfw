package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout;

/**
 * Класс представляющий область ячеек в Excel.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class Range {

    /**
     * Горизонтальная позиция начала области.
     */
    private int startX;
    
    /**
     * Вертикальная позиция начала области.
     */
    private int startY;
    
    /**
     * Ширина области.
     */
    private int width;
    
    /**
     * Высота области.
     */
    private int height;

    /**
     * Конструктор по умолчанию.
     */
    public Range() {
    }

    public int getX() {
        return startX;
    }

    public void setX(int startX) {
        this.startX = startX;
    }

    public int getY() {
        return startY;
    }

    public void setY(int startY) {
        this.startY = startY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Проверяет идентичность.
     * @param object - сравниваемый объект
     * @return boolean  - возвращает true, если объекты идентичны и false в противном случае
     */
    public boolean isEquals(Range object) {
        return object != null && object.getX() == getX() && object.getY() == getY() && object.getWidth() == getWidth()
                && object.getHeight() == getHeight();
    }
}
