package ru.croc.ctp.jxfw.facade;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.domain.meta.XFWServerOnly;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainResult;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.WebclientQueryParamsBuilder;
import ru.croc.ctp.jxfw.core.generator.Constants;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.context.LoadContextKeys;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWReference;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.squareup.javapoet.ClassName.get;
import static com.squareup.javapoet.TypeName.BOOLEAN;
import static org.apache.commons.lang.WordUtils.capitalize;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addExportFieldsToController;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addExportParametersToMethod;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addHintsConcatenationStatement;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addLoadContextParametersToMethod;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.createExportMethodBody;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.getClassName;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.getPackageName;
import static ru.croc.ctp.jxfw.facade.FacadeGeneratorUtils.getKeyClassName;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.GENERATE_EXPORT_OPTION;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.isComplexType;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.useFulltext;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.useFulltextByDefault;

/**
 * Класс для создания контроллера для WebClient.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class WebClientControllerCreator implements ControllerCreator {

    private static final XFWMMPackage XFW_PACKAGE = XFWMMPackage.eINSTANCE;

    private static final ClassName JXFW_CONTROLLER_BASE = get(ControllerBase.class);


    private static final ClassName JXFW_OBJECT_FILTER = get(ObjectFilter.class);

    private static final ClassName JXFW_DOMAIN_TO = get(DomainTo.class);

    private static final ClassName JXFW_DOMAIN_RESULT = get(DomainResult.class);

    private static final ParameterizedTypeName RESP_DOMAIN_RESULT = ParameterizedTypeName
            .get(get(ResponseEntity.class), JXFW_DOMAIN_RESULT);

    private static final String PARAMETR_EXPAND = "$expand";

    /**
     * Класс доменного объекта.
     */
    protected ClassName jxfwDomainType;

    @Override
    public List<JavaFile> create(XFWClass xfwClass, Map<String, Object> options) {
        List<JavaFile> result = new ArrayList<>();
        //в случае когда вместе с базовым хранилищем планируется использовать поиск через fulltext
        if (!xfwClass.getPersistenceModule().contains("SOLR") && useFulltext(xfwClass)) {
            result.add(create(xfwClass, options, true, false)); //для fulltext
            //"родной" который умеет форвардить запрос в контроллер fulltext
            result.add(create(xfwClass, options, false, true));
        } else {
            //"родной" контроллер (не нужно генерить в отдельном пакете, и форвардить запросы в иные контроллеры)
            result.add(create(xfwClass, options, false, false));
        }

        return result;
    }

    /**
     * Фильтр для списка полей, блокирующий поля с анотацией {@link XFWServerOnly}.
     *
     * @return предикат.
     */
    protected Predicate<EStructuralFeature> serverOnlyFilter() {
        return f -> f.getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) == null;
    }

    private JavaFile create(XFWClass xfwClass,
                            Map<String, Object> options,
                            boolean generateControllerForFulltext,
                            boolean useFulltextFeature) {
        final String simpleClassName = xfwClass.getName();
        String className = xfwClass.getInstanceClassName();

        boolean isDSE = true; //TODO разобраться с названием
        String packageFacade;
        if (generateControllerForFulltext) { //будем генерить контроллер fulltext в своем пакете
            packageFacade = getFulltextPkgFacade(className);
            String packageName = getPackageName(className);

            className = ClassName.get(packageName + ".solr", simpleClassName).toString();
            isDSE = false;
        } else {
            packageFacade = getPkgFacade(className);
        }


        jxfwDomainType = get(getPackageName(className), simpleClassName);


        final FieldSpec domainToServicesResolverField = FieldSpec
                .builder(DomainToServicesResolverWebClient.class, "domainToServicesResolver", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();

        final FieldSpec serviceTransObj = FieldSpec
                .builder(get(packageFacade, simpleClassName + "ToService"),
                        "serviceTO", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();

        ClassName domainClassName = getClassName(xfwClass, generateControllerForFulltext);
        ClassName keyDomainClassName = getKeyClassName(xfwClass, generateControllerForFulltext);

        final MethodSpec getAll = createGetAllMethod(simpleClassName, useFulltextFeature,
                xfwClass, generateControllerForFulltext);

        final MethodSpec.Builder getById = MethodSpec
                .methodBuilder("getById")
                .addModifiers(Modifier.PUBLIC)
                .returns(RESP_DOMAIN_RESULT)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                        .addMember("value", "\"$L({id})\"", simpleClassName)
                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.POST)
                        .build())
                .addParameter(ParameterSpec.builder(String.class, "id")
                        .addAnnotation(AnnotationSpec.builder(PathVariable.class).build())
                        .build())
                .addParameter(createExpandParameter());
        addLoadContextParametersToMethod(getById);
                // @RequestParam(required = false, name = "$hints", defaultValue = "") List<String> hints
        getById
                .addStatement("log.info(\"Fetch object {} by id = {}, with expand = {}\", $S, id, expand)",
                        simpleClassName)
                .addStatement("$1T<$2T, $3T> webclientQueryParamsBuilder = getWebclientQueryParamsBuilder().withDomainType($2T.TYPE_NAME)" +
                                ".withId(serviceTO.parseKey(id)).withExpand(expand)",
                        get(WebclientQueryParamsBuilder.class), domainClassName, keyDomainClassName)
                .addCode(getResponseDataAndMoreList(domainClassName, false))
                .build();

        String controllerSimpleName = simpleClassName + "Controller";

        AnnotationSpec restController = getRestControllerAnnotation(generateControllerForFulltext,
                controllerSimpleName);

        final TypeSpec.Builder controllerBuilder = TypeSpec
                .classBuilder(controllerSimpleName)
                .superclass(JXFW_CONTROLLER_BASE)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(restController)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$S", generateControllerForFulltext ? "**/api/fulltext" : "**/api")
                                .build())
                .addField(serviceTransObj)
                .addField(domainToServicesResolverField)
                .addMethod(getWebclientQueryParamsBuilder(domainClassName, keyDomainClassName))
                .addMethod(getRowWebclientQueryParamsBuilder())
                .addMethod(getAll)
                .addMethod(getById.build())
                .addMethod(getToDomainToListMethod(generateControllerForFulltext))
                .addMethod(getBuildResponseNavigablesOkMethod(domainClassName, keyDomainClassName));

        //для cass модуля поля
        addAdditionalFields(controllerBuilder, xfwClass, generateControllerForFulltext);
        //генерация поля loadService необходимых аннотаций и методов
        FieldSpec.Builder loadServiceFieldBuilder = getLoadServiceField();
        controllerBuilder.addMethod(getLoadServiceFieldSetter());
        controllerBuilder.addField(loadServiceFieldBuilder.build());

        controllerBuilder.addField(
                FieldSpec.builder(Logger.class, "log", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$T.getLogger($L.class)",
                                LoggerFactory.class, controllerSimpleName).build());

        if (useFulltextFeature) {
            //инжектим контроллер полнотекста
            final FieldSpec autowiredFullTextController = FieldSpec
                    .builder(get(getFulltextPkgFacade(className), controllerSimpleName),
                            "fulltextController", Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                    .build();

            controllerBuilder.addField(autowiredFullTextController);

            //ставим флаг, указывает из какого хранилища будет поиск по умолчанию
            FieldSpec flagFulltextRequest = FieldSpec.builder(BOOLEAN, Constants.FULLTEXT_ECORE_USE_FULLTEXT_BY_DEFAULT,
                    Modifier.PRIVATE)
                    .initializer(useFulltextByDefault(xfwClass) + "").build();

            controllerBuilder.addField(flagFulltextRequest);

        }

        if (isDSE) {
            // getters для навигируемых свойств
            xfwClass.getEAllStructuralFeatures()
                    .stream()
                    .filter(f -> f instanceof XFWReference)
                    .filter(f -> !isComplexType(f))
                    .filter(serverOnlyFilter())
                    .map(f -> (XFWReference) f)
                    .forEach(ref -> {
                        MethodSpec.Builder method = MethodSpec.methodBuilder("getProp" + capitalize(ref.getName()))
                                .addModifiers(Modifier.PUBLIC)
                                .returns(RESP_DOMAIN_RESULT)
                                .addAnnotation(AnnotationSpec
                                        .builder(RequestMapping.class)
                                        .addMember("value", "\"$L({id})/$L\"",
                                                simpleClassName, ref.getName())
                                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.POST)
                                        .build())
                                .addParameter(ParameterSpec.builder(String.class, "id")
                                        .addAnnotation(AnnotationSpec.builder(PathVariable.class)
                                                .build())
                                        .build())
                                .addParameter(createExpandParameter());
                        addLoadContextParametersToMethod(method);

                        addHintsConcatenationStatement(method);

                        method.addStatement("log.info(\"Fetch prop \\\"{}\\\" on object {} by id = {} with params = [expand=\\\"{}\\\"]\", $S, $S, id, expand)", ref.getName(), simpleClassName)
                                .addStatement("$1T<$2T, $3T> webclientQueryParamsBuilder = getWebclientQueryParamsBuilder().withDomainType($2T.TYPE_NAME)" +
                                                ".withId(serviceTO.parseKey(id)).withExpand(\"$4L\")",
                                        get(WebclientQueryParamsBuilder.class), domainClassName, keyDomainClassName, ref.getName())
                                .addStatement("$1T<?> loadContext = new $1T.Builder<>().withLocale(locale).withTimeZone(timeZone).withPrincipal(principal).withHints(hints).build()",
                                        get(LoadContext.class))
                                .addStatement("return buildResponseNavigablesOk(webclientQueryParamsBuilder.build(), loadContext, expand)")
                                .build();
                        controllerBuilder.addMethod(method.build());
                    });
        }

        // getters для бинарных свойств
        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(attr -> XFW_PACKAGE.getBlob().equals(attr.getEType()))
                .filter(f -> !isComplexType(f))
                .filter(serverOnlyFilter())
                .forEach(attr -> {
                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getBinProp" + capitalize(attr.getName()))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(void.class)
                            .addException(IOException.class)
                            .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                                    .addMember("value", "\"_file/binaryPropValue\"")
                                    .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                                    .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.POST)
                                    .addMember("params", "\"type=$L\"", simpleClassName)
                                    .addMember("params", "\"prop=$L\"", attr.getName())
                                    .build())
                            .addAnnotation(AnnotationSpec.builder(Transactional.class)
                                    .addMember("readOnly", "$L", true).build())
                            .addParameter(ParameterSpec.builder(HttpServletRequest.class, "request").build())
                            .addParameter(ParameterSpec.builder(HttpServletResponse.class, "response").build())
                            .addParameter(ParameterSpec.builder(String.class, "id")
                                    .addAnnotation(AnnotationSpec.builder(RequestParam.class).build()).build())
                            .addParameter(ParameterSpec.builder(String.class, "fileName")
                                    .addAnnotation(AnnotationSpec.builder(RequestParam.class)
                                            .addMember("required", "false").build()).build());
                    addLoadContextParametersToMethod(methodBuilder);
                    addHintsConcatenationStatement(methodBuilder);

                    methodBuilder.addStatement("$1T<$2T, $3T> webclientQueryParamsBuilder = getWebclientQueryParamsBuilder().withDomainType($2T.TYPE_NAME)"
                                    + ".withId(serviceTO.parseKey(id))", get(WebclientQueryParamsBuilder.class), domainClassName, keyDomainClassName)
                            .addCode("$1T<$2T> loadContext = new $1T.Builder<$2T>().withLocale(locale).withTimeZone(timeZone).withPrincipal(principal).withHints(hints).build();\n"
                                    + "loadContext.getCommonObjects().put($3T.BLOB_CONTENT, \"$4L\");\n\n"
                                    + "$2T obj = loadService.loadOne(webclientQueryParamsBuilder.build(), loadContext);\n",
                                    get(LoadContext.class), domainClassName, get(LoadContextKeys.class), attr.getName())
                            .addStatement("$T binPropValue = serviceTO.getBinProp$L(obj)",
                                    DomainToServiceImpl.BinPropValue.class, capitalize(attr.getName()))
                            .addCode("if(binPropValue != null && binPropValue.getContent() != null) {\n"
                                            + "    try($T stream = binPropValue.getContent().getBinaryStream()) {\n"
                                            + "        $T.copy(stream, response.getOutputStream());\n"
                                            + "    } catch ($T e) {\n"
                                            + "        throw new RuntimeException(e);\n"
                                            + "    }\n"
                                            + "}\n",
                                    InputStream.class,
                                    IOUtils.class,
                                    SQLException.class)
                            .addStatement("buildBinPropResponse(request, response, binPropValue)")
                            .build();
                    controllerBuilder.addMethod(methodBuilder.build());

                });

        boolean generateExport = (boolean) options.getOrDefault(GENERATE_EXPORT_OPTION, true);
        if (generateExport) {
            addExportFieldsToController(controllerBuilder);
            final MethodSpec method = createExportGetAllMethod(xfwClass,
                    simpleClassName, generateControllerForFulltext);
            controllerBuilder.addMethod(method);
        }

        TypeSpec controllerType = controllerBuilder.build();

        return JavaFile.builder(packageFacade, controllerType).build();
    }

    private void addAdditionalFields(
            TypeSpec.Builder controllerBuilder, XFWClass xfwClass,
            boolean generateControllerForFulltext
    ) {
        EList<String> persistenceModule = xfwClass.getPersistenceModule();
        if (persistenceModule.contains("CASS") && !generateControllerForFulltext) {
            FieldSpec.Builder builder = FieldSpec.builder(
                    get("ru.croc.ctp.jxfw.cass.facade.webclient", "SelectComposer"),
                    "selectComposer", Modifier.PRIVATE);
            builder.addAnnotation(AnnotationSpec.builder(Autowired.class).build());
            controllerBuilder.addField(builder.build());
        }
    }

    private MethodSpec getWebclientQueryParamsBuilder(ClassName className, ClassName keyClassName) {
        return MethodSpec
            .methodBuilder("getWebclientQueryParamsBuilder")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(AnnotationSpec.builder(Lookup.class).build())
            .returns(ParameterizedTypeName.get(get(WebclientQueryParamsBuilder.class), className, keyClassName))
            .build();
    }

    private MethodSpec getRowWebclientQueryParamsBuilder() {
        return MethodSpec
                .methodBuilder("getRowWebclientQueryParamsBuilder")
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(Lookup.class).build())
                .returns(get(WebclientQueryParamsBuilder.class))
                .build();
    }

    private FieldSpec.Builder getLoadServiceField() {
        return FieldSpec.builder(LoadService.class, "loadService", Modifier.PRIVATE);
    }

    private MethodSpec getLoadServiceFieldSetter() {
        return MethodSpec.methodBuilder("setLoadService")
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .addModifiers(Modifier.PUBLIC).addParameter(LoadService.class, "loadService")
                .addStatement("this.loadService = loadService").build();
    }

    public static MethodSpec getToDomainToListMethod(boolean generateControllerForFulltext) {
        TypeVariableName domainObjectType = TypeVariableName.get("T", ParameterizedTypeName.get(get(DomainObject.class), WildcardTypeName.subtypeOf(Object.class)));
        MethodSpec.Builder specBuilder = MethodSpec
            .methodBuilder("toDomainToList")
            .addTypeVariable(domainObjectType)
            .addModifiers(Modifier.PRIVATE)
            .returns(ParameterizedTypeName.get(get(Iterable.class), JXFW_DOMAIN_TO))
            .addParameter(ParameterizedTypeName.get(get(Iterable.class), domainObjectType), "data")
            .addParameter(ArrayTypeName.of(String.class), "expand")
            .varargs(true)
            .addStatement("$T<DomainTo> result = new $T<>()", List.class, ArrayList.class);
        if (generateControllerForFulltext) {
            specBuilder.addStatement("data.forEach(domainObject -> {"
                    + System.lineSeparator() + "$T domainToService = " + "domainToServicesResolver.$L(domainObject.getTypeName());"
                    + System.lineSeparator() + "DomainTo domainTo = domainToService." + "toTo" + "(domainObject, expand); "
                    + System.lineSeparator() + "result.add(domainTo);"
                    + System.lineSeparator() + "})",
                DomainToService.class, "resolveFulltextToService");

        } else {
            specBuilder
                .addStatement("$T<String, List<T>> groupOfDomainObjects = $T.stream(data.spliterator(),false)\n" +
                "   .collect(Collectors.groupingBy(o -> o.getTypeName(), Collectors.toList()))", Map.class, StreamSupport.class)
                .beginControlFlow("for (String type: groupOfDomainObjects.keySet())")
                .addStatement("$T domainToService = domainToServicesResolver.resolveToService(type)", ParameterizedTypeName.get(get(DomainToService.class), domainObjectType, WildcardTypeName.subtypeOf(Object.class)))
                .addStatement("List<DomainTo> domainTo = domainToService.toToPolymorphic(groupOfDomainObjects.get(type), type, expand)")
                .addStatement("result.addAll(domainTo)")
                .endControlFlow();
        }

        specBuilder.addStatement("return result");

        return specBuilder.build();
    }

    public static MethodSpec getBuildResponseNavigablesOkMethod(ClassName className, ClassName keyClassName) {
        return MethodSpec
            .methodBuilder("buildResponseNavigablesOk")
            .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                    .addMember("value", "{\"unchecked\", \"rawtypes\"}").build())
            .addModifiers(Modifier.PRIVATE)
            .returns(RESP_DOMAIN_RESULT)
            .addParameter(ParameterSpec.builder(ParameterizedTypeName
                    .get(get(QueryParams.class), className, keyClassName), "queryParams").build())
            .addParameter(ParameterSpec.builder(get(LoadContext.class), "loadContext").build())
            .addParameter(ParameterSpec.builder(get(String.class), "expand").build())
            .addStatement("loadService.load(queryParams, ($1T<$2T>) loadContext)", get(LoadContext.class), className)
            .addStatement("$1T<?> loadResult = loadContext.getLoadResult()", get(LoadResult.class))
            .addStatement("$1T<$2T<?>> navigablesList = loadResult.getMoreList()", get(List.class), get(DomainObject.class))
            .addStatement("$1T<$2T<?>> results = new $3T<>()", get(List.class), get(LoadResult.class), get(ArrayList.class))
            .addCode( "navigablesList.forEach(domainObject -> {\n"
                    + "  $1T paramsBuilder = getRowWebclientQueryParamsBuilder().withDomainType(domainObject.getTypeName()).withId(domainObject.getId()).withExpand(expand);\n"
                    + "  loadService.load(paramsBuilder.build(), loadContext);\n"
                    + "  results.add(loadContext.getLoadResult());\n"
                    + "});\n", get(WebclientQueryParamsBuilder.class))
            .addCode( "return buildResponseOk(toDomainToList(results.stream().flatMap(lr -> ((List<DomainObject<?>>)lr.getData()).stream()).collect($1T.toList()), getExpandFirstLevelProps(expand)),\n"
                    + "  toDomainToList($2T.distinct(results.stream().flatMap(lr -> lr.getMoreList().stream()).collect($1T.toList())), getExpandInnerProps(expand)));\n",
                    get(Collectors.class), get(DomainObjectUtil.class))
            .build();
    }

    private CodeBlock getResponseDataAndMoreList(ClassName className, boolean useHints) {


        CodeBlock.Builder builder = CodeBlock.builder()
                .addStatement("final $T<String> hints = $T.concat(hintsStr.stream(), hintsArr.stream()).collect($T.toList())",
                        get(List.class),
                        get(Stream.class),
                        get(java.util.stream.Collectors.class))
                .addStatement("$1T<$2T> loadContext = new $1T.Builder<$2T>().withLocale(locale).withTimeZone(timeZone).withPrincipal(principal).withHints(hints).build()",
                        get(LoadContext.class), className)
                .addStatement("loadService.load(webclientQueryParamsBuilder.build(), loadContext)")
                .addStatement("$1T<$2T> loadResult = loadContext.getLoadResult()", get(LoadResult.class), className);
        if (useHints) {
            builder.beginControlFlow("if (loadResult.getHints().size() > 0)")
                    .addStatement("return buildResponseOk(toDomainToList(loadResult.getData(), getExpandFirstLevelProps(expand)), "
                            + "toDomainToList(loadResult.getMoreList(), getExpandInnerProps(expand)), loadResult.getHints())")
                    .endControlFlow();
        }

        builder.addStatement("return buildResponseOk(toDomainToList(loadResult.getData(), getExpandFirstLevelProps(expand)), "
                + "toDomainToList(loadResult.getMoreList(), getExpandInnerProps(expand)))")
                .build();

        return builder.build();
    }

    private static ParameterSpec createExpandParameter() {
        return ParameterSpec
                .builder(String.class, "expand")
                .addAnnotation(
                        AnnotationSpec
                                .builder(RequestParam.class)
                                .addMember("value", "$S", PARAMETR_EXPAND)
                                .addMember("required", "$L", Boolean.FALSE).build())
                .build();
    }

    private AnnotationSpec getRestControllerAnnotation(boolean generateControllerForFulltext, String controllerSimpleName) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(RestController.class);
        if (generateControllerForFulltext) {
            builder.addMember("value", "\"" + "solr" + controllerSimpleName + "\"");
        }
        return builder.build();
    }

    private String getFulltextPkgFacade(String className) {
        String packageName = getPackageName(className);
        return packageName + ".facade.webclient.solr";
    }

    private String getPkgFacade(String className) {
        String packageName = getPackageName(className);
        return packageName + ".facade.webclient";
    }

    private MethodSpec createGetAllMethod(String simpleClassName, boolean useFulltextFeature,
                                          XFWClass xfwClass, boolean generateControllerForFulltext) {
        MethodSpec.Builder getAll = MethodSpec
                .methodBuilder("getAll")
                .addModifiers(Modifier.PUBLIC)
                .returns(RESP_DOMAIN_RESULT);

        getAll.addAnnotation(
                AnnotationSpec
                        .builder(RequestMapping.class)
                        .addMember("value", "$S", simpleClassName)
                        .addMember("method", "$T.$L",
                                RequestMethod.class, RequestMethod.GET)
                        .addMember("method", "$T.$L",
                                RequestMethod.class, RequestMethod.POST)
                        .build())
                .addParameter(createExpandParameter())
                .addParameter(
                        ParameterSpec
                                .builder(JXFW_OBJECT_FILTER, "filter")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$filter")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build())
                .addParameter(
                        ParameterSpec
                                .builder(Integer.class, "top")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$top")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build())
                .addParameter(
                        ParameterSpec
                                .builder(Integer.class, "skip")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$skip")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build())
                .addParameter(
                        ParameterSpec
                                .builder(String.class, "orderby")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$orderby")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build());
        addLoadContextParametersToMethod(getAll);

        if (useFulltextFeature) {
            getAll.addParameter(ParameterSpec
                    .builder(Boolean.class, "useFulltext")
                    .addAnnotation(
                            AnnotationSpec
                                    .builder(RequestParam.class)
                                    .addMember("value", "$S",
                                            "$usefulltext")
                                    .addMember("required", "$L",
                                            Boolean.FALSE).build())
                    .build());
        }

        ClassName domainClassName = getClassName(xfwClass, generateControllerForFulltext);
        ClassName keyDomainClassName = getKeyClassName(xfwClass, generateControllerForFulltext);

        getAll.addStatement("log.info(\"Fetch all {} by params = [expand=\\\"{}\\\", filter=\\\"{}\\\"]\", "
                + "$S, expand, filter != null ? filter.toString() : \"null\")", simpleClassName)
                .addCode(forwardRequest(useFulltextFeature))
                .addStatement("$1T<$2T, $3T> webclientQueryParamsBuilder = getWebclientQueryParamsBuilder().withDomainType($2T.TYPE_NAME)" +
                                ".withFilter(filter).withSkip(skip).withTop(top).withOrderBy(orderby).withExpand(expand)",
                        get(WebclientQueryParamsBuilder.class), domainClassName, keyDomainClassName)
                .addCode(getResponseDataAndMoreList(domainClassName, true));

        return getAll.build();
    }

    private static CodeBlock forwardRequest(boolean useFulltextFeature) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (useFulltextFeature) {
            builder.beginControlFlow("if (useFulltext != null)")
                    .beginControlFlow("if (useFulltext)")
                    .addStatement("return fulltextController.getAll(expand, filter, top, skip, orderby, locale, timeZone, principal, hintsStr, hintsArr)")
                    .endControlFlow()
                    .nextControlFlow("else if (useFulltextByDefault)")
                    .addStatement("return fulltextController.getAll(expand, filter, top, skip, orderby, locale, timeZone, principal, hintsStr, hintsArr)")
                    .endControlFlow();
        }
        return builder.build();
    }

    private MethodSpec createExportGetAllMethod(XFWClass xfwClass, String simpleClassName, boolean generateControllerForFulltext) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("getAllToExport")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(
                        AnnotationSpec
                                .builder(RequestMapping.class)
                                .addMember("value", "\"_export/$L\"",
                                        simpleClassName)
                                .addMember("method", "$T.$L",
                                        RequestMethod.class, RequestMethod.GET)
                                .addMember("method", "$T.$L",
                                        RequestMethod.class, RequestMethod.POST)
                                .build())
                .addParameter(
                        ParameterSpec
                                .builder(JXFW_OBJECT_FILTER, "filter")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$filter")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build())
                .addParameter(
                        ParameterSpec
                                .builder(String.class, "orderby")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$orderby")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build());
        addLoadContextParametersToMethod(builder);
        addExportParametersToMethod(builder);


        ClassName domainClassName = getClassName(xfwClass, generateControllerForFulltext);
        ClassName keyDomainClassName = getKeyClassName(xfwClass, generateControllerForFulltext);

        addHintsConcatenationStatement(builder);

        createExportMethodBody(builder, method ->
                method.addStatement("$1T<$2T, $3T> webclientQueryParamsBuilder = getWebclientQueryParamsBuilder().withDomainType($2T.TYPE_NAME).withFilter(filter)"
                                + ".withOrderBy(orderby).withExpand(exportConfigObj.getExpand())",
                            get(WebclientQueryParamsBuilder.class), domainClassName, keyDomainClassName)
                    .addStatement("$1T<$2T, $3T> queryParams = webclientQueryParamsBuilder.build()",
                            get(QueryParams.class), domainClassName, keyDomainClassName)
                    .addStatement("$1T<$2T> loadContext = new $1T.Builder<$2T>().withLocale(locale).withTimeZone(timeZone).withPrincipal(principal).withHints(hints).forExport().build()",
                            get(LoadContext.class), domainClassName));

        return builder.build();
    }


}
