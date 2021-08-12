package ru.croc.ctp.jxfw.reporting.xslfo.data;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;

/**
 * Результат, возвращаемый источником данных
 * при статическом построении отчёта.
 * @author PaNovikov
 * @since 17.05.2017.
 */
public class DataSourceReportResult {
    /**
     * Тип результата.
     */
    private enum Type {
        /**
         * Объект, возможно скаляр.
         */
        SCALAR,
        /**
         * Таблица в памяти.
         */
        TABLE,
        /**
         * Ридер.
         */
        READER
    }

    /**
     * Значение.
     */
    private Object value;

    /**
     * Тип результата.
     */
    private Type type;

    /**
     * Конструктор скаляра.
     * @param value значение результата
     */
    public DataSourceReportResult(Object value) {
        this.value = value;
        this.type = Type.SCALAR;
    }

    /**
     * Констурктор таблицы в памяти.
     * @param dataTable таблица в памяти
     */
    public DataSourceReportResult(IDataTable dataTable) {
        this.value = dataTable;
        this.type = Type.TABLE;
    }

    /**
     * Констурктор.
     * @param dataReader ридер
     */
    public DataSourceReportResult(IDataReader dataReader) {
        this.value = dataReader;
        this.type = Type.READER;
    }

    /**
     * Возварщает результат как объект.
     * @return value если это скалярный тип
     */
    public Object getScalarValue() {
        if (type != Type.SCALAR) {
            throw new ReportException("Значение не является скаляром " + value.getClass());
        }
        return value;
    }


    /**
     * Возварщает результат как таблицу в памяти.
     * @return value если это IDataTable
     */
    public IDataTable getDataTable() {
        if (type != Type.TABLE) {
            throw new ReportException("Значение не является таблицой в памяти " + value.getClass());
        }
        return (IDataTable) value;
    }

    /**
     * Возварщает результат как DataReader.
     * @return value если это IDataReader
     */
    public IDataReader getDataReader() {
        if (type != Type.READER) {
            throw new ReportException("Значение не является таблицой в памяти " + value.getClass());
        }
        return (IDataReader) value;
    }

    /**
     * Возвращает DataTable если значение DataTable. Если значение скаляр -
     * пробрасывает исключение IllegalStateException. Если значение IDataReader - преобразует его к DataTable
     * @return таблицу данных в памяти
     */
    public IDataTable getDataTableForce() {
        switch (type) {
            case SCALAR:
                throw new ReportException("Значение является скаляром " + value.getClass());
            case TABLE:
                return (IDataTable) value;
            case READER:
                IDataReader dataReader = (IDataReader) value;
                return dataReader.convertToDataTable();
            default:
                throw new IllegalStateException("Illegal type " + type);

        }
    }

    /**
     * Проверяет является ли значение DataReader.
     * @return true, если значение DataReader
     */
    public boolean valueIsDataReader() {
        return type == Type.READER;
    }

    /**
     * Проверяет является ли значение Скаляром.
     * @return true, если значение DataReader
     */
    public boolean valueIsScalar() {
        return type == Type.SCALAR;
    }
}
