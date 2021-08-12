package ru.croc.ctp.jxfw.jpa.exception.impl;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.exception.exceptions.XIntegrityViolationException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XReferenceIntegrityViolationException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XUniqueViolationException;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionBuilderSupport;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionDescriptor;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwNamedElement;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Fallback - имплементация. Обрабатывает все, что
 * не обработали прикладные ExceptionBuilder-ы. @Order - низший. Строит сообщение по шаблону из бандла ресурсов.
 *
 * @author OKrutova
 * @since 1.6
 */
@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class ExceptionBuilderImpl extends ExceptionBuilderSupport {

    @Override
    @Nullable
    public XIntegrityViolationException buildCheckViolationException(@Nonnull ExceptionDescriptor exceptionDescriptor) {

        XIntegrityViolationException.Builder builder = new XIntegrityViolationException.Builder<>(
                "ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwConstraint.check",
                "Check constraint {0} violated on object {1}: {2}")
                .addArgument(exceptionDescriptor.getConstraint())
                .addArgument(tablePlaceholder(exceptionDescriptor.getTableName(), exceptionDescriptor
                        .getXfwClassOptional()))
                .addArgument(exceptionDescriptor.getDetails())
                .cause(exceptionDescriptor.getCause());

        return builder.build();
    }


    @Override
    @Nullable
    public XIntegrityViolationException buildNotNullException(@Nonnull ExceptionDescriptor exceptionDescriptor) {
        if (exceptionDescriptor.getColumns().size() == 1) {
            XIntegrityViolationException.Builder builder = new XIntegrityViolationException.Builder<>(
                    "ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwConstraint.notnull",
                    "Required field {0} is empty of object {1}: {2}")
                    .addArgument(columnPlaceholder(exceptionDescriptor.getColumns().keySet().iterator().next(),
                            exceptionDescriptor.getColumns().values().iterator().next()))
                    .addArgument(tablePlaceholder(exceptionDescriptor.getTableName(), exceptionDescriptor
                            .getXfwClassOptional()))

                    .addArgument(exceptionDescriptor.getDetails())
                    .addViolations(exceptionDescriptor.getDomainViolations())
                    .cause(exceptionDescriptor.getCause());

            return builder.build();
        } else {
            return null;
        }
    }


    @Override
    @Nullable
    public XIntegrityViolationException buildUniqueException(@Nonnull ExceptionDescriptor exceptionDescriptor) {

        String bundleCode = "ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwConstraint.unique";
        String defaultMessage = "Unique constraint violated on field {0} of object {1}: {2}";

        if (exceptionDescriptor.getColumns().size() > 1) {
            bundleCode = "ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwConstraint.unique.many";
            defaultMessage = "Unique constraint violated on fields {0} of object {1}: {2}";
        }


        XUniqueViolationException.Builder builder = new XUniqueViolationException.Builder<>(
                bundleCode,
                defaultMessage)
                .addArgument(exceptionDescriptor.getColumns().entrySet().stream()
                        .map(entry -> columnPlaceholder(entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(","))
                )
                .addArgument(tablePlaceholder(exceptionDescriptor.getTableName(), exceptionDescriptor
                        .getXfwClassOptional()))
                .addArgument(exceptionDescriptor.getDetails())
                .addViolations(exceptionDescriptor.getDomainViolations())
                .cause(exceptionDescriptor.getCause())
                .entityTypeName(exceptionDescriptor
                        .getXfwClassOptional()
                        .map(XfwNamedElement::getName)
                        .orElse(""))
                .properties(exceptionDescriptor.getColumns().values().stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(XfwNamedElement::getName)
                        .collect(Collectors.toSet()));

        return builder.build();
    }

    @Override
    @Nullable
    public XReferenceIntegrityViolationException buildFkException(@Nonnull ExceptionDescriptor exceptionDescriptor) {

        XReferenceIntegrityViolationException.Builder builder = new XReferenceIntegrityViolationException.Builder<>(
                "ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwConstraint.fk",
                "Reference constraint violated on fields {0} of object {1}: {2}")
                .addArgument(exceptionDescriptor.getColumns().entrySet().stream()
                        .map(entry -> columnPlaceholder(entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(",")))
                .addArgument(tablePlaceholder(exceptionDescriptor.getTableName(), exceptionDescriptor
                        .getXfwClassOptional()))
                .addArgument(exceptionDescriptor.getDetails())
                .cause(exceptionDescriptor.getCause())
                .entityTypeName(exceptionDescriptor.getXfwClassOptional().map(XfwClass::getName)
                        .orElse(exceptionDescriptor.getTableName()))
                .navigationPropName(exceptionDescriptor.getColumns().entrySet().stream()
                        .map(entry -> entry.getValue().map(XfwStructuralFeature::getName).orElse(entry.getKey()))
                        .collect(Collectors.joining(",")));

        return builder.build();
    }


}
