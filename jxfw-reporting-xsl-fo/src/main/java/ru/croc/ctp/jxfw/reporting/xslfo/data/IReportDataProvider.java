package ru.croc.ctp.jxfw.reporting.xslfo.data;


import java.util.Map;

/**
 * Интерфейс поставщика данных в нужном Layout'у формате.
 * Created by vsavenkov on 21.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public interface IReportDataProvider {

    /**
     * Возвращает то, что вернул ReportDataSource.GetData в том случае,
     * если этот метод не вернул IDataReader или DataTable.
     * В противном случае - первую строку первого столбца
     * @param dataSourceName    - Наименование источника данных из профиля отчета
     * @param customData        - Пользовательские параметры для получения данных
     * @return  - Скалярное данное
     */
    Object getValue(String dataSourceName, Object customData);

    /**
     * Возвращает IDataReader для последовательного доступа.
     * @param dataSourceName    - Наименование источника данных из профиля отчета
     * @param customData        - Пользовательские параметры для получения данных
     * @return  - Объект ResultSet
     */
    IDataReader getDataReader(String dataSourceName, Object customData);

    /**
     * Возвращает данные для произвольного доступа.
     * @param dataSourceName Наименование источника данных из профиля отчета
     * @param customData Пользовательские параметры для получения данных
     * @return Объект DataTable
     */
    IDataTable getDataTable(String dataSourceName, Object customData);

    /**
     * Преобразование IDataReader-а в IDataTable.
     * В любом случае приводит к закрытию IDataReader-а.
     * @param dataReader    - Чего преобразовывать будем
     * @return IDataTable   - IDataTable с данными из IDataReader-а
     */
    IDataTable convertIDataReaderToDataTable(IDataReader dataReader);

    /**
     * Возвращает данные как карту.
     * @param dataSourceName    - Наименование источника данных из профиля отчета
     * @param customData        - Пользовательские параметры для получения данных
     * @return данные как карту.
     */
    Map<String, Object> getDataAsMap(String dataSourceName, Object customData);

    /**
     * Сервис источника данных по имени.
     * @param dsName имя источника данных
     * @return сервис источника данных
     */
  //  IReportDataSourceService getServiceByDataSourceName(String dsName);
}
