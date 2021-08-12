package ru.croc.ctp.jxfw.facade.report;

import static com.google.common.collect.Lists.newArrayList;
import static com.squareup.javapoet.ClassName.OBJECT;
import static com.squareup.javapoet.ClassName.get;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang.WordUtils.uncapitalize;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.apache.commons.lang3.ClassUtils.getShortClassName;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import org.eclipse.emf.ecore.EParameter;
import org.springframework.context.ApplicationContext;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;

/**
 * Создание DataSet для генерации отчетов Birt.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class BirtDataSetCreator {

    /**
     * Создать DataSet по XFWDataSource.
     * 
     * @param xfwDataSources - XFWDataSource-ы по которому создается DataSet.
     * @param controllerName - имя класса контроллера
     * @return java-файл DataSet'a
     */
    public List<JavaFile> create(String controllerName, Iterable<XFWDataSource> xfwDataSources) {

        final ArrayList<JavaFile> javaFiles = newArrayList();

        final String packageXtendClassName = getPackageName(controllerName);
        final String shortXtendClassName = getShortClassName(controllerName);
        final ClassName xtendClass = get(packageXtendClassName, shortXtendClassName);


        boolean dataSourceClasses = xfwDataSources.iterator().next().getOperationCount() == 0;


        /*

         private Iterator iterator;

  public void open(Object obj, Map<String, Object> map) {
    HttpServletRequest request = (HttpServletRequest) ((HashMap) obj).get("BIRT_VIEWER_HTTPSERVET_REQUEST");
    final Iterable data = (Iterable)request.getAttribute("data");
    iterator = data.iterator();
  }

  public Object next() {
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  public void close() {
  }

         */

        if(dataSourceClasses){

           xfwDataSources.forEach(xfwDataSource -> {

               final TypeSpec.Builder dataSetBuilder = TypeSpec
                       .classBuilder(xfwDataSource.getName() + "DataSet")
                       .addModifiers(Modifier.PUBLIC);


               final FieldSpec iterator = FieldSpec.builder(
                       ParameterizedTypeName.get(get(Iterator.class), WildcardTypeName.subtypeOf(Object.class)), "iterator")
                       .addModifiers(Modifier.PRIVATE)
                       .build();
               dataSetBuilder.addField(iterator);

               final MethodSpec.Builder openMethod = MethodSpec.methodBuilder("open")
                       .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                               .addMember("value", "{\"unchecked\"}").build())
                       .addParameter(OBJECT, "obj")
                       .addModifiers(Modifier.PUBLIC)
                       .addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "map")
                       .addStatement("$1T request = ($1T) (($2T<$3T, $4T>) obj).get(\"BIRT_VIEWER_HTTPSERVET_REQUEST\")",
                               get(HttpServletRequest.class), get(HashMap.class), get(String.class), WildcardTypeName.subtypeOf(Object.class))
                       .addStatement("final $1T<?> data = ($1T<?>)request.getAttribute(\"data\")",
                               Iterable.class)
                       .addStatement("iterator = data.iterator()");


               dataSetBuilder.addMethod(openMethod.build());

               final MethodSpec.Builder nextMethod = MethodSpec.methodBuilder("next")
                       .returns(OBJECT)
                       .addModifiers(Modifier.PUBLIC)
                       .beginControlFlow("if (iterator.hasNext())")
                       .addStatement("return iterator.next()")
                       .endControlFlow()
                       .addStatement("return null");

               dataSetBuilder.addMethod(nextMethod.build());

               final MethodSpec.Builder closeMethod = MethodSpec.methodBuilder("close").addModifiers(Modifier.PUBLIC);
               dataSetBuilder.addMethod(closeMethod.build());
               javaFiles.add(JavaFile.builder(packageXtendClassName, dataSetBuilder.build()).build());
           });

        }else {
            xfwDataSources.forEach(xfwDataSource -> {

                String requestMapping = xfwDataSource.getRequestMapping();

                final XFWOperation operation = (XFWOperation) xfwDataSource.getEOperations().get(0);
                if (!"Tuple".equals(operation.getEType().getName())) {
                    String returnClassName = operation.getEType().getInstanceTypeName();

                    final String packageName = getPackageName(returnClassName);
                    final String shortClassName = getShortClassName(returnClassName);

                    if (requestMapping.contains("/")) {
                        final String[] split = requestMapping.split("/");
                        requestMapping = split[split.length - 1];
                    }

                    final TypeSpec.Builder dataSetBuilder = TypeSpec
                            .classBuilder(requestMapping + "DataSet")
                            .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                                    .addMember("value", "{\"unchecked\"}").build())
                            .addModifiers(Modifier.PUBLIC);


                    final FieldSpec iterator = FieldSpec.builder(
                            ParameterizedTypeName.get(get(Iterator.class), get(packageName, shortClassName)), "iterator")
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                    dataSetBuilder.addField(iterator);

                    final MethodSpec.Builder openMethod = MethodSpec.methodBuilder("open")
                            .addParameter(OBJECT, "obj")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "map")
                            .addStatement("final $1T spring = ($1T) (($2T) obj).get($3S)",
                                    ApplicationContext.class, HashMap.class, "spring")
                            .addStatement("$1T request = ($1T) (($2T) obj).get(\"BIRT_VIEWER_HTTPSERVET_REQUEST\")",
                                    HttpServletRequest.class, HashMap.class)
                            .addCode("iterator = (($T) spring.getBean($S))", xtendClass, uncapitalize(shortXtendClassName))
                            .addCode(".$L(\n", operation.getName());

                    String params = operation.getEParameters().stream()
                            .map(param -> "\t\t" + stringByTypeParam(param)).collect(joining(",\n"));
                    params = params.replaceAll("\\$", "\\$\\$");

                    openMethod.addCode(params);
                    openMethod.addCode("\n).getData().iterator();\n");

                    dataSetBuilder.addMethod(openMethod.build());

                    final MethodSpec.Builder nextMethod = MethodSpec.methodBuilder("next")
                            .returns(OBJECT)
                            .addModifiers(Modifier.PUBLIC)
                            .beginControlFlow("if (iterator.hasNext())")
                            .addStatement("return iterator.next()")
                            .endControlFlow()
                            .addStatement("return null");

                    dataSetBuilder.addMethod(nextMethod.build());

                    final MethodSpec.Builder closeMethod = MethodSpec.methodBuilder("close").addModifiers(Modifier.PUBLIC);
                    dataSetBuilder.addMethod(closeMethod.build());
                    javaFiles.add(JavaFile.builder(packageXtendClassName, dataSetBuilder.build()).build());
                }
            });
        }

        return javaFiles;
    }

    private String stringByTypeParam(EParameter parameter) {
        final String typeName = parameter.getEType().getInstanceTypeName();

        return "(" + typeName + ") request.getAttribute(\"" + parameter.getName() + "\")";
    }
    
}
