package ru.croc.ctp.jxfw.reporting.xslfo.data;

/**
 * Представляет коллекцию объектов System.Data.DataColumn для System.Data.DataTable.
 * Created by vsavenkov on 22.05.2017.
 */
public interface IDataColumnCollection {

    /**
     * Возвращает колонку по её имени.
     * @param name - имя колонки
     * @return IDataColumn  - возвращает колонку
     */
    IDataColumn getColumnByName(String name);

    /**
     * Возвращает колонку по индексу.
     * @param index - индекс колонки
     * @return IDataColumn  - возвращает колонку
     */
    IDataColumn getColumn(int index);

    /**
     * Возвращает кол-во колонок.
     * @return int - возвращает кол-во колонок.
     */
    int getCount();
}
