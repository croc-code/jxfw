package ru.croc.ctp.jxfw.jpa.exception;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Описание исключения уровня БД.
 */
public interface DbExceptionDescriptor {

    /**
     * Имя констрейнта, вызвавшего исключение.
     *
     * @return имя
     */
    @Nullable
    String getConstraint();

    /**
     * Имя таблицы, на которой произошло исключение.
     *
     * @return имя
     */
    @Nullable
    String getTableName();

    /**
     * Имена столбцов, на которых произошло исключение.
     *
     * @return map
     */
    List<String> getColumnNames();

    /**
     * Детальная информация из исключения уровня БД.
     *
     * @return Детальная информация из исключения уровня БД.
     */
    String getDetails();

    /**
     * Исхоное исключение уровня БД.
     *
     * @return исключение
     */
    Throwable getCause();


    /**
     * Вид нарушенного ограничения.
     *
     * @return XfwViolationType
     */

    XfwViolationType getViolationType();


}
