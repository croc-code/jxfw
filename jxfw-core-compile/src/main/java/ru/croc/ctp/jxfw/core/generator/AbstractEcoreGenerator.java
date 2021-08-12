package ru.croc.ctp.jxfw.core.generator;

import static ru.croc.ctp.jxfw.core.generator.Constants.LANG;
import static ru.croc.ctp.jxfw.core.generator.Constants.LANG_PROP_NAME;
import static ru.croc.ctp.jxfw.core.generator.Constants.PROP_NAME;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.AnnotationTypeElementDeclaration;
import org.eclipse.xtend.lib.macro.declaration.CompilationUnit;
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.EnumerationValueDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Type;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import ru.croc.ctp.jxfw.core.domain.meta.XFWElementLabel;
import ru.croc.ctp.jxfw.core.domain.meta.XFWElementLabels;
import ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId;
import ru.croc.ctp.jxfw.core.generator.impl.DataSourceEcoreGenerator;
import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil;
import ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWMMFactory;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Абстрактный генератор ecore модели.
 *
 * @author Nosov Alexander on 09.12.15.
 * @see DataSourceEcoreGenerator
 */
public abstract class AbstractEcoreGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEcoreGenerator.class);

    /**
     * Фабрика метамодели jXFW.
     */
    public static final XFWMMFactory XFWMM_FACTORY = XFWMMFactory.eINSTANCE;
    /**
     * Пакет метамодели jXFW.
     */
    static final XFWMMPackage XFWMM_PACKAGE = XFWMMPackage.eINSTANCE;
    /**
     * Фабрика метамодели ECORE.
     */
    protected static final EcoreFactory ECORE_FACTORY = EcoreFactory.eINSTANCE;
    /**
     * Пакет метамодели ECORE.
     */
    protected static final EcorePackage ECORE_PACKAGE = EcorePackage.eINSTANCE;

    /**
     * Единица компиляции.
     */
    protected CompilationUnit compilationUnit;

    /**
     * Контекст кодогенерации.
     */
    protected CodeGenerationContext context;

    /**
     * EMF-ресурс доменной модели jXFW.
     */
    protected XFWModelGenerator xfwModel;


    /**
     * Свойства конфигурации xtend.properties.
     */
    protected final Properties properties;


    /**
     * Фабрика представлений классов для кодогенерации.
     */
    protected TypeReferenceProvider typeRefProvider;

    /**
     * Конструктор.
     *
     * @param compilationUnit Компилируемый модуль (xtend-файл) или null
     * @param context         Контекст кодогенерации
     */
    protected AbstractEcoreGenerator(CompilationUnit compilationUnit, CodeGenerationContext context) {
        Assert.notNull(compilationUnit, "Constructor parameter compilationUnit should not be null");
        Assert.notNull(context, "Condtructor parameter context should not be null");
        this.compilationUnit = compilationUnit;
        this.context = context;
        this.properties = DomainClassCompileUtil.loadProperties(compilationUnit, context);


        // Хак для доступа к TypeReferenceProvider. Иначе он недоступен в
        // CodeGenerationContext
        // TODO Убрать, так как уже есть в {@link PersistenceModuleContext}
        try {
            Method getTypeReferenceProvider =
                compilationUnit.getClass().getDeclaredMethod("getTypeReferenceProvider");
            this.typeRefProvider = (TypeReferenceProvider) getTypeReferenceProvider.invoke(compilationUnit);
        } catch (NoSuchMethodException | SecurityException
            | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Запуск генерации доменной модели в формате ECORE.
     */
    public abstract void generate();


    /**
     * Определение типа для ecore по типу и аннотациям.
     * @param type - описание типа java
     * @param annotationReferences - описание аннотаций
     * @return - описание типа ecore
     */
    protected EClassifier getEType(Type type,
                                   Iterable<? extends AnnotationReference> annotationReferences) {
        String fname = type.getSimpleName();

        switch (fname) {
            case "String":
                for (AnnotationReference a : annotationReferences) {
                    if (a.getAnnotationTypeDeclaration().getSimpleName().equals("Lob")) {
                        return ECORE_PACKAGE.getEString(); // TODO Поддержать Lob
                    }
                }
                return ECORE_PACKAGE.getEString();
            case "Integer":
                return ECORE_PACKAGE.getEIntegerObject();
            case "int":
                /*
                для перечислений этод метод никогда не вызывается

                for (AnnotationReference annotation : annotationReferences) {
                    if (annotation.getAnnotationTypeDeclaration().getSimpleName().equals("XFWEnumerated")) {
                        return ECORE_PACKAGE.getEEnumerator();
                    }
                }*/
                return ECORE_PACKAGE.getEInt();
            case "UUID":
                return XFWMM_PACKAGE.getUUID();
            case "BigDecimal":
                return ECORE_PACKAGE.getEBigDecimal();
            case "BigInteger":
                return ECORE_PACKAGE.getEBigInteger();
            case "byte":
                return ECORE_PACKAGE.getEByte();
            case "Byte":
                return ECORE_PACKAGE.getEByteObject();
            case "short":
                return ECORE_PACKAGE.getEShort();
            case "Short":
                return ECORE_PACKAGE.getEShortObject();
            case "long":
                return ECORE_PACKAGE.getELong();
            case "Long":
                return ECORE_PACKAGE.getELongObject();
            case "float":
                return ECORE_PACKAGE.getEFloat();
            case "Float":
                return ECORE_PACKAGE.getEFloatObject();
            case "double":
                return ECORE_PACKAGE.getEDouble();
            case "Double":
                return ECORE_PACKAGE.getEDoubleObject();
            case "boolean":
                return ECORE_PACKAGE.getEBoolean();
            case "Boolean":
                return ECORE_PACKAGE.getEBooleanObject();
            case "LocalTime":
                if (type.getQualifiedName().startsWith("org.threeten.bp")) {
                    return XFWMM_PACKAGE.getLocalTime_Java7();
                } else {
                    return XFWMM_PACKAGE.getLocalTime();
                }
            case "LocalDate":
                if (type.getQualifiedName().startsWith("org.threeten.bp")) {
                    return XFWMM_PACKAGE.getLocalDate_Java7();
                } else {
                    return XFWMM_PACKAGE.getLocalDate();
                }
            case "Duration":
                if (type.getQualifiedName().startsWith("org.threeten.bp")) {
                    return XFWMM_PACKAGE.getDuration_Java7();
                } else {
                    return XFWMM_PACKAGE.getDuration();
                }
            case "Blob":
                return XFWMM_PACKAGE.getBlob();
            case "LocalDateTime":
                if (type.getQualifiedName().startsWith("org.threeten.bp")) {
                    return XFWMM_PACKAGE.getLocalDateTime_Java7();
                } else {
                    return XFWMM_PACKAGE.getLocalDateTime();
                }
            case "ZonedDateTime":
                if (type.getQualifiedName().startsWith("org.threeten.bp")) {
                    return XFWMM_PACKAGE.getZonedDateTime_Java7();
                } else {
                    return XFWMM_PACKAGE.getZonedDateTime();
                }
            case "ObjectFilter":
                return XFWMMPackage.eINSTANCE.getObjectFilter();
            case "List":
                return XFWMMPackage.eINSTANCE.getList();
            case "Set":
                return XFWMMPackage.eINSTANCE.getSet();
            case "Timestamp":
                return XFWMMPackage.eINSTANCE.getTimestamp();
            case "Map":
            	return XFWMMPackage.eINSTANCE.getMap();
            default:
                throw new IllegalArgumentException("Type not supported in jxfw metamodel " + fname);

        }
    }

    //TODO: Однажды созданные enum дальше не обновляются. Сделать обновление
    // enum.

    /**
     * Найти или создать метаданных перечисления.
     * @param fieldType пересичление
     * @return метаданные
     */
    protected EEnum findOrCreateEnum(EnumerationTypeDeclaration fieldType) {
        XFWPackage epackage = xfwModel.findOrCreatePackageByClassName(fieldType.getQualifiedName());
        EClassifier fieldTypeClassifier = epackage.find(fieldType.getQualifiedName(), EClassifier.class);

        if (fieldTypeClassifier == null) {

            XFWClass xfwClass = epackage.find(fieldType.getQualifiedName(), XFWClass.class);
            if (xfwClass != null) {
                epackage.getEClassifiers().remove(xfwClass);
            }

            EEnum eenum = ECORE_FACTORY.createEEnum();
            eenum.setInstanceClassName(fieldType.getQualifiedName());
            eenum.setName(fieldType.getSimpleName());
            addi18nAnnotation(eenum, fieldType.getAnnotations());
            addCustomAnnotations(eenum, fieldType.getAnnotations());
            epackage.getEClassifiers().add(eenum);
            for (EnumerationValueDeclaration v : fieldType.getDeclaredValues()) {
                EEnumLiteral enumLiteral = ECORE_FACTORY.createEEnumLiteral();
                enumLiteral.setName(v.getSimpleName());
                AnnotationReference annotation = v
                        .findAnnotation(typeRefProvider.newTypeReference(XFWEnumId.class).getType());
                if (annotation != null) {
                    enumLiteral.setValue(annotation.getIntValue("value"));
                }
                addi18nAnnotation(enumLiteral, v.getAnnotations());
                addCustomAnnotations(enumLiteral, v.getAnnotations());
                eenum.getELiterals().add(enumLiteral);
            }
            return eenum;
        }
        return (EEnum) fieldTypeClassifier;
    }


    /**
     * Создание метданных по аннотациям локализации.
     * @param modelElement аннотируемый элемент.
     * @param annotations аннотации на нем
     */
    protected void addi18nAnnotation(EModelElement modelElement,
                                   Iterable<? extends AnnotationReference> annotations) {
        if (logger.isDebugEnabled()) {
            List<String> annotationsNames = new ArrayList<>();
            annotations.forEach(a -> annotationsNames.add(a.getAnnotationTypeDeclaration().getSimpleName()));
            logger.debug("addi18nAnnotation modelElement: {}, annotations: {}", modelElement, annotationsNames);
        }

        TypeReference xfwElementLabelAnnRef = typeRefProvider
                .newTypeReference(XFWElementLabel.class);

        TypeReference xfwElementLabelsAnnRef = typeRefProvider
                .newTypeReference(XFWElementLabels.class);

        for (AnnotationReference a : annotations) {
            if (a.getAnnotationTypeDeclaration() == xfwElementLabelAnnRef.getType()) {
                String lang = a.getStringValue(LANG);
                if (lang == null || lang.isEmpty()) {
                    lang = properties.getProperty(LANG_PROP_NAME);
                }

                logger.debug("add lang: {}, value: {}", lang, a.getStringValue("value"));
                EAnnotation i18nAnnotation = ECORE_FACTORY.createEAnnotation();
                i18nAnnotation.setSource(XFWConstants.getUri(XFWElementLabel.class.getSimpleName()));
                i18nAnnotation.getDetails()
                        .put("value", a.getStringValue("value"));
                i18nAnnotation.getDetails()
                        .put(LANG, lang);
                i18nAnnotation.getDetails()
                        .put(PROP_NAME, a.getStringValue(PROP_NAME));
                // этих аннотаций может быть много на одном классе.
                modelElement.getEAnnotations().add(i18nAnnotation);

            } else {
                if (a.getAnnotationTypeDeclaration() == xfwElementLabelsAnnRef.getType()) {
                    final AnnotationReference[] values = a.getAnnotationArrayValue("value");
                    addi18nAnnotation(modelElement, Arrays.asList(values));
                }
            }
        }

        /* Видимо, этот код собирал в одну ecore-аннотацию данные из разных XFWElementLabel
        с разными локалями. После реализации JXFW-777 в ecore аннотации складываются ровно так,
        как они описаны в xtend. Логика их обработки находится в метамодели.
        final boolean anyMatch =
                StreamSupport.stream(modelElement.getEAnnotations())
                        .anyMatch(a ->
                                XFWConstants.getUri(XFWElementLabel.class.getSimpleName()).equals(a.getSource()));

        if (!anyMatch) {
            if (i18nAnnotation.getDetails().keySet().size() > 0) {
                modelElement.getEAnnotations().add(i18nAnnotation);
            }
            logger.debug("anyMatch: {}, i18Annotation: {}, annotations: {}", anyMatch,
                    i18nAnnotation, modelElement.getEAnnotations());
        } else {
            logger.debug("anyMatch: {}, annotations: {}", anyMatch, modelElement.getEAnnotations());
        }*/
    }


    /**
     * Создание метданных по кастомным аннотациям.
     * @param modelElement аннотируемый элемент.
     * @param annotations аннотации на нем
     */
    protected void addCustomAnnotations(EModelElement modelElement,
                                      Iterable<? extends AnnotationReference> annotations) {
        annotations.forEach(a -> {
            String annotationName = a.getAnnotationTypeDeclaration().getSimpleName();
            if (!XFWConstants.isSpecialEcoreAnnotation(annotationName)) {
                logger.debug("add custom annotation {}", annotationName);
                EAnnotation eannotation = ECORE_FACTORY.createEAnnotation();
                eannotation.setSource(XFWConstants.getUri(annotationName));
                modelElement.getEAnnotations().add(eannotation);
                Iterable<? extends AnnotationTypeElementDeclaration> annotationElements
                        = a.getAnnotationTypeDeclaration().getDeclaredAnnotationTypeElements();

                annotationElements.forEach(element -> {
                    Object attr = a.getValue(element.getSimpleName());
                    eannotation.getDetails()
                            .put(element.getSimpleName(), stringifyAnnotationAttribute(attr));
                });


            }
        });
    }

    private String stringifyAnnotationAttribute(Object attr) {

        if (attr instanceof EnumerationValueDeclaration) { // перечисление
            return ((EnumerationValueDeclaration) attr).getSimpleName();
        } else if (attr instanceof AnnotationReference) { // аннотация
            AnnotationReference annotationReference = (AnnotationReference) attr;
            List<String> list = new ArrayList<>();
            list.add("annotationType = " + annotationReference.getAnnotationTypeDeclaration().getSimpleName());
            annotationReference.getAnnotationTypeDeclaration().getDeclaredAnnotationTypeElements().forEach(element -> {
                Object attr1 = annotationReference.getValue(element.getSimpleName());
                list.add(element.getSimpleName() + " = " + stringifyAnnotationAttribute(attr1));
            });
            return Arrays.toString(list.toArray());

        } else if (attr != null && attr.getClass() != null && attr.getClass().isArray()) { //массив
            int length = Array.getLength(attr);
            Object[] objArr = new Object[length];
            for (int i = 0; i < length; i++) {
                objArr[i] = Array.get(attr, i);
                objArr[i] = stringifyAnnotationAttribute(objArr[i]);
            }
            return Arrays.toString(objArr);

        } else if (attr instanceof TypeReference) { // класс
            return ((TypeReference) attr).getName();
        } else { // примитив или строка
            return String.valueOf(attr);
        }

    }



    protected XFWClass findOrCreateStub(TypeReference fieldType) {

        XFWClass clazz = findOrCreateStub(fieldType.getType().getQualifiedName(),
                fieldType.getSimpleName());
        /*
          FIXME Не понятно зачем нужен этот код, т.к. создается заглушка, которая потом будет
          заполнена нормальным объявлением класса в createClasses
         */
        /*final Type type = context.findTypeGlobally(fieldType.getName());
        if (complexObjTypeRef.isAssignableFrom(typeRefProvider.newTypeReference(type))) {
            clazz.setComplexType(true);
        }*/
        return clazz;
    }

    /*
     Если класс не нашелся, это значит что он в этом же модуле,
     но в другом compilationUnit, который еще не обрабатывался процессором
     и будет обработан позже. Создаем пустышку для такого случая.
     */
    protected XFWClass findOrCreateStub(String fqName, String simpleName) {


        XFWClass clazz = xfwModel.findByFqName(fqName, XFWClass.class);
        logger.debug("Found parent EClass: {}",
                clazz == null ? "null" : clazz.getInstanceClassName());
        if (clazz == null) {
            clazz = XFWMM_FACTORY.createXFWClass();
            clazz.setName(simpleName);
            clazz.setInstanceClassName(fqName);
            XFWPackage xfwPackage
                    = xfwModel.findOrCreatePackageByClassName(fqName);
            xfwPackage.getEClassifiers().add(clazz);
            logger.debug("Created new EClass as parent: {}", clazz.getInstanceClassName());
        }
        return clazz;
    }

}
