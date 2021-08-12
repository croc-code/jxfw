package ru.croc.ctp.jxfw.core.export;

import ru.croc.ctp.jxfw.core.export.impl.model.Column;

/**
 * Строка для экспорта.
 */
public interface ExportRow {

    /**
     * Получить строковое представление данной колонки.
     *
     * @param column конфигруаций колонки.
     * @return строка для экспорта.
     */
    Object getValueOfColumn(Column column);

}
