package ru.croc.ctp.jxfw.facade;

import static com.squareup.javapoet.ClassName.get;
import static org.apache.commons.lang.ClassUtils.getShortClassName;
import static ru.croc.ctp.jxfw.core.export.facade.webclient.ExportController.NO_EXPORT_ENGINE_MESSAGE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java8.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import ru.croc.ctp.jxfw.core.export.ExportDataProviderBuilder;
import ru.croc.ctp.jxfw.core.export.ExportFormatter;
import ru.croc.ctp.jxfw.core.export.ExportService;
import ru.croc.ctp.jxfw.core.export.impl.model.XfwExportConfig;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Методы для генерации контроллеров.
 *
 * @author OKrutova
 * @since 1.6
 */
class ControllerGenerationUtils {

    private static final ClassName JXFW_EXPORT_SERVICE = get(ExportService.class);
    public static final ClassName JXFW_EXPORT_CONFIG = get(XfwExportConfig.class);

    /**
     * Добавляет поля для экспорта в класс контроллера.
     *
     * @param controllerBuilder билдер
     */

    static void addExportFieldsToController(TypeSpec.Builder controllerBuilder) {

        final FieldSpec exportServiceField = FieldSpec
                .builder(JXFW_EXPORT_SERVICE, "exportService",
                        Modifier.PRIVATE)
                .addAnnotation(
                        AnnotationSpec.builder(Autowired.class)
                                .addMember("required", "$L", Boolean.FALSE)
                                .build())
                .build();
        controllerBuilder.addField(exportServiceField);

        final FieldSpec exportProviderField = FieldSpec
                .builder(get(ExportDataProviderBuilder.class), "exportDataProviderBuilder",
                        Modifier.PRIVATE)
                .addAnnotation(
                        AnnotationSpec.builder(Autowired.class)
                                .build())
                .build();
        controllerBuilder.addField(exportProviderField);

        final FieldSpec exportFormatters = FieldSpec
                .builder(ParameterizedTypeName.get(get(List.class), get(ExportFormatter.class)),
                        "exportFormatters",
                        Modifier.PRIVATE)
                .addAnnotation(
                        AnnotationSpec.builder(Autowired.class)
                                .build())
                .build();
        controllerBuilder.addField(exportFormatters);
    }

    /**
     * Генерирует тело метода экспорта.
     *
     * @param builder                       билдер
     * @param queryParamsCreator колбэк, создающий код построения queryParams
     */

    static void createExportMethodBody(MethodSpec.Builder builder, Consumer<MethodSpec.Builder> queryParamsCreator) {
        builder.beginControlFlow("if (exportService == null)")
                .addStatement("throw new $T(\"$L\")", get(IllegalStateException.class), NO_EXPORT_ENGINE_MESSAGE)
                .endControlFlow()

                .beginControlFlow("try")
                .addStatement("$T mapper = new $T()", ObjectMapper.class,
                        ObjectMapper.class)
                .addStatement(
                        "$T exportConfigObj = mapper.readValue(exportConfig, $T.class)",
                        JXFW_EXPORT_CONFIG, JXFW_EXPORT_CONFIG)
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
                .endControlFlow();
        queryParamsCreator.accept(builder);
        builder.addStatement(
                        "exportService.createExport(exportConfigObj, exportDataProviderBuilder"
                                + ".build(queryParams, loadContext, exportFormatter), request, response)")
                .nextControlFlow("catch (Exception e)")
                .addStatement("throw new RuntimeException(e)")
                .endControlFlow();

    }

    /**
     * Получить имя класса в терминах javaPoet.
     *
     * @param xfwClass                      метаданные класса
     * @param generateControllerForFulltext generateControllerForFulltext
     * @return имя класса в терминах javaPoet
     */

    static ClassName getClassName(XFWClass xfwClass, boolean generateControllerForFulltext) {
        final String simpleClassName = xfwClass.getName();
        String className = xfwClass.getInstanceClassName();
        String packageName = getPackageName(className);

        if (generateControllerForFulltext) { //будем генерить контроллер fulltext в своем пакете

            return ClassName.get(packageName + ".solr", simpleClassName);
        } else {
            return ClassName.get(packageName, simpleClassName);
        }

    }

    /**
     * Получить имя пакета.
     *
     * @param fullyQualifiedName полное имя класса
     * @return имя его пакета
     */
    static String getPackageName(String fullyQualifiedName) {
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return fullyQualifiedName.substring(0, lastDot);
    }

    /**
     * Добавляет методу параметры их клиентского запроса, нужные для экспорта.
     *
     * @param builder билдер
     */
    static void addExportParametersToMethod(MethodSpec.Builder builder) {
        builder.addParameter(
                ParameterSpec.builder(HttpServletRequest.class,
                        "request").build())
                .addParameter(
                        ParameterSpec.builder(HttpServletResponse.class,
                                "response").build())
                .addParameter(
                        ParameterSpec
                                .builder(String.class, "exportConfig")
                                .addAnnotation(
                                        AnnotationSpec
                                                .builder(RequestParam.class)
                                                .addMember("value", "$S",
                                                        "$export")
                                                .addMember("required", "$L",
                                                        Boolean.FALSE).build())
                                .build());


    }

    /**
     * Добавляет методу параметры их клиентского запроса, нужные для построения LoadContext.
     *
     * @param builder билдер
     */
    static void addLoadContextParametersToMethod(MethodSpec.Builder builder) {
        builder.addParameter(ParameterSpec.builder(Locale.class, "locale").build())
                .addParameter(ParameterSpec.builder(TimeZone.class, "timeZone").build())
                .addParameter(ParameterSpec.builder(Principal.class, "principal").build())
                .addParameter(hintsParameter("hintsStr", "$hints"))
                .addParameter(hintsParameter("hintsArr", "$hints[]"));

    }

    private static ParameterSpec hintsParameter(String paramName, String requestParamName) {
        return ParameterSpec
                .builder(ParameterizedTypeName.get(List.class, String.class), paramName)
                .addAnnotation(
                        AnnotationSpec
                                .builder(RequestParam.class)
                                .addMember("value", "$S", requestParamName)
                                .addMember("required", "$L", Boolean.FALSE)
                                .addMember("defaultValue", "$S", "").build())
                .build();
    }


    static void addHintsConcatenationStatement(MethodSpec.Builder builder){
        builder.addStatement("final $T<String> hints = $T.concat(hintsStr.stream(), hintsArr.stream()).collect($T.toList())",
                get(List.class),
                get(Stream.class),
                get(Collectors.class));
    }



}
