package ru.croc.ctp.jxfw.core.generator;

import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;

/**
 * Сервис для формирования содержимого ecore-модели.
 * Интерфейс реализуется в модулях кодогенератора для разных типов хранилищ.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public interface EcoreModelEmitter {
    /**
     * Для полей доменной модели, которые помечаны аннтоцией @PrimaryKey,
     * устанавливает порядок полей в комплексном ключе.
     */
    void addKeyFieldDetails();

    /**
     * Добавлять или нет поле в ecore-модель.
     * <p/>
     * @param field проверяемое поле
     * @return {@code true}, если поле нужно добавить в модель
     */
    boolean isFieldAddToModel(FieldDeclaration field);
}
