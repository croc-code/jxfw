package ru.croc.ctp.jxfw.metamodel;


/**
 * Константы для работы с Ecore моделью.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public enum XFWConstants {

    /**
     * Поле помечено аннотацией @XFWManyToMany @XFWManyToOne @XFWOneToOne @XFWOneToMany.
     */
    RELATION_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/relation_column"),
    /**
     * Поле помечено аннотацией @XFW***ToMany.
     */
    RELATION_MANY_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/relation_many_column"),
    /**
     * Поле помечено аннотацией @JoinTable.
     */
    JOINTABLE_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/join_table"),
    /**
     * Поле помечено аннотацией {@code @XFWElementLabel}.
     */
    I18N_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/i18n"),
    /**
     * Поле помечено аннотацией {@code @XFWReadOnly}.
     */
    READ_ONLY_TYPE_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/readOnlyType"),
    /**
     * Поле доменного объекта.
     */
    OBJECT_PROPS_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/objectProperties"),
    /**
     * Поле помечено аннотацией @XFWComplexType.
     */
    COMPLEX_TYPE_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/complexType"),
    /**
     * Поле помечено аннотацией {@code @Column}.
     */
    COLUMN_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/column"),
    /**
     * Аннотация {@link ru.croc.ctp.jxfw.core.XFWEnum}.
     */
  //  ENUM_TYPE_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/enumType"),
    /**
     * Поле помечено аннотацией {@code @XFWProtected}.
     */
    PROTECTED_ATTR_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/protectedAttr"),
    /**
     * Поле помечено аннотацией {@code @Id}.
     */
    PRIMARY_KEY_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/primaryKey"),
    /**
     * Параметр помечен аннотацией {@code @RequestParam}.
     */
    REQUEST_PARAM_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/requestParam"),
    /**
     * Параметр помечен аннотацией {@link ru.croc.ctp.jxfw.core.ToUpperCase}.
     */
    FIELD_TO_UPPER_CASE("http://www.croc.ru/ctp/model/fieldToUpperCase"),
    /**
     * Аннотация {@link ru.croc.ctp.jxfw.core.XFWContentType}.
     */
    CONTENT_TYPE_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/contentType"),

    /**
     * {@code @Pattern} валидация.
     */
    PATTERN_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/patternValidation"),
    /**
     * {@code @MinInclusive} валидация.
     */
    MIN_INCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/minInclusiveValidation"),
    /**
     * {@code @MinExclusive} валидация.
     */
    MIN_EXCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/minExclusiveValidation"),
    /**
     * {@code @MaxInclusive} валидация.
     */
    MAX_INCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/maxInclusiveValidation"),
    /**
     * {@code @MaxExclusive} валидация.
     */
    MAX_EXCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/maxExclusiveValidation"),
    /**
     * {@code @Max} валидация.
     */
    MAX_VALUE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/maxValueValidation"),
    /**
     * {@code @Min} валидация.
     */
    MIN_VALUE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/minValueValidation"),
    /**
     * {@code @Size} валидация.
     */
    SIZE_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/sizeValidation"),
    /**
     * {@code @{@link javax.validation.constraints.Digits}} валидация.
     */
    DIGITS_VALIDATED_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/digitsValidation"),

    /**
     * аннотация с деталями о дополнитеоьных свойствах полей entity в eCore.
     */
    ATTRIBUTE_PROPS_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/attributeProperties"),
    /**
     * Информация о работе с полнотекстом. {@link ru.croc.ctp.jxfw.core.generator.XFWSearchClass}
     */
    SEARCH_CLASS_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/searchClass"),
    /**
     * Информация о работе с полнотекстом(поля). {@link ru.croc.ctp.jxfw.core.generator.XFWSearchField}
     */
    SEARCH_FIELD_ANNOTATION_SOURCE("http://www.croc.ru/ctp/model/searchField"),
    /**
     * Информация для фасада, что это поле должно игнорироваться. {@code @XFWFacadeIgnore}
     */
    FACADE_IGNORE_ANNOTATION("http://www.croc.ru/ctp/model/facadeIgnore"),
    /**
     * Пометка, что поле используется только на сервере. {@code @XFWServerOnly}
     */
    SERVER_ONLY_ANNOTATION("http://www.croc.ru/ctp/model/serverOnly"),
    /**
     * Информация для фасада, является ли сущность для фасада временной {@code XFWObject}
     */
    XFW_OBJECT_ANNOTATION("http://www.croc.ru/ctp/model/XFWObject"),
    /**
     * Информация для фасада, является ли поле фасада временным{@code XFWTransient}.
     */
    XFW_TRANSIENT_ANNOTATION("http://www.croc.ru/ctp/model/XFWTransient");

    private final String uri;

    /**
     * Конструктор.
     *
     * @param uri Значение URI для аннотации
     */
    XFWConstants(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Формирует URI ecore-аннотации по java аннотации.
     *
     * @param annotationClassName короткое имя класса java аннотации
     * @return URI ecore-аннотации.
     */
    public static String getUri(String annotationClassName) {
        switch (annotationClassName) {
            //FIXME
            case "XFWManyToOne":
            case "XFWOneToOne":
                return RELATION_ANNOTATION_SOURCE.getUri();
            case "XFWManyToMany":
            case "XFWOneToMany":
                return RELATION_MANY_ANNOTATION_SOURCE.getUri();
            case "XFWJoinTable":
                return JOINTABLE_ANNOTATION_SOURCE.getUri();
            case "XFWElementLabel":
            case "XFWElementLabels":
                return I18N_ANNOTATION_SOURCE.getUri();
            case "XFWReadOnly":
                return READ_ONLY_TYPE_ANNOTATION_SOURCE.getUri();
            case "XFWComplexType":
                return COMPLEX_TYPE_ANNOTATION_SOURCE.getUri();
            case "Column":
                return COLUMN_ANNOTATION_SOURCE.getUri();
            case "XFWProtected":
                return PROTECTED_ATTR_ANNOTATION_SOURCE.getUri();
            case "RequestParam":
                return REQUEST_PARAM_ANNOTATION_SOURCE.getUri();
            case "XFWToUpperCase":
                return FIELD_TO_UPPER_CASE.getUri();
            case "XFWContentType":
                return CONTENT_TYPE_ANNOTATION_SOURCE.getUri();

            case "Pattern":
                return PATTERN_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "Min":
                return MIN_VALUE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "Max":
                return MAX_VALUE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "Size":
                return SIZE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "XFWMaxInclusive":
                return MAX_INCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "XFWMaxExclusive":
                return MAX_EXCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "XFWMinExclusive":
                return MIN_EXCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "XFWMinInclusive":
                return MIN_INCLUSIVE_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();
            case "Digits":
                return DIGITS_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri();

            case "XFWSearchClass":
                return SEARCH_CLASS_ANNOTATION_SOURCE.getUri();
            case "XFWSearchField":
                return SEARCH_FIELD_ANNOTATION_SOURCE.getUri();

            case "XFWFacadeIgnore":
                return FACADE_IGNORE_ANNOTATION.getUri();
            case "XFWServerOnly":
                return SERVER_ONLY_ANNOTATION.getUri();
            case "XFWObject":
                return XFW_OBJECT_ANNOTATION.getUri();
            case "XFWTransient":
                return XFW_TRANSIENT_ANNOTATION.getUri();


            default:
                return "http://www.croc.ru/ctp/model/" + annotationClassName;
        }
    }

    /**
     * java - аннотации, помещаемые в ecore отдельным нестандартным образом
     * или не помещаемые в ecore вообще.
     *
     * @param annotationClassName имя класса java аннотации
     * @return - да\нет
     */
    public static boolean isSpecialEcoreAnnotation(String annotationClassName) {
        switch (annotationClassName) {

            /* эти аннотации уже обработаны особым образом
               при генерации ecore
             */
            case "XFWManyToOne":
            case "XFWOneToOne":
            case "XFWManyToMany":
            case "XFWOneToMany":
            case "XFWJoinTable":
            case "XFWElementLabel":
            case "XFWElementLabels":
            case "XFWReadOnly":
            case "XFWComplexType":
            case "XFWProtected":
            case "RequestParam":
            case "XFWToUpperCase":
            case "XFWContentType":

            case "XFWEnum":
            case "XFWEnumId":
            case "XFWEnumerated":


            case "Pattern":
            case "Min":
            case "Max":
            case "Size":
            case "XFWMaxInclusive":
            case "XFWMaxExclusive":
            case "XFWMinExclusive":
            case "XFWMinInclusive":
            case "Digits":

            case "XFWSearchClass":
            case "XFWSearchField":
            case "XFWPrimaryKey":



         /*   case "XFWObject":
            case "Entity":
            case "SuppressWarnings":
            case "Accessors":
            case "SolrDocument":
            case "Indexed":
            case "Table":
            case "XFWFolder":
            case "XFWDocument":
            case "Embeddable":
            case "XFWToString":
            case "NotFound":
            case "Column":
            case "OneToOne":
            case "OneToMany":
            case "ManyToOne":
            case "ManyToMany":
            case "PrimaryKeyColumn":
            case "XFWReadOnlyCheck":
            case "XFWDefaultValue":
            case "XFWBasic":
            case "Basic":
            case "JoinTable":
            case "JoinColumn":*/
                return true;

            default:
                return false;
        }

    }
}
