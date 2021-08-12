package ru.croc.ctp.jxfw.core.generator.impl;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import ru.croc.ctp.jxfw.core.generator.EcoreModelEmitter;
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext;

import javax.annotation.Nonnull;

/**
 * Базовая реализация сервиса формирования модели.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public class EcoreModelEmitterBaseImpl implements EcoreModelEmitter {

    private final ClassDeclaration clazz;
    private final PersistenceModuleContext context;

    /**
     * Фабрика метамодели ECORE.
     */
    protected static final EcoreFactory ECORE_FACTORY = EcoreFactory.eINSTANCE;
    private EClass ecoreClass = null;

    /**
     * Конструктор.
     *
     * @param clazz   Класс доменной модели, для которого строится ecore-модель
     * @param context Контекст для доступа к вспомогательным сервисам xtend
     */
    public EcoreModelEmitterBaseImpl(@Nonnull ClassDeclaration clazz, @Nonnull PersistenceModuleContext context) {
        this.clazz = clazz;
        this.context = context;
    }

    protected ClassDeclaration getClazz() {
        return clazz;
    }

    protected PersistenceModuleContext getContext() {
        return context;
    }

    /**
     * Формируемый класс ecore-модели.
     *
     * @return класс ecore-модели
     */
    protected EClass getEcoreClass() {
        if (ecoreClass == null) {
             ecoreClass =  context.getModelEmfResource().findByFqName(clazz.getQualifiedName(),EClass.class);
        }

        return ecoreClass;
    }

    @Override
    public boolean isFieldAddToModel(FieldDeclaration field) {
        return GeneratorHelper.isFieldAddToModel(field, context.getCodeGenerationContext());
    }


    @Override
    public void addKeyFieldDetails() {

    }

}
