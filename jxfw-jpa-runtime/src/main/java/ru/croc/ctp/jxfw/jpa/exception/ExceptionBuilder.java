package ru.croc.ctp.jxfw.jpa.exception;

import ru.croc.ctp.jxfw.core.exception.exceptions.XIntegrityViolationException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XReferenceIntegrityViolationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис построения исключения в формате jXFW по данным об исключении на уровне БД.
 */
public interface ExceptionBuilder {


    /**
     * Конструирует исключение нарушения констрейнта CHECK.
     *
     * @param exceptionDescriptor описание исключения
     * @return XIntegrityViolationException или null, если данный сервис не обрабатывает данное исключение.
     */
    @Nullable
    XIntegrityViolationException buildCheckViolationException(@Nonnull ExceptionDescriptor exceptionDescriptor);

    /**
     * Конструирует исключение нарушения констрейнта NOTNULL.
     *
     * @param exceptionDescriptor описание исключения
     * @return XIntegrityViolationException или null, если данный сервис не обрабатывает данное исключение.
     */
    @Nullable
    XIntegrityViolationException buildNotNullException(@Nonnull ExceptionDescriptor exceptionDescriptor);

    /**
     * Конструирует исключение нарушения констрейнта UNIQUE.
     *
     * @param exceptionDescriptor описание исключения
     * @return XIntegrityViolationException или null, если данный сервис не обрабатывает данное исключение.
     */
    @Nullable
    XIntegrityViolationException buildUniqueException(@Nonnull ExceptionDescriptor exceptionDescriptor);

    /**
     * Конструирует исключение нарушения констрейнта FK.
     *
     * @param exceptionDescriptor описание исключения
     * @return XReferenceIntegrityViolationException или null, если данный сервис не обрабатывает данное исключение.
     */
    @Nullable
    XReferenceIntegrityViolationException buildFkException(@Nonnull ExceptionDescriptor exceptionDescriptor);


}
