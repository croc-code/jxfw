package ru.croc.ctp.jxfw.core.generator.impl;

import static ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.getEcoreGenerator;
import static ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.loadProperties;
import static ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.getPropertyInnerClassQName;

import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.ValidationContext;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.CompilationUnit;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.core.generator.EcoreGenerator;
import ru.croc.ctp.jxfw.core.generator.PersistenceModule;
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext;
import ru.croc.ctp.jxfw.core.generator.meta.XFWMappedSuperclass;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Inheritance;

/**
 * Процессор обработки аннотации XFWMappedSuperclass.
 *
 * @author SPyatykh
 */
public class XfwMappedSuperclassProcessor extends AbstractClassProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XfwMappedSuperclassProcessor.class);

    private final AccessorsProcessor accessorsProcessor = new AccessorsProcessor();
    private List<MutableClassDeclaration> mutableClasses;
    private PersistenceModulesManager persistenceModulesManager = null;
    private PersistenceModuleContext moduleContext = null;

    @Override
    public void doRegisterGlobals(List<? extends ClassDeclaration> annotatedClasses, RegisterGlobalsContext context) {
        logger.debug("doRegisterGlobals started");
        CompilationUnit cu = annotatedClasses.get(0).getCompilationUnit();
        logger.debug("Compilation Unit: " + cu.getSimpleName());


        moduleContext = new PersistenceModuleContext(loadProperties(cu, context), cu);
        persistenceModulesManager = new PersistenceModulesManager(moduleContext);
        moduleContext.setRegisterGlobalsContext(context);

        final List<ClassDeclaration> filteredElms = annotatedClasses.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());


        filteredElms.forEach(classDeclaration -> {
            //получить кодогенерилку соответсвующую классу (PersistenceModule)
            PersistenceModule module = persistenceModulesManager.getClassModule(classDeclaration);
            //регистрация необходимых классов, для следующих этапов генерации
            module.registerClasses(classDeclaration);
            // регистрируем внутренний класс Property
            context.registerClass(getPropertyInnerClassQName(classDeclaration));
        });

        //т.к. на данном этапе еще нет в ключах мапы MutableClassDeclaration
        persistenceModulesManager.clearModulesMap();
    }

    @Override
    public void doTransform(List<? extends MutableClassDeclaration> annotatedClasses, TransformationContext context) {
        logger.debug("doTransform started");

        mutableClasses = annotatedClasses.stream().filter(Objects::nonNull).collect(Collectors.toList());

        moduleContext.setTransformationContext(context);
        moduleContext.setMutableClasses(mutableClasses);


        super.doTransform(annotatedClasses, context);

        mutableClasses.forEach(element -> {
            DomainClassCompileUtil.checkAnnotationsCombination(element, context);

            EnumCompileUtil.processXFWEnumeratedField(element, context);
            DomainClassCompileUtil.addPropertyConstants(element, context);

            PersistenceModule module = persistenceModulesManager.getClassModule(element);
            //добавляем аннотации для связи навигируемых свойств и информацию об id и version
            module.extendMappedSuperclass();

            //догенерация требуемых дополнительных полей в зависимости от типа поля модели
            module.produceRequiredFields();
        });

        accessorsProcessor.doTransform(mutableClasses, context);


        mutableClasses.forEach(it -> EnumCompileUtil.processEnumFields(it, context));
    }


    @Override
    public void doValidate(ClassDeclaration annotatedClass, ValidationContext context) {
        if (!annotatedClass.isAbstract()) {
            context.addError(annotatedClass, "MappedSuperclass should be an abstact");
        }
        if(GeneratorHelper.findAnnotation(annotatedClass, XFWMappedSuperclass.class)!= null
            && GeneratorHelper.findAnnotation(annotatedClass, Inheritance.class)!= null) {
            context.addError(annotatedClass,
                "An entity cannot be annotated with both @Inheritance and @XFWMappedSuperclass");
        }

        final List<ClassDeclaration> parentsOfClass = GeneratorHelper.getParents(annotatedClass);
        parentsOfClass.forEach(parent -> {
            if (!parent.isAbstract() && (GeneratorHelper.findXFWObjectAnnotation(parent) != null)) {
                context.addError(annotatedClass, "MappedSuperclass should be inherited from abstract classes");
            }
        });

    }

    @Override
    public void doValidate(List<? extends ClassDeclaration> annotatedClasses, ValidationContext context) {
        super.doValidate(annotatedClasses, context);
        annotatedClasses.stream()
            .filter(Objects::nonNull).forEach(clazz -> {
                GeneratorHelper.checkClassPackage(clazz, context);
        });

    }

    @Override
    public void doGenerateCode(List<? extends ClassDeclaration> annotatedSourceElements,
                               CodeGenerationContext context) {
        logger.debug("doGenerateCode started");

        moduleContext.setCodeGenerationContext(context);

        if (mutableClasses == null || mutableClasses.size() == 0) {
            return;
        }

        EcoreGenerator generator = getEcoreGenerator(
            persistenceModulesManager,
            mutableClasses,
            context
        );

        generator.generate();

        //предоставить возможность модулям поучаствовать в генерации
        mutableClasses.forEach(clz -> {
            PersistenceModule module = persistenceModulesManager.getClassModule(clz);
            module.doGenerateCode();
        });
    }
}
