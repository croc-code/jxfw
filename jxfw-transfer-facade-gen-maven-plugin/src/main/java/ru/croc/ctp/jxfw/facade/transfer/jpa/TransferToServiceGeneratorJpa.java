package ru.croc.ctp.jxfw.facade.transfer.jpa;

import static com.squareup.javapoet.ClassName.get;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.isComplexType;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ctp.jxfw.facade.transfer.TransferToServiceGenerator;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWReference;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.lang.model.element.Modifier;
import javax.persistence.Transient;

/**
 * Генерация ТО сервисов для JPA сущностей.
 *
 * @param <K> - тип первичного ключа
 * @author Nosov Alexander on 17.11.15.
 */
public class TransferToServiceGeneratorJpa<K extends TypeName> extends TransferToServiceGenerator<K> {

    /**
     * Конструктор.
     * @param clazz       - класс ecore модели.
     * @param storageType - тип хранилища в котором хранится доменная модель.
     * @param keyType     - тип первичного ключа
     */
    public TransferToServiceGeneratorJpa(XFWClass clazz, String storageType, K keyType) {
        super(clazz, storageType, keyType);
    }

    @Override
    protected void addClassLevelAnnotations() {
        final TypeSpec.Builder service = getToService();
        service.addAnnotation(AnnotationSpec.builder(Transactional.class).addMember("readOnly", "$L", true).build());

    }

    @Override
    protected TypeName getParentClass() {
        return ParameterizedTypeName
                .get(get("ru.croc.ctp.jxfw.jpa.facade.webclient",
                        "DomainToServiceJpaImpl"), jxfwDomainType, getKeyType());
    }

    @Override
    protected MethodSpec.Builder createParseKeyMethod() {
        final MethodSpec.Builder method = MethodSpec.methodBuilder("parseKey")
                .addModifiers(Modifier.PUBLIC).returns(getKeyType())
                .addParameter(get(String.class), "key");

        if (get(String.class).equals(getKeyType())) {
            method.addStatement("String result = key");
        } else if (get(UUID.class).equals(getKeyType())) {
            method.addStatement("UUID result = UUID.fromString(key)");
        } else if (get(Integer.class).equals(getKeyType())) {
            method.addStatement("$1T result = $1T.parseInt(key)", getKeyType());
        } else {
            method.addStatement("$1T result = $1T.parse$1T(key)", getKeyType());
        }

        //FIXME: идентификатор не должен определяться по наименованию поля
        final Optional<EStructuralFeature> idField = xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(f -> "id".equals(f.getName()))
                .findFirst();

        if (idField.isPresent()) {
            final EAnnotation eAnnotation = idField.get().getEAnnotation(XFWConstants.FIELD_TO_UPPER_CASE.getUri());

            if (eAnnotation == null) {
                method.addStatement("return result");
            } else {
                method.addStatement("return result instanceof String ? result.toUpperCase() : result");
            }
        } else {
            method.addStatement("return result");
        }
        return method;
    }


    @Override
    protected boolean overrideCreateDummyDomainObject(CodeBlock.Builder codeBlock) {
        codeBlock.addStatement("return v");
        return true;
    }

    @Override
    protected void addCreatePathProperty() {
        final MethodSpec.Builder builder = MethodSpec
                .methodBuilder("createPathProperty");
        builder.addModifiers(Modifier.PUBLIC).returns(get(String.class))
                .addParameter(get(String.class), "propName")
                .addAnnotation(Override.class);


        final CodeBlock.Builder codeBlock = CodeBlock
                .builder()
                .beginControlFlow("switch(propName)");

        xfwClass.getEAllStructuralFeatures()
                .stream()
                .filter(ref -> (ref instanceof XFWReference && !isComplexType(ref)))
                .filter(serverOnlyFilter())
                .forEach(ref -> {
                    codeBlock.add("case $S:\n", ref.getName());
                    codeBlock.addStatement("return propName + \".id\"");
                });

        codeBlock.addStatement("default: return propName");
        codeBlock.endControlFlow();

        builder.addCode(codeBlock.build());

        final TypeSpec.Builder service = getToService();
        service.addMethod(builder.build());
    }

    @Override
    protected MethodSpec.Builder bodyGetDObyIdMethodReturnStatement(MethodSpec.Builder builder) {
        return builder.addStatement("return $1T.initializeAndUnproxy(loadService.loadOne(queryParamsBuilder.build(), loadContext))",
                get("ru.croc.ctp.jxfw.jpa.hibernate.impl.util", "ProxyHelper"));
    }

    @Override
    protected NavigableStructureInfo buildNavigableStructureInfo(XFWReference xfwReference) {
        if (markedAsJpaTransient(xfwReference)) {
            final EAnnotation eAnnotation =
                    xfwReference.getEAnnotation(XFWConstants.getUri(Transient.class.getSimpleName()));
            final ClassName containerType = "Set".equals(eAnnotation.getDetails().get("container"))
                    ? get(Set.class) : get(List.class);
            final String fieldType = xfwReference.getEType().getInstanceTypeName();

            return new NavigableStructureInfo(eAnnotation, containerType, fieldType, null);
        }
        return super.buildNavigableStructureInfo(xfwReference);
    }

    private boolean markedAsJpaTransient(EStructuralFeature ref) {
        EAnnotation annotation = ref.getEAnnotation(XFWConstants.getUri(Transient.class.getSimpleName()));
        if (annotation != null) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean isCollectionField(EStructuralFeature ref) {
        if (markedAsJpaTransient(ref)) {
            EAnnotation annotation = ref.getEAnnotation(XFWConstants.getUri(Transient.class.getSimpleName()));
            return annotation.getDetails().get("container") != null;
        }

        return super.isCollectionField(ref);
    }
}
