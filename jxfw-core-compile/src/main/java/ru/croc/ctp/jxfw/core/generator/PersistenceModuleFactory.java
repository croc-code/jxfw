package ru.croc.ctp.jxfw.core.generator;

import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;

import javax.annotation.Nonnull;

/**
 * Фабрика модулей кодогенератора.
 * Каждый модуль отвечает за генерацию кода по одному классу доменной модели.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public interface PersistenceModuleFactory {

    /**
     * Установка контектса
     * @param moduleContext контекст содержит вспомогательные сервисы xtend
     */
    void setContext(PersistenceModuleContext moduleContext);


        /**
         * @return Тип хранилища.
         */
    String getStorageType();

    /**
     * Возвращает true, если заданная сущность может быть сохранена в хранилище
     * этого типа.
     *
     * @param clazz тип сущности, возможность сохранения которой нужно проверить
     * @return флаг поддерживается или нет реализация {@link PersistenceModule} для класса {@link ClassDeclaration}.
     */
    Boolean supported(ClassDeclaration clazz);

    /**
     * Создает и инициализирует экземпляр модуля кодогенератора для заданного класса доменной модели.
     * <p/>
     * @param clazz класс доменной модели
     * @return созданный модуль кодогенератора
     */
    @Nonnull
    PersistenceModule createModule(MutableClassDeclaration clazz);

    /**
     * Создает и инициализирует экземпляр модуля кодогенератора для заданного класса доменной модели.
     * <p/>
     * Используется на этапе doRegisterGlobals т.к. еще нету MutableClassDeclaration' ов, но есть необходимость
     * зарегестрировать классы и был доступен контекст {@link org.eclipse.xtend.lib.macro.RegisterGlobalsContext}
     * после этапа doRegisterGlobals необходимо выполнить {@link PersistenceModulesManager#clearModulesMap()}
     * @param clazz класс доменной модели
     * @return созданный модуль кодогенератора
     */
    @Nonnull
    PersistenceModule createModuleForRegisterStage(ClassDeclaration clazz);
}
