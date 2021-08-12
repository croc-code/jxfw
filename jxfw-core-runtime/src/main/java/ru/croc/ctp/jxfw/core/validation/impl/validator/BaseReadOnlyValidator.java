package ru.croc.ctp.jxfw.core.validation.impl.validator;

import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Базовая реализация read-only валидатора.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public abstract class BaseReadOnlyValidator<T extends Annotation> implements ConstraintValidator<T, DomainObject<?>> {

    @Autowired
    private DomainServicesResolver serviceResolver;

    @Override
    public void initialize(T t) {
    }

    /**
     * Возвращает признак валидатор для фасада или нет.
     *
     * @return {@code true} валидатор для фасада.
     */
    protected abstract boolean isFacade();

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(DomainObject<?> value, ConstraintValidatorContext context) {
        if (serviceResolver != null) {
            DomainService service = serviceResolver.resolveService(value);
            return service.validate(value, isFacade(), context);
        } else {
            return true;
        }
    }
}
