package ru.croc.ctp.jxfw.facade;

import com.squareup.javapoet.JavaFile;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс создания фасада для DataSources
 *
 * @author Nosov Alexander
 *         on 09.12.15.
 */
public interface DataSourceCreator {
    /**
     * Фабричный метод для создания java-файла контроллера для Data Source.
     * 
     * @param controllerName - имя контроллера для datasource
     * @param xfwDataSources - ecore представление datasource
     * @param options - параметры для генерации
     * @param birtReports  Признак того, что делаем контроллер для отчетов BIRT
     * @return - коллекция java-файлов
     */
    List<JavaFile> create(String controllerName, Iterable<XFWDataSource> xfwDataSources,
                          Map<String, Object> options, boolean birtReports);
}
