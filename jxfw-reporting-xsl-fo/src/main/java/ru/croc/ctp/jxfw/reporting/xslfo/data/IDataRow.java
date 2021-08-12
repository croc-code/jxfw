package ru.croc.ctp.jxfw.reporting.xslfo.data;

/**
 * Представляет строку данных в DataTable.
 * Created by vsavenkov on 04.05.2017.
 */
public interface IDataRow {

    /**
     * Возвращает значение, расположенное в этой строке по индексу.
     * @param index - индекс значения в строке
     * @return Object - возвращает значение, расположенное в этой строке
     */
    Object getItem(int index);

    /**
     * Возвращает данные, сохраненные в столбце, указанном по имени.
     * @param columnName - имя столбца
     * @return Object   - возвращает объект, содержащий данные.
     */
    Object getItemByName(String columnName);

    /**
     * задает все значения для этой строки с помощью массива.
     * @param itemArray - массив значений строки
     */
    void setItemArray(Object[] itemArray);

    /**
     * Размер ряда.
     * @return size
     */
    int size();
}
