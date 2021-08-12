package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.ParseBooleanResult;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

/**
 * Класс, инкапсулирующий обработку переносов.
 * Created by vsavenkov on 07.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoWrapOption {

    /**
     * Превращение в boolean атрибута переноса.
     * @param wrap      - Значение атрибута wrap-option
     * @return ParseBooleanResult - возвращает результат разбора
     */
    public static ParseBooleanResult parse(String wrap) {

        switch (wrap) {
            case GlobalData.WRAP_OPTION:
                return new ParseBooleanResult(true, true);
            case GlobalData.NO_WRAP_OPTION:
                return new ParseBooleanResult(true, false);
            default:
                return new ParseBooleanResult(false,
                        ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_WRAP_OPTION);
        }
    }
}
