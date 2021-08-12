package ru.croc.ctp.jxfw.transfer.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToProperty;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.transfer.component.exp.exception.ExportBinaryPropertyException;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportBinaryPropertyException;
import ru.croc.ctp.jxfw.transfer.domain.DomainToServicesResolverTransfer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.Map;

/**
 * Вспомогательный сервис для преобразования бинарных значений в {@link DomainTo}.
 *
 * @author Golovin Alexander
 * @since 1.6
 */
@Service
public class BinaryPropertyService {
    /** Resolver сервисов DTO. */
    private DomainServicesResolver domainServicesResolver;
    /** Сервис получения бина To-сервиса. */
    private DomainToServicesResolver domainToServicesResolver;
    /** Хранилище ресурсов. */
    private ResourceStore resourceStore;

    /**
     * Заголовки для свойств бинарного свойства в DTO.
     */
    public interface HeaderName {
        /** Заголовок параметра - размер данных. */
        String FILE_SIZE = "size";
        /** Заголовок параметра - имя файла. */
        String FILE_NAME = "fileName";
        /** Заголовок параметра - тип данных. */
        String MIME_TYPE = "mimeType";
        /** Заголовок параметра - значение. */
        String VALUE = "$value";
        /** Заголовок параметра - ид ресурса в хранилище ресурсов. */
        String RESOURCE_ID = "resourceId";
    }

    /**
     * Вспомогательный сервис для преобразования бинарных значений в {@link DomainTo}.
     *
     * @param domainServicesResolver resolver сервисов DTO.
     * @param domainToServicesResolver resolver сервисов DTO.
     * @param resourceStore хранилище ресурсов.
     */
    public BinaryPropertyService(DomainServicesResolver domainServicesResolver,
                                 DomainToServicesResolverTransfer domainToServicesResolver,
                                 ResourceStore resourceStore) {
        this.domainServicesResolver = domainServicesResolver;
        this.domainToServicesResolver = domainToServicesResolver;
        this.resourceStore = resourceStore;
    }

    /**
     * Загружает добавляет бинарные свойства в Base64.
     *
     * @param domainTo DTO
     * @return этот же DTO.
     * @throws ExportBinaryPropertyException если ошибка при заполнении бинарного свойства.
     */
    public DomainTo transformBinaryPropertiesToBase64(DomainTo domainTo) throws ExportBinaryPropertyException {
        for (Map.Entry<String, DomainToProperty> metaInfo : domainTo.getPropertyMetadata().entrySet()) {
            if (isBlob(metaInfo.getValue()) && domainTo.getProperty(metaInfo.getKey()) != null)  {
                final Map<String, Object> binProps = (Map) domainTo.getProperty(metaInfo.getKey());
                binProps.put(HeaderName.VALUE, getBase64BlobProperty(domainTo, metaInfo.getKey()));
            }
        }
        return domainTo;
    }

    /**
     * Достаётся из {@link DomainTo} бинарное свойство в формате Base64.
     * т.к. его можно получить вызвав метод, которого нет в интерфейсе.
     *
     * @param domainTo DTO объект
     * @param binPropertyName имя свойства
     * @return бинарное свойство в формате Base64.
     * @throws ExportBinaryPropertyException если при получении свойства произошла ошибка.
     */
    protected String getBase64BlobProperty(DomainTo domainTo, String binPropertyName)
            throws ExportBinaryPropertyException {
        try {
            // Вызываем метод для получения бинарного свойства через reflection
            final DomainToService domainToService = domainToServicesResolver.resolveToService(domainTo.getType());
            final Method method = domainToService.getClass()
                    .getMethod("getBinProp" + StringUtils.capitalize(binPropertyName), String.class);
            final DomainToServiceImpl.BinPropValue binPropValue =
                    (DomainToServiceImpl.BinPropValue) method.invoke(domainToService, domainTo.getId());

            // Преобразуем значение в строку base64
            if (binPropValue != null && binPropValue.getContent() != null) {
                try (InputStream stream = binPropValue.getContent().getBinaryStream()) {
                    return Base64Utils.encodeToString(StreamUtils.copyToByteArray(stream));
                }
            }
        } catch (Exception e) {
            throw new ExportBinaryPropertyException(e);
        }
        return null;
    }

    /**
     * Проверяет является ли свойство бинарным.
     *
     * @param domainToProperty свойство
     * @return true, если бинарное.
     */
    public boolean isBlob(DomainToProperty domainToProperty) {
        return "Blob".equals(domainToProperty.getTypeName())
                && DomainToProperty.Type.Simple.equals(domainToProperty.getType());
    }

    /**
     * Проверяет является ли свойство бинарным.
     *
     * @param dto DTO с свойством и метаданными к нему
     * @param fieldName имя свойства.
     * @return true, если бинарное.
     */
    public boolean isBlob(DomainTo dto, String fieldName) {
        final DomainToProperty domainToProperty = dto.getPropertyMetadata().get(fieldName);
        return domainToProperty != null ? isBlob(domainToProperty) : false;
    }

    /**
     * Преобразует base64 бинарные свойства в стандартное представление в виде ресурса в хранилище.
     * В DTO будет хранится мета информация и ид ресурса.
     *
     * @param domainTo DTO
     * @return этот же DTO.
     * @throws ImportBinaryPropertyException если ошибка при заполнении бинарного свойства.
     */
    public DomainTo replaceBinaryPropertyValuesToResources(DomainTo domainTo) throws ImportBinaryPropertyException {
        final XfwClass xfwClass = domainServicesResolver.resolveService(domainTo.getType())
                .createNew(null)
                .getMetadata();
        xfwClass.getScalarFieldsOfType(Blob.class).forEach(field -> {
            final String fieldName = field.getName();
            final Map<String, Object> binaryMetaInfo = (Map) domainTo.getProperty(fieldName);
            if (binaryMetaInfo != null) {
                try {
                    replaceBinaryPropertyValueToResource(binaryMetaInfo);
                } catch (IOException e) {
                    throw new ImportBinaryPropertyException(e);
                }
            }
        });
        return domainTo;
    }


    /**
     * Подменяет в значении бинарного свойства контент base64 на идентификатор ресурса в хранилище.
     *
     * @param binaryMetaInfo значени бинарного свойства.
     * @throws IOException если возникли проблемы при взаимодействии с хранилищем ресурсов.
     */
    protected void replaceBinaryPropertyValueToResource(Map<String, Object> binaryMetaInfo) throws IOException {
        final String base64Value = (String) binaryMetaInfo.get(HeaderName.VALUE);
        if (base64Value == null) {
            return;
        }
        final Integer size = (Integer) binaryMetaInfo.get(HeaderName.FILE_SIZE);
        final String resourceId;
        try (InputStream data = new ByteArrayInputStream(Base64Utils.decodeFromString(base64Value))) {
            resourceId = resourceStore.addResource(
                    new ResourceProperties(
                            (String) binaryMetaInfo.get(HeaderName.MIME_TYPE),
                            (String) binaryMetaInfo.get(HeaderName.FILE_NAME),
                            (size != null) ? new Long(size) : 1000L
                    ),
                    data
            );
        }
        binaryMetaInfo.remove(HeaderName.VALUE);
        binaryMetaInfo.put(HeaderName.RESOURCE_ID, resourceId);
    }
}
