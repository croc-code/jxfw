package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout;

/**
 * Значения свойств внешних и внутренних отступов.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class MarginProps {

    /**
     * Величина отступа от верхнего края элемента.
     */
    private int marginTop;

    /**
     * Величина отступа от нижнего края элемента.
     */
    private int marginBottom;

    /**
     * Значение поля сверху содержимого элемента.
     */
    private int paddingTop;

    /**
     * Значение поля снизу содержимого элемента.
     */
    private int paddingBottom;

    /**
     * Величина отступа от левого края элемента.
     */
    private int marginLeft;

    /**
     * Величина отступа от правого края элемента.
     */
    private int marginRight;

    /**
     * Значение поля слева от содержимого элемента.
     */
    private int paddingLeft;

    /**
     * Значение поля справа от содержимого элемента.
     */
    private int paddingRight;

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }
}
