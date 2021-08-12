package ru.croc.ctp.jxfw.core.generator.impl

import java.util.List
import org.eclipse.xtend.lib.macro.CodeGenerationContext
import org.eclipse.xtend.lib.macro.CodeGenerationParticipant
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext
import org.eclipse.xtend.lib.macro.RegisterGlobalsParticipant
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.ValidationParticipant
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import org.slf4j.Logger
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.EnumCompileUtil.*
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString
import ru.croc.ctp.jxfw.core.generator.PersistenceModule

class XFWObjectProcessor implements TransformationParticipant<MutableClassDeclaration>, RegisterGlobalsParticipant<ClassDeclaration>,
ValidationParticipant<ClassDeclaration>, CodeGenerationParticipant<TypeDeclaration> {

    static final Logger logger = LoggerFactory.getLogger(XFWObjectProcessor);

    val accessorsProcessor = new ru.croc.ctp.jxfw.core.generator.impl.AccessorsProcessor
    
    val toStringProcessor = new ToStringProcessor    

    List<MutableClassDeclaration> mutableClasses

    PersistenceModuleContext moduleContext = null

    PersistenceModulesManager persistenceModulesManager = null

    override doRegisterGlobals(List<? extends ClassDeclaration> elements, extension RegisterGlobalsContext context) {
        logger.debug("doRegisterGlobals started")
        val cu = elements.get(0).compilationUnit
         logger.debug("Compilation Unit: " + cu.simpleName)
          
 
        moduleContext = new PersistenceModuleContext(loadProperties(cu, context), cu)
        persistenceModulesManager = new PersistenceModulesManager(moduleContext)
        moduleContext.registerGlobalsContext = context

        val filteredElms = elements.filter(typeof(ClassDeclaration)).toList()
        filteredElms.forEach [
            //получить кодогенерилку соответсвующую классу (PersistenceModule)
            val module = persistenceModulesManager.getClassModule(it)
            //регистрация необходимых классов, для следующих этапов генерации
            module.registerClasses(it)
            // регистрируем внутренний класс Property
            context.registerClass(getPropertyInnerClassQName(it))
        ]

        //т.к. на данном этапе еще нет в ключах мапы MutableClassDeclaration
        persistenceModulesManager.clearModulesMap
    }

    override doTransform(List<? extends MutableClassDeclaration> elements, extension TransformationContext context) {
        try {
        	
        	logger.debug("doTransform started")

			mutableClasses = elements.filter(typeof(MutableClassDeclaration)).toList()

			moduleContext.transformationContext = context
			moduleContext.mutableClasses = mutableClasses

			persistenceModulesManager.clearModulesMap

			mutableClasses.forEach [
				// TODO: удалить после деприкации XFWEnumerated
				it.processXFWEnumeratedField(context)

				it.addPropertyConstants(context)
				it.addSerialVersionUID(context) // Добавление поля serialVersionUID в класс доменной модели
				val module = persistenceModulesManager.getClassModule(it)
				module.validateBeforeTransform
				module.extendClazz
				module.produceRequiredFields // догенерация требуемых дополнительных полей в зависимости от типа поля модели
				addAnnotationIfNotExist(XFWToString.findTypeGlobally, XFWToString.newAnnotationReference)
			]

			accessorsProcessor.doTransform(mutableClasses, context)
			toStringProcessor.doTransform(mutableClasses, context)

			mutableClasses.forEach [
				it.processEnumFields(context)
			]

			mutableClasses.forEach[addClasses(context, persistenceModulesManager.getClassModule(it))]

		} catch (Exception exception) {
			// TODO: experimental for removing unexpected errors in maven log
			logger.debug("doTransform exception " + exception)
		}        
    }

    override doValidate(List<? extends ClassDeclaration> elements, extension ValidationContext context) {
    	try {

			val cu = elements.get(0).compilationUnit
			logger.debug("doValidate started " + cu.simpleName)

			moduleContext.validationContext = context

			val filteredElms = elements.filter(typeof(ClassDeclaration)).toList()
			filteredElms.forEach [
				checkClassPackage(it, context)
				val module = persistenceModulesManager.getClassModule(it)
				module.doValidate
			]

		} catch (Exception exception) {
			// TODO: experimental for removing unexpected errors in maven log
			logger.debug("doValidate exception " + exception)
		}
    }

    def void addClasses(MutableClassDeclaration clazz, extension TransformationContext context, PersistenceModule module) {
        if(isPersistence(clazz)) {
            module.createComplexKey
            module.createRepository
            module.createRepositoryImpl
            module.createService
        }
    }

    override doGenerateCode(List<? extends TypeDeclaration> elements, extension CodeGenerationContext context) {
        logger.debug("doGenerateCode started")

        moduleContext.codeGenerationContext = context

        if(mutableClasses === null || mutableClasses.size == 0) return;

        val generator = getEcoreGenerator(
                persistenceModulesManager,
                mutableClasses,
                context
        )

        generator.generate

        //предоставить возможность модулям поучаствовать в генерации
        mutableClasses.forEach[ clz |
            val module = persistenceModulesManager.getClassModule(clz)
            module.doGenerateCode
        ]
    }

}
