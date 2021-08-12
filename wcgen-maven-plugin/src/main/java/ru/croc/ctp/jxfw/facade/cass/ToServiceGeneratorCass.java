package ru.croc.ctp.jxfw.facade.cass;

import static com.squareup.javapoet.ClassName.get;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.PRIMARY_KEY_ANNOTATION_SOURCE;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ctp.jxfw.facade.ToServiceGenerator;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.function.Predicate;

import javax.lang.model.element.Modifier;

/**
 * Генерация ТО сервисов для CASS сущностей.
 *
 * @param <K> - тип первичного ключа
 * @author Nosov Alexander
 * @since 1.1
 */
public class ToServiceGeneratorCass<K extends TypeName> extends ToServiceGenerator<K> {

    private ClassName selectComposerType = ClassName.get("ru.croc.ctp.jxfw.cass.facade.webclient", "SelectComposer");
    private ClassName queryContextType = get("ru.croc.ctp.jxfw.cass.predicate", "CassandraQueryContext");

    /**
     * Конструктор.
     *
     * @param clazz       - класс ecore модели.
     * @param storageType - тип хранилища в котором хранится доменная модель.
     * @param keyType     - тип первичного ключа
     */
    public ToServiceGeneratorCass(XFWClass clazz, Set<XFWClass> xfwClasses, String storageType, K keyType) {
        super(clazz, xfwClasses, storageType, keyType);
    }

    @Override
    protected void autowireFieldsBySetters() {
        TypeSpec.Builder toService = getToService();
        toService.addField(
                FieldSpec.builder(selectComposerType, "selectComposer")
                        .addModifiers(Modifier.PRIVATE)
                        .build()
        );
        toService.addMethod(
                MethodSpec
                        .methodBuilder("setSelectComposer")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(selectComposerType, "selectComposer").build())
                        .addStatement("this.selectComposer = selectComposer")
                        .addAnnotation(Autowired.class)
                        .build()
        );
    }

    @Override
    protected void addKeyMethods() {
        final TypeSpec.Builder builder = getToService();

        final MethodSpec.Builder serializeKeyMethod = MethodSpec.methodBuilder("serializeKey")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addParameter(getKeyType(), "key");

        serializeKeyMethod
                .addStatement("$1T mapper = new $1T()", ObjectMapper.class)
                .addStatement("mapper.setVisibility($T.FIELD,"
                        + "com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)", PropertyAccessor.class)
                .beginControlFlow("try")
                .addStatement("String str = mapper.writeValueAsString(key.toStringArray())")
                .addStatement("return new String($T.getUrlEncoder().encode(str.getBytes()))", Base64.class)
                .nextControlFlow("catch ($T e)", JsonProcessingException.class)
                .addStatement("throw new RuntimeException(e.getCause())")
                .endControlFlow();

        builder.addMethod(serializeKeyMethod.build());

        final MethodSpec.Builder parseKeyMethod = MethodSpec.methodBuilder("parseKey")
                .addModifiers(Modifier.PUBLIC)
                .returns(getKeyType())
                .addParameter(String.class, "encodedKey");


        parseKeyMethod
                .addStatement("$1T mapper = new $1T()", ObjectMapper.class)
                .addStatement("mapper.setVisibility($T.FIELD,"
                        + "com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)", PropertyAccessor.class)
                .beginControlFlow("try")
                .addStatement("String decodedKey = new String($T.getUrlDecoder().decode(encodedKey))", Base64.class)
                .addStatement("String[] array = mapper.readValue(decodedKey, String[].class)")
                .addStatement("return $T.fromStringArray(array)", getKeyType())
                .nextControlFlow("catch ($T e)", IOException.class)
                .addStatement("throw new RuntimeException(e.getCause())")
                .endControlFlow();

        builder.addMethod(parseKeyMethod.build());
    }


    @Override
    public Predicate<EStructuralFeature> fromToFieldsFilter() {
        //не генерить поля которые внутри комплексного ключа
        Predicate<EStructuralFeature> notKeyField
                = (field) -> field.getEAnnotation(PRIMARY_KEY_ANNOTATION_SOURCE.getUri()) == null;

        Predicate<EStructuralFeature> sysField = super.fromToFieldsFilter();

        return notKeyField.and(sysField);
    }

    @Override
    protected CodeBlock codeBlockGetSortedFromService() {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$1T sort = SortUtil.parse(orderBy)",
                get("org.springframework.data.domain", "Sort"))
                .addStatement("$1T queryContext = new $1T($2T.class, selectComposer, null, sort, null)",
                        queryContextType, jxfwDomainType)
                .addStatement("$T objects = service.getObjects(queryContext)",
                        ParameterizedTypeName.get(get(Iterable.class),
                                jxfwDomainType));
        return builder.build();
    }

    @Override
    protected String variableName(EStructuralFeature ref) {
        //для Cass модуля поля могут содержатся внутри комплексного ключа, оттуда их и нужно доставать
        if (ref.getEAnnotation(PRIMARY_KEY_ANNOTATION_SOURCE.getUri()) != null) {
            return "keyForDomainObject";
        }

        return super.variableName(ref);
    }

}
