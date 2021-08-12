package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.visibility;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.ParseBooleanResult;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData;

/**
 * Класс, инкапсулирующий обработку атрибута visibility.
 * Created by vsavenkov on 07.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoVisibility {

    /**
     * Превращение в boolean значения атрибута.
     * @param attributeValue - Значение атрибута
     * @return ParseBooleanResult - возвращает результат разбора
     */
    public static ParseBooleanResult parse(String attributeValue) {

        switch (attributeValue) {
            case GlobalData.VISIBILITY_COLLAPSE:
            case GlobalData.VISIBILITY_HIDDEN:
                return new ParseBooleanResult(true, false);
            case GlobalData.VISIBILITY_VISIBLE:
                return new ParseBooleanResult(true, true);
            default:
                return new ParseBooleanResult(false, false);
        }
    }
}
