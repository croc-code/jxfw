package ru.croc.ctp.jxfw.jpa.exception;

import static com.google.common.collect.Sets.newHashSet;

import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolation;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Описание исключения уровня БД, расширенное метаданными и {@link StoreContext}.
 */
public interface ExceptionDescriptor extends DbExceptionDescriptor {

    /**
     * Метаданные класса, соответствующие таблице, на которой произошло исключение.
     *
     * @return метаданные если они найдены.
     */
    Optional<XfwClass> getXfwClassOptional();

    /**
     * Map:  имя столбца, на котором нарушение, -
     * метаданные поля, на котором произошло нарушение, если удалось их найти.
     *
     * @return map
     */
    Map<String, Optional<XfwStructuralFeature>> getColumns();


    /**
     * Контекст сохранения, в котором произошло исключение.
     *
     * @return контекст сохранения. До решения JXFW-1415 в нем только локаль.
     */
    @Nonnull
    StoreContext getStoreContext();


    /**
     * Набор описаний исключений, содержащий идентификаторов доменных объектов и полей,
     * на которых потенциально могло произойти исключение.
     *
     * @return Набор {@link DomainViolation}
     */
    @Nonnull
    default Set<DomainViolation> getDomainViolations() {
        return newHashSet();
    }
}
