package ru.croc.ctp.jxfw.facade.cmis;

import static com.squareup.javapoet.ClassName.get;
import static org.apache.commons.lang.WordUtils.capitalize;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;
import ru.croc.ctp.jxfw.facade.ToServiceGenerator;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Генерация ТО сервисов для Cmis сущностей.
 *
 * @param <KeyTypeT> Тип ключа
 * @author Nosov Alexander
 *         on 17.11.15.
 */
public class ToServiceGeneratorCmis<KeyTypeT extends TypeName> extends ToServiceGenerator<KeyTypeT> {
    private ClassName classCmisBlob = get("ru.croc.ctp.spring.data.cmis.core", "CmisBlob");
    private ClassName classContentStreamInfo = get("ru.croc.ctp.spring.data.cmis.core", "ContentStreamInfo");

    /**
     * Конструктор генератора TO-сервисов.
     *
     * @param clazz       Класс доменного объекта из метамодели
     * @param xfwClasses  Все классы модели
     * @param storageType Тип хранилища
     * @param keyType     Тип идентификатора доменного объекта
     */
    public ToServiceGeneratorCmis(XFWClass clazz, Set<XFWClass> xfwClasses, String storageType, KeyTypeT keyType) {
        super(clazz, xfwClasses, storageType, keyType);
    }

    @Override
    protected MethodSpec.Builder createGetBinPropMethod(ClassName jxfwDomainClass, XFWAttribute attr) {
        return MethodSpec
            .methodBuilder(
                "getBinProp"
                    + capitalize(attr.getName()))
            .addModifiers(Modifier.PUBLIC)
            .returns(DomainToServiceImpl.BinPropValue.class)
            .addParameter(
                ParameterSpec
                    .builder(jxfwDomainClass, "o")
                    .build())
            .addStatement(
                "    return new BinPropValue(o.getContentStreamFileName(), o.getContentStreamMimeType(),\n"
                    + "        o.get$1L())",
                capitalize(attr.getName()));
    }

    @Override
    protected TypeName getParentClass() {
        return ParameterizedTypeName.get(
            get("ru.croc.ctp.jxfw.cmis.facade.webclient", "DomainToServiceCmisImpl"),
            jxfwDomainType, getKeyType());
    }

    @Override
    protected void addBlobProperty(MethodSpec.Builder builder, XFWAttribute attribute, String fieldName) {
        builder.addStatement(
            "dto.addProperty($1S, createBinPropDescriptor(domainObject.get$2L(),\n"
                + "    domainObject.getContentStreamFileName(), domainObject.getContentStreamLength(), domainObject.getContentStreamMimeType()))",
            fieldName,
            capitalize(fieldName));
    }

    @Override
    protected void setBlobPropertyToDomainObject(MethodSpec.Builder builder, XFWAttribute attribute, String fieldName) {
        if (fieldName.equals("content")) {
            builder.addCode(
                "dto.copyPropValue(\"content\", v -> \n"
                    + "{\n"
                    + "    $T resourceProperties = get$T(v);\n"
                    + "    o.setContent(createBlob(v));\n"
                    + "    o.setContentStreamFileName(getFileNameFromDescriptor(v));\n"
                    + "    o.setContentStreamLength("
                    + "resourceProperties != null ? resourceProperties.getContentSize() : null);\n"
                    + "    o.setContentStreamMimeType("
                    + "resourceProperties != null ? resourceProperties.getContentType() : null);\n"
                    + "});\n",
                ResourceProperties.class,
                ResourceProperties.class
            );
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



}
