package ru.croc.ctp.jxfw.fulltext.generator.solr;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.systemFields;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.ATTRIBUTE_PROPS_ANNOTATION_SOURCE;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.PRIMARY_KEY_ANNOTATION_SOURCE;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Type;
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider;
import org.springframework.data.solr.core.mapping.Indexed;

import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey;
import ru.croc.ctp.jxfw.core.generator.EcoreModelEmitter;
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper;
import ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator;

/**
 * Реализация сервиса формирования модели для Solr-хранилища.
 *
 * @author SMufazzalov
 * @since 1.4
 */
public class EcoreModelEmitterSolr implements EcoreModelEmitter {

    /**
     * Порядок поля в комплексном ключе, кладем в аннотацию attributeProperties.
     */
    private static final String COMPLEX_KEY_ORDER = "complexKeyOrder";
    private static final EcoreFactory ECORE_FACTORY = EcoreFactory.eINSTANCE;

    private ClassDeclaration clazz;
    private CodeGenerationContext context;
    private XFWModelGenerator modelEmfResource;
    private TypeReferenceProvider typeRef;

    /**
     * Конструктор.
     *
     * @param clazz   Класс доменной модели, для которого строится ecore-модель
     * @param modelEmfResource modelEmfResource
     * @param context Контекст для доступа к вспомогательным сервисам xtend
     * @param typeRef провайдер типов
     */
    public EcoreModelEmitterSolr(
            ClassDeclaration clazz,
            CodeGenerationContext context,
            XFWModelGenerator modelEmfResource,
            TypeReferenceProvider typeRef
    ) {
        this.clazz = clazz;
        this.context = context;
        this.modelEmfResource = modelEmfResource;
        this.typeRef = typeRef;
    }

    @Override
    public void addKeyFieldDetails() {
        Iterable<? extends FieldDeclaration> fields = clazz.getDeclaredFields();
        for (FieldDeclaration field : fields) {
            Type annotationType = typeRef.newTypeReference(XFWPrimaryKey.class).getType();
            AnnotationReference primaryKeyAnnotation = field.findAnnotation(annotationType);
            EStructuralFeature esField = getEcoreClass().getEStructuralFeature(field.getSimpleName());
            if (primaryKeyAnnotation != null) {
                EAnnotation primaryKey = ECORE_FACTORY.createEAnnotation();
                primaryKey.setSource(PRIMARY_KEY_ANNOTATION_SOURCE.getUri());

                AnnotationReference fieldAnnotation = null;
                for (AnnotationReference an : field.getAnnotations()) {
                    if (Indexed.class.getSimpleName().equals(an.getAnnotationTypeDeclaration().getSimpleName())) {
                        fieldAnnotation = an;
                        break;
                    }
                }
                if (fieldAnnotation != null) {
                    String value = fieldAnnotation.getStringValue("value");
                    if (value != null && value.length() > 1) {
                        primaryKey.getDetails().put("idFieldName", value);
                    }
                    final String idFieldName = LOWER_CAMEL.to(LOWER_UNDERSCORE, field.getSimpleName());
                    primaryKey.getDetails().put("idFieldName", idFieldName);
                }
                esField.getEAnnotations().add(primaryKey);

                // добавим аннотацию
                EAnnotation annotation = ECORE_FACTORY.createEAnnotation();
                annotation.setSource(ATTRIBUTE_PROPS_ANNOTATION_SOURCE.getUri());
                int order = primaryKeyAnnotation.getIntValue("order");
                // укажем в деталях аннотации порядок поля в комплексном ключе
                annotation.getDetails().put(COMPLEX_KEY_ORDER, Integer.toString(order));
                esField.getEAnnotations().add(annotation);
            } else if (esField instanceof EAttribute) {
                esField.setUnsettable(true);
            }
        }
    }

    @Override
    public boolean isFieldAddToModel(FieldDeclaration field) {
        return !(systemFields.contains(field.getSimpleName()) || "id".equals(field.getSimpleName()));
    }

    /**
     * Формируемый класс ecore-модели.
     *
     * @return класс ecore-модели
     */
    protected EClass getEcoreClass() {
        return modelEmfResource.findByFqName(clazz.getQualifiedName(),EClass.class);
    }
}
