package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_AFTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_BEFORE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_CENTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_END;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_JUSTIFY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_MIDDLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_START;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_TOP;

import com.aspose.cells.TextAlignmentType;

/**
 * Класс, инкапсулирующий тип выравнивания.
 * Created by vsavenkov on 28.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoAlignmentType {
    
    /**
     * Парсинг значения атрибута вертикального и горизонтального выравнивания.
     * @param align - значение атрибута text-align, display-align, vertical-align
     * @return int  - возвращает константу из TextAlignmentType, соответствующую переданному выравниванию
     */
    public static int parse(String align) {
        
        switch (align) {
            case ALIGNMENT_TYPE_BEFORE:
            case ALIGNMENT_TYPE_TOP:
                return TextAlignmentType.TOP;
            case ALIGNMENT_TYPE_AFTER:
            case ALIGNMENT_TYPE_BOTTOM:
                return TextAlignmentType.BOTTOM;
            case ALIGNMENT_TYPE_LEFT:
            case ALIGNMENT_TYPE_START:
                return TextAlignmentType.LEFT;
            case ALIGNMENT_TYPE_RIGHT:
            case ALIGNMENT_TYPE_END:
                return TextAlignmentType.RIGHT;
            case ALIGNMENT_TYPE_CENTER:
            case ALIGNMENT_TYPE_MIDDLE:
                return TextAlignmentType.CENTER;
            case ALIGNMENT_TYPE_JUSTIFY:
                return TextAlignmentType.JUSTIFY;
            default:
                return TextAlignmentType.CENTER;
        }
    }
}
