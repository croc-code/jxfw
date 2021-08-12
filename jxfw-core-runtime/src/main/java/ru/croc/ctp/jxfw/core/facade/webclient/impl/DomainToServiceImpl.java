package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.FilterHelper;
import ru.croc.ctp.jxfw.core.facade.webclient.ValueParser;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStoreBlob;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Базовый класс для реализации сервисов трансформации доменных объектов в DTO и обратно.
 *
 * @param <T>   Тип доменного объекта
 * @param <IdT> Тип идентификатора доменного объекта
 */

public abstract class DomainToServiceImpl<T extends DomainObject<IdT>, IdT extends Serializable> implements
        DomainToService<T, IdT>, FilterHelper<T> {
    private static final Logger logger = LoggerFactory.getLogger(DomainToServiceImpl.class);

    private static final String FILE_SIZE = "size";

    private static final String FILE_NAME = "fileName";

    private static final String MIME_TYPE = "mimeType";
    /**
     * Сервис поиска доменных сервисов по типу сущности.
     */
    @Autowired
    protected DomainServicesResolver serviceResolver;
    /**
     * Сервис поиска доменных сервисов по типу сущности.
     *
     * @since 1.6
     */
    protected DomainToServicesResolver domainToServicesResolver;
    /**
     * Включено ли игнорирование элементов доменной модели помеченных аннотацией
     * {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore} для данного фасада.
     *
     * @since 1.6
     */
    protected boolean defaultEnableIgnoreFacade;
    /**
     * Сервис временного хранения файлов.
     */
    protected ResourceStore resourceStore;
    /**
     * Учитывается ли регистр в операторах сравнения строк в фильтрах.
     * Поумолчанию, значение false.
     */
    private boolean isIgnoreCaseForOperatorOfFiltering;


    /**
     * Сервис разбора значений из транспортного формата в целевой тип доменного объекта.
     * Дефолтное значение сервиса устанавливается для поддержки обратной совместимости.
     */
    private ValueParser valueParser = new ValueParserImpl();

    /**
     * Конструктор.
     *
     * @param resourceStore - файловое хранилище
     * @param resolver      сервис для поиска сервисов трансформации доменных объектов
     */
    public DomainToServiceImpl(ResourceStore resourceStore, DomainToServicesResolver resolver) {
        this.resourceStore = resourceStore;
        this.domainToServicesResolver = resolver;
    }

    /**
     * Создает новый экземпляр доменного объекта с заданным идентификатором.
     *
     * @param clazz Тип доменного объекта
     * @param id    Идентификатор доменного объекта
     * @param <T>   Тип доменного объекта
     * @return Созданный экземпляр доменного объекта
     */
    public static <T extends DomainObject<? super String>> T createDummyDomainObject(Class<T> clazz, String id) {
        try {
            final T instance = clazz.newInstance();
            instance.setId(id);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод создает коллекцию параметров бинарного свойства.
     *
     * @param blob     Значение бинарного свойства
     * @param fileName Имя файла
     * @param fileSize размер файла
     * @param mimeType тип контента (MIME Content Type)
     * @return Коллекция параметров бинарного свойства или null, если значение бинарного свойства не задано
     */
    protected Map<String, Object> createBinPropDescriptor(Blob blob, String fileName, Long fileSize, String mimeType) {
        if (blob != null) {
            Map<String, Object> descriptor = new HashMap<>();

            descriptor.put("$value", "LobPropValue");
            descriptor.put(FILE_NAME, fileName);
            descriptor.put(FILE_SIZE, fileSize);
            descriptor.put(MIME_TYPE, mimeType);

            return descriptor;
        }
        return null;
    }

    /**
     * Возвращает имя файла из коллекции параметров бинарного свойства.
     *
     * @param binPropDescriptor Параметры бинарного свойства
     * @return Имя файла или null, если имя файла не задано
     */
    protected String getFileNameFromDescriptor(Object binPropDescriptor) {
        if (binPropDescriptor == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> descriptor = (Map<String, Object>) binPropDescriptor;

        return (String) descriptor.get(FILE_NAME);
    }

    /**
     * Возвращает размер файла из коллекции параметров бинарного свойства.
     *
     * @param binPropDescriptor Параметры бинарного свойства
     * @return Размер файла или null
     */
    protected Long getFileSizeFromDescriptor(Object binPropDescriptor) {
        if (binPropDescriptor == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> descriptor = (Map<String, Object>) binPropDescriptor;

        if (descriptor.get(FILE_SIZE) == null) {
            return null;
        }

        return ((Number) descriptor.get(FILE_SIZE)).longValue();
    }

    /**
     * Возвращает список идентификаторов переданных доменных объектов.
     *
     * @param objectList Список доменных объектов
     * @return Список идентификаторов
     */
    public List<? extends Serializable> getObjectIds(Collection<? extends DomainObject<?>> objectList) {
        if (objectList == null) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(objectList)
                .filter(o -> o != null)
                .map(o -> (Serializable) o.getId())
                .collect(Collectors.toList());
    }

    /**
     * Выключает доменный объект из контекста сохранения данных.
     *
     * @param domainObject Экземпляр доменного объекта
     */
    public void detachEntity(DomainObject<?> domainObject) {

    }

    @Nonnull
    @Override
    public DomainTo toToPolymorphic(T domainObject, String... expand) {
        Assert.notNull(domainObject, "Parameter domainObject should not be null");
        DomainToService<T, IdT> domainToService = domainToServicesResolver.resolveToService(domainObject.getTypeName());
        return domainToService.toTo(domainObject, expand);
    }

    @Override
    public List<DomainTo> toToPolymorphic(List<T> domainObjectList, String type, String... expand) {
        Assert.notNull(domainObjectList, "Parameter domainObject should not be null");
        DomainToService<T, IdT> domainToService = domainToServicesResolver.resolveToService(type);
        return domainToService.toTo(domainObjectList, expand);
    }

    @Override
    public DomainTo toTo(T domainObject, String... expand) {
        return toTo(domainObject);
    }


    /**
     * Преобразует переданных доменный объект в DTO без формирования полей
     * для массивных ссылочных свойств.
     *
     * @param domainObject Доменный объект, который нужно преобразовать в DTO
     * @return DTO доменного объекта
     */
    public DomainTo toTo(T domainObject) {
        return this.toTo(domainObject, new String[]{});
    }

    /**
     * Создает новый экземпляр {@link Blob}.
     *
     * @param binPropDescriptor Структура параметров бинарного свойства
     * @return Созданный экземпляр {@link Blob} или {@code null}
     */
    protected Blob createBlob(Object binPropDescriptor) {
        if (binPropDescriptor == null) {
            return null;
        }

        String resourceId = getResourceId(binPropDescriptor);

        Blob blob = new ResourceStoreBlob(resourceStore, resourceId);
        logger.debug("Created Blob for resourceId: " + resourceId);
        return blob;
    }

	protected String getResourceId(Object binPropDescriptor) {
		@SuppressWarnings("unchecked")
        Map<String, Object> descriptor = (Map<String, Object>) binPropDescriptor;

        String resourceId = (String) descriptor.get("resourceId");
		return resourceId;
	}

    /**
     * Возвращает контейнер свойств хранимого ресурса.
     *
     * @param binPropDescriptor Структура параметров бинарного свойства
     * @return Контейнер свойств хранимого ресурса
     */
    protected ResourceProperties getResourceProperties(Object binPropDescriptor) {
        if (binPropDescriptor == null) {
            return null;
        }

        String resourceId = getResourceId(binPropDescriptor);

        return resourceStore.getResourceProperties(resourceId);
    }

    /**
     * Возвращает название фасада.
     *
     * @return название фасада
     * @since 1.6
     */
    protected abstract String getFacadeName();

    /**
     * Включено ли игнорирование элементов доменной модели помеченных аннотацией
     * {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore} для данного фасада.
     *
     * @param facades список названий фасадов для которых включено. Если null, то включено.
     * @since 1.6
     */
    @Value("${domain.ignore.facades:#{null}}")
    public void setDefaultEnableIgnoreFacades(String[] facades) {
        defaultEnableIgnoreFacade = facades == null ? true : Arrays.asList(facades).contains(getFacadeName());
    }

    @Override
    public boolean isIgnoreCaseForOperatorOfFiltering() {
        return isIgnoreCaseForOperatorOfFiltering;
    }

    @Value("${service.filter.predicate.isIgnoreCase:false}")
    public void setIgnoreCaseForOperatorOfFiltering(boolean ignoreCaseForOperatorOfFiltering) {
        isIgnoreCaseForOperatorOfFiltering = ignoreCaseForOperatorOfFiltering;
    }

    /**
     * Кнтейнер для передачи значения бинарного свойства объекта.
     */
    public static class BinPropValue {
        private final String fileName;
        private final String contentType;
        private final Blob content;

        /**
         * Конструктор.
         *
         * @param fileName    Имя файла
         * @param contentType Тип контента
         * @param content     {@link Blob} для считывания контента
         */
        public BinPropValue(String fileName, String contentType, Blob content) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.content = content;
        }

        public String getFileName() {
            return fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public Blob getContent() {
            return content;
        }
    }


    /**
     * Получить Сервис разбора значений из транспортного формата в целевой тип доменного объекта.
     *
     * @return Сервис разбора значений из транспортного формата в целевой тип доменного объекта.
     */
    public ValueParser getValueParser() {
        return valueParser;
    }

    /**
     * Установить Сервис разбора значений из транспортного формата в целевой тип доменного объекта.
     *
     * @param valueParser Сервис разбора значений из транспортного формата в целевой тип доменного объекта.
     */
    @Autowired
    public void setValueParser(ValueParser valueParser) {
        this.valueParser = valueParser;
    }
}
