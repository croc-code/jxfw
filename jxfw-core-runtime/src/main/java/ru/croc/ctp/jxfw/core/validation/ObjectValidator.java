package ru.croc.ctp.jxfw.core.validation;

import static ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate.formatPropertyNamePlaceholder;
import static ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate.formatTypeNamePlaceholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWReadOnlyCheck;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис валидации объекта.
 * Валидирует стандартным валидатором, строит сообщение об ошибках
 * с использованием метамодели.
 *
 * @since 1.6
 * @author OKrutova
 */
@Service
public class ObjectValidator {

    private final Logger log = LoggerFactory.getLogger(ObjectValidator.class);
    private final SmartValidator validator;

    /**
     * Конструктор.
     * @param validator стандартный вадидатор.
     */
    public ObjectValidator(SmartValidator validator) {
        this.validator = validator;
    }

    /**
     * Провалидировать объект.
     * @param object объект
     * @param typeName имя типа объекта
     * @throws XInvalidDataException если валидация неуспешна
     */
    public void validateAndThrow(Object object, String typeName, Class<?>... validationGroups) {
        final BindException errors = new BindException(object, typeName);
        validator.validate(object, errors, (Object[]) validationGroups);

        if (errors.hasErrors()) {
            ErrorStringBuilder builder = new ErrorStringBuilder(object, typeName);
            for (ObjectError error : errors.getAllErrors()) {
                if (XFWReadOnlyCheck.class.getSimpleName().equals(error.getCode())) {
                    DomainObject<?> domain = (DomainObject<?>) object;
                    throw new XInvalidDataException.Builder<>(error.getDefaultMessage(),
                            "Validation failed: {0}").identity(domain).build();
                }

                builder.addError(error);
            }
            log.error("invalid value for: " + builder.buildForLog());

            if (builder.isServerOnly()) {
                throwInternalServerError(builder.buildForException());
            } else {
                throwBadRequest(builder.buildForException());
            }
        }
    }

    private void throwInternalServerError(String message) {
        throw new XException.Builder<>(
                "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.validation.message",
                "Validation failed: {0}")
                .addArgument(message)
                .build();
    }

    private void throwBadRequest(String message) {
        throw new XInvalidDataException.Builder<>(
                "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.validation.message",
                "Validation failed: {0}")
                .addArgument(message)
                .build();
    }

    /**
     * Билдер для строк об ошибках для лога и для исключения
     */
    private static class ErrorStringBuilder {
        private final Object object;
        private final String typeName;
        private final boolean isServerOnlyObject;
        private final List<String> objectErrors = new ArrayList<>();
        private final List<FieldError> fieldErrors = new ArrayList<>();

        public ErrorStringBuilder(Object object, String typeName) {
            this.object = object;
            this.typeName = typeName;
            isServerOnlyObject = (object instanceof DomainObject) && ((DomainObject) object).getMetadata()
                    .getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) != null;
        }

        public ErrorStringBuilder addError(ObjectError error) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                return addError(fieldError);
            } else {
                objectErrors.add(error.getDefaultMessage() + ", ");
                return this;
            }
        }

        public ErrorStringBuilder addError(FieldError fieldError) {
            fieldErrors.add(fieldError);
            return this;
        }

        public boolean isServerOnly() {
            return isServerOnlyObject || fieldErrors.stream().anyMatch(serverOnlyField());
        }

        public String buildForLog() {
            return "'" + formatTypeNamePlaceholder(typeName) + "': "
                    + String.join("", objectErrors)
                    + fieldErrors.stream()
                        .map(error -> getErrorMessage(error, true))
                        .collect(Collectors.joining());
        }

        public String buildForException() {
            String typeName = isServerOnlyObject ? "<internal server object>" : formatTypeNamePlaceholder(this.typeName);
            return "'" + typeName + "': "
                    + (!isServerOnlyObject ? String.join("", objectErrors) : "")
                    + fieldErrors.stream()
                        .filter(serverOnlyField())
                        .map(error -> getErrorMessage(error, false))
                        .collect(Collectors.joining())
                    + fieldErrors.stream()
                        .filter(serverOnlyField().negate())
                        .map(error -> getErrorMessage(error, true))
                        .collect(Collectors.joining());
        }

        private Predicate<FieldError> serverOnlyField() {
            return err -> (object instanceof DomainObject) && ((DomainObject) object).getMetadata()
                    .getEStructuralFeature(err.getField())
                    .getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) != null;
        }

        private String getErrorMessage(FieldError error, boolean withFieldName) {
            if (withFieldName) {
                return formatPropertyNamePlaceholder(typeName, error.getField())
                        + "(" + error.getRejectedValue() + ") "
                        + error.getDefaultMessage() + ", ";
            } else {
                return error.getDefaultMessage() + ", ";
            }
        }
    }
}
