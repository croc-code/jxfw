package ru.croc.ctp.jxfw.reporting.xslfo.data;

/**
 * Позволяет читать один или несколько потоков результирующих наборов только
 * в направлении вперед, выполняя команду над источником данных. Он реализуется
 * поставщиками данных .NET Framework, обращающимися к реляционным базам данных.
 * Created by vsavenkov on 04.05.2017. Import from System.Data
 */
public interface IDataReader {

    /**
     * Закрывает объект IDataReader.
     */
    void close();

    /**
     * Перемещает IDataReader к следующей записи.
     * @return boolean  - Возвращает: true, если остались еще строки; в обратном случае - false.
     */
    boolean read();

    /**
     * Конвертация из DataReader в DataTable.
     * @return таблицу в памяти
     */
    IDataTable convertToDataTable();

}
