package ru.croc.ctp.jxfw.core.datasource;

/**
 * реализация загрузчика источника данных для загрузки
 * данных произвольного типа и нетипизированных доплнительных данных.
 * @param <T> тип загружаемых данных
 */
public abstract class GeneralDataSourceLoader<T> extends BaseDataSourceLoader implements DataLoader<T, Object> {
    
}
