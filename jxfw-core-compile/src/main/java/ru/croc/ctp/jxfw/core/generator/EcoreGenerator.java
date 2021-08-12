package ru.croc.ctp.jxfw.core.generator;

import static java.lang.String.valueOf;
import static java8.lang.Iterables.forEach;
import static ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator.REENTRANT_LOCK;

import com.google.common.base.Verify;
import java8.util.Optional;
import java8.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.AnnotationTypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.EnumerationValueDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Type;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.file.Path;
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider;
import org.eclipse.xtext.xbase.lib.Functions;
import org.eclipse.xtext.xbase.lib.Procedures;
import org.slf4j.Logger;
import ru.croc.ctp.jxfw.core.domain.meta.XFWContentType;
import ru.croc.ctp.jxfw.core.domain.meta.XFWDefaultValue;
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey;
import ru.croc.ctp.jxfw.core.domain.meta.XFWToUpperCase;
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWJoinTable;
import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil;
import ru.croc.ctp.jxfw.core.generator.impl.EnumCompileUtil;
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper;
import ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator;
import ru.croc.ctp.jxfw.core.generator.meta.XFWBlobInfo;
import ru.croc.ctp.jxfw.core.generator.meta.XFWComplexType;
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject;
import ru.croc.ctp.jxfw.core.generator.meta.XFWProtected;
import ru.croc.ctp.jxfw.core.validation.meta.XFWMaxExclusive;
import ru.croc.ctp.jxfw.core.validation.meta.XFWMaxInclusive;
import ru.croc.ctp.jxfw.core.validation.meta.XFWMinExclusive;
import ru.croc.ctp.jxfw.core.validation.meta.XFWMinInclusive;
import ru.croc.ctp.jxfw.core.validation.meta.XFWReadOnly;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.XFWReference;
import ru.croc.ctp.jxfw.metamodel.XfwValueType;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.FetchType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Класс для генерации Ecore модели из модели Xtend.
 *
 * @since 1.0
 */
public class EcoreGenerator extends AbstractEcoreGenerator {

    private static final Logger logger = LoggerFactory.getLogger(EcoreGenerator.class);

    //private final TypeReference complexObjTypeRef = typeRefProvider.newTypeReference(ComplexType.class);


    private GeneratorHelper generatorHelper;

    /**
     * Фабрика представлений классов для кодогенерации.
     */
    private List<MutableClassDeclaration> clazzes;
    private Functions.Function4<FieldDeclaration,
            EClass, List<MutableClassDeclaration>, TypeReferenceProvider, EReference> oppositeProvider;
    private Functions.Function3<ClassDeclaration, XFWModelGenerator,
            TypeReferenceProvider, EcoreModelEmitter> ecoreModelEmitterProvider;
    private Procedures.Procedure5<ClassDeclaration, EStructuralFeature,
            Iterable<? extends AnnotationReference>, TypeReferenceProvider, XfwValueType> ecoreAddColumnPropsProcedure;
    private Functions.Function1<ClassDeclaration, String> storageTypeProvider;
    private Functions.Function1<ClassDeclaration, String> keyTypeProvider;
    private Functions.Function1<ClassDeclaration, Boolean> useFulltextProvider;
    private Functions.Function1<ClassDeclaration, List<FieldDeclaration>> fieldsListProvider;


    /**
     * Конструктор.
     *
     * @param clazzes                      Список классов xtend, по которым будет выполняться генерация
     * @param oppositeProvider             Провайдер свойств Opposite для поля
     * @param ecoreModelEmitterProvider    Провайдер сервиса формирования ecore-модели
     * @param ecoreAddColumnPropsProcedure Процедура добавления, описаний свойств поля, в ecore
     * @param storageTypeProvider          Провайдер StorageType
     * @param keyTypeProvider              Процедура keyType
     * @param useFulltextProvider          Использование классом полнотекста
     * @param fieldsListProvider           Провайдер списка полей
     * @param context                      Контекст кодогенератора
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EcoreGenerator(@Nonnull List<MutableClassDeclaration> clazzes,
                          Functions.Function4 oppositeProvider,
                          Functions.Function3 ecoreModelEmitterProvider,
                          Procedures.Procedure5 ecoreAddColumnPropsProcedure,
                          Functions.Function1 storageTypeProvider,
                          Functions.Function1 keyTypeProvider,
                          Functions.Function1 useFulltextProvider,
                          Functions.Function1 fieldsListProvider,
                          CodeGenerationContext context) {
        super(clazzes.get(0).getCompilationUnit(), context);

        this.clazzes = clazzes;
        this.oppositeProvider = oppositeProvider;
        this.ecoreModelEmitterProvider = ecoreModelEmitterProvider;
        this.ecoreAddColumnPropsProcedure = ecoreAddColumnPropsProcedure;
        this.storageTypeProvider = storageTypeProvider;
        this.keyTypeProvider = keyTypeProvider;
        this.useFulltextProvider = useFulltextProvider;
        this.fieldsListProvider = fieldsListProvider;
        this.context = context;
        this.generatorHelper = new GeneratorHelper(typeRefProvider);
    }

    /**
     * Сгенерировать ecore файл.
     */
    @Override
    public void generate() {
        REENTRANT_LOCK.lock();
        try {

            Path projectPath = context.getTargetFolder(compilationUnit.getFilePath());
            java.net.URI projectJavaUri = context.toURI(projectPath);
            logger.debug("CompilationUnit {}", compilationUnit.getFilePath());
            logger.debug("Loading models from " + EcoreGenerator.class.getName());
            xfwModel = new XFWModelGenerator(projectJavaUri, logger);

            // Сначала создаются все классы
            createClasses();

            // Затем указываются предки для всех классов
            setParentOfClasses();

            // Здесь запоминаются все навигируемые свойства
            List<FieldDeclaration> refFields = new ArrayList<>();

            // А затем создаются все атрибуты для всех классов
            setupFieldsOfClasses(refFields);

            // Устанавливаются EOpposite для всех связей
            for (final FieldDeclaration field : refFields) {
                logger.debug("Set EOpposite for field {}", field.getSimpleName());

                final EClass clazz = xfwModel.findByFqName(field.getDeclaringType().getQualifiedName(), XFWClass.class);
                logger.debug("Find EClass by field.getDeclaringType().getQualifiedName() for field {} - {}",
                        field.getSimpleName(), clazz == null ? "null" : clazz);

                EReference ref = null;
                if (clazz != null) {
                    ref = (EReference) clazz.getEStructuralFeature(field.getSimpleName());
                    logger.debug("Find EReference on class {} for field {} - {}, refClass {} ",
                            clazz.getName(), field.getSimpleName(),
                            ref == null ? "null" : ref,
                            ref == null ? "null" : ref.getEReferenceType());
                }
                EReference opposite = null;
                if (ref != null) {
                    opposite = oppositeProvider.apply(field, ref.getEReferenceType(), clazzes, typeRefProvider);
                    logger.debug("Find opposite {}", opposite == null ? "null" : opposite.getName());
                    if (opposite == null) {
                        // TODO Создаем временную ссылку на объект, так как мы не знаем порядок 
                        // в котором получим классы для обработки
                        XFWReference xfwRef = XFWMM_FACTORY.createXFWReference();
                        xfwRef.setEType(clazz);
                        Optional<? extends AnnotationReference> reference =
                                StreamSupport.stream(((List<? extends AnnotationReference>) field.getAnnotations()))
                                        .filter(ar -> {
                                            final AnnotationTypeDeclaration declaration =
                                                    ar.getAnnotationTypeDeclaration();
                                            return declaration.getSimpleName().equals("OneToMany")
                                                    || declaration.getSimpleName().equals("ManyToMany")
                                                    || declaration.getSimpleName().equals("OneToOne");
                                        })
                                        .findFirst();
                        if (reference.isPresent()) {
                            String mappedBy = reference.get().getStringValue("mappedBy");

                            if (!StringUtils.isEmpty(mappedBy)) {
                                xfwRef.setName(mappedBy);
                                ref.setEOpposite(xfwRef);
                                xfwRef.setEOpposite(ref);
                                ref.getEReferenceType().getEStructuralFeatures().add(xfwRef);
                            }
                        }
                    } else {
                        ref.setEOpposite(opposite);
                        opposite.setEOpposite(ref);
                    }
                }
            }

            xfwModel.save();
        } finally {
            REENTRANT_LOCK.unlock();
        }
    }

    private void setupFieldsOfClasses(List<FieldDeclaration> refFields) {
        // А затем создаются все атрибуты для всех классов
        for (ClassDeclaration clazz : clazzes) {
            setupFieldsOfClass(clazz, refFields);
        }
    }

    private void setupFieldsOfClass(ClassDeclaration clazz, List<FieldDeclaration> refFields) {

        EcoreModelEmitter ecoreModelEmitter = ecoreModelEmitterProvider.apply(clazz, xfwModel, typeRefProvider);

        EClass ecoreClass = xfwModel.findByFqName(clazz.getQualifiedName(), EClass.class);

        Iterable<? extends FieldDeclaration> fieldDeclarations = null;
        if (clazz instanceof MutableClassDeclaration) {
            fieldDeclarations = fieldsListProvider.apply(clazz);
        } else {
            fieldDeclarations = ClassUtil.getFieldsList(clazz);
        }

        for (FieldDeclaration field : fieldDeclarations) {
            if (!ecoreModelEmitter.isFieldAddToModel(field)) {
                continue;
            }


            TypeReference fieldType = field.getType();


            if (generatorHelper.isDomain(fieldType)) {
                // навигируемое свойство
                refFields.add(field);
                XFWReference xfwRef = findOrCreateRef(ecoreClass, field);
                xfwRef.setName(field.getSimpleName());
                xfwRef.setEType(findOrCreateStub(fieldType));
                addi18nAnnotation(xfwRef, field.getAnnotations());
                addCustomAnnotations(xfwRef, field.getAnnotations());
                addRelationAnnotation(xfwRef, field);
                addReadOnlyInfo(xfwRef, clazz, field); // readOnly свойство
                ecoreAddColumnPropsProcedure.apply(clazz, xfwRef, field.getAnnotations(), typeRefProvider, XfwValueType.OBJECT);
                addIfAbsent(ecoreClass, xfwRef);
                addPatternValidationAnnotation(field, xfwRef);
            } else if (generatorHelper.isDomainCollection(fieldType)) {
                // навигируемое свойство множественное
                refFields.add(field);
                XFWReference xfwRef = findOrCreateRef(ecoreClass, field);
                xfwRef.setName(field.getSimpleName());
                xfwRef.setEType(findOrCreateStub(generatorHelper.getDomainCollectionArgument(fieldType)));
                xfwRef.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
                addi18nAnnotation(xfwRef, field.getAnnotations());
                addCustomAnnotations(xfwRef, field.getAnnotations());
                addContainerTypeForJpaTransientCollection(xfwRef, field);
                addRelationAnnotation(xfwRef, field);
                addReadOnlyInfo(xfwRef, clazz, field); // readOnly проверка
                ecoreAddColumnPropsProcedure.apply(clazz, xfwRef, field.getAnnotations(), typeRefProvider, XfwValueType.OBJECT);
                addIfAbsent(ecoreClass, xfwRef);
                addPatternValidationAnnotation(field, xfwRef);
            } else if (EnumCompileUtil.getEnumTypeForField(field, context) != null) {
                // перечисление
                EEnum eenum = findOrCreateEnum(EnumCompileUtil.getEnumTypeForField(field, context));
                XFWAttribute xfwAttribute = XFWMM_FACTORY.createXFWAttribute();
                xfwAttribute.setName(field.getSimpleName());
                xfwAttribute.setEType(eenum);
                xfwAttribute.setUpperBound(EnumCompileUtil.isEnumMany(field, context)
                        ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
                addi18nAnnotation(xfwAttribute, field.getAnnotations());
                addCustomAnnotations(xfwAttribute, field.getAnnotations());
                addReadOnlyInfo(xfwAttribute, clazz, field); // readOnly проверка
                addDefaultValue(xfwAttribute, field); // значение по умолчанию если доступно
                ecoreAddColumnPropsProcedure.apply(clazz, xfwAttribute, field.getAnnotations(), typeRefProvider, XfwValueType.ENUM);
                addIfAbsent(ecoreClass, xfwAttribute);
            } else if (generatorHelper.isComplex(fieldType)) {
                // свойство комплексного типа
                XFWReference xfwRef = findOrCreateRef(ecoreClass, field);
                xfwRef.setName(field.getSimpleName());

                final EClassifier ct = findOrCreateStub(fieldType);
                Verify.verifyNotNull(ct);
                xfwRef.setEType(ct);

                addi18nAnnotation(xfwRef, field.getAnnotations());
                addCustomAnnotations(xfwRef, field.getAnnotations());
                ecoreAddColumnPropsProcedure.apply(clazz, xfwRef, field.getAnnotations(), typeRefProvider, XfwValueType.COMPLEX);
                addIfAbsent(ecoreClass, xfwRef);
            } else {
                // простой тип
                XFWAttribute attribute = XFWMM_FACTORY.createXFWAttribute();
                attribute.setName(field.getSimpleName());
                attribute.setEType(getEType(field.getType().getType(), field.getAnnotations()));
                addProtectedAnnotation(field, attribute);
                addi18nAnnotation(attribute, field.getAnnotations());
                addCustomAnnotations(attribute, field.getAnnotations());
                addReadOnlyInfo(attribute, clazz, field); // readOnly проверка
                addDefaultValue(attribute, field); // значение по умолчанию если доступно
                //FIXME  XfwValueType.UNDEFINED добавлен в JXFW-953, где затрагиваются только комплексные типы
                // для примитивных типов можно определять XfwValueType, но пока нет необходимости
                ecoreAddColumnPropsProcedure.apply(clazz, attribute, field.getAnnotations(), typeRefProvider, XfwValueType.UNDEFINED);
                addIfAbsent(ecoreClass, attribute);
                addSearchFieldAnnotation(clazz, field, attribute);
                addPatternValidationAnnotation(field, attribute);
                addToUpperCaseAnnotation(field, attribute);
                addMinInclusiveValidation(field, attribute);
                addMinExclusiveValidation(field, attribute);

                addMaxInclusiveValidation(field, attribute);
                addMaxExclusiveValidation(field, attribute);
                addSizeValidation(field, attribute);

                addMaxValueValidation(field, attribute);
                addMinValueValidation(field, attribute);

                // генерация доп. полей (size, type ..) true / false
                addBlobInfoAnnotation(field, attribute);
                // простановка свойства contentType в model.js
                addContentTypeAnnotation(field, attribute);

                // т.к. в javax.validation указывается через одну аннотацию, и
                // оба поля обязательны
                addTotalAndFractionDigitsValidation(field, attribute);
            }
        }

        if (isComplexKeyInfoPresentInClass(clazz)) {
            ecoreModelEmitter.addKeyFieldDetails();
        }
    }

    /**
     * Добавить в <eAnnotations source="http://www.croc.ru/ctp/model/Transient"/>.
     * детали о типе контейнера List иди Set
     * @param xfwRef XFWReference
     * @param field FieldDeclaration
     */
    private void addContainerTypeForJpaTransientCollection(XFWReference xfwRef, FieldDeclaration field) {
        xfwRef.getEAnnotations().forEach(ann -> {
            if (ann.getSource().equals(XFWConstants.getUri(Transient.class.getSimpleName()))) {
                ann.getDetails().put("container", field.getType().getType().getSimpleName());
            }
        });
    }

    private XFWReference findOrCreateRef(EClass eclass, FieldDeclaration field) {
        if (eclass != null) {
            Optional<EStructuralFeature> structuralFeature = StreamSupport.stream(eclass.getEStructuralFeatures())
                    .filter(feature -> feature.getName().equals(field.getSimpleName())).findFirst();
            if (structuralFeature.isPresent() && structuralFeature.get() instanceof XFWReference) {
                return (XFWReference) structuralFeature.get();
            }
        }
        return XFWMM_FACTORY.createXFWReference();
    }

    private void addTotalAndFractionDigitsValidation(FieldDeclaration field, XFWAttribute attribute) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(Digits.class).getType());
        if (annotation != null) {
            final EAnnotation digitsAnnotation = ECORE_FACTORY.createEAnnotation();
            digitsAnnotation.setSource(XFWConstants.getUri(Digits.class.getSimpleName()));
            digitsAnnotation.getDetails().put("integer", valueOf(annotation.getIntValue("integer")));
            digitsAnnotation.getDetails().put("fraction", valueOf(annotation.getIntValue("fraction")));

            attribute.getEAnnotations().add(digitsAnnotation);
        }
    }

    private void addMinValueValidation(FieldDeclaration field, XFWAttribute attribute) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(Min.class).getType());
        if (annotation != null) {
            final EAnnotation minAnnotation = ECORE_FACTORY.createEAnnotation();
            minAnnotation.setSource(XFWConstants.getUri(Min.class.getSimpleName()));
            minAnnotation.getDetails().put("value", valueOf(annotation.getLongValue("value")));

            attribute.getEAnnotations().add(minAnnotation);
        }
    }

    private void addMaxValueValidation(FieldDeclaration field, XFWAttribute attribute) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(Max.class).getType());
        if (annotation != null) {
            final EAnnotation maxAnnotation = ECORE_FACTORY.createEAnnotation();
            maxAnnotation.setSource(XFWConstants.getUri(Max.class.getSimpleName()));
            maxAnnotation.getDetails().put("value", valueOf(annotation.getLongValue("value")));

            attribute.getEAnnotations().add(maxAnnotation);
        }
    }

    private void addSizeValidation(FieldDeclaration field, XFWAttribute attribute) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(Size.class).getType());
        if (annotation != null) {
            final EAnnotation sizeAnnotation = ECORE_FACTORY.createEAnnotation();
            sizeAnnotation.setSource(XFWConstants.getUri(Size.class.getSimpleName()));
            sizeAnnotation.getDetails().put("length", valueOf(annotation.getIntValue("max")));
            sizeAnnotation.getDetails().put("maxLen", valueOf(annotation.getIntValue("max")));
            sizeAnnotation.getDetails().put("minLen", valueOf(annotation.getIntValue("min")));

            attribute.getEAnnotations().add(sizeAnnotation);
        }
    }

    private void addBlobInfoAnnotation(FieldDeclaration field, XFWAttribute attribute) {
        String fname = field.getType().getSimpleName();
        // если поле типа Blob
        if ("Blob".equals(fname)) {
            final AnnotationReference annotation = field
                    .findAnnotation(typeRefProvider.newTypeReference(XFWBlobInfo.class).getType());
            if (annotation != null) {
                attribute.setGenerateBlobInfoFields(annotation.getBooleanValue("value"));
                attribute.setContentSizeSuffix(annotation.getStringValue("sizeFieldNameSuffix"));
                attribute.setFileNameSuffix(annotation.getStringValue("fileNameFieldNameSuffix"));
                attribute.setContentTypeSuffix(annotation.getStringValue("contentTypeFieldNameSuffix"));
            }
        }

    }

    private void addContentTypeAnnotation(FieldDeclaration field, XFWAttribute attribute) {
        String fname = field.getType().getSimpleName();
        // если поле типа Blob
        if ("Blob".equals(fname)) {
            final AnnotationReference annotation =
                    field.findAnnotation(typeRefProvider.newTypeReference(XFWContentType.class).getType());
            final EAnnotation contentTypeAnnotation = ECORE_FACTORY.createEAnnotation();
            contentTypeAnnotation.setSource(XFWConstants.getUri(XFWContentType.class.getSimpleName()));
            if (annotation != null) {
                contentTypeAnnotation.getDetails().put("value", valueOf(annotation.getStringValue("value")));
                contentTypeAnnotation.getDetails().put(
                        "acceptFileTypes",
                        valueOf(annotation.getStringValue("acceptFileTypes"))
                );
                attribute.getEAnnotations().add(contentTypeAnnotation);
            }
        }
    }

    private void addToUpperCaseAnnotation(FieldDeclaration field, XFWAttribute attribute) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(XFWToUpperCase.class).getType());
        if (annotation != null) {
            final EAnnotation toUpperCaseAnnotation = ECORE_FACTORY.createEAnnotation();
            toUpperCaseAnnotation.setSource(XFWConstants.getUri(XFWToUpperCase.class.getSimpleName()));

            attribute.getEAnnotations().add(toUpperCaseAnnotation);
        }
    }

    private void addMaxInclusiveValidation(FieldDeclaration field, XFWAttribute attribute) {
        final Type maxInclAnnotationType = typeRefProvider.newTypeReference(XFWMaxInclusive.class).getType();
        final Type maxInclAnnotationType_jdk7 = typeRefProvider
                .newTypeReference(ru.croc.ctp.jxfw.core.validation.jdk7.meta.XFWMaxInclusive.class).getType();

        AnnotationReference annotation = field.findAnnotation(maxInclAnnotationType);
        if (annotation == null) {
            annotation = field.findAnnotation(maxInclAnnotationType_jdk7);
        }
        if (annotation != null) {
            final EAnnotation maxInclAnnotation = ECORE_FACTORY.createEAnnotation();
            maxInclAnnotation.setSource(XFWConstants.getUri(XFWMaxInclusive.class.getSimpleName()));
            maxInclAnnotation.getDetails().put("value", annotation.getStringValue("value"));

            attribute.getEAnnotations().add(maxInclAnnotation);
        }
    }

    private void addMaxExclusiveValidation(FieldDeclaration field, XFWAttribute attribute) {
        final Type maxExclAnnotationType = typeRefProvider.newTypeReference(XFWMaxExclusive.class).getType();
        final Type maxExclAnnotationType_jdk7 = typeRefProvider
                .newTypeReference(ru.croc.ctp.jxfw.core.validation.jdk7.meta.XFWMaxExclusive.class).getType();

        AnnotationReference annotation = field.findAnnotation(maxExclAnnotationType);
        if (annotation == null) {
            annotation = field.findAnnotation(maxExclAnnotationType_jdk7);
        }
        if (annotation != null) {
            final EAnnotation maxExclAnnotation = ECORE_FACTORY.createEAnnotation();
            maxExclAnnotation.setSource(XFWConstants.getUri(XFWMaxExclusive.class.getSimpleName()));
            maxExclAnnotation.getDetails().put("value", annotation.getStringValue("value"));

            attribute.getEAnnotations().add(maxExclAnnotation);
        }
    }

    private void addMinExclusiveValidation(FieldDeclaration field, XFWAttribute attribute) {
        final Type minExclAnnotationType = typeRefProvider.newTypeReference(XFWMinExclusive.class).getType();
        final Type minExclAnnotationType_jdk7 = typeRefProvider
                .newTypeReference(ru.croc.ctp.jxfw.core.validation.jdk7.meta.XFWMinExclusive.class).getType();

        AnnotationReference annotation = field.findAnnotation(minExclAnnotationType);
        if (annotation == null) {
            annotation = field.findAnnotation(minExclAnnotationType_jdk7);
        }
        if (annotation != null) {
            final EAnnotation minExclAnnotation = ECORE_FACTORY.createEAnnotation();
            minExclAnnotation.setSource(XFWConstants.getUri(XFWMinExclusive.class.getSimpleName()));
            minExclAnnotation.getDetails().put("value", annotation.getStringValue("value"));

            attribute.getEAnnotations().add(minExclAnnotation);
        }
    }

    private void addMinInclusiveValidation(FieldDeclaration field, XFWAttribute attribute) {
        final Type minInclAnnotationType = typeRefProvider.newTypeReference(XFWMinInclusive.class).getType();
        final Type minInclAnnotationType_jdk7 = typeRefProvider
                .newTypeReference(ru.croc.ctp.jxfw.core.validation.jdk7.meta.XFWMinInclusive.class).getType();

        AnnotationReference annotation = field.findAnnotation(minInclAnnotationType);
        if (annotation == null) {
            annotation = field.findAnnotation(minInclAnnotationType_jdk7);
        }
        if (annotation != null) {
            final EAnnotation minInclAnnotation = ECORE_FACTORY.createEAnnotation();
            minInclAnnotation.setSource(XFWConstants.getUri(XFWMinInclusive.class.getSimpleName()));
            minInclAnnotation.getDetails().put("value", annotation.getStringValue("value"));

            attribute.getEAnnotations().add(minInclAnnotation);
        }
    }

    private void addPatternValidationAnnotation(FieldDeclaration field, EStructuralFeature reference) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(Pattern.class).getType());
        if (annotation != null) {
            final EAnnotation patternValidatedAnnotation = ECORE_FACTORY.createEAnnotation();
            patternValidatedAnnotation.setSource(XFWConstants.getUri(Pattern.class.getSimpleName()));

            final String regexp = annotation.getStringValue("regexp");
            final String message = annotation.getStringValue("message");
            final EnumerationValueDeclaration[] flags = annotation.getEnumArrayValue("flags");
            String flagsString = "";
            for (EnumerationValueDeclaration flag : flags) {
                if (flag.getSimpleName().equalsIgnoreCase("CASE_INSENSITIVE")) {
                    flagsString += "i";
                }
                if (flag.getSimpleName().equalsIgnoreCase("MULTILINE")) {
                    flagsString += "m";
                }
            }

            patternValidatedAnnotation.getDetails().put("regexp", regexp);
            patternValidatedAnnotation.getDetails().put("message", message);
            patternValidatedAnnotation.getDetails().put("flags", flagsString);

            reference.getEAnnotations().add(patternValidatedAnnotation);
        }
    }

    /**
     * Если поле класса (простой тип) помечается аннотацией XFWReadOnly, то
     * указываем дефолтное значение для XFWAttribute.
     *
     * @param attribute - аттрибут из ecore модели
     * @param field     - поле из класса
     */
    private void addDefaultValue(XFWAttribute attribute, FieldDeclaration field) {
        Type defaultValueAnnotationType = typeRefProvider.newTypeReference(XFWDefaultValue.class).getType();
        AnnotationReference annotation = field.findAnnotation(defaultValueAnnotationType);
        boolean typeEnum = (attribute.getEType() instanceof EEnum); //для перечислений не доступен NOW (время)
        if (annotation != null) {
            String val = annotation.getStringValue("value");
            // XFWDefaultValue.asCurrent() == true для простых полей типа
            // dateTime
            if (annotation.getBooleanValue("asCurrent") && !typeEnum) {
                attribute.setDefaultValue("now");
            } else if (val.length() > 0) {
                attribute.setDefaultValue(val);
            }
        }
    }

    /**
     * Предполагается использовать только над XFWClass, НЕ над полями.
     *
     * @param ecoreClass - класс ecore модели
     * @param clazz      - java класс
     */
    private void addReadOnlyAnnotation(XFWClass ecoreClass, ClassDeclaration clazz) {
        if (isReadOnly(clazz, null)) {
            EAnnotation readOnlyAnnotation = ECORE_FACTORY.createEAnnotation();
            readOnlyAnnotation.setSource(XFWConstants.getUri(XFWReadOnly.class.getSimpleName()));
            ecoreClass.getEAnnotations().add(readOnlyAnnotation);
        }
    }

    //проставить аннотации XFWSearchField над полями классов ecore модели
    private void addSearchFieldAnnotation(ClassDeclaration clazz, FieldDeclaration field, XFWAttribute attribute) {
        if (useFulltextProvider != null && useFulltextProvider.apply(clazz)) {
            field.getAnnotations().forEach(ann -> {
                if (ann.getAnnotationTypeDeclaration().getSimpleName().equalsIgnoreCase("XFWSearchField")) {
                    final EAnnotation searchAnnotation = ECORE_FACTORY.createEAnnotation();
                    searchAnnotation.setSource(XFWConstants.getUri("XFWSearchField"));
                    attribute.getEAnnotations().add(searchAnnotation);
                }
            });
        }
    }

    /**
     * Иформация о возможности использования полнотекста для получения сущностей.
     * Предполагается использовать только над XFWClass, НЕ над полями.
     *
     * @param ecoreClass - класс ecore модели
     * @param clazz      - java класс
     */
    private void addSearchAnnotation(XFWClass ecoreClass, ClassDeclaration clazz) {
        if (useFulltextProvider != null && useFulltextProvider.apply(clazz)) {
            EAnnotation search = ECORE_FACTORY.createEAnnotation();
            search.setSource(XFWConstants.getUri("XFWSearchClass"));
            //из пропертей
            boolean useFulltextByDefault = Boolean.valueOf(properties.getProperty(Constants.FULLTEXT_PROP_NAME));
            clazz.getAnnotations().forEach(ann -> {
                //копирование деталей из аннотации XFWSearchClass
                if (ann.getAnnotationTypeDeclaration().getSimpleName().equalsIgnoreCase("XFWSearchClass")) {
                    search.getDetails().put(
                            Constants.FULLTEXT_SEARCH_VERSION,
                            String.valueOf(ann.getDoubleValue(Constants.FULLTEXT_SEARCH_VERSION))
                    );
                    EnumerationValueDeclaration store = ann.getEnumValue("defaultSearchDataStore");
                    if (store.getSimpleName().toLowerCase().contains("FULL_TEXT".toLowerCase())) {
                        search.getDetails().put(
                                Constants.FULLTEXT_ECORE_USE_FULLTEXT_BY_DEFAULT,
                                Boolean.TRUE.toString()
                        );
                    }
                    ecoreClass.setPersistenceType(ann.getEnumValue("persistence").getSimpleName());

                }
            });
            if (!search.getDetails().containsKey(Constants.FULLTEXT_ECORE_USE_FULLTEXT_BY_DEFAULT)) {
                search.getDetails().put(
                        Constants.FULLTEXT_ECORE_USE_FULLTEXT_BY_DEFAULT,
                        Boolean.valueOf(useFulltextByDefault).toString()
                );

            }
            ecoreClass.getEAnnotations().add(search);
        }
    }

    /**
     * Пометить EStructuralFeature как неизменяемое (readOnly).
     *
     * @param structuralFeature - простое либо навигируемое св-во {@link EStructuralFeature}
     * @param clazz             {@link ClassDeclaration}
     * @param field             {@link FieldDeclaration}
     */
    private void addReadOnlyInfo(EStructuralFeature structuralFeature, ClassDeclaration clazz, FieldDeclaration field) {
        if (isReadOnly(clazz, field)) {
            structuralFeature.setChangeable(false);
        }
    }

    /**
     * поле является readOnly если класс либо само поле помечены аннотацией
     * XFWReadOnly.
     *
     * @param clazz {@link ClassDeclaration}
     * @param field {@link FieldDeclaration}
     */
    private boolean isReadOnly(ClassDeclaration clazz, FieldDeclaration field) {
        Type xfwReadOnly = typeRefProvider.newTypeReference(XFWReadOnly.class).getType();
        Type xfwObject = typeRefProvider.newTypeReference(XFWObject.class).getType();

        AnnotationReference findAnnotation = clazz.findAnnotation(xfwObject);
        if (findAnnotation != null && findAnnotation.getBooleanValue("isReadonly")) {
            // если нашли аннотацию XFWObject (isReadonly == true) над классом
            // то распространяется на все поля
            return true;
        }

        findAnnotation = clazz.findAnnotation(xfwReadOnly);
        if (findAnnotation != null) {
            // если нашли аннотацию xfwobject (isReadonly == true) над классом
            // то распространяется на все поля
            return true;
        }

        if (field != null) {
            findAnnotation = field.findAnnotation(xfwReadOnly);
            if (findAnnotation != null) {
                // если НЕ нашли аннотацию XFWReadOnly над классом, возможно
                // само поле помечено
                return findAnnotation.getBooleanValue("value");
            }
        }

        return false;
    }

    private void addIfAbsent(EClass eclass, EStructuralFeature eref) {
        if (eclass != null) {
            final boolean isFoundMatch = StreamSupport.stream(eclass.getEStructuralFeatures())
                    .anyMatch(feature -> feature.getName().equals(eref.getName()));
            if (!isFoundMatch) {
                eclass.getEStructuralFeatures().add(eref);
            }
        }
    }

    private void setParentOfClasses() {
        logger.debug("Start setParentOfClasses()");

        // Затем указываются предки для всех классов
        for (ClassDeclaration clazz : clazzes) {
            logger.debug("Processing class: {}", clazz.getQualifiedName());

            EClass eclass = xfwModel.findByFqName(clazz.getQualifiedName(), EClass.class);
            logger.debug("Found EClass: {}", eclass == null ? "null" : eclass.getInstanceClassName());
            if (clazz.getExtendedClass() != null
                    && !clazz.getExtendedClass().isAssignableFrom(typeRefProvider.newTypeReference(Object.class))) {
                if (eclass != null) {
                    XFWClass parentClass = findOrCreateStub(clazz.getExtendedClass().getName(),
                            clazz.getExtendedClass().getSimpleName());
                    eclass.getESuperTypes().add(parentClass);
                    logger.debug("Set parent for class: {}", eclass.getInstanceClassName());
                }
            }
        }
    }


    private void createClasses() {
        logger.debug("Started createClasses()");
        // Сначала создаются все классы
        for (ClassDeclaration clazz : clazzes) {
            logger.debug("Processing class: {}", clazz.getQualifiedName());
            XFWPackage xfwPackage = xfwModel.findOrCreatePackageByClassName(clazz.getQualifiedName());

            XFWClass eclass = xfwPackage.find(clazz.getQualifiedName(), XFWClass.class);
            boolean isClassFound = true;
            if (eclass == null) {
                isClassFound = false;
                eclass = XFWMM_FACTORY.createXFWClass();
            } else {
                eclass.getEAnnotations().clear();
                eclass.getPersistenceModule().clear();
            }

            if (clazz.isAbstract()) {
                eclass.setAbstract(true);
            }

            eclass.setName(clazz.getSimpleName());
            eclass.setInstanceClassName(clazz.getQualifiedName());

            final AnnotationReference xfwComplexTypeAnnotation = clazz
                    .findAnnotation(typeRefProvider.newTypeReference(XFWComplexType.class).getType());
            addCustomAnnotations(eclass, clazz.getAnnotations());

            if (xfwComplexTypeAnnotation != null) {
                eclass.setComplexType(true);

            } else {

                final String storageType = storageTypeProvider.apply(clazz);
                eclass.getPersistenceModule().add(storageType);

                eclass.setKeyTypeName(keyTypeProvider.apply(clazz));
                addi18nAnnotation(eclass, clazz.getAnnotations()); // fixme
                // дублирование аннотаций
                // если класс уже создан
                addReadOnlyAnnotation(eclass, clazz);
                //информация о возможности использования полнотекста для получения сущностей
                addSearchAnnotation(eclass, clazz);
                final AnnotationReference xfwObjectAnnotation
                        = clazz.findAnnotation(typeRefProvider.newTypeReference(XFWObject.class).getType());
                if (xfwObjectAnnotation != null) {
                    eclass.setPersistenceType(xfwObjectAnnotation.getEnumValue("persistence").getSimpleName());
                }

                XFWClass xfwClass = eclass;
                clazz.getAnnotations().forEach(ann -> {
                    //копирование деталей из аннотации XFWSearchClass
                    if (ann.getAnnotationTypeDeclaration().getSimpleName().equalsIgnoreCase("XFWSearchClass")) {
                        xfwClass.setPersistenceType(ann.getEnumValue("persistence").getSimpleName());

                    }
                });
            }

            if (!isClassFound) {
                xfwPackage.getEClassifiers().add(eclass);
                logger.debug("Created EClass: {}", eclass.getInstanceClassName());
            }
        }
    }



    private void addProtectedAnnotation(FieldDeclaration field, XFWAttribute xfwAttribute) {
        final AnnotationReference annotation = field
                .findAnnotation(typeRefProvider.newTypeReference(XFWProtected.class).getType());
        if (annotation != null) {
            final EAnnotation eAnnotation = ECORE_FACTORY.createEAnnotation();
            eAnnotation.setSource(XFWConstants.getUri(XFWProtected.class.getSimpleName()));
            eAnnotation.getDetails().put("value", annotation.getStringValue("value"));
            xfwAttribute.getEAnnotations().add(eAnnotation);
        }
    }

    private void addRelationAnnotation(XFWReference xfwRef, FieldDeclaration field) {
        forEach(field.getAnnotations(), annotationReference -> {
            final String simpleName = annotationReference.getAnnotationTypeDeclaration().getSimpleName();

            if ("XFWManyToMany".equals(simpleName) || "XFWManyToOne".equals(simpleName)
                    || "XFWOneToOne".equals(simpleName) || "XFWOneToMany".equals(simpleName)) {

                if ("XFWOneToMany".equals(simpleName) || "XFWManyToMany".equals(simpleName)) {

                    final EAnnotation relAnnotation = ECORE_FACTORY.createEAnnotation();
                    relAnnotation.setSource(XFWConstants.getUri(simpleName));
                    xfwRef.getEAnnotations().add(relAnnotation);
                }

                final EAnnotation relAnnotation = ECORE_FACTORY.createEAnnotation();
                // FIXME
                relAnnotation.setSource(XFWConstants.getUri("XFWOneToOne"));

                relAnnotation.getDetails().put("name", simpleName);

                final EnumerationValueDeclaration fetch = annotationReference.getEnumValue("fetch");
                if (fetch != null) {
                    if (fetch.getSimpleName().equals(FetchType.LAZY.toString())) {
                        relAnnotation.getDetails().put("lazyLoad", "true");
                    } else {
                        relAnnotation.getDetails().put("lazyLoad", "false");
                    }
                    if (field.getType().getActualTypeArguments().size() > 0) {
                        relAnnotation.getDetails().put("container", field.getType().getType().getSimpleName());
                        relAnnotation.getDetails().put("actualType",
                                field.getType().getActualTypeArguments().get(0).getName());
                    } else {
                        relAnnotation.getDetails().put("container", field.getType().getSimpleName());
                        relAnnotation.getDetails().put("actualType", field.getType().getName());
                    }
                }
                if ("XFWOneToOne".equals(simpleName) || "XFWManyToMany".equals(simpleName)
                        || "XFWOneToMany".equals(simpleName)) {
                    relAnnotation.getDetails().put("mappedBy", annotationReference.getStringValue("mappedBy"));
                }

                if ("XFWOneToMany".equals(simpleName)) {
                    relAnnotation.getDetails().put("targetEntity",
                            annotationReference.getClassValue("targetEntity").getSimpleName());
                    relAnnotation.getDetails().put("targetEntityQN",
                            annotationReference.getClassValue("targetEntity").getName());
                }
                if ("XFWOneToMany".equals(simpleName) || "XFWOneToOne".equals(simpleName)) {
                    relAnnotation.getDetails().put("orphanRemoval",
                            Boolean.valueOf(annotationReference.getBooleanValue("orphanRemoval")).toString());
                }
                xfwRef.getEAnnotations().add(relAnnotation);
            }

            if ("XFWJoinTable".equals(simpleName)) {
                final EAnnotation jointTableAnnotation = ECORE_FACTORY.createEAnnotation();
                jointTableAnnotation.setSource(XFWConstants.getUri(XFWJoinTable.class.getSimpleName()));

                final String crossTableName = annotationReference.getStringValue("name");
                Optional<MutableClassDeclaration> first = StreamSupport.stream(clazzes)
                        .filter(cl -> cl.getSimpleName().equals(crossTableName)).findFirst();
                if (!first.isPresent()) {
                    throw new IllegalStateException("Cross table " + crossTableName + "not found in classes");
                }
                final ClassDeclaration crossTableClass = first.get();
                jointTableAnnotation.getDetails().put("crossTableName", crossTableName);
                jointTableAnnotation.getDetails().put("crossTableNameQN", crossTableClass.getQualifiedName());

                final AnnotationReference[] inverseJoinColumnses = annotationReference
                        .getAnnotationArrayValue("inverseJoinColumns");
                if (inverseJoinColumnses != null && inverseJoinColumnses.length > 0) {
                    final String inverseJoinColumns = inverseJoinColumnses[0].getStringValue("name");
                    jointTableAnnotation.getDetails().put("inverseJoinColumns", inverseJoinColumns);
                }
                final AnnotationReference[] joinColumnses = annotationReference.getAnnotationArrayValue("joinColumns");
                if (joinColumnses != null && joinColumnses.length > 0) {
                    final String joinColumns = joinColumnses[0].getStringValue("name");
                    jointTableAnnotation.getDetails().put("joinColumns", joinColumns);
                }

                // искомое поле searchField в аннотации @XFWJoinTable
                final String searchFieldName = annotationReference.getAnnotationArrayValue("joinColumns")[0]
                        .getStringValue("name");

                FieldDeclaration foundField = null;
                for (FieldDeclaration f : crossTableClass.getDeclaredFields()) {
                    if (f.getSimpleName().equals(searchFieldName)) {
                        foundField = f;
                        break;
                    }
                }

                assert foundField != null;
                final Iterable<? extends AnnotationReference> annotations = foundField.getAnnotations();
                AnnotationReference found = null;
                for (AnnotationReference a : annotations) {
                    if (a.getAnnotationTypeDeclaration().getSimpleName().equals("Indexed")) {
                        found = a;
                        break;
                    }
                }
                assert found != null;
                final String columnName = found.getStringValue("name");
                if (StringUtils.isEmpty(columnName)) {
                    jointTableAnnotation.getDetails().put("searchField", searchFieldName);
                } else {
                    jointTableAnnotation.getDetails().put("searchField", columnName);
                }
                xfwRef.getEAnnotations().add(jointTableAnnotation);
            }
        });
    }

    /**
     * проверка что в классе есть информация о полях комплексного ключа т.е.
     * мнинмум одно из полей имеет аннотацию @PrimaryKey
     *
     * @param clazz - экземпляр класса
     * @return комплексный тип или нет
     */
    private boolean isComplexKeyInfoPresentInClass(ClassDeclaration clazz) {
        for (FieldDeclaration field : clazz.getDeclaredFields()) {
            Type annotationType = typeRefProvider.newTypeReference(XFWPrimaryKey.class).getType();
            AnnotationReference primaryKeyAnnotation = field.findAnnotation(annotationType);
            //для cass модуля
            AnnotationReference cassComplexKey = ClassUtil.annotationReferenceBySimpleName(field, "PrimaryKey");
            if (primaryKeyAnnotation != null || cassComplexKey != null) {
                return true;
            }
        }
        return false;
    }







}
