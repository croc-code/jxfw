package ru.croc.ctp.jxfw.cass.generator;

import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.springframework.data.cassandra.core.mapping.Table;

import ru.croc.ctp.jxfw.core.generator.PersistenceModule;
import ru.croc.ctp.jxfw.core.generator.StorageType;
import ru.croc.ctp.jxfw.core.generator.impl.PersistenceModuleFactoryBaseImpl;

import javax.annotation.Nonnull;

/**
 * Фабрика модулей кодогенератора для JPA-хранилища.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public class PersistenceModuleFactoryCass extends PersistenceModuleFactoryBaseImpl {


    @Override
    public String getStorageType() {
        return StorageType.CASS.name();
    }

    @Override
    public Boolean supported(ClassDeclaration clazz) {
        return isAnnotated(clazz, Table.class);
    }

    @Nonnull
    @Override
    public PersistenceModule createModule(MutableClassDeclaration clazz) {
        return new PersistenceModuleCass(clazz, moduleContext);
    }

    @Nonnull
    @Override
    public PersistenceModule createModuleForRegisterStage(ClassDeclaration clazz) {
        return new PersistenceModuleCass(moduleContext);
    }
}
