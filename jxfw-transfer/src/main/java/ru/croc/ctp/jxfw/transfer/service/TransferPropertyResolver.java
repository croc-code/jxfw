package ru.croc.ctp.jxfw.transfer.service;

import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Интерфейс для установки значений (имени свойства и значения) в ТО объект.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@Service
public interface TransferPropertyResolver {

    /**
     * Добавить типизированное значение свойства в ТО объект.
     *
     * @param dto   ТО объект
     * @param key   ключ, под которым будет храниться значение свойства
     * @param type  тип свойства
     * @param value значение свойства
     */
    void addTypedValue(@Nonnull DomainTo dto, String key, String type, @Nullable Object value);

    /**
     * Добавить список значений в ТО объект.
     *
     * @param dto  ТО объект
     * @param key  ключ, под которым будет храниться значение свойства
     * @param list список значение для установки
     */
    void addListValue(@Nonnull DomainTo dto, String key, List<?> list);
}
