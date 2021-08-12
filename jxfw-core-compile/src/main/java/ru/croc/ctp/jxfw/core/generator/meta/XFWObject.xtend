package ru.croc.ctp.jxfw.core.generator.meta

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.core.generator.impl.XFWObjectProcessor

/**
 * Объект, сохраняемый в хранилище.
 * Автоматически генерируются Spring Data репозиторий, сервисы и контроллер.
 * А также генерируются JavaScript модель и i18n-ресурсы.
 */
@Target(ElementType.TYPE)
@Active(XFWObjectProcessor)
@Retention(RetentionPolicy.SOURCE)
annotation XFWObject {
    boolean isReadonly = false

    PersistenceType persistence = PersistenceType.FULL

    /**
      * FULL - объекты типа свойств загружаются и сохраняются в хранилище.
      * TRANSIENT - объекты типа свойств не загружаются и не сохраняются в хранилище. 
      *             Также в хранилище не создаются соответствующие структуры хранения (таблицы и столбцы в БД).
      */
    enum PersistenceType {
        /**
         * объекты типа свойств загружаются и сохраняются в хранилище.
         */
        FULL,
        /**
         * объекты типа свойств не загружаются и не сохраняются в хранилище. 
         * Также в хранилище не создаются соответствующие структуры хранения (таблицы и столбцы в БД).
         */
        TRANSIENT
    }

    /**
    * Указывает нужно ли устанавливать на фасаде признак временного объекта.
    * Внимание: использует только с persistence={@link TRANSIENT}, при попытки установить для FULL
    * произойдет ошибка компиляции.
    *
    * @return нужно ли устанавливать на фасаде признак временного объекта.
    */
    boolean temp = true;

    /**
     * Определяет, должно ли для доменного объекта сохраняться состояние при операциях чтения и записи,
     * для возможности получить состояние объекта до изменений.
     */
    boolean saveState = false

}
