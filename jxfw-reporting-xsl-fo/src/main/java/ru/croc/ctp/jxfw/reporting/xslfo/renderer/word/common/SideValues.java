package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common;

/**
 * Класс, хранящий размеры, связанные со сторонами (верх, низ, лево, право).
 * Например margins и padding
 * Created by vsavenkov on 27.06.2017.
 */
public class SideValues {

    /**
     * Размер сверху.
     */
    private double top = 0;

    /**
     * Размер снизу.
     */
    private double bottom = 0;

    /**
     * Размер слева.
     */
    private double left = 0;

    /**
     * Размер справа.
     */
    private double right = 0;

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }
}
