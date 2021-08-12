package ru.croc.ctp.jxfw.jpa.exception;

import ru.croc.ctp.jxfw.core.exception.exceptions.XIntegrityViolationException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XReferenceIntegrityViolationException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Базовая реализация, позволяющая в прикладном коде определять не все методы, а только те, которые нужно
 * в конкретном обработчике.
 */
public class ExceptionBuilderSupport implements ExceptionBuilder {

    @Override
    @Nullable
    public XIntegrityViolationException buildCheckViolationException(@Nonnull ExceptionDescriptor exceptionDescriptor) {
        return null;
    }

    @Override
    @Nullable
    public XIntegrityViolationException buildNotNullException(@Nonnull ExceptionDescriptor exceptionDescriptor) {
        return null;
    }

    @Override
    @Nullable
    public XIntegrityViolationException buildUniqueException(@Nonnull ExceptionDescriptor exceptionDescriptor) {
        return null;
    }

    @Override
    @Nullable
    public XReferenceIntegrityViolationException buildFkException(@Nonnull ExceptionDescriptor exceptionDescriptor) {
        return null;
    }

    /**
     * Построение плейсхолдера для доменного типа. Используется в XfwMessageTemplateResolver.
     *
     * @param tableName        имя таблицы в БД
     * @param xfwClassOptional метаданные класса, если они были найдены
     * @return плейсхолдер вида [ru.croc.ctp.jxfw.domain.BusinessObject]
     */
    protected String tablePlaceholder(String tableName, Optional<XfwClass> xfwClassOptional) {
        return xfwClassOptional
                .map(xfwClass ->
                        XfwMessageTemplate.formatTypeNamePlaceholder(xfwClass.getInstanceClassName()))
                .orElseGet(() -> tableName);
    }

    /**
     * Построение плейсхолдера для поля доменного типа. Используется в XfwMessageTemplateResolver.
     *
     * @param columnName      имя столбца в БД
     * @param featureOptional метаданные поля, если они были найдены.
     * @return плейсхолдер вида [ru.croc.ctp.jxfw.domain.BusinessObject#property]
     */
    protected String columnPlaceholder(String columnName, Optional<XfwStructuralFeature> featureOptional) {
        // в БД могут существовать столбцы, про которые доменная модель ничего не знает.
        return featureOptional
                .map(structuralFeature ->
                        XfwMessageTemplate.formatPropertyNamePlaceholder(
                                structuralFeature.getEContainingClass().getInstanceClassName(),
                                structuralFeature.getName()))
                .orElseGet(() -> columnName);

    }


}
