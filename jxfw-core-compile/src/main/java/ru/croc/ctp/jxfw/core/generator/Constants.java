package ru.croc.ctp.jxfw.core.generator;

import ru.croc.ctp.jxfw.core.generator.meta.XFWBlobInfo;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;

/**
 * Назначение класса агрегация констант используемых при генерации, иначе разбросаны по коду как inline
 *
 * @author SMufazzalov
 * @since 1.4
 */
public class Constants {

    /**
     * Имя поля для serialVersionUID.
     */
    public static final String SERIAL_VERSION_UID_LABEL = "serialVersionUID";
    /**
     * Значение для serialVersionUID.
     */
    public static final String SERIAL_VERSION_UID_EXPR = "1L";
    /**
     * Порядок поля в комплексном ключе, кладем в аннотацию attributeProperties.
     */
    public static final String COMPLEX_KEY_ORDER = "complexKeyOrder";
    /**
     * Сообщение об ошибке не объяаленног языка по умолчанию в пропертях.
     */
    public static final String ERR_MSG_NO_DEFAULT = "Default language should be set in xtend.properties file";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_LENGTH = "Language code should be 2 chars length as for ISO 639-1";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_STRING_TYPE_EXPECTED = "Type must be a String";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_CASCADE = "Cascade parameter is not supported";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_ENUM_MARK = "All enum elements should be marked with XFWEnumId annotation";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_UNSUPPORTED_ANNOTATION_FOR_CLASS = "Annotation %s unsupported in class %s";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_UNSUPPORTED_ANNOTATION_FOR_FIELD_OF_CLASS
            = "Annotation %s unsupported in class %s field %s";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_NOT_PUBLIC_SET_METHOD = "Set method %s must have public access in class %s";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_NOT_PUBLIC_GET_METHOD = "Get method %s must have public access in class %s";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_FIELD_NAME_IS_VERSION =
            "Field name doesn't must be \"version\" without @Version annotation.";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_YOU_USE_TWO_ANNOTATION_VERSION_FOR_ONE_FILED =
            "You use two annotations @Version for one filed.";
    /**
     * Сообщение об ошибке использования Blob полей в трансиент доменных объектах.
     */
    public static final String ERR_BAN_BLOB_IN_TRANSIENT_MODEL =
            "No Blob field type allowed in transient objects";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_DATA_SOURCE_METHOD_PARAMETR_IS_DOMAIN_TYPE =
            "The parameter \"%s\" cannot have domain type(%s).";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_MANY_TO_MANY_MAPPEDBY_IS_NOT_EXISTS =
            "MappedBy param of ManyToMany is not exists.";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_MANY_TO_MANY_MAPPEDBY_FOUND_TWICE =
            "MappedBy param of ManyToMany is found twice.";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_ANNOTATION_COMBINATION_IS_INADMISSIBLE =
            "The annotations combination of %s and %s is inadmissible";
    /**
     * Предупреждение.
     */
    public static final String ERR_MSG_ANNOTATION_COMBINATION_IS_INEFFICIENT =
            "The annotations combination of %s and %s is inefficient";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_MANY_TO_MANY_TARGET_ENTITY_NOT_IS_EXISTS =
            "Target entity of ManyToMany is not exists.";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_PERSISTENCE_IS_NOT_TRANSIENT_OBJECT_USE_TEMP_PARAM =
            "Сan not use the temp parameter when the object is not TRANSIENT.";/**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_DIFFERENT_PERSISTENCE_TYPE_FOR_HIERARCHICAL =
            "Classes hierarchical can not use different persistence type.";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_MSG_DIFFERENT_PARAMS = "We cannot use different params value - XFWBasic(optional) "
            + "XFWMany[One]ToOne(optional) JoinColumn(nullable)";
    /**
     * Сообщение об ошибке.
     */
    public static final String ERR_SHADOWING_FORBIDDEN = "Child class fields should not shadow parent class fields";
    /**
     * Наименование проперти {@link XFWElementLabel#lang()}.
     */
    public static final String LANG = "lang";
    /**
     * Наименование проперти {@link XFWElementLabel#propName()}.
     */
    public static final String PROP_NAME = "propName";
    /**
     * Наименование проперти в файле.
     */
    public static final String LANG_PROP_NAME = "jxfw.default-lang";
    /**
     * Свойство определяет, должно ли для доменного объекта сохраняться состояние при операциях чтения и записи, 
     * для возможности получить состояние объекта до изменений. Значение по умолчанию для {@link XFWObject#saveState}. 
     * Если установлено в true, не учитывается значение из аннотации.
     */
    public static final String SAVE_STATE_PROP_NAME = "jxfw.domain.service.save-state";
    /**
     * Наименование проперти в файле. true/false общая настройка, откуда производить поиск при использовании fulltext
     */
    public static final String FULLTEXT_PROP_NAME = "jxfw.use-fulltext-search-by-default";
    /**
     * Наменование проперти в деталях ecore аннотации
     * {@link XFWConstants#SEARCH_CLASS_ANNOTATION_SOURCE}
     */
    public static final String FULLTEXT_ECORE_USE_FULLTEXT_BY_DEFAULT = "useFulltextByDefault";
    /**
     * Генерация дополнительных полей для работы с Blob.
     */
    public static final String ADDITIONAL_FIELDS_FOR_BLOB = "jxfw.generateAdditionalFieldsForBlobType";
    /**
     * Имя файла пропертей.
     */
    public static final String XTEND_PROPERTIES = "xtend.properties";
    /**
     * Имя файла пропертей.
     */
    public static final String KEYSPACE_PROPERTIES = "keyspace.properties";
    /**
     * Суффикс. {@link XFWBlobInfo#fileNameFieldNameSuffix()}
     */
    public static final String FILE_NAME_SUFFIX = "fileNameFieldNameSuffix";
    /**
     * Суффикс. {@link XFWBlobInfo#sizeFieldNameSuffix()}
     */
    public static final String FILE_SIZE_SUFFIX = "sizeFieldNameSuffix";
    /**
     * Суффикс. {@link XFWBlobInfo#contentTypeFieldNameSuffix()}
     */
    public static final String FILE_TYPE_SUFFIX = "contentTypeFieldNameSuffix";
    /**
     * org.springframework.data.solr.core.mapping.Indexed#searchable.
     */
    public static final String FULLTEXT_SEARCHABLE = "searchable";
    /**
     * org.springframework.data.solr.core.mapping.Indexed#stored.
     */
    public static final String FULLTEXT_STORED = "stored";
    /**
     * XFWSearchField#indexed.
     */
    public static final String FULLTEXT_INDEXED = "indexed";
    /**
     * XFWSearchClass#dbSearchType.
     */
    public static final String FULLTEXT_DB_SEARCH_TYPE = "dbSearchType";
    /**
     * org.springframework.data.solr.core.mapping.SolrDocument#solrCoreName.
     */
    public static final String FULLTEXT_SOLR_CORE_NAME = "solrCoreName";
    /**
     * XFWSearchClass#version.
     */
    public static final String FULLTEXT_SEARCH_VERSION = "version";

}
