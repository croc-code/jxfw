package ru.croc.ctp.jxfw.facade.report;

import static com.google.common.collect.Lists.newArrayList;
import static com.squareup.javapoet.ClassName.get;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.apache.commons.lang3.ClassUtils.getShortClassName;
import static ru.croc.ctp.jxfw.core.reporting.XfwReportService.NO_REPORTING_ENGINE;
import static ru.croc.ctp.jxfw.facade.WebClientDataSourceCreator.fillParameters;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.facade.DataSourceCreator;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWOperation;
import ru.croc.ctp.jxfw.reporting.birt.BirtReportService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Класс создания контроллеров DataSource для отчетов на основе BIRT.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class WebClientBirtReportControllerCreator implements DataSourceCreator {
    private static final ClassName JXFW_CONTROLLER_BASE
            = get("ru.croc.ctp.jxfw.core.facade.webclient", "ControllerBase");

    private static final AnnotationSpec REST_CONTROLLER = AnnotationSpec.builder(RestController.class).build();

    @Override
    public List<JavaFile> create(String controllerName,
                                 Iterable<XFWDataSource> xfwDataSources,
                                 Map<String, Object> options, boolean birtReports) {
        final ArrayList<JavaFile> javaFiles = newArrayList();

        final String packageName = getPackageName(controllerName);
        final String shortClassName = getShortClassName(controllerName);
        final String packageFacade = packageName + ".facade.webclient";


        final TypeSpec.Builder controllerBuilder = TypeSpec
                .classBuilder(shortClassName + "ReportController")
                .superclass(JXFW_CONTROLLER_BASE)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(REST_CONTROLLER)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class).addMember("value", "$S", "**/api/_reports").build())
                .addField(FieldSpec.builder(Logger.class, "log", Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                        .initializer("$T.getLogger(" + shortClassName + "ReportController" + ".class)",
                                LoggerFactory.class).build());

        final FieldSpec reportService = FieldSpec
                .builder(BirtReportService.class, "reportService", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class)
                        .addMember("required", "$L", Boolean.FALSE)
                        .build())
                .build();
        controllerBuilder.addField(reportService);

        xfwDataSources.forEach(xfwDataSource -> {

            final XFWOperation operation = (XFWOperation) xfwDataSource.getEOperations().get(0);

            final MethodSpec.Builder method = MethodSpec.methodBuilder(xfwDataSource.getName() + "Report")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(
                            AnnotationSpec.builder(RequestMapping.class)
                                    .addMember("value", "$S", xfwDataSource.getRequestMapping())
                                    .addMember("method", "$T.$L", RequestMethod.class, RequestMethod.GET)
                                    .build());

            fillParameters(operation, method);

            method
                    .addParameter(ParameterSpec.builder(HttpServletRequest.class, "request").build())
                    .addParameter(ParameterSpec.builder(HttpServletResponse.class, "response").build())
                    .addParameter(ParameterSpec.builder(String.class, "format")
                            .addAnnotation(AnnotationSpec.builder(RequestParam.class)
                                    .addMember("required", "$L", Boolean.FALSE)
                                    .addMember("defaultValue", "$S", "html").build()).build());


            method.returns(void.class);

            String fileName = xfwDataSource.getRequestMapping();
            if (fileName.contains("/")) {
                final String[] split = fileName.split("/");
                fileName = split[split.length - 1];
            }

            method.addStatement("$1T outputFormat = $1T.from(format)", OutputFormat.class)
                    .addCode("\n")
                    .addStatement("final $T<String, Object> params = new $T<>()", Map.class, HashMap.class);

            operation.getEParameters().forEach(param -> {
                method.addStatement("params.put($1S, $1L)", param.getName());
                method.addStatement("request.setAttribute($1S, $1L)", param.getName());
            });


            method.addStatement("log.debug(\"Rendering  " + fileName + "  in \" + format + \".\")")
                    .addCode("\n")
                    .beginControlFlow("if (reportService == null)")
                    .addStatement("throw new $T(\"$L\")", get(IllegalStateException.class), NO_REPORTING_ENGINE)
                    .endControlFlow()
                    .addStatement("reportService.renderReport(request, response, outputFormat, params, $S)",
                            fileName);

            controllerBuilder.addMethod(method.build());

        });

        javaFiles.add(JavaFile.builder(packageFacade, controllerBuilder.build()).build());

        return javaFiles;
    }


}
