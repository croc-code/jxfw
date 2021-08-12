package ru.croc.ctp.jxfw.core.generator.impl;

import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;

import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext;
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleFactory;

/**
 * Абстрактная базовая реализация фабрики модулей кодогенератора.
 * Реализуется для каждого типа хранилища.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public abstract class PersistenceModuleFactoryBaseImpl implements PersistenceModuleFactory {
    /**
     * Контекст содержит вспомогательные сервисы xtend.
     */
    protected PersistenceModuleContext moduleContext;

    @Override
    public void setContext(PersistenceModuleContext moduleContext) {
        this.moduleContext = moduleContext;
    }

    /**
     * Поиск аннотации над классом.
     *
     * @param clazz           класс в котором ищем аннотацию
     * @param annotationClass искомая аннотация
     * @return true/false
     */
    public boolean isAnnotated(ClassDeclaration clazz, Class annotationClass) {
        return ClassUtil.existAnnotation(clazz, annotationClass);
    }



}
