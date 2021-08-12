package ru.croc.ctp.jxfw.facade;

import static com.google.common.collect.Lists.newArrayList;
import static com.squareup.javapoet.ClassName.get;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.apache.commons.lang3.ClassUtils.getShortClassName;
import static ru.croc.ctp.jxfw.core.export.facade.webclient.ExportController.NO_EXPORT_ENGINE_MESSAGE;
import static ru.croc.ctp.jxfw.core.reporting.XfwReportService.NO_REPORTING_ENGINE;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.JXFW_EXPORT_CONFIG;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addExportFieldsToController;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addExportParametersToMethod;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addHintsConcatenationStatement;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.addLoadContextParametersToMethod;
import static ru.croc.ctp.jxfw.facade.ControllerGenerationUtils.createExportMethodBody;
import static ru.croc.ctp.jxfw.facade.FacadeType.WEBCLIENT_EXPORT;
import static ru.croc.ctp.jxfw.facade.WebClientControllerCreator.getToDomainToListMethod;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.GENERATE_EXPORT_OPTION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.ClassUtils;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ctp.jxfw.core.datasource.DataSourceResult;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.export.impl.ClientExportDataProvider;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainResult;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.load.GeneralLoadContext;
import ru.croc.ctp.jxfw.core.load.GeneralLoadResult;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWOperation;
import ru.croc.ctp.jxfw.metamodel.XFWReference;
import ru.croc.ctp.jxfw.reporting.birt.BirtReportService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * Класс для создания фасада DataSource для WebClient.
 *
 * @author Nosov Alexander on 09.12.15.
 */
public class WebClientDataSourceCreator implements DataSourceCreator {

    private static final Logger logger = LoggerFactory.getLogger(WebClientDataSourceCreator.class);

    private static final ClassName JXFW_CONTROLLER_BASE = get(ControllerBase.class);


    private static final AnnotationSpec VALIDATED = AnnotationSpec.builder(Validated.class).build();

    private static final ParameterizedTypeName RESP_DOMAIN_RESULT =
            ParameterizedTypeName.get(get(ResponseEntity.class), get(DomainResult.class));

    @Override
    public List<JavaFile> create(String dataSourceServiceName, Iterable<XFWDataSource> xfwDataSources,
                                 Map<String, Object> options, boolean birtReports) {
        boolean generateExport = (boolean) options.getOrDefault(GENERATE_EXPORT_OPTION, true);

        boolean dataSourceClasses = xfwDataSources.iterator().next().getOperationCount() == 0;

        final String packageName = getPackageName(dataSourceServiceName);
        final String shortClassName = getShortClassName(dataSourceServiceName);
        final String packageFacade = packageName + ".facade.webclient";

        final ClassName serviceClass = ClassName.get(packageName, shortClassName);
        final FieldSpec service = FieldSpec
                .builder(serviceClass, "service", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();

        final FieldSpec loadService = FieldSpec
                .builder(ClassName.get(LoadService.class), "loadService", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();

        final FieldSpec domainServicesResolver = FieldSpec
                .builder(DomainServicesResolver.class, "domainServicesResolver", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();

        final FieldSpec domainToServicesResolver = FieldSpec
                .builder(DomainToServicesResolverWebClient.class, "domainToServicesResolver", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();

        final FieldSpec applicationContext = FieldSpec
                .builder(get(ApplicationContext.class),
                        "applicationContext", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build();


        final TypeSpec.Builder controllerBuilder = TypeSpec
                .classBuilder(shortClassName + "Controller")
                .superclass(JXFW_CONTROLLER_BASE)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(RestController.class).addMember("value", "$S",
                        packageFacade + "." + shortClassName).build())
                .addAnnotation(VALIDATED)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                        .addMember("value", "$S", "**/api").build())
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "{\"unchecked\", \"rawtypes\"}").build())
                .addField(loadService)
                .addField(domainServicesResolver)
                .addField(domainToServicesResolver)
                .addField(applicationContext)
                .addField(FieldSpec.builder(Logger.class, "log", Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                        .initializer("$T.getLogger(" + shortClassName + "Controller" + ".class)",
                                LoggerFactory.class).build());


        final FieldSpec reportService = FieldSpec
                .builder(BirtReportService.class, "reportService", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class)
                        .addMember("required", "$L", Boolean.FALSE)
                        .build())
                .build();
        if (birtReports) {
            controllerBuilder.addField(reportService);
        }


        if (!dataSourceClasses) {
            controllerBuilder.addField(service);
        }

        if (!dataSourceClasses) {
            xfwDataSources.forEach(xfwDataSource -> {

                final XFWOperation operation = (XFWOperation) xfwDataSource.getEOperations().get(0);
                final String returnClassName = operation.getEType().getName();

                final MethodSpec.Builder method = MethodSpec.methodBuilder(xfwDataSource.getName())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(
                                AnnotationSpec.builder(RequestMapping.class)
                                        .addMember("value", "$S", xfwDataSource.getRequestMapping())
                                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.POST)
                                        .build());

                fillParameters(operation, method);

                final EAnnotation transientAnnotation =
                        operation.getEType().getEAnnotation(XFWConstants.OBJECT_PROPS_ANNOTATION_SOURCE.getUri());

                if ("Tuple".equals(returnClassName)) {
                    method.returns(RESP_DOMAIN_RESULT);
                    method
                            .addCode(getDSLoaderLoadStmt(xfwDataSource, operation))
                            .addStatement("final $1T result = new $1T.Builder().result(dataSourceResult).build()",
                                    DomainResult.class)
                            .addStatement("return new $T<>(result, $T.OK)", ResponseEntity.class, HttpStatus.class);

                } else if (transientAnnotation != null && transientAnnotation.getDetails() != null
                        && "true".equals(transientAnnotation.getDetails().get("transient"))) {
                    method.returns(RESP_DOMAIN_RESULT);
                    method
                            .addCode(getDSLoaderLoadStmt(xfwDataSource, operation))
                            .addStatement("$1T toMoreList = ($1T) dataSourceResult.getMore()", List.class)
                            .addStatement("$1T resultHints = ($2T) list.getHints()",
                                    ParameterizedTypeName.get(Map.class, String.class, Object.class),
                                    ParameterizedTypeName.get(HashMap.class, String.class, Object.class))
                            .addStatement("return buildResponseOk(list.getData(), toMoreList, resultHints)");

                } else if ("List".equals(returnClassName) || "Set".equals(returnClassName)) {
                    method.returns(RESP_DOMAIN_RESULT);
                    method
                            .addCode(getDSLoaderLoadStmt(xfwDataSource, operation))
                            .addStatement("final $1T result = new $1T.Builder().result(dataSourceResult).build()",
                                    DomainResult.class)
                            .addStatement("return new $T<>(result, $T.OK)", ResponseEntity.class, HttpStatus.class);
                } else {
                    method.returns(RESP_DOMAIN_RESULT);
                    addToListCode(method, xfwDataSource, operation);
                    method.addStatement("$1T toMoreList = ($1T) list.getMore().stream().map(o -> {\n"
                                    + "    $4T toService = "
                                    + "domainToServicesResolver.resolveToService((($2T)o).getTypeName());\n"
                                    + "    return toService.toToPolymorphic(($2T)o);"
                                    + "\n}).collect($3T.toList())",
                            List.class, DomainObject.class, Collectors.class,
                            get(DomainToService.class))
                            .addStatement("$1T resultHints = ($2T) list.getHints()",
                                    ParameterizedTypeName.get(Map.class, String.class, Object.class),
                                    ParameterizedTypeName.get(HashMap.class, String.class, Object.class))
                            .addStatement("return buildResponseOk(toList, toMoreList, resultHints)");
                }

                controllerBuilder.addMethod(method.build());
            });
        } else {
            xfwDataSources.forEach(xfwDataSource -> {
                generateControllerMethod(xfwDataSource, controllerBuilder,
                        birtReports ? FacadeType.BIRT_REPORT : FacadeType.WEBCLIENT_CONTROLLER);
            });

        }


        if (generateExport && !birtReports) {
            addExportFieldsToController(controllerBuilder);
            if (!dataSourceClasses) {
                createExportAllMethod(controllerBuilder, xfwDataSources);
            } else {
                xfwDataSources.forEach(xfwDataSource -> {
                    generateControllerMethod(xfwDataSource, controllerBuilder, WEBCLIENT_EXPORT);
                });
            }
        }

        //controllerBuilder.addMethod(getToDomainToListMethod());
        controllerBuilder.addMethod(getToDomainToListMethod(false));


        return newArrayList(JavaFile.builder(packageFacade, controllerBuilder.build()).build());
    }

    private void generateControllerMethod(XFWDataSource xfwDataSource, TypeSpec.Builder controllerBuilder, FacadeType facadeType) {
        String url = xfwDataSource.getRequestMapping();
        switch (facadeType) {
            case BIRT_REPORT:
                url = "reports/" + url;
                break;
            case WEBCLIENT_EXPORT:
                url = "_export/" + url;
                break;
        }
        final MethodSpec.Builder method = MethodSpec.methodBuilder(
                "dataSource" + xfwDataSource.getName() + facadeType.name())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$S", url)
                                .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                                .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.POST)
                                .build());


        addControllerMethodParameter(method, "expand", String.class);
        addControllerMethodParameter(method, "top", Integer.class);
        addControllerMethodParameter(method, "skip", Integer.class);
        addControllerMethodParameter(method, "orderby", String.class);
        addControllerMethodParameter(method, "fetchTotal", Boolean.class);


        addLoadContextParametersToMethod(method);
        if (xfwDataSource.isGeneral() && facadeType == WEBCLIENT_EXPORT) {
            method.addStatement("throw new $1T(\"$2N\")", UnsupportedOperationException.class, NO_EXPORT_ENGINE_MESSAGE);
            controllerBuilder.addMethod(method.build());
            return;
        }


         for (EStructuralFeature feature : xfwDataSource.getEStructuralFeatures()) {
            if(feature instanceof XFWReference){
              // это объект-фильтр
                XFWClass filter = (XFWClass)((XFWReference)feature).getEReferenceType();
                for (Map.Entry<String, EStructuralFeature> entry : filter.getFeaturesFlatMap().entrySet()) {
                    addControllerMethodParameter(method, entry.getKey(), entry.getValue(), true);
                }
            } else {
                addControllerMethodParameter(method, feature.getName(), feature, false);
            }
        }

        if (facadeType == FacadeType.BIRT_REPORT) {
            method
                    .addParameter(ParameterSpec.builder(HttpServletRequest.class, "request").build())
                    .addParameter(ParameterSpec.builder(HttpServletResponse.class, "response").build())
                    .addParameter(ParameterSpec.builder(String.class, "format")
                            .addAnnotation(AnnotationSpec.builder(RequestParam.class)
                                    .addMember("required", "$L", Boolean.FALSE)
                                    .addMember("defaultValue", "$S", "html").build()).build());
        }
        if (facadeType == WEBCLIENT_EXPORT) {
            addExportParametersToMethod(method);

        }

        method.addStatement("$1T<String, Object> filter = new $2T()", get(Map.class), get(HashMap.class));

        for (EStructuralFeature feature : xfwDataSource.getEStructuralFeatures()) {
            if(feature instanceof XFWReference){
                // это объект-фильтр
                XFWClass filter = (XFWClass)((XFWReference)feature).getEReferenceType();
                for (Map.Entry<String, EStructuralFeature> entry : filter.getFeaturesFlatMap().entrySet()) {
                    method.beginControlFlow("if($L!=null)", getParameterName(entry.getKey()));
                    method.addStatement(" filter.put($S,$L)", entry.getKey(), getParameterName(entry.getKey()));
                    method.endControlFlow();
                }
            } else {
                method.beginControlFlow("if($L!=null)", getParameterName(feature.getName()));
                method.addStatement(" filter.put($S,$L)", feature.getName(), getParameterName(feature.getName()));
                method.endControlFlow();
            }
        }

        String instanceTypeName = xfwDataSource.getInstanceClassName();

        addHintsConcatenationStatement(method);

        method.addStatement("$1T ds = applicationContext.getBean($1T.class)",
                get(ClassUtils.getPackageName(instanceTypeName), ClassUtils.getShortClassName(instanceTypeName)));

        method.addStatement("$1T loadContext = new $1T.Builder().withLocale(locale).withTimeZone(timeZone).withPrincipal(principal).withHints(hints)$2L.build()",
                get(typeOfLoadContextForDataSource(xfwDataSource)),
                facadeType == WEBCLIENT_EXPORT && typeOfLoadContextForDataSource(xfwDataSource).equals(LoadContext.class) ? ".forExport()" : "");
        method.addStatement("Iterable data = null");


        switch (facadeType) {
            case BIRT_REPORT:
                method.addStatement("ds.setParams(expand, top, skip, orderby, fetchTotal, filter)");
                method.addStatement("$T loadResult = ds.load(loadContext)", get(GeneralLoadResult.class));
                method.addStatement("data = loadResult.getData()");
                birtReportBody(xfwDataSource, method);
                break;
            case WEBCLIENT_CONTROLLER:
                method.addStatement("ds.setParams(expand, top, skip, orderby, fetchTotal, filter)");
                method.addStatement("$T loadResult = ds.load(loadContext)", get(GeneralLoadResult.class));
                method.addStatement("return buildGeneralResponseOk(loadResult, domainToServicesResolver, expand)");
                break;
            case WEBCLIENT_EXPORT:
                createExportMethodBody(method, builder ->
                        builder.addStatement("ds.setParams(exportConfigObj.getExpand(), null, null, orderby, null, filter)")
                                .addStatement("$T queryParams = ds.queryParams()", get(QueryParams.class)));
                method.addStatement("return");
                break;
        }

        if (facadeType != FacadeType.WEBCLIENT_CONTROLLER) {
            method.returns(void.class);
        } else {
            method.returns(RESP_DOMAIN_RESULT);
        }

        controllerBuilder.addMethod(method.build());
    }

    /**
     * Возвращает тип контекста для источника данных.
     * @param xfwDataSource источник данных
     * @return тип контекста.
     */
    private Class<?> typeOfLoadContextForDataSource(XFWDataSource xfwDataSource) {
        return xfwDataSource.isGeneral() ? GeneralLoadContext.class : LoadContext.class;
    }

    /**
     * Добавляет параметры в мето контроллера.
     * @param method билдер метода
     * @param chainingName имя свойста, м.б. через точку в случае комплексных полей.
     * @param feature метаданные
     * @param isFilter признак того, что поле из объекта- фильтра
     */
    private void addControllerMethodParameter(MethodSpec.Builder method, String chainingName, EStructuralFeature feature, boolean isFilter) {

        method.addParameter(ParameterSpec.builder(
                getParameterType(feature, isFilter),
                getParameterName(chainingName)).addAnnotation(
                AnnotationSpec.builder(RequestParam.class)
                        .addMember("required", "$L",
                                feature.getEAnnotation(
                                        XFWConstants.getUri(NotNull.class.getSimpleName())) != null)
                        .addMember("name", "$S", getParameterNameInRequestParam(chainingName, feature))
                        .build()
        ).build());
    }

    /**
     * Формирует имя параметра для аннотации @RequetsParam. Т.е. в том виде, в каком шлет клиент.
     * @param chainingName имя свойста, м.б. через точку в случае комплексных полей.
     * @param feature метаданные
     * @return имя для контроллера.
     */
    private String getParameterNameInRequestParam(String chainingName, EStructuralFeature feature) {
        // массивное навигируемое свойство.
        if (massiveControllerProperty(feature)) {
            return chainingName + "[]";
        } else {
            return chainingName;
        }

    }

    /**
     * Формирует имя параметра для контроллера
     * @param chainingName  имя свойста, м.б. через точку в случае комплексных полей.
     * @return
     */
    private String getParameterName(String chainingName) {
        return chainingName.replaceAll("\\.", "");
    }

    /**
     * Тип параметра, который будет в контроллере.
     * @param feature метаданные
     * @param isFilter признак того, что поле из объекта- фильтра
     * @return
     */
    private TypeName getParameterType(EStructuralFeature feature, boolean isFilter) {

        // массивное навигируемое свойство принимаем как List<String>
        if (massiveControllerProperty(feature)) {
            return ParameterizedTypeName.get(get(List.class), get(String.class));
        } else if (!isFilter) {
            // для примитивных полей датасорса в контроллере принимаем те типы, которые объявлены в датасорсе
            String instanceTypeName = feature.getEType().getInstanceClassName();
            return get(ClassUtils.getPackageName(instanceTypeName), ClassUtils.getShortClassName(instanceTypeName));
        } else {
            // для объекта фильтра все поля принимаем строками. Парсинг выполняется то-сервисами.
            return get(String.class);
        }
    }


    /**
     * Проверяем, что данное свойство - массивное навигируемое.
     * Если свойство из объекта - фильтра, то у него выставлено isMany.
     * Если свойство примитивное, то массивным может быть только List<String>. Другие коллекции запрещены в xtend,
     * потому что Spring MVC может принять только List<String>.
     *
     * @param feature метаданные
     * @return
     */
    private boolean massiveControllerProperty(EStructuralFeature feature) {
        return feature instanceof XFWReference && feature.isMany()
                || feature.getEType().equals(XFWMMPackage.eINSTANCE.getList());
    }


    private void birtReportBody(XFWDataSource xfwDataSource, MethodSpec.Builder method) {
        String fileName = xfwDataSource.getRequestMapping();
        if (fileName.contains("/")) {
            final String[] split = fileName.split("/");
            fileName = split[split.length - 1];
        }
        method.addStatement("$1T outputFormat = $1T.from(format)", OutputFormat.class)
                .addStatement("final $T<String, Object> params = new $T<>()", Map.class, HashMap.class);

        xfwDataSource.getEStructuralFeatures().stream().forEach(eStructuralFeature -> {
            method.addStatement("params.put($1S,$1L)", eStructuralFeature.getName());
        });

        method.addStatement("request.setAttribute(\"data\", data)");


        method.addStatement("log.debug(\"Rendering  " + fileName + "  in \" + format + \".\")")
                .addCode("\n")
                .beginControlFlow("if (reportService == null)")
                .addStatement("throw new $T(\"$L\")", get(IllegalStateException.class), NO_REPORTING_ENGINE)
                .endControlFlow()
                .addStatement("reportService.renderReport(request, response, outputFormat, params, $S)",
                        fileName);


    }

    private void addControllerMethodParameter(MethodSpec.Builder method, String name, Class<?> type) {
        method.addParameter(ParameterSpec.builder(get(type), name).addAnnotation(
                AnnotationSpec.builder(RequestParam.class)
                        .addMember("value", "$S", "$" + name)
                        .addMember("required", "$L", false)
                        .build()
        ).build());
    }


    /**
     * Добавляет параметры метода.
     *
     * @param operation Описание метода в метаданных
     * @param method    Билдер метода
     */
    public static void fillParameters(XFWOperation operation, MethodSpec.Builder method) {
        operation.getEParameters().forEach(param -> {
            final String pcn = param.getEType().getInstanceTypeName();
            final ClassName paramType = get(getPackageName(pcn), getShortClassName(pcn));
            final ParameterSpec.Builder paramBuilder = ParameterSpec.builder(paramType, param.getName());

            param.getEAnnotations().forEach(eAnnotation -> {
                if (XFWConstants.REQUEST_PARAM_ANNOTATION_SOURCE.getUri().equals(eAnnotation.getSource())) {
                    final AnnotationSpec.Builder ab = AnnotationSpec.builder(RequestParam.class)
                            .addMember("required", "$L", eAnnotation.getDetails().get("required"))
                            .addMember("value", "$S", eAnnotation.getDetails().get("value"));
                    paramBuilder.addAnnotation(ab.build());
                } else {
                    final String className = eAnnotation.getDetails().get("className");
                    final Class<?> annotationClass;
                    try {
                        annotationClass = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    final AnnotationSpec.Builder ab = AnnotationSpec.builder(annotationClass);
                    eAnnotation.getDetails().forEach(entry -> {
                        if (!"className".equalsIgnoreCase(entry.getKey())) {
                            final String[] values = entry.getValue().split(",");
                            ab.addMember(entry.getKey(), values[0], values[1].trim());
                        }
                    });
                    paramBuilder.addAnnotation(ab.build());
                }
            });

            if (param.getEAnnotation(XFWConstants.REQUEST_PARAM_ANNOTATION_SOURCE.getUri()) == null
                    && param.getEType().getInstanceClassName().startsWith("java.time")) {
                final AnnotationSpec.Builder ab = AnnotationSpec.builder(RequestParam.class)
                        .addMember("required", "$L", false);
                paramBuilder.addAnnotation(ab.build());
            }

            method.addParameter(paramBuilder.build());
        });

        addLoadContextParametersToMethod(method);

    }

    private String parametersString(XFWOperation operation) {
        return operation.getEParameters().stream()
                .map(ENamedElement::getName)
                .collect(joining(", "));
    }

    private void createExportAllMethod(TypeSpec.Builder controllerBuilder, Iterable<XFWDataSource> xfwDataSources) {
        xfwDataSources.forEach(xfwDataSource -> {

            final XFWOperation operation = (XFWOperation) xfwDataSource.getEOperations().get(0);
            final String returnClassName = operation.getEType().getName();

            if (!"Tuple".equals(returnClassName)) {
                final MethodSpec.Builder method = MethodSpec.methodBuilder(xfwDataSource.getName())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(
                                AnnotationSpec.builder(RequestMapping.class)
                                        .addMember("value", "\"_export/$L\"", xfwDataSource.getRequestMapping())
                                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                                        .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.POST)
                                        .build());

                fillParameters(operation, method);

                method.addParameter(
                        ParameterSpec.builder(String.class, "exportConfig")
                                .addAnnotation(
                                        AnnotationSpec.builder(RequestParam.class)
                                                .addMember("value", "$S", "$export")
                                                .addMember("required", "$L", Boolean.FALSE)
                                                .build()
                                ).build())
                        .addParameter(ParameterSpec.builder(HttpServletRequest.class, "request").build())
                        .addParameter(ParameterSpec.builder(HttpServletResponse.class, "response").build());


                method.returns(void.class);

                method
                        .beginControlFlow("if (exportService == null)")
                        .addStatement("throw new $T(\"$L\")", get(IllegalStateException.class),
                                NO_EXPORT_ENGINE_MESSAGE)
                        .endControlFlow()
                        .addStatement("String _exportConfig = exportConfig")
                        .beginControlFlow("try")
                        .addStatement("$T mapper = new $T()", ObjectMapper.class, ObjectMapper.class)
                        .addStatement("$1T exportConfigObj = mapper.readValue(_exportConfig, $1T.class)",
                                JXFW_EXPORT_CONFIG)

                        .addStatement("ExportFormatter exportFormatter = null")
                        .beginControlFlow("for (ExportFormatter formatter: exportFormatters)")
                        .beginControlFlow("if(formatter.supportsFormat(exportConfigObj.getOutputFormat()))")
                        .addStatement("exportFormatter = formatter")
                        .addStatement("break")
                        .endControlFlow()
                        .endControlFlow()
                        .beginControlFlow("if (exportFormatter == null)")
                        .addStatement("throw new $T(\"No export formatter available format = \" + exportConfigObj.getFormat())",
                                get(IllegalStateException.class))
                        .endControlFlow()

                        .addCode(getDSLoaderLoadStmt(xfwDataSource, operation))

                        .addStatement(
                                "exportService.createExport(exportConfigObj, " +
                                        "new $T(toDomainToList(dataSourceResult.getData())), request, response)",
                                get(ClientExportDataProvider.class))
                        .nextControlFlow("catch (Exception e)")
                        .addStatement("throw new RuntimeException(e)")
                        .endControlFlow();


                controllerBuilder.addMethod(method.build());
            }
        });


    }

    protected void addToListCode(MethodSpec.Builder method, XFWDataSource xfwDataSource, XFWOperation operation) {
        CodeBlock loaderLoadStmt = getDSLoaderLoadStmt(xfwDataSource, operation);
        method.addCode(loaderLoadStmt);
        ClassName generic = null;
        try {
            String instanceTypeName = operation.getEGenericType().getEClassifier().getInstanceTypeName();
            generic = get(ClassUtils.getPackageName(instanceTypeName), ClassUtils.getShortClassName(instanceTypeName));
        } catch (Exception e) {
            logger.debug("Couldn't resolve generic type for Collection, for dataSource: "
                    + xfwDataSource.getName() + "#" + operation.getName(), e);
        }
        if (generic != null) {
            method.addStatement("$1T<$2T> list = dataSourceResult",
                    get(DataSourceResult.class),
                    generic);
        } else {
            method.addStatement("$T list = dataSourceResult",
                    get(DataSourceResult.class));
        }

        method.addStatement("$1T toList = ($1T) list.getData().stream().map(o -> {\n"
                        + "    $4T toService = domainToServicesResolver.resolveToService((($2T)o).getTypeName());\n"
                        + "    return toService.toToPolymorphic(($2T)o);"
                        + "\n}).collect($3T.toList())",
                List.class, DomainObject.class, Collectors.class,
                get(DomainToService.class));

    }

    private CodeBlock getDSLoaderLoadStmt(XFWDataSource xfwDataSource, XFWOperation operation) {


        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$1T dataSourceResult = service.$2L($3L)",
                get(DataSourceResult.class), xfwDataSource.getName(), parametersString(operation));

        return builder.build();

    }


}
