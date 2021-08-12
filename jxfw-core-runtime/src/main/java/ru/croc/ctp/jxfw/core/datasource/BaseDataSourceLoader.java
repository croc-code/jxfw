package ru.croc.ctp.jxfw.core.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainDeserializer;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.validation.ObjectValidator;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Базовая реализация загрузчика источника данных.
 */
public abstract class BaseDataSourceLoader {

    private String expand;
    private Integer top;
    private Integer skip;

    private String orderby;

    private Boolean fetchTotal;

    private ObjectValidator objectValidator;
    private DomainDeserializer domainDeserializer;

    /**
     * Представление параметров запроса данных в виде
     * QueryParams. Не поддерживается для недоменных источников данных.
     *
     * @return параметры запроса
     * @throws UnsupportedOperationException для недоменных источников данных.
     */
    @Nonnull
    public QueryParams<?, ?> queryParams() {
        throw new UnsupportedOperationException("QueryParams cannot be provided for BaseDataSourceLoader");
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public Boolean getFetchTotal() {
        return fetchTotal;
    }

    public void setFetchTotal(Boolean fetchTotal) {
        this.fetchTotal = fetchTotal;
    }


    public ObjectValidator getObjectValidator() {
        return objectValidator;
    }

    @Autowired
    public void setObjectValidator(ObjectValidator objectValidator) {
        this.objectValidator = objectValidator;
    }

    public DomainDeserializer getDomainDeserializer() {
        return domainDeserializer;
    }

    @Autowired
    public void setDomainDeserializer(DomainDeserializer domainDeserializer) {
        this.domainDeserializer = domainDeserializer;
    }


    /**
     * Установить в объект датасорса параметры, принятые в контроллере.
     *
     * @param expand       прелоады
     * @param top          размер старницы
     * @param skip         сколько записей пропустить
     * @param orderby      сортировка
     * @param fetchTotal   признак того, что требуется посчитать общее количество записей без учета пагинации
     * @param filterValues значния параметров источника данных
     */
    public void setParams(@Nullable final String expand, @Nullable final Integer top, @Nullable final Integer skip,
                          @Nullable final String orderby,
                          @Nullable final Boolean fetchTotal, @Nonnull final Map<String, Object> filterValues) {
        if (expand != null) {
            setExpand(expand);
        }
        if (top != null) {
            setTop(top);
        }
        if (skip != null) {
            setSkip(skip);
        }
        if (orderby != null) {
            setOrderby(orderby);
        }
        if (fetchTotal != null) {
            setFetchTotal(fetchTotal);
        }

        setPrimitiveValues(filterValues);

        createFilter();


        if (getFilter() != null) {
            getDomainDeserializer().setProperties(getFilter(), filterValues);
            getObjectValidator().validateAndThrow(getFilter(), getFilter().getTypeName());
        }

        getObjectValidator().validateAndThrow(this, this.getClass().getSimpleName());


    }

    /**
     * Создать объект-фильтр, если он существует в данном источнике данных.
     */
    protected void createFilter() {

    }

    /**
     * Установить в примитивные поля датасорса параметры, принятые в контроллере.
     *
     * @param filterValues значния параметров источника данных
     */
    protected abstract void setPrimitiveValues(@Nonnull final Map<String, Object> filterValues);


    /**
     * Получить объект-фильтр, если он существует в данном источнике данных.
     *
     * @return объект-фильтр или null, если датасорс только с примитивными полями
     */
    public DomainObject getFilter() {
        return null;
    }

}
