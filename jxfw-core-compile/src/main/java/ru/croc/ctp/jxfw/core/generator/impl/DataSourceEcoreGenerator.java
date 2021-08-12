package ru.croc.ctp.jxfw.core.generator.impl;

import static java.lang.String.valueOf;
import static java8.util.stream.StreamSupport.stream;
import static ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator.REENTRANT_LOCK;

import com.querydsl.core.types.QTuple;
import java8.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.AnnotationTypeElementDeclaration;
import org.eclipse.xtend.lib.macro.declaration.EnumerationValueDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.ParameterDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Type;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.file.Path;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.generator.AbstractEcoreGenerator;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWMMFactory;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;

import java.util.List;


/**
 * Генератор Ecore модели для Data Source
 *
 * @author Nosov Alexander
 *         on 09.12.15.
 * @see ru.croc.ctp.jxfw.core.generator.meta.XFWDataSource
 */
public class DataSourceEcoreGenerator extends AbstractEcoreGenerator {
    private static Logger logger = LoggerFactory.getLogger(DataSourceEcoreGenerator.class);
    private final List<? extends MethodDeclaration> methods;

    /**
     * Конструктор для генератора.
     *
     * @param methods - методы помеченные аннотацией @XFWDataSource
     * @param context - контест
     */
    public DataSourceEcoreGenerator(List<? extends MethodDeclaration> methods, CodeGenerationContext context) {
        super(methods.get(0).getCompilationUnit(), context);
        this.methods = methods;
    }

    @Override
    public void generate() {
        REENTRANT_LOCK.lock();
        try {

            Path projectPath = context.getTargetFolder(compilationUnit.getFilePath());
            java.net.URI projectJavaUri = context.toURI(projectPath);
            logger.debug("CompilationUnit {}", compilationUnit.getFilePath());
            logger.debug("Loading models from " + DataSourceEcoreGenerator.class.getName());
            xfwModel = new XFWModelGenerator(projectJavaUri, logger);


            // Сначала создаются все Датасорсы
            createDataSources();

            // А затем создаются все параметры для всех датасорсов
            setupParametersOfDataSources();

            xfwModel.save();
        } finally {
            REENTRANT_LOCK.unlock();
        }
    }

    private void createDataSources() {
        // Сначала создаются все классы для ДатаСорсов
        for (MethodDeclaration method : methods) {
            XFWPackage myPackage = xfwModel.findOrCreatePackageByClassName(
                    method.getDeclaringType().getQualifiedName());

            final String dataSourceName = method.getSimpleName();

            XFWDataSource dataSource =  myPackage.find(dataSourceName, XFWDataSource.class);
            boolean isDataSourceFound = true;
            if (dataSource == null) {
                isDataSourceFound = false;
                dataSource = XFWMMFactory.eINSTANCE.createXFWDataSource();
            }

            dataSource.setRequestMapping(getRequestMappingUrl(method));
            dataSource.setName(method.getSimpleName());
            dataSource.setInstanceClassName(method.getDeclaringType().getQualifiedName());

            if (!isDataSourceFound) {
                myPackage.getEClassifiers().add(dataSource);
            }
        }

    }

    private void setupParametersOfDataSources() {
        logger.debug("Setting of methods parameters for DataSource started.");
        methods.forEach(this::setupParametersOfDataSource);
        logger.debug("Setting of methods parameters for DataSource finished.");
    }

    private String getRequestMappingUrl(MethodDeclaration method) {
        final Type annotationType = typeRefProvider
                .newTypeReference(ru.croc.ctp.jxfw.core.generator.meta.XFWDataSource.class).getType();
        return method.findAnnotation(annotationType).getStringValue("value");
    }

    private void setupParametersOfDataSource(MethodDeclaration method) {
        final String typeName = method.getDeclaringType().getQualifiedName();
        XFWPackage myPackage = xfwModel.findOrCreatePackageByClassName(typeName);
        logger.debug("Method: " + method.getSimpleName());
        logger.debug("Package: " + myPackage.getName());

        XFWDataSource dataSource = myPackage.find(method.getSimpleName(),XFWDataSource.class);
        logger.debug("DataSource: " + dataSource != null ? dataSource.getName() : null);
        if (dataSource == null) {
            return;
        }

        final EList<EOperation> eOperations = dataSource.getEOperations();
        final Optional<EOperation> maybeDataSource = stream(eOperations)
                .filter(op -> method.getSimpleName().equals(op.getName()))
                .findFirst();

        final EOperation dataSourceOp;
        if (maybeDataSource.isPresent()) {
            dataSourceOp = maybeDataSource.get();
        } else {
            dataSourceOp = XFWMMFactory.eINSTANCE.createXFWOperation();
        }
        dataSourceOp.setName(method.getSimpleName());

        dataSourceOp.getEParameters().clear();

        final Iterable<? extends ParameterDeclaration> parameters = method.getParameters();
        for (ParameterDeclaration parameter : parameters) {
            final EParameter eParameter = ECORE_FACTORY.createEParameter();

            logger.debug("Parameter: " + parameter.getSimpleName() + ", type: " + parameter.getType().getName());
            if (typeRefProvider.newTypeReference(DomainObject.class).isAssignableFrom(parameter.getType())) {
                // TODO JXFW-861: Установлена валидациия. В эту часть кода заходить не должны.
                final XFWPackage p = xfwModel.findOrCreatePackageByClassName(
                        parameter.getType().getName());
                eParameter.setEType(p.find(parameter.getType().getName(),XFWClass.class));
                logger.debug("Parameter is DomainObject: " + eParameter.getEType());
            } else {
                eParameter.setEType(getEType(parameter.getType().getType(), parameter.getAnnotations()));
                logger.debug("Parameter is not DomainObject: " + eParameter.getEType());
            }

            eParameter.setName(parameter.getSimpleName());

            final Iterable<? extends AnnotationReference> annotations = parameter.getAnnotations();
            for (AnnotationReference annotation : annotations) {
                final EAnnotation eAnnotation = ECORE_FACTORY.createEAnnotation();
                if (typeRefProvider.newTypeReference(RequestParam.class).getType()
                        .isAssignableFrom(annotation.getAnnotationTypeDeclaration())) {
                    eAnnotation.setSource(XFWConstants.REQUEST_PARAM_ANNOTATION_SOURCE.getUri());

                    final String value = annotation.getStringValue("value");
                    if (StringUtils.isEmpty(value)) {
                        eAnnotation.getDetails().put("value", annotation.getStringValue("name"));
                    } else {
                        eAnnotation.getDetails().put("value", value);
                    }
                    eAnnotation.getDetails().put("required", valueOf(annotation.getBooleanValue("required")));
                } else {
                    final Iterable<? extends AnnotationTypeElementDeclaration> elements
                            = annotation.getAnnotationTypeDeclaration().getDeclaredAnnotationTypeElements();
                    for (AnnotationTypeElementDeclaration element : elements) {
                        String resultValue = "";

                        final String attributeName = element.getSimpleName();
                        final Object rawValue = annotation.getValue(attributeName);
                        String value = rawValue.toString() + " ";
                        // пробел вставляется в конец для того чтобы обработать ситуацию,
                        // когда в качестве значения испольхуется пустая строка.

                        if (rawValue instanceof EnumerationValueDeclaration) {
                            value = ((EnumerationValueDeclaration) rawValue).getDeclaringType().getQualifiedName()
                                    + "." + ((EnumerationValueDeclaration) rawValue).getSimpleName();

                            resultValue += "$L,";
                        } else if (rawValue instanceof TypeReference[]) {
                            TypeReference[] typeReferences = annotation.getClassArrayValue(attributeName);
                            if (typeReferences.length == 0) {
                                continue;
                            }
                        } else {
                            resultValue += "$S,";
                        }

                        resultValue += value;

                        eAnnotation.getDetails().put(attributeName, resultValue);
                    }
                    eAnnotation.getDetails().put("className",
                            annotation.getAnnotationTypeDeclaration().getQualifiedName());
                }
                eParameter.getEAnnotations().add(eAnnotation);
            }

            dataSourceOp.getEParameters().add(eParameter);
            logger.debug("EParameter added to dataSourceOperation.");
        }

        final TypeReference returnType = method.getReturnType();
        final TypeReference actualReturnType = method.getReturnType().getActualTypeArguments().get(0);
        if (returnType.getActualTypeArguments().size() > 0
                && typeRefProvider.newTypeReference(QTuple.class).isAssignableFrom(actualReturnType)) {
            dataSourceOp.setEType(XFWMMPackage.eINSTANCE.getTuple());
        } else if (actualReturnType.getName().equals(DomainObject.class.getName())){
        	dataSourceOp.setEType(XFWMMPackage.eINSTANCE.getDomainObject());
        } else {
        	final XFWPackage xfwPackage = xfwModel.findOrCreatePackageByClassName(
                actualReturnType.getName());

            XFWClass xfwClass = xfwPackage.find(actualReturnType.getName(), XFWClass.class);
            boolean isClassFound = true;
            if (xfwClass == null) {
                isClassFound = false;
                xfwClass = XFWMMFactory.eINSTANCE.createXFWClass();
            }
            xfwClass.setName(actualReturnType.getSimpleName());
            xfwClass.setInstanceClassName(actualReturnType.getName());
 
            if (!isClassFound) {
                xfwPackage.getEClassifiers().add(xfwClass);
            }

            dataSourceOp.setEType(xfwClass);
        }

        dataSource.getEOperations().add(dataSourceOp);
        myPackage.getEClassifiers().add(dataSource);
    }

}
