package ru.croc.ctp.jxfw.reporting.xslfo.data.impl;

import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.data.DataSourceReportResult;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataReader;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataTable;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractDataSourceClass;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Класс реализует логику доступа к данным и кэширования данных.
 * Created by vsavenkov on 21.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */

public class DataProvider implements IReportDataProvider {

    /**
     * Коллекция параметров отчета.
     */
    private ReportParams params;

    /// <summary>
    /// Объект доступа к данным
    /// </summary>
    // TODO: постараюсь обойти его использование - а вдруг повезёт private Data.XStorageConnection m_Storage;

    /**
     * Профиль источников данных.
     */
    private List<AbstractDataSourceClass> abstractDataSourceProfiles;

    /**
     * Кэш данных, полученных из источников данных.
     */
    private Hashtable<DataSourceKey, DataSourceReportResult> cache = new Hashtable<>();

    /**
     * Коллекция имен источников данных, подлежащих кэшированию.
     */
    private List<String> toCache = new ArrayList<>();

    /**
     * Класс - композитный ключ для закэшированных данных в cache.
     * Создан ввиду необходимости кэширования по совокупности параметров "Name" и "CustomData"
     */
    private class DataSourceKey {

        /**
         * Имя источника данных.
         */
        protected String name;

        /**
         * Пользовательские данные.
         */
        protected Object customData;

        /**
         * Конструктор.
         * @param name имя источника даных
         * @param customData пользовательские данные
         */
        public DataSourceKey(String name, Object customData) {
            this.name = name;
            this.customData = customData;
        }

        /**
         * Логика сравнения двух ключей.
         * Считается что ключи равны если равны их компоненты
         * @param obj   - С чем сравнивать
         * @return boolean  - true если равны, false иначе
         */
        @Override
        public boolean equals(Object obj) {
            DataSourceKey key = (DataSourceKey) obj;
            if (key == null || name != key.name) {
                return false;
            }
            if (customData == null) {
                return null == key.customData;
            } else {
                return customData.equals(key.customData);
            }
        }

        /**
         * Вычисление Hash-кода для реализации логики сравнения двух ключей.
         * @return int  - Для простоты возвращается Hash-код источника
         */
        @Override
        public int hashCode() {
            //TODO: Возможно стоит каким-то боком прицепить customData?
            return name.hashCode();
        }
    }

    /**
     * Создает новый экземпляр объекта.
     * @param params коллекция параметров отчета
     * @param abstractDataSourceProfiles профили источников данных
     */
    public DataProvider(List<AbstractDataSourceClass> abstractDataSourceProfiles, ReportParams params) {
        // Отложим значения в "сторонку" дабы использовать их при вызове источников данных
        this.params = params;
        this.abstractDataSourceProfiles = abstractDataSourceProfiles;
    }

    /**
     * Список имен источников данных, подлежащих кэшированию (чуств. к регистру).
     * @return cписок имен источников данных, подлежащих кэшированию
     */
    public List<String> getCachedDataSourceNames() {
        return toCache;
    }

    /**
     * Возвращает объект datasource по имени.
     * @param dataSourceName наименование источника данных
     * @return объект datasource
     */
    private AbstractDataSourceClass getDataSource(String dataSourceName)  {
        for (AbstractDataSourceClass ds : abstractDataSourceProfiles) {
            if (ds.getN().equalsIgnoreCase(dataSourceName)) {
                return ds;
            }
        }
        throw new ReportException("No such datasource " + dataSourceName);
    }

    /**
     * Возвращает набор данных, полученный у IReportDataSource.
     * Одновременно реализует логику кэширования данных.
     * @param dataSourceName имя источника данных
     * @param customData произвольные пользовательские данные
     * @return набор данных, полученный у IReportDataSource.
     */
    private DataSourceReportResult getData(String dataSourceName, Object customData) {


        DataSourceKey key = new DataSourceKey(dataSourceName, customData); // Ключ для поиска в кэше

        DataSourceReportResult someData = cache.get(key);
        // Если такой источник уже в кэше - вернем значение из кэша
        if (someData != null) {
            return someData;
        }
        throw new UnsupportedOperationException("getData");

        // Профиль источника данных
       /* AbstractDataSourceClass sourceProfile = getDataSource(dataSourceName);
        if (sourceProfile == null) {
            //Передано некорректное имя профиля - отвалим с ошибкой
            //TODO: Заводить своё исключение?
            throw new ArgumentException("data source profile not found for " + dataSourceName, "dataSourceName");
        }

        // Поднимем DataSource
        IReportDataSource dataSource = DataSourceFactory.getDataSource(sourceProfile);
        ReportDataSourceData reportDataSourceData = new ReportDataSourceData(params, customData);
        // Набор даных, полученных от источника
        someData = dataSource.getData(sourceProfile, reportDataSourceData);

        // Возможно данные надо закэшировать...
        if (!toCache.contains(dataSourceName)) {
            return someData;
        }

        if (!someData.valueIsScalar()) {
            IDataTable dataTable = someData.getDataTableForce();
            someData = new DataSourceReportResult(dataTable);
        }
        // скаляр или таблицу то просто положим в кэш
        cache.put(key, someData);

        return someData;*/
    }

    @Override
    public IDataTable convertIDataReaderToDataTable(IDataReader dataReader) {
        return dataReader.convertToDataTable();
    }

    @Override
    public IDataReader getDataReader(String dataSourceName, Object customData) {
        DataSourceReportResult someData = getData(dataSourceName, customData); // Исходные данные
        return someData.getDataReader();
    }

    @Override
    public IDataTable getDataTable(String dataSourceName, Object customData) {
        DataSourceReportResult someData = getData(dataSourceName, customData); // Исходные данные
        return someData.getDataTable();
    }

    /**
     * Возвращает то, что вернул ReportDataSource.getData как скалярное значение.
     * @param dataSourceName наименование источника данных из профиля отчета
     * @param customData пользовательские параметры для получения данных
     * @return то, что вернул ReportDataSource.getData в том случае, если этот метод не вернул IDataReader или DataTable
     *
     */
    @Override
    public Object getValue(String dataSourceName, Object customData) {
        DataSourceReportResult someData = getData(dataSourceName, customData); // Исходные данные
        return someData.getScalarValue();
    }

    @Override
    public Map<String, Object> getDataAsMap(String dataSourceName, Object customData) {
        //TODO: Реализоавть метод преобразования DataReader или DataTable в Map
        throw new NotImplementedException();
    }

 /*   @Override
    public IReportDataSourceService getServiceByDataSourceName(String dsName) {
        for (AbstractDataSourceClass ds : abstractDataSourceProfiles) {
            if (ds.getN().equalsIgnoreCase(dsName)) {
                IReportDataSource reportDs = DataSourceFactory.getDataSource(ds);
                return reportDs.getReportDataSourceService();
            }
        }
        throw new IllegalArgumentException("No such data source service " + dsName);
    }*/
}
