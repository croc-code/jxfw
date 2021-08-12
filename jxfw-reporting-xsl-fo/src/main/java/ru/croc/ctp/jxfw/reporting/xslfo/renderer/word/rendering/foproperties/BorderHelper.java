package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_TOP;

import com.aspose.words.Border;
import com.aspose.words.BorderCollection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute.FoBorder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

/**
 * Вспомогательные методы работы с рамками.
 * Created by vsavenkov on 27.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class BorderHelper {

    /**
     * Установка границ области.
     * @param borderTo   - целевой объект
     * @param borderFrom - объект - источник
     */
    public static void assign(Border borderTo, FoBorder borderFrom) {
        
        if (borderFrom == null || !borderFrom.isDefined()) {
            return;
        }
        borderTo.setColor(borderFrom.getColor());
        borderTo.setLineStyle(borderFrom.getLineStyle());
        borderTo.setLineWidth(borderFrom.getWidth());
    }

    /**
     *  Установка границ области.
     * @param bordersTo - целевой объект
     * @param areaFrom  - объект - источник
     * @throws Exception генерируют классы com.aspose.words
     */
    public static void assign(BorderCollection bordersTo, GenericArea areaFrom) throws Exception {

        if (areaFrom == null) {
            return;
        }
        assign(bordersTo.getTop(), (FoBorder)areaFrom.getPropertyValue(BORDER_TOP));
        assign(bordersTo.getLeft(), (FoBorder)areaFrom.getPropertyValue(BORDER_LEFT));
        assign(bordersTo.getRight(), (FoBorder)areaFrom.getPropertyValue(BORDER_RIGHT));
        assign(bordersTo.getBottom(), (FoBorder)areaFrom.getPropertyValue(BORDER_BOTTOM));
    }

    /**
     * Функция копирует бордюр из источника приемнику.
     * @param borderTo   - целевой объект
     * @param borderFrom - объект - источник
     */
    public static void assign(Border borderTo, Border borderFrom) {
        
        borderTo.setColor(borderFrom.getColor());
        borderTo.setLineStyle(borderFrom.getLineStyle());
        borderTo.setLineWidth(borderFrom.getLineWidth());
        // Следующие 2 свойства закомментированы, ибо не используются нигде. Расскоментить при необходимости
        // oBorderTo.DistanceFromText = oBorderFrom.DistanceFromText;
        // oBorderTo.Shadow = oBorderFrom.Shadow;
    }
}
