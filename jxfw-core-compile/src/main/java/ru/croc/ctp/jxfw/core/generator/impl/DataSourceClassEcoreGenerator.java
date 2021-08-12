package ru.croc.ctp.jxfw.core.generator.impl;

import static ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator.REENTRANT_LOCK;

import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.file.Path;
import org.slf4j.Logger;
import ru.croc.ctp.jxfw.core.datasource.DataSourceLoader;
import ru.croc.ctp.jxfw.core.generator.AbstractEcoreGenerator;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWMMFactory;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.XFWReference;

import java.util.List;

/**
 * Генератор Ecore модели для Data Source -класса.
 *
 * @author OKrutova
 * @see ru.croc.ctp.jxfw.core.generator.meta.XFWDataSource
 * @since 1.6
 */
public class DataSourceClassEcoreGenerator extends AbstractEcoreGenerator {

    private static Logger logger = LoggerFactory.getLogger(DataSourceClassEcoreGenerator.class);
    private final ClassDeclaration clazz;
    private final GeneratorHelper generatorHelper;
    private final List<? extends FieldDeclaration> fields;

    /**
     * Конструктор.
     *
     * @param clazz   Класс датасорса
     * @param fields  поля, которые попадут в контроллер, за исключением стандартных
     * @param context Контекст кодогенерации
     */
    public DataSourceClassEcoreGenerator(ClassDeclaration clazz, List<? extends FieldDeclaration> fields,
                                         CodeGenerationContext context) {
        super(clazz.getCompilationUnit(), context);
        this.clazz = clazz;
        this.fields = fields;
        generatorHelper = new GeneratorHelper(typeRefProvider);
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

            XFWPackage xfwPackage = xfwModel.findOrCreatePackageByClassName(
                    clazz.getQualifiedName());

            if (xfwPackage.find(clazz.getQualifiedName(), XFWDataSource.class) == null) {

                XFWDataSource dataSource = XFWMMFactory.eINSTANCE.createXFWDataSource();
                dataSource.setRequestMapping(clazz.findAnnotation(typeRefProvider
                        .newTypeReference(ru.croc.ctp.jxfw.core.generator.meta.XFWDataSource.class)
                        .getType()).getStringValue("value"));
                dataSource.setName(clazz.getSimpleName());
                dataSource.setInstanceClassName(clazz.getQualifiedName());
                dataSource.setGeneral(isDataSourceGeneral());

                xfwPackage.getEClassifiers().add(dataSource);
                createFields(dataSource);
            }

            xfwModel.save();
        } finally {
            REENTRANT_LOCK.unlock();
        }

    }

    /**
     * Проверяет является ли источник данных общим, а не для доменных объектов.
     *
     * @return true, если general.
     */
    private boolean isDataSourceGeneral() {
        final TypeReference domainDataSourceLoader = typeRefProvider.newTypeReference(DataSourceLoader.class);
        final TypeReference dataSource = typeRefProvider.newTypeReference(clazz);
        return !domainDataSourceLoader.isAssignableFrom(dataSource);
    }

    private void createFields(XFWDataSource ecoreClass) {


        for (FieldDeclaration field : fields) {


            TypeReference fieldType = field.getType();


            if (generatorHelper.isDomain(fieldType)) {
                // доменный объект
                XFWReference xfwRef = XFWMM_FACTORY.createXFWReference();
                xfwRef.setName(field.getSimpleName());
                xfwRef.setEType(findOrCreateStub(fieldType));
                addCustomAnnotations(xfwRef, field.getAnnotations());
                ecoreClass.getEStructuralFeatures().add(xfwRef);
            } else {
                // простой тип
                XFWAttribute attribute = XFWMM_FACTORY.createXFWAttribute();
                attribute.setName(field.getSimpleName());
                attribute.setEType(getEType(field.getType().getType(), field.getAnnotations()));
                addCustomAnnotations(attribute, field.getAnnotations());
                ecoreClass.getEStructuralFeatures().add(attribute);
            }
        }


    }


}
