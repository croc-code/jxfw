package ru.croc.ctp.jxfw.core.generator.impl

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import javax.annotation.Nonnull
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.services.ProblemSupport

import java.util.ServiceLoader;
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleFactory
import ru.croc.ctp.jxfw.core.generator.PersistenceModule
import ru.croc.ctp.jxfw.core.generator.StorageType

class PersistenceModulesManager {


    final PersistenceModuleContext moduleContext
    List<PersistenceModuleFactory> moduleFactories = null
    val modulesMap = new HashMap<ClassDeclaration, PersistenceModule>()

    new(@Nonnull PersistenceModuleContext moduleContext) {
        this.moduleContext = moduleContext
    }

    def clearModulesMap() {
        modulesMap.clear
    }

    private def TransformationContext getTransformationContext() {
        if(moduleContext.transformationContext === null) throw new RuntimeException("Please, set transformation context")
        moduleContext.transformationContext
    }

    def PersistenceModuleContext getModuleContext() {
        moduleContext
    }

    /**
    * Возвращает модуль кодогенератора, соответствующий переданному классу.
    * @param clazz класс, для которого требуется определить модуль кодогенератора
    * @throws java.lang.IllegalStateException В случае, если модуль кодогенератора не был найден
    */
    @Nonnull
    def PersistenceModule getClassModule(ClassDeclaration clazz) {
        var moduleForClass = modulesMap.get(clazz);
        if(moduleForClass !== null) return moduleForClass

        loadModulesClasses

        var moduleFactory = moduleFactories.findFirst[supported(clazz)]

        if(moduleFactory === null)
            moduleFactory = moduleFactories.findFirst[StorageType.JPA.name().equals(storageType)]

        if(moduleFactory !== null) {
            if (clazz instanceof MutableClassDeclaration) {
                moduleForClass = moduleFactory.createModule(clazz as MutableClassDeclaration)
            } else {
                //случай возникает на стадии doRegisterGlobals, наши моедли еще не MutableClassDeclaration
                moduleForClass = moduleFactory.createModuleForRegisterStage(clazz)
            }
            modulesMap.put(clazz, moduleForClass)
            return moduleForClass
        }

        throw new IllegalStateException('''
                    jXFW Generator module not found for class «clazz.qualifiedName»
                List of registered module factories:
                «FOR m : moduleFactories»
                    «m.storageType»
                «ENDFOR»
            '''
        );
    }

    def String getStorageType(ClassDeclaration clazz) {
        loadModulesClasses

        if(moduleFactories === null || moduleFactories.empty) {
            val ps = getTransformationContext() as ProblemSupport
            ps.addError(clazz, "No persistence module defined")
            return StorageType.NONE.name()
        }

        for(moduleFactory : moduleFactories) {
            if(moduleFactory.supported(clazz)) return moduleFactory.storageType
        }

        StorageType.JPA.name()
    }

    private def loadModulesClasses() {
        if(moduleFactories !== null) return moduleFactories

        moduleFactories = new ArrayList<PersistenceModuleFactory>


        val serviceLoader = ServiceLoader.load(PersistenceModuleFactory,StorageType.classLoader);

        for (PersistenceModuleFactory moduleFactory : serviceLoader) {
            moduleFactory.setContext(moduleContext);
            moduleFactories.add(moduleFactory);
        }
        if(moduleFactories.size == 0) throw new IllegalStateException("No jXFW Generator modules were found")

        moduleFactories

    }

}
