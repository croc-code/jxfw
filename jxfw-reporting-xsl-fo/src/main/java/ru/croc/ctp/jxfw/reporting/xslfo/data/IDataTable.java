package ru.croc.ctp.jxfw.reporting.xslfo.data;

import java.util.List;

/**
 * Представляет одну таблицу с данными в памяти.
 * Created by vsavenkov on 04.05.2017.
 */
public interface IDataTable {

    /**
     * Возвращает строки таблицы.
     * @return {@literal List<IDataRow>} - Возвращает строки таблицы
     */
    List<IDataRow> getRows();

    /**
     * Получает коллекцию столбцов, принадлежащих данной таблице.
     * @return IDataColumnCollection  - Возвращает:
     *      Коллекция System.Data.DataColumnCollection, содержащая коллекцию объектов
     *      System.Data.DataColumn для таблицы. Если отсутствуют объекты System.Data.DataColumn,
     *      возвращается пустая коллекция.
     */
    IDataColumnCollection getColumns();

    /**
     * Копирует структуру объекта System.Data.DataTable, включая все схемы и ограничения System.Data.DataTable.
     * @return IDataTable   - возвращает новый класс System.Data.DataTable, имеющий ту же схему, что и текущий класс
     *      System.Data.DataTable.
     */
    IDataTable clone();

    /**
     * Создает новый класс System.Data.DataRow, имеющий ту же схему, что и таблица.
     * @return IDataRow - возвращает класс System.Data.DataRow, имеющий ту же схему, что и класс System.Data.DataTable.
     */
    IDataRow newRow();
}
