package ru.croc.ctp.jxfw.facade.solr;

import static com.squareup.javapoet.ClassName.get;
import static com.squareup.javapoet.TypeName.VOID;
import static org.apache.commons.lang.ClassUtils.getPackageName;
import static org.apache.commons.lang.ClassUtils.getShortClassName;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang.WordUtils.uncapitalize;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.JOINTABLE_ANNOTATION_SOURCE;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.PRIMARY_KEY_ANNOTATION_SOURCE;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.RELATION_ANNOTATION_SOURCE;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.isComplexType;

import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java8.lang.Iterables;
import java8.util.stream.StreamSupport;
import org.apache.commons.lang.ClassUtils;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.generator.StorageType;
import ru.croc.ctp.jxfw.facade.FacadeGeneratorUtils;
import ru.croc.ctp.jxfw.facade.ToServiceGenerator;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWReference;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

/**
 * Генерация ТО сервисов для SOLR сущностей.
 *
 * @param <K> - тип для первичного ключа.
 * @author Nosov Alexander
 * @since 1.1
 */
public class ToServiceGeneratorSolr<K extends TypeName> extends
        ToServiceGenerator<K> {

    private static final Logger logger = LoggerFactory.getLogger(ToServiceGeneratorSolr.class);

    private static final TypeName CRITERIA_COMPOSER_TYPE = get(
            "ru.croc.ctp.jxfw.solr.facade.webclient", "CriteriaComposer");

    private static final TypeName SIMPLE_QUERY_TYPE = get(
            "org.springframework.data.solr.core.query", "SimpleQuery");

    private static final TypeName CRITERIA_TYPE = get(
            "org.springframework.data.solr.core.query", "Criteria");

    private static final TypeName SOLR_UTILS_TYPE = get(
            "ru.croc.ctp.jxfw.solr.utils", "XfwSolrUtils");
    public static final String CONVERSION_SERVICE = "conversionService";

    /**
     * Используется ли возможности полнотекста совместно с jpa хранилищем.
     */
    boolean isJpa = false;

    ClassName solrQueryContextType = ClassName.get("ru.croc.ctp.jxfw.solr.predicate", "SolrQueryContext");

    /**
     * @param clazz       - класс ecore модели.
     * @param xfwClasses  - все классы модели.
     * @param storageType - тип хранилища в котором хранится доменная модель.
     * @param keyType     - тип первичного ключа
     */
    public ToServiceGeneratorSolr(XFWClass clazz, Set<XFWClass> xfwClasses, String storageType,
                                  K keyType) {
        super(clazz, xfwClasses, storageType, keyType, false);
    }

    /**
     * @param clazz         - класс ecore модели.
     * @param xfwClasses  - все классы модели.
     * @param storageType   - тип хранилища в котором хранится доменная модель.
     * @param inSeparatePkg - Если ТО сервис генерится для fullText сущности то кладем его в отдельный пакет
     */
    public ToServiceGeneratorSolr(XFWClass clazz, Set<XFWClass> xfwClasses, String storageType, boolean inSeparatePkg) {
        super(clazz, xfwClasses, storageType, FacadeGeneratorUtils.getKeyClassName(clazz, true), inSeparatePkg);

        isJpa = xfwClass.getPersistenceModule().contains("JPA");
    }

    /**
     * Выключает синхронизацию связей ManyToMany и т.д. т.к. модели solr не полноценны, в часности не поддерживают
     * getDirtyAttributes.
     */
    protected boolean enableSynchronizeRelations() {
        return false;
    }

    @Override
    public Predicate<EStructuralFeature> parsePropMethodFieldsFilter() {
        Predicate<EStructuralFeature> notInSystemFieldsPredicate
                = f -> !((f instanceof XFWAttribute) && (((XFWAttribute) f).isVersionField())
                //FIXME
                || f.getName().equals("id"));

        Predicate<EStructuralFeature> searchFieldPredicate = field -> {
            //в касандре добавляем все, а в солре только при наличии аннотации
            final boolean isSearchField = field
                    .getEAnnotation(XFWConstants.SEARCH_FIELD_ANNOTATION_SOURCE.getUri()) != null;
            return (!isJpa || isSearchField);
        };

        return notInSystemFieldsPredicate.and(searchFieldPredicate).and(serverOnlyFilter());
    }

    @Override
    public Predicate<EStructuralFeature> fromToFieldsFilter() {
        return parsePropMethodFieldsFilter();
    }

    @Override
    public Predicate<EStructuralFeature> toToMethodFieldsFilter() {
        return parsePropMethodFieldsFilter();
    }

    @Override
    protected void addKeyMethods() {
        final boolean isSimplePrimaryKey = !useSyntheticKey(xfwClass);

        final TypeSpec.Builder builder = getToService();

        final MethodSpec.Builder serializeKeyMethod = MethodSpec
                .methodBuilder("serializeKey").addModifiers(Modifier.PUBLIC)
                .returns(
                        ClassName.get(String.class)
                )
                .addParameter(getKeyType(), "key");

        if (isSimplePrimaryKey) {
            serializeKeyMethod.beginControlFlow("if (!$1L.canConvert(key.getClass(), $2T.class))",
                    CONVERSION_SERVICE, String.class);
            serializeKeyMethod.addCode("throw new $1T(\"no suitable scalar converter found, from $2T to String\");",
                    RuntimeException.class, getKeyType());
            serializeKeyMethod.endControlFlow();
            serializeKeyMethod.addStatement(
                    "return $1T.serializeKey($2L.convert(key, $3T.class), false)",
                    SOLR_UTILS_TYPE, CONVERSION_SERVICE, String.class);
        } else {
            serializeKeyMethod.addStatement(
                    "return $T.serializeKey(key, true)", SOLR_UTILS_TYPE);
        }

        builder.addMethod(serializeKeyMethod.build());

        final MethodSpec.Builder parseKeyMethod = MethodSpec
                .methodBuilder("parseKey").addModifiers(Modifier.PUBLIC)
                .returns(getKeyType()).addParameter(String.class, "encodedKey");

        if (isSimplePrimaryKey) {
            parseKeyMethod.addStatement("String parsed = $T.parseKey(encodedKey, false)", SOLR_UTILS_TYPE);
            parseKeyMethod.beginControlFlow("if (!$1L.canConvert(parsed.getClass(), $2T.class))",
                    CONVERSION_SERVICE, getKeyType());
            parseKeyMethod.addCode("throw new $1T(\"no suitable scalar converter found, from String to $2T\");",
                    RuntimeException.class, getKeyType());
            parseKeyMethod.endControlFlow();
            parseKeyMethod.addStatement(
                    "return $1L.convert(parsed, $2T.class)", CONVERSION_SERVICE, getKeyType());
        } else {
            parseKeyMethod.addStatement("return $T.parseKey(encodedKey, true)",
                    SOLR_UTILS_TYPE);
        }

        builder.addMethod(parseKeyMethod.build());
    }

    /**
     * dse solr использует синтетический ключ, при работе с композитными
     * ключами, для нашей модели наличие более одного поля аннотацией
     * PrimaryKey, означает работу с композитным ключом.
     *
     * @param xfwClass - исследуемый класс-сущность
     * @return признак используется ли композитный ключ
     */
    private static boolean useSyntheticKey(XFWClass xfwClass) {
        final long foundPrimaryKeys = xfwClass
                .getEAllStructuralFeatures()
                .stream()
                .filter(f -> f instanceof XFWAttribute)
                .map(f -> (XFWAttribute) f)
                .filter(attribute -> attribute
                        .getEAnnotation(PRIMARY_KEY_ANNOTATION_SOURCE.getUri()) != null)
                .count();
        return foundPrimaryKeys > 1;
    }

    /**
     * Вспомогательный класс для дедуплецирования полей в stream api.
     */
    protected static class DistinctFieldWrapper {
        /**
         * {@link EStructuralFeature}.
         */
        protected final XFWReference field;

        /**
         * @param fieldSpec - {@link XFWReference} описание поля.
         */
        DistinctFieldWrapper(XFWReference fieldSpec) {
            this.field = fieldSpec;
        }

        /**
         * @return получить {@link XFWReference} поля.
         */
        XFWReference unwrap() {
            return field;
        }

        @Override
        public int hashCode() {
            return field != null ? field.getEType().getInstanceClassName().hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            ToServiceGeneratorSolr.DistinctFieldWrapper that = (ToServiceGeneratorSolr.DistinctFieldWrapper) obj;

            return !(field != null ? !field.getEType().getInstanceClassName()
                    .equals(that.field.getEType().getInstanceClassName())
                    : that.field != null);
        }
    }

    //доп параметр конструктора
    @Override
    protected void subConstructor(MethodSpec.Builder constructor) {
        constructor.addException(ClassNotFoundException.class);
        constructor.addStatement("addDeclaredFields()");
    }

    /**InheritanceControllerTest
     * Установка полей в генерируемый ТО сервис.
     */
    protected void setFields() {
        getToService().addField(
                FieldSpec.builder(get(Logger.class), "logger")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$1T.getLogger($2L.class)", LoggerFactory.class, getToService().build().name)
                        .build()
        );
        autowireQueryParamsBuilderFactoryBySetter();
        autowireLoadServiceBySetter();

        getToService().addField(FieldSpec.builder(jxfwServiceType, "service")
                .addAnnotation(Autowired.class).build());

        getToService().addField(FieldSpec.builder(
                get(ApplicationEventPublisher.class), "publisher").addModifiers(Modifier.PROTECTED).build());

        /* ParameterizedTypeName doUtilType = ParameterizedTypeName.get(
                get(DomainObjectUtil.class), domainObjectType);
        getToService().addField(FieldSpec.builder(doUtilType, "doUtil")
                .initializer("new $T()", doUtilType).build());*/

        //добавить поле в ToService DefaultConversionService
        ClassName conversionService = get(DefaultConversionService.class);
        getToService()
                .addField(FieldSpec.builder(conversionService, CONVERSION_SERVICE)
                        .initializer("new $T()", conversionService).build());

        getToService().addField(FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class),
                "declaredFields", Modifier.PRIVATE).build());

        final TypeSpec.Builder service = getToService();

        String suffix = null;
        if (!isJpa) {
            suffix = ".";
        } else {
            suffix = ".solr.";
        }

        service.addMethod(MethodSpec
                .methodBuilder("addDeclaredFields")
                .addModifiers(Modifier.PRIVATE)
                .addException(ClassNotFoundException.class)
                .returns(VOID)
                .addCode("Class<?> clazz = Class.forName($1S);\n"
                                + "declaredFields = $2T.stream(clazz.getDeclaredFields())\n"
                                + "\t\t\t.map($3T::getName).collect(java.util.stream.Collectors.toList());\n",
                        getPackageName(xfwClass.getInstanceClassName()) + suffix + xfwClass.getName(),
                        get(Arrays.class), get(Field.class))
                .build());

        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(f -> f instanceof XFWReference)
                .map(f -> (XFWReference) f)
                .filter(f -> !isComplexType(f))
                // XFWReference не может быть системным полем
                // системные поля всегда атрибуты.
                //  .filter(f -> !SYSTEM_FIELDS.contains(f.getName()))
                .map(DistinctFieldWrapper::new)
                .distinct()
                .map(DistinctFieldWrapper::unwrap)
                .forEach(ref -> {
                    if (!Objects.equals(xfwClass.getInstanceClassName(), ref.getEType().getInstanceClassName())) {
                        final String className = ref.getEType().getName();

                        if (StorageType.SOLR.name().equals(storageType) && !isJpa) {
                            final String instanceClassName = ref.getEType().getInstanceClassName();
                            final ClassName typeName = get(
                                    getPackageName(instanceClassName) + getFacadeWebclientSuffix(),
                                    className + "ToService");

                            getToService().addField(
                                    FieldSpec.builder(typeName, uncapitalize(className) + "ServiceTo")
                                            .addModifiers(Modifier.PRIVATE)
                                            .addAnnotation(Autowired.class).build());


                            getToService().addField(
                                    FieldSpec.builder(get(packageName + ".service", className + "Service"),
                                            uncapitalize(className) + "Service")
                                            .addAnnotation(Autowired.class).build());
                        }
                    }
                });
    }

    @Override
    protected void addCrosstableField() {
        if (!isJpa) {
            final TypeSpec.Builder service = getToService();
            List<XFWReference> listFields = getXfwManyToManyAnnotatedFields(xfwClass);
            listFields
                    .stream()
                    .forEach(f -> {
                        final EAnnotation annotation = f
                                .getEAnnotation(XFWConstants.JOINTABLE_ANNOTATION_SOURCE
                                        .getUri());
                        final String crossTableName = annotation
                                .getDetails().get("crossTableName");

                        Verify.verifyNotNull(crossTableName);

                        String fieldName = uncapitalize(crossTableName)
                                + "Service";
                        TypeName fieldType = ClassName.get(packageName
                                + ".service", crossTableName + "Service");
                        service.addField(FieldSpec
                                .builder(fieldType, fieldName)
                                .addAnnotation(Autowired.class).build());
                    });
        }
    }

    @Override
    protected void addToToPolymorphicMethod() {
        if (!inSeparatePkg) {
            return;
        }

        //продолжаем для TO сервисов полнотекста
        ParameterSpec parameterSpec = ParameterSpec.builder(jxfwDomainType, "domainObject").build();

        MethodSpec.Builder toToPolymorphic = MethodSpec.methodBuilder("toToPolymorphic")
                .addModifiers(Modifier.PUBLIC)
                .returns(DomainTo.class)
                .addParameter(parameterSpec);

        toToPolymorphic.addStatement("Assert.notNull(domainObject, \"Parameter domainObject should not be null\")");
        //вообще хак так как передается имя типа, которого нет, но которое потом преобразуется к нужному имени бина
        toToPolymorphic
                .addStatement("$T domainToService = "
                                + "domainToServicesResolver.resolveToService($S+ domainObject.getClass().getSimpleName())",
                        ParameterizedTypeName.get(get(DomainToService.class), jxfwDomainType, WildcardTypeName.subtypeOf(Object.class)), "Solr");
        toToPolymorphic.addStatement("return domainToService.toTo(domainObject)");

        final TypeSpec.Builder builder = getToService();
        builder.addMethod(toToPolymorphic.build());
    }

    @Override
    protected TypeName getParentClass() {
        return ParameterizedTypeName.get(
                get("ru.croc.ctp.jxfw.solr.impl.facade.webclient",
                        "DomainToServiceSolrImpl"), jxfwDomainType,
                getKeyType());
    }

    @Override
    protected void specificFromToEnhancement(MethodSpec.Builder builder) {
        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(f -> f instanceof XFWReference)
                .map(f -> (XFWReference) f)
                .filter(f -> !isComplexType(f))
                // XFWReference не может быть системным полем
                // системные поля всегда атрибуты.
                //  .filter(f -> !SYSTEM_FIELDS.contains(f.getName()))
                .forEach(field -> {
                    final String mappedTypeName = field.getEType()
                            .getName();
                    final String siblingName = uncapitalize(mappedTypeName);
                    String name = field.getName();
                    if (!isJpa) {
                        EAnnotation oneToMany = field
                                .getEAnnotation(RELATION_ANNOTATION_SOURCE
                                        .getUri());
                        if (oneToMany != null
                                && "XFWOneToMany".equals(oneToMany
                                .getDetails().get("name"))) {
                            builder.beginControlFlow(
                                    "if (o.get$L().size() < 1)",
                                    capitalize(name))
                                    //TODO JXFW-1177
                                    //Починить поддержку связей в Solr модули
                                    /*     .addStatement("get$1LDO(dto.getId()).forEach($2L -> { "
                                                         + "$2L.setRemoved(true); context.objects.add($2L);})",
                                                 capitalize(name), siblingName)*/
                                    .endControlFlow();
                        }
                    }
                });
    }

    private static List<XFWReference> getXfwManyToManyAnnotatedFields(XFWClass xfwClass) {
        return xfwClass
                .getEAllStructuralFeatures()
                .stream()
                .filter(f -> f instanceof XFWReference)
                .map(f -> (XFWReference) f)
                .filter(f -> f
                        .getEAnnotation(XFWConstants.RELATION_ANNOTATION_SOURCE
                                .getUri()) != null)
                .filter(f -> f
                        .getEAnnotation(XFWConstants.RELATION_MANY_ANNOTATION_SOURCE
                                .getUri()) != null)
                .filter(f -> f
                        .getEAnnotation(XFWConstants.JOINTABLE_ANNOTATION_SOURCE
                                .getUri()) != null)
                .collect(Collectors.toList());
    }

    private static String obtainIdByRelationAnnotation(XFWReference field) {
        final EMap<String, String> details = field.getEAnnotation(
                RELATION_ANNOTATION_SOURCE.getUri()).getDetails();
        final String relationName = details.get("name");
        switch (relationName) {
            case "XFWManyToOne":
                return field.getName() + "_id";
            case "XFWOneToMany":
                return details.get("mappedBy") + "_id";
            default:
                return null;
        }
    }

    private void addManyToOneDo(TypeSpec.Builder builder, XFWReference ref,
                                String pkName, String toServiceName) {
        builder.addMethod(MethodSpec
                .methodBuilder("get" + capitalize(ref.getName()) + "DO")
                .addModifiers(Modifier.PUBLIC)
                .returns(get(packageName, ref.getEType().getName()))
                .addParameter(getKeyType(), "id")
                .addStatement("$T o = getDomainObjectById(serializeKey(id))",
                        jxfwDomainType)
                .addStatement("String key = o.get$L()", capitalize(pkName))
                .addStatement("if (key == null) {return null;}")
                .addStatement("return $L.getDomainObjectById(key)", toServiceName)
                .build());
    }

    private void addOneToManyDo(TypeSpec.Builder service, XFWReference ref) {
        final String fieldTypeClassName = ref.getEType().getName();
        String packageSuff = inSeparatePkg ? "solr." : "";
        final String instanceClassName = ref.getEType().getInstanceClassName()
                .replace(fieldTypeClassName, packageSuff + fieldTypeClassName);
        final TypeName fieldType = get(
                ClassUtils.getPackageName(instanceClassName),
                fieldTypeClassName);

        String secondParam;
        String statement;
        if (inSeparatePkg) {
            if (fieldTypeClassName.equals(xfwClass.getName())) {
                secondParam = "service";
            } else {
                secondParam = fieldTypeClassName + "Service";
            }
            statement = "$T<$T> objects = $L.getObjects(SolrQueryContext.of(sq))";
        } else {
            secondParam = uncapitalize(fieldTypeClassName);
            statement = "$T<$T> objects = $LService.getObjects(SolrQueryContext.of(sq))";
        }

        service.addMethod(MethodSpec
                .methodBuilder("get" + capitalize(ref.getName()) + "DO")
                .addModifiers(Modifier.PUBLIC)
                .returns(
                        ParameterizedTypeName.get(
                                get(Collection.class),
                                WildcardTypeName.subtypeOf(ParameterizedTypeName
                                        .get(get(DomainObject.class),
                                                WildcardTypeName
                                                        .subtypeOf(Object.class)))))
                .addParameter(getKeyType(), "id")
                .addStatement("$1T sq = new $1T(new $2T(\"$3L\").is(id))",
                        SIMPLE_QUERY_TYPE, CRITERIA_TYPE,
                        obtainIdByRelationAnnotation(ref))
                .addStatement(statement, Iterable.class, fieldType, secondParam)
                .addStatement(
                        "return $T.stream($T.spliterator(objects), false).collect($T.toList())",
                        StreamSupport.class, Iterables.class,
                        java8.util.stream.Collectors.class).build());
    }

    private void addManyToManyDo(TypeSpec.Builder builder, XFWReference ref) {
        final MethodSpec.Builder method = MethodSpec
                .methodBuilder("get" + capitalize(ref.getName()) + "DO")
                .addModifiers(Modifier.PUBLIC)
                .returns(
                        ParameterizedTypeName.get(
                                get(Collection.class),
                                WildcardTypeName.subtypeOf(ParameterizedTypeName
                                        .get(get(DomainObject.class),
                                                WildcardTypeName
                                                        .subtypeOf(Object.class)))))
                .addParameter(getKeyType(), "id");

        final String resultFieldName = getResultFieldName(ref);
        final String crossTableName = getCrossTableName(ref);
        final String mappedTypeName = ref.getEType().getName();
        final String searchField = getSearchFieldOrAnnotationName(ref);

        String listType1 = ClassUtils.getPackageName(crossTableName) + "." + mappedTypeName;


        /**
         * #TODO если поиск полнотекстовый поиск с хранилищем jpa
         * то возьмеем ключевое поле _unique_key
         * сейчас так генерится по умолчанию
         * по необходимости переделать
         * Возможно переделать саму определялку JPA:
         * xfwClass.getPersistenceModule().contains("JPA")
         */
        String keyFieldNameVal;
        if (isJpa) {
            keyFieldNameVal = "_uniqueKey";
        } else {
            final EAnnotation keyAnnotation = xfwClass
                    .getEAllStructuralFeatures()
                    .stream()
                    .filter(f -> f.getEAnnotation(PRIMARY_KEY_ANNOTATION_SOURCE.getUri()) != null)
                    .findFirst()
                    .get()
                    .getEAnnotation(PRIMARY_KEY_ANNOTATION_SOURCE.getUri());
            keyFieldNameVal = keyAnnotation.getDetails().get(
                    "idFieldName");
        }
        final String keyFieldName = keyFieldNameVal;

        final ClassName crossClassType = get(getPackageName(crossTableName),
                getShortClassName(crossTableName));
        method.addStatement("$T $L = getDomainObjectById(id)", jxfwDomainType,
                uncapitalize(xfwClass.getName()))
                .addStatement(
                        "$1T sq = new $1T(new $2T(\"$3L\").is($4L.getId()))",
                        SIMPLE_QUERY_TYPE, CRITERIA_TYPE, searchField,
                        uncapitalize(xfwClass.getName()))
                .addStatement(
                        "$1T page = ($1T) $2LService.getObjects(SolrQueryContext.of(sq))",
                        ParameterizedTypeName.get(get(Page.class),
                                crossClassType),
                        uncapitalize(getShortClassName(crossTableName)))
                .beginControlFlow("if (page.hasContent())")
                .addStatement("$1T<String> ids = new $1T<>()", ArrayList.class)
                .addStatement("$1T<$2L> $3Ls = new $1T<>()", ArrayList.class, listType1,
                        uncapitalize(mappedTypeName))
                .addStatement("$T.forEach(page,(g) -> ids.add(g.get$L()))",
                        Iterables.class, capitalize(resultFieldName))
                .addStatement("$1T<$1T<String>> splits = $2T.partition(ids, 100)", List.class, Lists.class)
                .addCode(
                        "$1T.forEach(splits, (split) -> {\n"
                                + "     $2T q = new $2T(new $3T($4S).in(split));\n"
                                + "     $5Ls.addAll($5LService.getObjects(SolrQueryContext.of(q)).getContent());\n"
                                + "   });\n", Iterables.class, SIMPLE_QUERY_TYPE, CRITERIA_TYPE, keyFieldName,
                        uncapitalize(mappedTypeName))
                .addStatement(
                        "return $T.stream($T.spliterator($Ls), false).collect($T.toList())",
                        StreamSupport.class, Iterables.class,
                        uncapitalize(mappedTypeName),
                        java8.util.stream.Collectors.class).endControlFlow()
                .addStatement("return null");
        builder.addMethod(method.build());
    }

    private static String getResultFieldName(XFWReference field) {
        final EAnnotation annotation = field
                .getEAnnotation(JOINTABLE_ANNOTATION_SOURCE.getUri());
        return annotation.getDetails().get("inverseJoinColumns");
    }

    /**
     * Определить имя кросс таблицы.
     *
     * @param field поле
     * @return имя кросс таблицы
     */
    protected String getCrossTableName(XFWReference field) {
        final EAnnotation annotation = field
                .getEAnnotation(JOINTABLE_ANNOTATION_SOURCE.getUri());
        String str = annotation.getDetails().get("crossTableNameQN");
        String className = str.substring(str.lastIndexOf('.'));
        return inSeparatePkg
                ? str.replaceAll(className, ".solr" + className)
                :
                str;
    }

    protected String getFacadeWebclientSuffix() {
        return inSeparatePkg ? ".facade.webclient.solr" : ".facade.webclient";
    }

    //TODO разобраться в назначении
    protected String getGeneratorInstanceClassName(EStructuralFeature ref) {
        String instanceClassName = ref.getEType().getInstanceClassName(); //java.time.LocalDateTime
        //это под вопросом, нужно понятиь зачем делался отдельный пакет,
        // может это для того чтобы иметь возможность сослаться на доменные типы ???
        if (inSeparatePkg && !escapeList.contains(instanceClassName)) {
            String simpleClassName = ref.getEType().getName(); //LocalDateTime
            //добавление в структуру имени пакетов, пакет solr
            return instanceClassName.replace(simpleClassName,
                    "solr." + simpleClassName);
        } else {
            return instanceClassName;
        }
    }

    private static String getSearchFieldOrAnnotationName(XFWReference field) {
        final EAnnotation annotation = field
                .getEAnnotation(JOINTABLE_ANNOTATION_SOURCE.getUri());
        final String fieldName = annotation.getDetails().get("searchField");
        return fieldName;
    }

    private static List<String> escapeList = new ArrayList<String>() {
        {
            add("java.time.LocalDateTime");
            add("java.time.ZonedDateTime");
        }
    };
    
    @Override
    protected void addPropertyForObjectIds(Builder builder, EStructuralFeature ref) {
      //TODO JXFW-1177
        /*   builder.addStatement(
                   "dto.addProperty($S, getObjectIdsBase64(get$LDO(dto.getId())))",
                   fieldName,
                   capitalize(fieldName));*/
    }

    @Override
    public void addPropertyForObjectId(MethodSpec.Builder builder, String fieldName) {
        //TODO JXFW-1177
       /* builder.addStatement(
                "dto.addProperty($S, getIdInBase64(get$LDO(domainObject.getId())))",
                fieldName,
                capitalize(fieldName));*/
    }
}
