package ru.croc.ctp.jxfw.reporting.xslfo.data;

/**
 * Представляет схему столбца в таблице System.Data.DataTable.
 * Created by vsavenkov on 22.05.2017.
 */
public interface IDataColumn {

    /**
     * Получается положение столбца в коллекции System.Data.DataColumnCollection.
     * @return int  - Положение столбца. Получает значение -1, если столбец не является членом коллекции
     */
    int getOrdinal();

    /**
     * Возвращает имя столбца в System.Data.DataColumnCollection.
     * @return String   - возвращает имя столбца.
     */
    String getColumnName();
}
