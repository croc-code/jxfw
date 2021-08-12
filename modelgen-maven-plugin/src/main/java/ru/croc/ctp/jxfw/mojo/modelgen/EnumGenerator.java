package ru.croc.ctp.jxfw.mojo.modelgen;


import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.croc.ctp.jxfw.core.domain.XfwCodeEnum;
import ru.croc.ctp.jxfw.core.domain.meta.XFWElementLabel;
import ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.XFWModel;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.lang.model.element.Modifier;

/**
 * Генератор enum классов по xfwmm метамодели.
 *
 * @author AKogun
 */
public class EnumGenerator {

    private final File sourceModel;
    private final Path outputFolder;
    private final String basePackage;

    private static final Logger logger = LoggerFactory.getLogger(EnumGenerator.class);

    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z_$]+[a-zA-Z0-9_$]*");

    /**
     * Конструктор.
     *
     * @param sourceModel  файл содержащий описание модели
     * @param outputFolder дирректория в которой будет размещен результат генерации
     * @param basePackage  базовый пакет для классов модели
     * @param options      свойства генерации
     */
    public EnumGenerator(File sourceModel, Path outputFolder, String basePackage, Map<String, Object> options) {
        this.sourceModel = sourceModel;
        this.basePackage = basePackage;
        this.outputFolder = outputFolder;
    }

    private static boolean isValidName(String name) {
        return (name.length() > 0) && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Генерация Java-файлов - c enum классами модели.
     */
    public void generate() {

        final File model = sourceModel;

        final XFWModel xfwModel = new XFWModelImpl(model.toPath());
        final Set<EEnum> classes = xfwModel.getAll(EEnum.class);


        String enumCodeFieldName = "code";

        for (EEnum enumClass : classes) {


            Builder enumTypeBuilder = TypeSpec.enumBuilder(enumClass.getName())
                    .addSuperinterface(ParameterizedTypeName
                            .get( ClassName.get(XfwCodeEnum.class), TypeName.get(String.class)))
                    .addModifiers(Modifier.PUBLIC)
                    .addField(String.class, enumCodeFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .addField(FieldSpec
                            .builder(XfwEnumeration.class, "METADATA",  Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .initializer("$1T.getInstance().findEnum($2L.class)",
                                    TypeName.get(XfwModelFactory.class),
                                    enumClass.getName())
                            .build())
                    .addMethod(MethodSpec.constructorBuilder().addParameter(String.class, enumCodeFieldName)
                            .addStatement("this.$N = $N", enumCodeFieldName, enumCodeFieldName).build())
                    .addMethod(MethodSpec.methodBuilder("getCode").addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class).returns(String.class)
                            .addStatement("return this.$N", enumCodeFieldName).build());

            int literalNumber = 0;

            for (EEnumLiteral literal : enumClass.getELiterals()) {

                literalNumber++;

                String literalStr = literal.getLiteral();
                TypeSpec.Builder enumSpecBuilder = TypeSpec.anonymousClassBuilder("$S", literalStr);

                if (literal.getEAnnotations() != null) {
                    for (EAnnotation annotation : literal.getEAnnotations()) {
                        for (String key : annotation.getDetails().keySet()) {
                            String annotationLabel = annotation.getDetails().get(key);
                            enumSpecBuilder
                                    .addAnnotation(
                                            AnnotationSpec
                                                    .builder(XFWElementLabel.class)
                                                    .addMember("value", "$S", annotationLabel).addMember("lang", "$S", key)
                                                    .build());
                        }
                    }
                }

                enumSpecBuilder
                        .addAnnotation(
                                AnnotationSpec.builder(XFWEnumId.class)
                                        .addMember("value", "$L",
                                                 (1 << (literalNumber - 1)) ).build());

                enumTypeBuilder.addEnumConstant((isValidName(literalStr) ? literalStr : ("_" + literalStr)),
                        enumSpecBuilder.build());

            }

            String enumPackage = ModelUtils.getFqName(enumClass.getEPackage(), basePackage);

            JavaFile javaFile = JavaFile.builder(enumPackage, enumTypeBuilder.build()).build();

            save(javaFile);
        }
    }


    private void save(JavaFile javaFile) {
        try {
            System.setProperty("file.encoding", "UTF-8");
            javaFile.writeTo(outputFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
