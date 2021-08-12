package ru.croc.ctp.jxfw.jpa.generator;

import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;

import ru.croc.ctp.jxfw.core.generator.PersistenceModule;
import ru.croc.ctp.jxfw.core.generator.StorageType;
import ru.croc.ctp.jxfw.core.generator.impl.PersistenceModuleFactoryBaseImpl;

import javax.annotation.Nonnull;
import javax.persistence.Entity;

/**
 * Фабрика модулей кодогенератора для JPA-хранилища.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public class PersistenceModuleFactoryJpa extends PersistenceModuleFactoryBaseImpl {


    @Override
    public String getStorageType() {
        return StorageType.JPA.name();
    }

    @Override
    public Boolean supported(ClassDeclaration clazz) {
        return isAnnotated(clazz, Entity.class);
    }

    @Nonnull
    @Override
    public PersistenceModule createModule(MutableClassDeclaration clazz) {
        return new PersistenceModuleJpa(clazz, moduleContext);
    }

    @Nonnull
    @Override
    public PersistenceModule createModuleForRegisterStage(ClassDeclaration clazz) {
        return new PersistenceModuleJpa(moduleContext);
    }
}
