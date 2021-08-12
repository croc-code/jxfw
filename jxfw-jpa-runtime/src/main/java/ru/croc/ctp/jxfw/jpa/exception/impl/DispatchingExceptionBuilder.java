package ru.croc.ctp.jxfw.jpa.exception.impl;

import java8.util.function.Function;
import java8.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.exception.exceptions.XIntegrityViolationException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XReferenceIntegrityViolationException;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionBuilder;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionBuilderSupport;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionDescriptor;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Диспетчер ExceptionBuilder - ов. Пробегает по всем доступным в контексте
 * бинам этого типа с учетом @Order и отдает результат из первого ExceptionBuilder -а, который вернул не null.
 *
 * @author OKrutova
 * @since 1.6
 */
@Service
public class DispatchingExceptionBuilder extends ExceptionBuilderSupport {

    private final List<ExceptionBuilder> exceptionBuilders;

    /**
     * Конструтктор.
     *
     * @param exceptionBuilders упорядоченный список всех ExceptionBuilder, доступных в контексте.
     */
    @Autowired
    public DispatchingExceptionBuilder(List<ExceptionBuilder> exceptionBuilders) {
        this.exceptionBuilders = exceptionBuilders;
    }

    @Override
    @Nullable
    public XIntegrityViolationException buildCheckViolationException(@Nonnull ExceptionDescriptor exceptionDescriptor) {
        return dispatch(exceptionBuilder -> exceptionBuilder.buildCheckViolationException(exceptionDescriptor));
    }

    @Override
    @Nullable
    public XIntegrityViolationException buildNotNullException(@Nonnull ExceptionDescriptor exceptionDescriptor) {

        return dispatch(exceptionBuilder -> exceptionBuilder.buildNotNullException(exceptionDescriptor));
    }

    @Override
    @Nullable
    public XIntegrityViolationException buildUniqueException(@Nonnull ExceptionDescriptor exceptionDescriptor) {

        return dispatch(exceptionBuilder -> exceptionBuilder.buildUniqueException(exceptionDescriptor));
    }


    @Override
    @Nullable
    public XReferenceIntegrityViolationException buildFkException(@Nonnull ExceptionDescriptor exceptionDescriptor) {

        return dispatch(exceptionBuilder -> exceptionBuilder.buildFkException(exceptionDescriptor));
    }

    private <T extends XIntegrityViolationException> T dispatch(Function<ExceptionBuilder, T> mapper) {
        return StreamSupport.stream(exceptionBuilders)
                .filter(exceptionBuilder -> !(exceptionBuilder instanceof DispatchingExceptionBuilder))
                .map(mapper)
                .filter(ex -> ex != null)
                .findFirst()
                .orElse(null);
    }


}
