package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BASELINE_SHIFT_BASELINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BASELINE_SHIFT_SUB;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BASELINE_SHIFT_SUPER;

/**
 * Класс, инкапсулирующий обработку атрибута baseline-shift.
 * Created by vsavenkov on 28.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoBaseLineShift {

    /**
     * Верхний индекс шрифта.
     */
    private boolean isSuperscript;

    /**
     * Нижний индекс шрифта.
     */
    private boolean isSubscript;

    /**
     * Инициализирующий конструктор.
     * @param isSuperscript - Верхний индекс шрифта
     * @param isSubscript   - Нижний индекс шрифта
     */
    public FoBaseLineShift(boolean isSuperscript, boolean isSubscript) {
        this.isSuperscript = isSuperscript;
        this.isSubscript = isSubscript;
    }

    /**
     * Разбор значения атрибута baseline-shift.
     * @param attributeValue - Значение атрибута baseline-shift
     * @return FoBaseLineShift  - возвращает экземпляр класса или null
     */
    public static FoBaseLineShift parseFoBaseLineShift(String attributeValue) {

        switch (attributeValue) {
            case BASELINE_SHIFT_BASELINE:
                return new FoBaseLineShift(false, false);
            case BASELINE_SHIFT_SUB:
                return new FoBaseLineShift(false, true);
            case BASELINE_SHIFT_SUPER:
                return new FoBaseLineShift(true, false);
            default:
                return null;
        }
    }

    /**
     *  Верхний индекс шрифта.
     * @return boolean - возвращает true, если верхний индекс шрифта, и false в противном случае
     */
    public boolean isSuperscript() {
        return isSuperscript;
    }

    /**
     * Нижний индекс шрифта.
     * @return boolean - возвращает true, если нижний индекс шрифта, и false в противном случае
     */
    public boolean isSubscript() {
        return isSubscript;
    }

    /**
     * Метод получения состояния объекта в виде строки.
     * @return String   - возвращает строку состояния объекта
     */
    @Override
    public String toString() {
        return String.format("isSuperscript = '%1$b', IsSuscript = '%2$b'", isSuperscript, isSubscript);
    }
}
