package ru.croc.ctp.jxfw.facade.transfer;

import static com.squareup.javapoet.ClassName.OBJECT;
import static com.squareup.javapoet.ClassName.get;
import static org.apache.commons.lang.ClassUtils.getPackageName;
import static org.apache.commons.lang.ClassUtils.getShortClassName;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang.WordUtils.uncapitalize;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.isComplexType;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java8.lang.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.meta.XFWServerOnly;
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToMany;
import ru.croc.ctp.jxfw.core.facade.webclient.ConvertContext;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainObjectFactory;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToProperty;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;
import ru.croc.ctp.jxfw.core.generator.StorageType;
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilder;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilderFactory;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWReference;
import ru.croc.ctp.jxfw.transfer.domain.DomainToServicesResolverTransfer;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

/**
 * Базовый класс для TO-генераторов.
 *
 * @param <K> Тип ключевого поля доменного объекта
 * @author Nosov Alexander on 16.11.15.
 */
public class TransferToServiceGenerator<K extends TypeName> {
    /**
     * javapoet-представление типа списка DTO.
     */
    protected static final ParameterizedTypeName LIST_OF_DOMAIN_TO = ParameterizedTypeName
            .get(List.class, DomainTo.class);

    /**
     * javapoet-представление словаря объектов.
     */
    protected static final ParameterizedTypeName MAP_OF_STRING_AND_OBJECT = ParameterizedTypeName
            .get(Map.class, String.class, Object.class);

    /**
     * javapoet-представление списка строк.
     */
    protected static final ParameterizedTypeName LIST_OF_STRINGS = ParameterizedTypeName
            .get(List.class, String.class);

    private static final Logger logger = LoggerFactory.getLogger(TransferToServiceGenerator.class);

    private static final XFWMMPackage XFW_PACKAGE = XFWMMPackage.eINSTANCE;

    private static final Predicate<EStructuralFeature> FEATURE_PREDICATE =
            (f -> !((f instanceof XFWAttribute) && ((XFWAttribute) f).isSystemField()));

    private static final Predicate<EStructuralFeature> PARSE_PROP_PREDICATE =
        (f -> !((f instanceof XFWAttribute) && ((XFWAttribute) f).isVersionField()));

    /**
     * Название фасада.
     */
    protected static final String FACADE_NAME = "transfer";

    /**
     * Класс доменного объекта.
     */
    protected final ClassName jxfwDomainType;

    /**
     * javapoet-представление класса доменного объекта.
     */
    protected final ParameterizedTypeName domainObjectType;

    /**
     * javapoet-представление ТО-сервиса.
     */
    protected final ParameterizedTypeName domainToServiceType;

    /**
     * javapoet-представление аннотации {@link SuppressWarnings}.
     */
    protected final AnnotationSpec suppressWarningsType;

    /**
     * Представление класса доменного объекта в метамодели.
     */
    protected final XFWClass xfwClass;

    /**
     * Имя пакета доменной модели (xtend-файла).
     */
    protected final String packageName;

    /**
     * Билдр для создания ТО сервиса.
     */
    protected final TypeSpec.Builder dtoService;

    /**
     * Пакет, в который будет генериться ТО фасад сервиса.
     */
    protected final String packageFacade;

    /**
     * Класс сервиса сохранения доменных объектов.
     */
    protected final ClassName jxfwServiceType;

    /**
     * Тип хранилища.
     */
    protected final String storageType;

    private final K keyType;

    /**
     * Признак "в отдельном пакете" был введен в связи с поддержкой полнотекста, когда нужно хранить
     * второй комплект сервисов с одинаковом именем, но нельзя это сделать в одном пакете.
     */
    protected boolean inSeparatePkg;

    /**
     * Конструктор генератора TO-сервисов.
     *
     * @param xfwClass Класс доменного объекта из метамодели
     */
    public TransferToServiceGenerator(XFWClass xfwClass) {
        this(xfwClass, null, null);
    }

    /**
     * Конструктор генератора TO-сервисов.
     *
     * @param xfwClass    Класс доменного объекта из метамодели
     * @param storageType Тип хранилища
     * @param keyType     Тип идентификатора доменного объекта
     */
    public TransferToServiceGenerator(XFWClass xfwClass, String storageType,
                                      K keyType) {
        this(xfwClass, storageType, keyType, false);
    }

    /**
     * Конструктор генератора TO-сервисов.
     *
     * @param xfwClass      Класс доменного объекта из метамодели
     * @param storageType   Тип хранилища
     * @param keyType       Тип идентификатора доменного объекта
     * @param inSeparatePkg Если ТО сервис генерится для fullText сущности то кладем его в отдельный пакет
     */
    public TransferToServiceGenerator(XFWClass xfwClass, String storageType,
                                      K keyType, boolean inSeparatePkg) {
        this.xfwClass = xfwClass;
        this.storageType = storageType;
        this.keyType = keyType;
        this.inSeparatePkg = inSeparatePkg;

        final String className = xfwClass.getName();
        String toServiceSimpleName = className + "ToServiceTransfer";
        dtoService = TypeSpec.classBuilder(toServiceSimpleName)
                .addAnnotation(getServiceAnnotation(toServiceSimpleName, inSeparatePkg))
                .addModifiers(Modifier.PUBLIC);

        if (inSeparatePkg) {
            packageName = getPackageName(xfwClass.getInstanceClassName()) + ".solr";
            packageFacade = getPackageName(xfwClass.getInstanceClassName()) + ".facade.transfer.solr";
        } else {
            packageName = getPackageName(xfwClass.getInstanceClassName());
            packageFacade = packageName + ".facade.transfer";
        }

        jxfwServiceType = get(packageName + ".service", className + "Service");
        jxfwDomainType = get(packageName, className);

        domainObjectType = ParameterizedTypeName.get(get(DomainObject.class),
                WildcardTypeName.subtypeOf(Object.class));

        domainToServiceType = ParameterizedTypeName.get(
                get(DomainToService.class), domainObjectType,
                WildcardTypeName.subtypeOf(Object.class));

        suppressWarningsType = AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "\"unchecked\"").build();
    }

    private AnnotationSpec getServiceAnnotation(String toServiceSimpleName, boolean inSeparatePkg) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(Service.class);
        if (inSeparatePkg) {
            builder.addMember("value", "\"" + "solr" + toServiceSimpleName + "\"");
        }
        return builder.build();
    }

    /**
     * Доп. параметр конструктора.
     *
     * @param constructor конструктор.
     */
    protected void subConstructor(Builder constructor) {

    }

    /**
     * Запуск генератора.
     * TODO хорошо бы сделать метод final, сейчас невозможно из-за transient генераторов
     *
     * @return Сгенерированный файл исходных кодов TO-сервиса
     */
    public JavaFile generate() {
        final TypeSpec.Builder toService = getToService();

        setSuperInterfacesOrClasses();

        addClassLevelAnnotations();

        setFields();

        Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .addParameter(get(ResourceStore.class), "resourceStore")
                .addParameter(get(DomainToServicesResolverTransfer.class), "domainToServicesResolver")
                .addStatement("super(resourceStore, domainToServicesResolver)");

        //внедряемся в конструктор
        subConstructor(constructor);

        toService.addMethod(constructor.build());

        createGettersForBlob(toService);

        toService.addMethod(createPathMethod().build());

        toService.addMethod(createParsePropValueMethod().build());

        toService.addMethod(createToToMethod().build());
        toService.addMethod(createToToListMethod().build());

        toService.addMethod(createFromToMethod().build());
        toService.addMethod(createFromToWithDomainMethod().build());


        toService.addMethod(createCreateNewDoMethod().build());

        toService.addMethod(createGetDObyIdMethod().build());

        toService.addMethod(createGetDObyIdWithDefaultLoadContextMethod().build());

        toService.addMethod(createGetFacadeNameMethod().build());

        addKeyMethods();

        addCrosstableField();

        //базовая реализация резолвит бин по имени сущности, возможно стоит научить по имени бина
        //т.к. с появлением полнотекста сервисы и сущности могут совпадать по именам
        addToToPolymorphicMethod();

        addCreatePathProperty();

        toService.addJavadoc("Сервис трансформации для сущности $T в объект класса DomainTo\n", jxfwDomainType);
        return JavaFile.builder(packageFacade, toService.build()).build();
    }

    /**
     * Перелпределение дефолтного метода FilterHelper.createPathProperty
     */
    protected void addCreatePathProperty() {
    }

    protected TypeSpec.Builder getToService() {
        return dtoService;
    }

    /**
     * Установка супер класса или интерфейсов для генерируемого ТО сервиса.
     */
    protected void setSuperInterfacesOrClasses() {
        getToService().superclass(getParentClass());
    }

    /**
     * Аннотации на классе.
     */
    protected void addClassLevelAnnotations() {

    }

    /**
     * Добавление переменной контекста приложения.
     */
    protected void autowireQueryParamsBuilderFactoryBySetter() {
        TypeSpec.Builder toService = getToService();
        toService.addField(
                FieldSpec.builder(QueryParamsBuilderFactory.class, "queryParamsBuilderFactory")
                        .addModifiers(Modifier.PRIVATE)
                        .build()
        );
        toService.addMethod(
                MethodSpec
                        .methodBuilder("setQueryParamsBuilderFactory")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(QueryParamsBuilderFactory.class, "queryParamsBuilderFactory")
                        .addStatement("this.queryParamsBuilderFactory = queryParamsBuilderFactory")
                        .addAnnotation(Autowired.class)
                        .build()
        );
    }

    /**
     * Добавление переменной сервиса загрузки.
     */
    protected void autowireLoadServiceBySetter() {
        TypeSpec.Builder toService = getToService();
        toService.addField(
                FieldSpec.builder(LoadService.class, "loadService")
                        .addModifiers(Modifier.PRIVATE)
                        .build()
        );
        toService.addMethod(
                MethodSpec
                        .methodBuilder("setLoadService")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(LoadService.class, "loadService")
                        .addStatement("this.loadService = loadService")
                        .addAnnotation(Autowired.class)
                        .build()
        );
    }

    /**
     * Добавление переменной доменного сервиса.
     */
    protected void autowireServiceBySetter() {
        TypeSpec.Builder toService = getToService();
        toService.addField(
                FieldSpec.builder(jxfwServiceType, "service")
                        .addModifiers(Modifier.PRIVATE)
                        .build()
        );
        toService.addMethod(
                MethodSpec
                        .methodBuilder("setService")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(jxfwServiceType, "domainService").build())
                        .addStatement("this.service = domainService")
                        .addAnnotation(Autowired.class)
                        .build()
        );
    }

    /**
     * Установка полей в генерируемый ТО сервис.
     */
    protected void setFields() {
        //добавить поле (наследник domainService) и инжектить через setter
        autowireServiceBySetter();
        autowireFieldsBySetters();
        autowireQueryParamsBuilderFactoryBySetter();
        autowireLoadServiceBySetter();

        /*        ParameterizedTypeName doUtilType = ParameterizedTypeName.get(
                get(DomainObjectUtil.class), domainObjectType);
        getToService().addField(FieldSpec.builder(doUtilType, "doUtil")
                .initializer("new $T()", doUtilType).build());*/

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

                        if (StorageType.SOLR.name().equals(storageType)) {
                            final String instanceClassName = ref.getEType().getInstanceClassName();
                            final ClassName typeName = get(
                                    getPackageName(instanceClassName) + ".facade.transfer",
                                    className + "ToServiceTransfer");

                            getToService().addField(
                                    FieldSpec.builder(typeName, uncapitalize(className) + "ServiceToTransfer")
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

    /**
     * Устанавливает автоматически определяемые поля для set-методов.
     */
    protected void autowireFieldsBySetters() {
    }

    /**
     * Создание геттеров для Блоб свойств сущности.
     *
     * @param toService - генерируемый ТО сервис.
     */
    protected void createGettersForBlob(TypeSpec.Builder toService) {
        // getters для бинарных свойств
        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(attr -> attr.getEType().equals(XFW_PACKAGE.getBlob()))
                .filter(f -> !isComplexType(f))
                .forEach(attr -> {
                    MethodSpec method = createGetBinPropMethod(jxfwDomainType, (XFWAttribute) attr).build();
                    toService.addMethod(method);
                });
    }

    private Builder unsupportedOperationBody(Builder builder) {
        return builder.addStatement("throw new $T(\"Abstract Type ToService\")",
                get(UnsupportedOperationException.class));
    }

    private Builder createPathMethod() {
        final Builder builder = MethodSpec
                .methodBuilder("createPath")
                .addModifiers(Modifier.PUBLIC)
                .returns(
                        ParameterizedTypeName.get(get(Path.class),
                                jxfwDomainType));

        // FIX ME
        if (xfwClass.getName().equalsIgnoreCase("group")) {
            builder.addStatement("return $T.path($T.class, $S)",
                    Expressions.class, jxfwDomainType,
                    uncapitalize(xfwClass.getName()) + "1");
        } else {
            builder.addStatement("return $T.path($T.class, $S)",
                    Expressions.class, jxfwDomainType,
                    uncapitalize(xfwClass.getName()));
        }

        return builder;
    }


    /**
     * Создание метода {@code public Object parsePropValue(String propName, Object value)}.
     *
     * @return билдр для метода
     */
    protected Builder createParsePropValueMethod() {
        final Builder builder = MethodSpec
                .methodBuilder("parsePropValue");
        builder.addModifiers(Modifier.PUBLIC).returns(OBJECT)
                .addParameter(get(String.class), "propName")
                .addParameter(OBJECT, "v");
        return bodyParsePropValueMethod(builder);


    }

    /**
     * Место для переопределения ParsePropValueMethod.
     *
     * @param builder - код
     * @return дополненный код.
     */
    protected Builder bodyParsePropValueMethod(Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock
                .builder()
                .addStatement(
                        "if (!(v instanceof String) && !(v instanceof Integer)) return v")
                .beginControlFlow("switch(propName)");

        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(parsePropMethodFieldsFilter())
                .forEach(ref -> {
                    codeBlock.add("case $S:\n", ref.getName());

                    final String packageName = getPackageName(getGeneratorInstanceClassName(ref));
                    final String className = getShortClassName(getGeneratorInstanceClassName(ref));
                    final ClassName fieldType = getClassName(packageName, className);

                    if ("Blob".equals(className)) {
                        codeBlock.addStatement("return v");
                    } else if (isNotComplexTypeReference(ref)) {
                        if (!overrideCreateDummyDomainObject(codeBlock)) {
                            codeBlock.addStatement("return createDummyDomainObject($T.class, (String) v)",
                                    fieldType);
                        }
                    } else if (ref.getEType() instanceof EEnum) {
                        codeBlock.addStatement("return v");
                    } else {
                        codeBlock.add("return (");
                        codeBlock.add(createPropertyValueByType(ref, fieldType, "v"));
                        codeBlock.add(");\n");
                    }
                });

        codeBlock.add("\n");
        codeBlock.addStatement("default: return v");
        codeBlock.endControlFlow();

        builder.addCode(codeBlock.build());
        return builder;
    }


    /**
     * Место для переопределения обарботки XFWReference  в parsPropValue.
     *
     * @param codeBlock - код
     * @return - было ли переопределение.
     */
    protected boolean overrideCreateDummyDomainObject(CodeBlock.Builder codeBlock) {
        return false;
    }

    private Builder createToToMethod() {
        final Builder builder = MethodSpec
                .methodBuilder("toTo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(jxfwDomainType, "domainObject")
                .returns(DomainTo.class);


        builder.addStatement("$T.notNull(domainObject, \"Parameter domainObject should not be null\")", Assert.class)
                .addStatement("$1T.isTrue(domainObject instanceof $2T,"
                                + "\"Parameter domainObject should be of type $2T or his child\")",
                        Assert.class, jxfwDomainType)
                .addStatement("$T keyForDomainObject = domainObject.getId()", getKeyType())
                .addStatement("DomainTo dto = new DomainTo(domainObject.getTypeName(), "
                        + "serializeKey(keyForDomainObject))");

        addGetVersionStmt(builder);

        builder.addStatement("dto.setNew(domainObject.isNew())")
                .addStatement("dto.setRemoved(domainObject.isRemoved())");

        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(toToMethodFieldsFilter())
                .forEach(ref -> {
                    final EAnnotation facadeIgnoreAnnotation = ref
                            .getEAnnotation(XFWConstants.FACADE_IGNORE_ANNOTATION.getUri());

                    if (facadeIgnoreAnnotation != null && isTransferIncludeFacadeList(facadeIgnoreAnnotation)) {
                        // не генерируем
                        return;
                    }
                    final boolean isIncludeCheckIgnoreFiled = facadeIgnoreAnnotation != null
                            && isFacadeListEmpty(facadeIgnoreAnnotation);
                    if (isIncludeCheckIgnoreFiled) {
                        builder.beginControlFlow("if (!defaultEnableIgnoreFacade)");
                        createToToFieldBlockCode(builder, ref);
                        builder.endControlFlow();
                    } else {
                        createToToFieldBlockCode(builder, ref);
                    }
                });
        builder.addStatement("return dto");
        return builder;
    }

    protected MethodSpec.Builder createToToListMethod() {
        final MethodSpec.Builder builder = MethodSpec
            .methodBuilder("toTo")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterizedTypeName.get(get(Iterable.class),jxfwDomainType), "domainObjectList")
            .addParameter(ArrayTypeName.of(String.class), "expand")
            .varargs(true)
            .returns(ParameterizedTypeName.get(get(List.class), get(DomainTo.class)));
        builder.addStatement("List<DomainTo> result = new $T<>()", get(ArrayList.class))
            .addStatement("for($T domainObject: domainObjectList) {", jxfwDomainType)
            .addStatement("result.add(toTo(domainObject, expand))")
            .addStatement("}")
            .addStatement("return result");
        return builder;
    }

    /**
     * Создаёт блок кода toTo трансформации конкретного поля доменного объекта.
     *
     * @param builder код
     * @param ref поле
     */
    protected void createToToFieldBlockCode(Builder builder, EStructuralFeature ref) {
        final String fieldName = ref.getName();

        if (isNotComplexTypeReference(ref)) {
            if (isCollectionField(ref)) {
                addPropertyForObjectIds(builder, fieldName);
            } else {
                addPropertyForObjectId(builder, fieldName);
            }
            addDomainObjectPropertyMetadata(builder, ref);
        } else {
            final String fieldTypeString = ref.getEType().getName();

            //обычно "domainObject" но для Cass в случае если поле из ключа - то "key"
            String variableName = variableName(ref);

            if ("Blob".equals(fieldTypeString)) {
                addBlobProperty(builder, (XFWAttribute) ref, fieldName);
            } else if ("EString".equals(fieldTypeString)) {
                if (ref.getEAnnotation(XFWConstants.PROTECTED_ATTR_ANNOTATION_SOURCE.getUri()) != null) {
                    final EAnnotation eAnnotation = ref
                            .getEAnnotation(XFWConstants.PROTECTED_ATTR_ANNOTATION_SOURCE.getUri());
                    builder.addStatement("dto.addProperty($S, $S)", fieldName,
                            eAnnotation.getDetails().get("value"));
                } else {
                    builder.addStatement("dto.addProperty($1S, $3L.get$2L())", fieldName,
                            capitalize(fieldName), variableName);
                }
            } else if (fieldTypeString.startsWith("LocalDateTime")
                    || fieldTypeString.startsWith("LocalTime")
                    || fieldTypeString.startsWith("LocalDate")
                    || "ZonedDateTime".equals(fieldTypeString)) {
                logger.debug("FieldTypeName:" + fieldTypeString);
                builder.addStatement(
                        "dto.addProperty($1S, $3L.get$2L())",
                        fieldName,
                        capitalize(fieldName), variableName);
            } else if ("ZonedDateTime_Java7".equals(fieldTypeString)) {
                builder.addStatement(
                        "dto.addProperty($1S, domainObject.get$2L() != null ?"
                                + " domainObject.get$2L().format($3T.ISO_INSTANT) : null)",
                        fieldName,
                        capitalize(fieldName),
                        org.threeten.bp.format.DateTimeFormatter.class);
            } else if (fieldTypeString.startsWith("Duration")) {
                builder.addStatement(
                        "dto.addProperty($1S, $3L.get$2L() != null ?"
                                + " $3L.get$2L().toMillis() : null)",
                        fieldName,
                        capitalize(fieldName),
                        variableName);
            } else if ("UUID".equals(fieldTypeString)) {
                builder.addStatement(
                        "dto.addProperty($1S, $3L.get$2L() != null ?"
                                + " $3L.get$2L().toString() : null)",
                        fieldName,
                        capitalize(fieldName), variableName);
            } else if ("BigInteger".equals(fieldTypeString)) {
                builder.addStatement(
                        "dto.addProperty($1S, $3L.get$2L() != null ?"
                                + " $3L.get$2L().longValue() : null)",
                        fieldName,
                        capitalize(fieldName),
                        variableName);
            } else if ("EBoolean".equals(fieldTypeString)) {
                builder.addStatement("dto.addProperty($1S, $3L.is$2L())",
                        fieldName,
                        capitalize(fieldName),
                        variableName);
            } else if (isComplexType(ref)) {
                final String className = ref.getEType().getInstanceClassName();
                final String packageName = getPackageName(className);
                final String shortClassName = getShortClassName(className);
                final ClassName fieldType = get(packageName, shortClassName);

                builder.addStatement(
                        "$T $LCT = domainObject.get$L()",
                        get(packageName, shortClassName),
                        uncapitalize(fieldName),
                        capitalize(fieldName))
                        .addStatement("$1T<String, Object> $2LFieldsMap", get(Map.class), uncapitalize(fieldName))
                        .beginControlFlow(
                                "if ($LCT != null)",
                                uncapitalize(fieldName))
                        .addStatement(
                                "$1LFieldsMap = ($2T)$1LCT.getAllFields(\""
                                        + fieldName + "\")",
                                uncapitalize(fieldName),
                                get(HashMap.class))
                        .nextControlFlow("else")
                        .addStatement(
                                "$1LFieldsMap = ($2T) new $3T().getAllFields(\""
                                        + fieldName + "\")",
                                uncapitalize(fieldName),
                                get(HashMap.class),
                                fieldType)
                        .endControlFlow()
                        .beginControlFlow("for (String key : $LFieldsMap.keySet())",
                                uncapitalize(fieldName))
                        .addStatement("dto.addProperty(key, $LFieldsMap.get(key))",
                                uncapitalize(fieldName))
                        .endControlFlow();
            } else if (ref.getEType() instanceof EEnum) {
                final String className = ref.getEType().getInstanceClassName();
                final String packageName = getPackageName(className);
                final String shortClassName = getShortClassName(className);
                builder.addStatement("dto.addProperty($1S, $4T.METADATA.convertToInt($3L.get$2L()))",
                        fieldName,
                        capitalize(fieldName),
                        variableName,
                        get(packageName, shortClassName)
                );

            } else {
                builder.addStatement("dto.addProperty($1S, $3L.get$2L())",
                        fieldName,
                        capitalize(fieldName),
                        variableName);
            }
            builder.addStatement(
                    "dto.addPropertyMetadata($1S, $3T.create($2S, $4T.Simple))",
                    fieldName, ref.getEType().getName(), get(DomainToProperty.class),
                    get(DomainToProperty.Type.class));
        }
    }

    private boolean isNotComplexTypeReference(EStructuralFeature ref) {
        return ref instanceof XFWReference && !isComplexType(ref);
    }

    /**
     * Проверяет является ли поле навигуемым на множество объектов.
     *
     * @param ref поле
     * @return true если является
     */
    protected boolean isCollectionField(EStructuralFeature ref) {
        return ref.getEAnnotation(XFWConstants.RELATION_MANY_ANNOTATION_SOURCE.getUri()) != null;
    }

    /**
     * Добавляет блок кода добавления поля в {@link DomainTo} по ID доменного объекта.
     *
     * @param builder код
     * @param fieldName имя поля
     */
    protected void addPropertyForObjectId(Builder builder, String fieldName) {
        builder.addStatement(
                "dto.addProperty($1S, domainObject.get$2L() != null ?"
                        + " domainObject.get$2L().getId() : null)",
                fieldName,
                capitalize(fieldName));
    }

    /**
     * Добавляет блок кода добавления поля в {@link DomainTo} по списку ID доменных объектов.
     *
     * @param builder код
     * @param fieldName имя поля
     */
    protected void addPropertyForObjectIds(Builder builder, String fieldName) {
        builder.addStatement(
                "dto.addProperty($S, getObjectIds(domainObject.get$L()))",
                fieldName,
                capitalize(fieldName));
    }

    private void addDomainObjectPropertyMetadata(Builder builder, EStructuralFeature ref) {
        final String fieldName = ref.getName();

        builder.addStatement(
                "dto.addPropertyMetadata($1S, $3T.create($2S, $4T.DomainObject))",
                fieldName, ref.getEType().getName(), get(DomainToProperty.class),
                get(DomainToProperty.Type.class));
    }

    /**
     * Место для переопределения DeleteMethod.
     *
     * @param builder - код
     * @return дополненный код.
     */
    protected Builder bodyDeleteMethod(Builder builder) {
        return builder
                .addStatement("service.delete(id)");
    }


    /**
     * Создание метода {@code public DomainObject fromTo(DomainTo dto, ConvertContext context)}.
     * FIXME надо сделать метод private, сейчас невозможно из-за transient генераторов
     *
     * @return билдр метода
     */
    protected Builder createFromToMethod() {

        logger.debug("createFromToMethod() started for domain class {}", jxfwDomainType.simpleName());
        Builder builder= MethodSpec
                .methodBuilder("fromTo")
                .addModifiers(Modifier.PUBLIC)
                .returns(jxfwDomainType)
                .addParameter(ParameterSpec.builder(get(DomainTo.class), "dto")
                        .addAnnotation(Nonnull.class).build())
                .addParameter(ParameterSpec.builder(get(ConvertContext.class), "context")
                        .addAnnotation(Nonnull.class).build());
        return bodyFromToMethod(builder);

    }

    protected Builder createFromToWithDomainMethod() {


        logger.debug("createFromToMethod() started for domain class {}", jxfwDomainType.simpleName());
        final Builder builder = MethodSpec
                .methodBuilder("fromTo")
                .addAnnotation(suppressWarningsType)
                .addModifiers(Modifier.PUBLIC)
                .returns(jxfwDomainType)
                .addParameter(ParameterSpec.builder(jxfwDomainType, "o")
                        .addAnnotation(Nonnull.class).build())
                .addParameter(ParameterSpec.builder(get(DomainTo.class), "dto")
                        .addAnnotation(Nonnull.class).build())
                .addParameter(ParameterSpec.builder(get(ConvertContext.class), "context")
                        .addAnnotation(Nonnull.class).build());

        return bodyFromToWithDomainMethod(builder);

    }

    protected Builder bodyFromToMethod(Builder builder){
        if (xfwClass.isAbstract()) {
            return unsupportedOperationBody(builder);
        }
        return builder.addStatement("DomainObjectFactory factory = new DomainObjectFactory(context, domainToServicesResolver)")
                .addStatement("return fromTo(factory.create(this, dto), dto, context)");

    }


    /**
     * Создане метода {@code protected String getFacadeName()}.
     *
     * @return билдр метода.
     */
    protected Builder createGetFacadeNameMethod() {
        logger.debug("createGetFacadeNameMethod() started for domain class {}", jxfwDomainType.simpleName());
        return MethodSpec
                .methodBuilder("getFacadeName")
                .addModifiers(Modifier.PROTECTED)
                .returns(ClassName.get(String.class))
                .addStatement("return \"" + FACADE_NAME + "\"");
    }

    /**
     * Точка расширения для переопределения тела метода в наследниках генератора.
     *
     * @param builder - код
     * @return дополненный код.
     */
    protected Builder bodyFromToWithDomainMethod(Builder builder) {

        if (xfwClass.isAbstract()) {
            return unsupportedOperationBody(builder);
        }


        builder.addStatement("assert(dto.getType().equals(\"$T\"))",
                jxfwDomainType)
                .addStatement(
                        "$1T factory = new $1T(context, domainToServicesResolver)",
                        get(DomainObjectFactory.class));

        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(fromToFieldsFilter())
                .forEach(ref -> {
                    logger.debug("Processing class feature {}", ref.getName());
                    final EAnnotation facadeIgnoreAnnotation = ref
                            .getEAnnotation(XFWConstants.FACADE_IGNORE_ANNOTATION.getUri());
                    /*
                     * 1) Аннотации нет => всё генерируем
                     * 2) Аннотация есть, но список не пуст в списке transfer нет => всё генерируем
                     * 3) Аннотация есть, но список не пуст в списке transfer есть => не генерируем
                     * 4) Аннотация есть, список пуст => генерируем с динамическим рассмотрением
                     */
                    if (facadeIgnoreAnnotation != null && isTransferIncludeFacadeList(facadeIgnoreAnnotation)) {
                        // не генерируем
                        return;
                    }
                    final boolean isIncludeCheckIgnoreFiled = facadeIgnoreAnnotation != null
                            && isFacadeListEmpty(facadeIgnoreAnnotation);
                    if (isIncludeCheckIgnoreFiled) {
                        builder.beginControlFlow("if (!defaultEnableIgnoreFacade)");
                        createFromToFieldBlockCode(builder, ref);
                        builder.endControlFlow();
                    } else {
                        createFromToFieldBlockCode(builder, ref);
                    }
                });

        builder.addStatement("o.setPropChangedValues(dto.getOriginal())");

        specificFromToEnhancement(builder);

        builder.addStatement("return o");

        return builder;
    }

    /**
     * Создаёт блок кода fromTo трансформации конкретного поля доменного объекта.
     *
     * @param builder код
     * @param ref поле
     */
    protected void createFromToFieldBlockCode(Builder builder, EStructuralFeature ref) {
        final String fieldName = ref.getName();

        final String className = ref.getEType().getName();
        final String instanceClassName = getGeneratorInstanceClassName(ref);
        final String packageName = getPackageName(instanceClassName);
        final String simpleClassName = getShortClassName(instanceClassName);
        final ClassName fieldType = getClassName(packageName, simpleClassName);

        if (isNotComplexTypeReference(ref)) {
            if (isCollectionField(ref)) {
                populateNavigables(builder, (XFWReference) ref, fieldName, className, fieldType);
            } else {
                populateOpposite(builder, (XFWReference) ref, fieldName, className, fieldType);
            }
        } else {

            if ("Blob".equals(className)) {
                setBlobPropertyToDomainObject(builder, (XFWAttribute) ref, fieldName);
            } else if (isComplexType(ref)) {

                final String complexClassName = ref.getEType().getInstanceClassName();
                final String complexPackageName = getPackageName(complexClassName);
                final String shortClassName = getShortClassName(complexClassName);
                builder.addCode(
                        "$1T $2N = o.get$4L()!= null ?  o.get$4L() : new $1T();\n"
                                + "dto.forEachProperty((String key, Object v)-> {\n"
                                + "    $5T<String, Object> props = new $5T<>();\n"
                                + "    if (key.startsWith(\"$3L.\")) {\n",
                        get(complexPackageName, shortClassName),
                        uncapitalize(fieldName),
                        fieldName,
                        capitalize(fieldName),
                        get(HashMap.class));

                ((XFWClass) ref.getEType()).getEAllStructuralFeatures().stream()
                        .filter(fromToFieldsFilter())
                        .forEach(refComplex -> {
                            final String instanceClassNameComplex = refComplex
                                    .getEType().getInstanceClassName();
                            final String packageNameComplex = getPackageName(instanceClassNameComplex);
                            final String simpleClassNameComplex =
                                    getShortClassName(instanceClassNameComplex);

                            final ClassName fieldTypeComplex =
                                    get(packageNameComplex, simpleClassNameComplex);

                            builder.addCode("if ($1S.equals(key)) {\n"
                                    + "v = ", fieldName + "." + refComplex.getName());
                            builder.addCode(createPropertyValueByType(refComplex, fieldTypeComplex));
                            builder.addCode(";\n}\n");
                        });

                builder.addCode("       props.put(key, v);\n"
                                + "    }\n"
                                + "    o.set$2L($1N.createComplexType(\""
                                + fieldName + "\", props));\n"
                                + "});\n",
                        uncapitalize(fieldName),
                        capitalize(fieldName));
            } else {
                builder.addCode("dto.copyPropValue($S, v -> o.set$L(",
                        fieldName,
                        capitalize(fieldName));
                builder.addCode(createPropertyValueByType(ref, fieldType));
                builder.addStatement("))");
            }
        }
    }

    /**
     * Проверяет включен ли фасад в список фасадов {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore}.
     *
     * @param facadeIgnoreAnnotation аннотация
     * @return true если пуст
     */
    private boolean isTransferIncludeFacadeList(EAnnotation facadeIgnoreAnnotation) {
        return getFacadeList(facadeIgnoreAnnotation).contains("transfer");
    }

    /**
     * Проверяет пуст ли список фасадов у анатации {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore}.
     *
     * @param facadeIgnoreAnnotation аннотация
     * @return true если пуст
     */
    private boolean isFacadeListEmpty(EAnnotation facadeIgnoreAnnotation) {
        return getFacadeList(facadeIgnoreAnnotation).isEmpty();
    }

    /**
     * Достаёт из {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore} список фасадов.
     *
     * @param facadeIgnoreAnnotation аннотация
     * @return список фасадов
     */
    private List<String> getFacadeList(EAnnotation facadeIgnoreAnnotation) {
        String facades = facadeIgnoreAnnotation.getDetails().get("facades");
        // парсим строковое пердаставление массива строк
        facades = facades.substring(1, facades.length() - 1);
        if (facades.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(facades.split(","))
                .map(x -> x.trim())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает ClassName собранный из частей или null для примитивных типов.
     *
     * @param packageName     имя пакета
     * @param simpleClassName простое имя класса.
     * @return представление полного имени класса.
     */
    protected static ClassName getClassName(String packageName, String simpleClassName) {
        if (simpleClassName != null
                && simpleClassName.matches("(boolean|byte|short|int|long|float|double|char)")) {
            return null;
        }

        return get(packageName, simpleClassName);
    }

    //генерация метода пробразования/заполнения ссылки на объект
    private void populateOpposite(Builder builder, XFWReference ref, String fieldName, String className,
                                  ClassName fieldType) {
        CodeBlock codeBlockOpposite = CodeBlock.builder().build();

        CodeBlock codeBlock = CodeBlock.builder()
                .add("dto.copyPropValue($1S, v -> {\n", fieldName)
                .beginControlFlow("if (v == null)")
                .addStatement("o.set$1L(null)", capitalize(fieldName))
                .nextControlFlow("else")
                .addStatement("$1T master = factory.create((String) v, \"$1T\")",
                        fieldType)
                .addStatement("o.set$1L(master)", capitalize(fieldName))
                .add(codeBlockOpposite)
                .endControlFlow()
                .add("});\n")
                .build();
        builder.addCode(codeBlock);

    }

    /**
     * Генерация метода пробразования/заполнения навигируемых свойств.
     *
     * @param builder   код
     * @param ref       ссылка
     * @param fieldName имя поля
     * @param className имя класса
     * @param fieldType тип поля
     */
    protected void populateNavigables(Builder builder, XFWReference ref, String fieldName, String className,
                                      ClassName fieldType) {

        NavigableStructureInfo structureInfo = buildNavigableStructureInfo(ref);

        Builder mbuilder = MethodSpec
                .methodBuilder("blank")
                .returns(void.class)
                .beginControlFlow("if (o.get$1L() != null && !o.get$1L().isEmpty())", capitalize(fieldName))
                .beginControlFlow("for ($1T<?> iter = o.get$2L().iterator(); iter.hasNext(); )",
                        get(Iterator.class),
                        capitalize(fieldName))
                .addStatement("iter.next()")
                .addStatement("iter.remove()")
                .endControlFlow()
                .endControlFlow()
                .beginControlFlow("if (v instanceof String)")
                .beginControlFlow("if (o.get$1L() != null)", capitalize(fieldName))
                .addStatement("o.get$1L().addAll(factory.create$2L($3T.singletonList((String) v), $4S))",
                        capitalize(fieldName),
                        structureInfo.containerType.simpleName(),
                        get(Collections.class), className)
                .nextControlFlow("else")
                .addStatement("o.set$1L(factory.create$2L($3T.singletonList((String) v), $4S))",
                        capitalize(fieldName),
                        structureInfo.containerType.simpleName(),
                        get(Collections.class), className)
                .endControlFlow()
                .nextControlFlow("else")
                .beginControlFlow("if (o.get$1L() != null)", capitalize(fieldName))
                .addStatement("o.get$1L().addAll(factory.create$4L(($2T) v, $3S))",
                        capitalize(fieldName), LIST_OF_STRINGS, className, structureInfo.containerType.simpleName())
                .nextControlFlow("else")
                .addStatement("o.set$1L(factory.create$4L(($2T) v, $3S))",
                        capitalize(fieldName), LIST_OF_STRINGS, className, structureInfo.containerType.simpleName())
                .endControlFlow()
                .endControlFlow();
        //Документация хибернейт указывает на то, что при использования bidirectional association,
        //синхронизация ссылок с обеих сторон лежит на плечах разработчика
        //поэтому здесь происходит апдейт ссылки со стороны ManyToOne для навигируемых свойств
        if (StringUtils.isNotEmpty(structureInfo.mappedBy)
                && XFWOneToMany.class.getSimpleName().equals(structureInfo.annotation.getDetails().get("name"))
                && !structureInfo.mappedBy.equals(fieldName)) {
            mbuilder.addStatement(
                    "$3T.forEach(o.get$1L(), (it) -> it.set$2L(o))",
                    capitalize(fieldName),
                    capitalize(structureInfo.mappedBy),
                    get(Iterables.class)
            );
        }
        builder.addCode("dto.copyPropValue($1S, v -> {\n$2L});\n", fieldName, mbuilder.build().code);
    }


    private CodeBlock createPropertyValueByType(EStructuralFeature ref, ClassName fieldType) {
        return createPropertyValueByType(ref, fieldType, "v");
    }


    /**
     * Создаёт значение свойства по его типу.
     *
     * @param ref       ссылка
     * @param fieldType тип свойства
     * @param variableName имя переменной, содержащей значение.
     * @return блок кода
     */
    protected CodeBlock createPropertyValueByType(EStructuralFeature ref, ClassName fieldType, String variableName) {

        final CodeBlock.Builder builder = CodeBlock.builder();

        final String fieldTypeName = ref.getEType().getName();
        final String className = ref.getEType().getInstanceClassName();
        final String packageName = getPackageName(className);
        final String shortClassName = getShortClassName(className);

        if (fieldTypeName.startsWith("LocalDateTime")) {
            builder.add(
                    "getValueParser().parseLocalDateTime(v)");
        } else if (fieldTypeName.startsWith("ZonedDateTime")) {
            builder.add(
                    "getValueParser().parseZonedDateTime(v)");
        } else if (fieldTypeName.startsWith("LocalTime")) {
            if (fieldType.packageName().equals("java.time")) {
                builder.add("getValueParser().parseLocalTime(v)");
            } else {
                builder.add(
                        "v == null ? null :"
                                + " $T.parse((String) v).toLocalTime()",
                        org.threeten.bp.LocalDateTime.class);
            }

        } else if (fieldTypeName.startsWith("LocalDate")) {
            if (fieldType.packageName().equals("java.time")) {
                builder.add("getValueParser().parseLocalDate(v)");
            } else {
                builder.add(
                        "v == null ? null :"
                                + " $T.parse((String) v).toLocalDate()",
                        org.threeten.bp.LocalDateTime.class);
            }

        } else if (fieldTypeName.startsWith("Duration")) {
            builder.add(
                    "v == null ? null :"
                            + " $T.ofMillis($T.parseLong(v.toString()))",
                    fieldType,
                    Long.class);
        } else if ("UUID".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null : $T.fromString((String) v)",
                    get(UUID.class));
        } else if ("EByteObject".equals(fieldTypeName) || "EByte".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Byte.parseByte(v.toString())");
        } else if ("EShortObject".equals(fieldTypeName) || "EShort".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Short.parseShort(v.toString())");
        } else if ("EIntegerObject".equals(fieldTypeName) || "EInt".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Integer.parseInt(v.toString())");
        } else if ("ELongObject".equals(fieldTypeName) || "ELong".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Long.parseLong(v.toString())");
        } else if ("EFloatObject".equals(fieldTypeName) || "EFloat".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Float.parseFloat(v.toString())");
        } else if ("EDoubleObject".equals(fieldTypeName) || "EDouble".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Double.parseDouble(v.toString())");
        } else if ("EBooleanObject".equals(fieldTypeName) || "EBoolean".equals(fieldTypeName)) {
            builder.add(
                    "v == null ? null :"
                            + " Boolean.parseBoolean(v.toString())");
        } else if (fieldTypeName.startsWith("EBigDecimal")) {
            builder.add(
                    "v == null ? null :"
                            + " new $T(v.toString())",
                    fieldType);
        } else if (fieldTypeName.startsWith("EBigInteger")) {
            builder.add(
                    "v == null ? null :"
                            + " new $T(v.toString())",
                    fieldType);
        } else if (ref.getEType() instanceof EEnum) {
            builder.add(
                    "v == null ? null : $1T.METADATA.convertToEnum$2L(Integer.parseInt(v.toString()), $1T.class)",
                    get(packageName, shortClassName),
                    ref.isMany() ? "Set" : "");
        } else {
            builder.add(
                    "($T) v",
                    fieldType);
        }

        return builder.build();
    }

    private Builder createCreateNewDoMethod() {
        final Builder builder = MethodSpec.methodBuilder("createNewDomainObject")
                .addModifiers(Modifier.PUBLIC).returns(jxfwDomainType)
                .addParameter(String.class, "key");
        return bodyCreateNewDoMethod(builder);


    }

    /**
     * Место для переопределения CreateNewDoMethod.
     *
     * @param builder - код
     * @return дополненный код.
     */
    protected Builder bodyCreateNewDoMethod(Builder builder) {
        if (xfwClass.isAbstract()) {
            return unsupportedOperationBody(builder);
        }
        return builder
                .addStatement("return service.createNew(parseKey(key))");
    }


    private MethodSpec.Builder createGetDObyIdMethod() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("getDomainObjectById")
                .addModifiers(Modifier.PUBLIC).returns(jxfwDomainType)
                .addParameter(String.class, "key")
                .addParameter(ParameterizedTypeName.get(get(LoadContext.class), jxfwDomainType), "loadContext");
        return bodyGetDObyIdMethod(builder);
    }

    private MethodSpec.Builder createGetDObyIdWithDefaultLoadContextMethod() {
        return MethodSpec.methodBuilder("getDomainObjectById")
                .addModifiers(Modifier.PUBLIC).returns(jxfwDomainType)
                .addAnnotation(AnnotationSpec.builder(Deprecated.class).build())
                .addParameter(String.class, "key")
                .addStatement("$1T<$2T> loadContext = new $1T.Builder<$2T>().build()", get(LoadContext.class), jxfwDomainType)
                .addStatement("return getDomainObjectById(key, loadContext)");
    }

    /**
     * Место для переопределения GetDObyIdMethod.
     *
     * @param builder - код
     * @return дополненный код.
     */
    protected Builder bodyGetDObyIdMethod(Builder builder) {
        return bodyGetDObyIdMethodReturnStatement(builder
                .addStatement("$1T<$2T, $3T> queryParamsBuilder = queryParamsBuilderFactory.newBuilder($2T.class, parseKey(key))",
                        get(QueryParamsBuilder.class), jxfwDomainType, keyType));
    }

    /**
     * Место для переопределения строки с возвращаемым результатом.
     *
     * @param builder - код
     * @return дополненный код.
     */
    protected Builder bodyGetDObyIdMethodReturnStatement(Builder builder) {
        return builder.addStatement("return loadService.loadOne(queryParamsBuilder.build(), loadContext)", jxfwDomainType);
    }

    /**
     * Генерация методов сериализации комплексного ключа.
     */
    protected void addKeyMethods() {
        final TypeSpec.Builder toService = getToService();

        toService.addMethod(createSerializeKeyMethod().build());

        toService.addMethod(createParseKeyMethod().build());
    }

    /**
     * Генерирует поле для кросс-таблицы.
     */
    protected void addCrosstableField() {

    }

    /**
     * Добавление метода ToToPolymorphic.
     */
    protected void addToToPolymorphicMethod() {

    }

    /**
     * Возвращает класс, от которого должен быть унаследован генерируемый TO-сервис.
     *
     * @return Класс, от которого нужно наследовать сервис
     */
    protected TypeName getParentClass() {
        return ParameterizedTypeName
                .get(get(DomainToServiceImpl.class), jxfwDomainType, getKeyType());
    }

    /**
     * Создает билдер для генерации метода getBinPropMethod TO-сервиса.
     *
     * @param jxfwDomainClass Доменный класс
     * @param attr            EMF-метаданные бинарного поля
     * @return Билдер для генерации метода
     */
    protected Builder createGetBinPropMethod(ClassName jxfwDomainClass, XFWAttribute attr) {
        boolean generate = attr.getGenerateBlobInfoFields();
        String prefix = getNameFromColumn(attr);
        return MethodSpec
                .methodBuilder("getBinProp" + capitalize(attr.getName()))
                .addModifiers(Modifier.PUBLIC)
                .returns(DomainToServiceImpl.BinPropValue.class)
                .addParameter(ParameterSpec.builder(getKeyType(), "id").build())
                .addStatement("$T o = service.getObjectById(id)", jxfwDomainClass)
                .addCode("$T blob = o.get$L();\n"
                                + "if(blob == null) return null;\n"
                                + "BinPropValue result = new BinPropValue($L, $L,\n"
                                + "blob);\n"
                                + "return result;\n",
                        Blob.class,
                        capitalize(attr.getName()),
                        generate ? "o.get" + capitalize(prefix) + attr.getFileNameSuffix() + "()" : "null",
                        generate ? "o.get" + capitalize(prefix) + attr.getContentTypeSuffix() + "()" : "null");
    }

    protected K getKeyType() {
        return keyType;
    }

    /**
     * Добавляет метод getVersion.
     *
     * @param builder код.
     * @return дополненный код.
     */
    protected Builder addGetVersionStmt(Builder builder) {
        if (!isReadOnlyType()) {
            builder.addStatement("dto.setTs(domainObject.getVersion())");
        }
        return builder;
    }

    /**
     * Получить переменную из которой будет прочитано поле.
     *
     * @param ref поле
     * @return имя "контейнера" в котором есть нужная пропертя
     */
    protected String variableName(EStructuralFeature ref) {
        return "domainObject";
    }

    /**
     * Добавляет код, который устанавливает значение бинартного свойства в DTO.
     *
     * @param builder   Билдер метода toTo
     * @param attribute EMF-метаданные бинарного поля
     * @param fieldName Имя устанавливаемого поля
     */
    protected void addBlobProperty(Builder builder, XFWAttribute attribute, String fieldName) {
        boolean generate = attribute.getGenerateBlobInfoFields();
        if (generate) {
            String prefix = getNameFromColumn(attribute);
            builder.addStatement(
                    "dto.addProperty($S, createBinPropDescriptor(domainObject.get$L(), "
                            + "domainObject.get$L(), domainObject.get$L(), domainObject.get$L()))",
                    fieldName,
                    capitalize(fieldName),
                    capitalize(prefix + attribute.getFileNameSuffix()),
                    capitalize(prefix + attribute.getContentSizeSuffix()),
                    capitalize(prefix + attribute.getContentTypeSuffix()));
        } else {
            builder.addStatement(
                    "dto.addProperty($S, createBinPropDescriptor(domainObject.get$L(), null, null, null))",
                    fieldName,
                    capitalize(fieldName));
        }
    }

    /*protected String addSetVersionStmt() {
        if (!isReadOnlyType()) {
            return "  o.setVersion(dto.getTs());\n";
        }
        return "";
    }*/

    /**
     * Фильтр для списка полей, блокирующий поля с анотацией {@link XFWServerOnly}.
     *
     * @return предикат.
     */
    protected Predicate<EStructuralFeature> serverOnlyFilter() {
        return f -> f.getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) == null
                && f.getEType().getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) == null;
    }

    /**
     * Фильтр полей fromTo метода.
     *
     * @return предикат
     */
    public Predicate<EStructuralFeature> fromToFieldsFilter() {
        return FEATURE_PREDICATE.and(serverOnlyFilter());
    }

    /**
     * Фильтр полей parsePropValue метода.
     *
     * @return предикат
     */
    public Predicate<EStructuralFeature> parsePropMethodFieldsFilter() {
        return PARSE_PROP_PREDICATE.and(serverOnlyFilter());
    }

    /**
     * Фильтр полей toTo метода.
     *
     * @return предикат
     */
    public Predicate<EStructuralFeature> toToMethodFieldsFilter() {
        return FEATURE_PREDICATE.and(serverOnlyFilter());
    }


    /**
     * Добавляет код копирования значения бинарного свойства из DTO в доменный объект.
     *
     * @param builder   Билдер метода fromTo
     * @param attribute EMF-метаданные бинарного поля
     * @param fieldName имя бинарного поля
     */
    protected void setBlobPropertyToDomainObject(Builder builder, XFWAttribute attribute, String fieldName) {
        boolean generateAdditionalFields = attribute.getGenerateBlobInfoFields();
        if (generateAdditionalFields) {
            builder.addCode(
                    "dto.copyPropValue($S, v -> \n"
                            + "{\n"
                            + "    $T resourceProperties = get$T(v);\n"
                            + "    o.set$L(createBlob(v));\n"
                            + "    o.set$L(getFileNameFromDescriptor(v));\n"
                            + "    o.set$L(resourceProperties != null ? resourceProperties.getContentSize() : null);\n"
                            + "    o.set$L(resourceProperties != null ? resourceProperties.getContentType() : null);\n"
                            + "});\n",
                    fieldName,
                    ResourceProperties.class,
                    ResourceProperties.class,
                    capitalize(fieldName),
                    capitalize(getFieldNameWithSuffix(
                            attribute, attribute.getFileNameSuffix())),
                    capitalize(getFieldNameWithSuffix(
                            attribute, attribute.getContentSizeSuffix())),
                    capitalize(getFieldNameWithSuffix(
                            attribute, attribute.getContentTypeSuffix())));
        } else {
            builder.addCode(
                    "dto.copyPropValue($S, v -> \n"
                            + "{\n"
                            + "    o.set$L(createBlob(v));\n"
                            + "});\n",
                    fieldName,
                    capitalize(fieldName));
        }
    }

    /**
     * Генерирует дополнительный код в метод fromTo.
     *
     * @param builder Билдер метода
     */
    protected void specificFromToEnhancement(Builder builder) {
    }


    /**
     * Генерирует метод сериализации значения свойства ключа в строковое представления.
     *
     * @return Билдер метода
     */
    protected Builder createSerializeKeyMethod() {
        return MethodSpec.methodBuilder("serializeKey")
                .addModifiers(Modifier.PUBLIC).returns(get(String.class))
                .addParameter(getKeyType(), "key").addStatement("return String.valueOf(key)");
    }

    /**
     * Генерирует метод парсинга строкового представления свойства ключа.
     *
     * @return Билдер метода
     */
    protected Builder createParseKeyMethod() {
        return MethodSpec.methodBuilder("parseKey")
                .addModifiers(Modifier.PUBLIC).returns(getKeyType())
                .addParameter(get(String.class), "key")
                .addStatement("return key");
    }

    private static String getNameFromColumn(EStructuralFeature attr) {
        final EAnnotation annotation = attr.getEAnnotation(XFWConstants.COLUMN_ANNOTATION_SOURCE.getUri());
        if (annotation != null) {
            final String name = annotation.getDetails().get("name");
            if (StringUtils.isNotEmpty(name)) {
                return GeneratorHelper.toValidJavaIdentifier(name);
            }
        }
        return attr.getName();
    }

    /**
     * Проверяет имеет ли класс или его предки анотацию {@code @XFWReadOnly}.
     *
     * @return true, если анотация имеется.
     */
    private boolean isReadOnlyType() {
        if (xfwClass.getEAnnotation(XFWConstants.READ_ONLY_TYPE_ANNOTATION_SOURCE.getUri()) != null) {
            return true;
        }

        for (EClass eclass : xfwClass.getESuperTypes()) {
            if (eclass.getEAnnotation(XFWConstants.READ_ONLY_TYPE_ANNOTATION_SOURCE.getUri()) != null) {
                return true;
            }
        }
        return false;
    }

    private static String getFieldNameWithSuffix(EStructuralFeature field, String suffix) {
        String prefix = getNameFromColumn(field);
        return prefix + suffix;
    }

    /**
     * Вспомогательный класс для дедуплецирования полей в stream api.
     */
    protected static class DistinctFieldWrapper {
        /**
         * Поле класса.
         */
        protected final XFWReference field;

        /**
         * Конструктор.
         *
         * @param fieldSpec - {@link XFWReference} описание поля.
         */
        DistinctFieldWrapper(XFWReference fieldSpec) {
            this.field = fieldSpec;
        }

        /**
         * Получить {@link XFWReference} поля.
         *
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

            DistinctFieldWrapper that = (DistinctFieldWrapper) obj;

            return !(field != null ? !field.getEType().getInstanceClassName()
                    .equals(that.field.getEType().getInstanceClassName())
                    : that.field != null);
        }
    }

    /**
     * Возврщает имя класса ссылки.
     *
     * @param ref ссылка
     * @return имя класса.
     */
    protected String getGeneratorInstanceClassName(EStructuralFeature ref) {
        return ref.getEType().getInstanceClassName();
    }

    /**
     * Объект с полями для передачи методам.
     */
    protected static class NavigableStructureInfo {
        private EAnnotation annotation;
        private ClassName containerType;
        private String fieldType;
        private String mappedBy;

        /**
         * Конструктор.
         * @param annotation аннотация из ecore
         * @param containerType Set или List (указан в аннотации)
         * @param fieldType тиа дженерика (обычно)
         * @param mappedBy обратное св-во
         */
        public NavigableStructureInfo(EAnnotation annotation, ClassName containerType,
                                      String fieldType, String mappedBy) {
            this.annotation = annotation;
            this.containerType = containerType;
            this.fieldType = fieldType;
            this.mappedBy = mappedBy;
        }
    }

    /**
     * Метод построения объекта NavigableStructureInfo.
     * @param xfwReference ссылка
     * @return NavigableStructureInfo
     */
    protected NavigableStructureInfo buildNavigableStructureInfo(XFWReference xfwReference) {
        EAnnotation annotation =
                xfwReference.getEAnnotation(XFWConstants.RELATION_ANNOTATION_SOURCE.getUri());
        String fieldType = annotation.getDetails().get("actualType");
        //Тип коллекции навигируемых свойств
        String container = annotation.getDetails().get("container");
        ClassName containerType = "Set".equals(container) ? get(Set.class) : get(List.class);
        //название обратного свойства
        String mappedBy = annotation.getDetails().get("mappedBy");

        return new NavigableStructureInfo(annotation, containerType, fieldType, mappedBy);
    }

}
