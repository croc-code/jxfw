package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.vt;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

/**
 * Класс, инкапсулирующий обработка атрибута vt блока, представляющий тип данных в ячейке Excel.
 * Created by vsavenkov on 07.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ValueType {

    /**
     * Содержит значение стиля типа данных.
     */
    private int number;

    /**
     * Содержит значение является ли тип данных DateTime.
     */
    private boolean isDateTime;

    /**
     * Содержит значение является ли тип данных Integer.
     */
    private boolean isInteger;

    /**
     * Содержит значение является ли тип данных Float.
     */
    private boolean isFloat;

    /**
     * Содержит значение является ли тип данных Double.
     */
    private boolean isDouble;

    /**
     * Конструктор.
     * @param dataType - Тип данных
     */
    public ValueType(String dataType) {

        switch (dataType) {
            case GlobalData.VALUE_TYPE_I2:
            case GlobalData.VALUE_TYPE_I4:
                number = 1;
                isInteger = true;
                break;
            //////////////////////////////////////////////////////////////////////////
            // 23.11.2006 DKL
            // Изменено по запросу заказчика. см. письмо от 20.11.2006
            case GlobalData.VALUE_TYPE_FIXED_14_4:
                number = 39;
                isFloat = true;
                break;
            //
            //////////////////////////////////////////////////////////////////////////
            case GlobalData.VALUE_TYPE_R4:
            case GlobalData.VALUE_TYPE_R8:
                number = 0;
                isDouble = true;
                break;
            case GlobalData.VALUE_TYPE_DATETIME_TZ:
                number = 22;
                isDateTime = true;
                break;
            case GlobalData.VALUE_TYPE_DATE:
                number = 14;
                isDateTime = true;
                break;
            //////////////////////////////////////////////////////////////////////////
            // 23.11.2006 DKL
            // Изменено по запросу заказчика. см. письмо от 20.11.2006
            case GlobalData.VALUE_TYPE_TIME_TZ:
                number = 21;
                isDateTime = true;
                break;
            //
            //////////////////////////////////////////////////////////////////////////
            case GlobalData.VALUE_TYPE_STRING:
            case GlobalData.VALUE_TYPE_UUID:
                number = 49;
                break;
            default:
                // В импортруемом коде ничего не было
        }
    }

    /**
     * Свойство - номер типа данных.
     * @return byte - возвращает номер типа данных
     */
    public byte getNumber() {
        return (byte) number;
    }

    /**
     * Свойство - тип данных Integer ?.
     * @return boolean - возвращает true, если тип данных Integer и false в противном случае
     */
    public boolean isInteger() {
        return isInteger;
    }

    /**
     * Свойство - тип данных Float ?.
     * @return boolean - возвращает true, если тип данных Float и false в противном случае
     */
    public boolean isFloat() {
        return isFloat;
    }

    /**
     * Свойство - тип данных Double ?.
     * @return boolean - возвращает true, если тип данных Double и false в противном случае
     */
    public boolean isDouble() {
        return isDouble;
    }

    /**
     * Свойство - тип данных DateTime ?.
     * @return boolean - возвращает true, если тип данных DateTime и false в противном случае
     */
    public boolean isDateTime() {
        return isDateTime;
    }
}
