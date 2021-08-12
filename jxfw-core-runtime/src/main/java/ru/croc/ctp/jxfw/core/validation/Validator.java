package ru.croc.ctp.jxfw.core.validation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * ConstraintValidators по дефолту создаются через reflection.
 * Это затрудняет создание валидаторов в которых описана логика
 * взаимодействия с внешними сервисами - решение использовать более 
 * расширяемый подгружать валидаторы использую Spring.
 *
 * @author SMufazzalov
 * @since 1.1
 * @deprecated 1.6 в XfwCoreConfig добавлен бин LocalValidatorFactoryBean
 */
@Deprecated
@Component
public class Validator implements org.springframework.validation.Validator,
        InitializingBean, ApplicationContextAware, ConstraintValidatorFactory {

    private javax.validation.Validator validator;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .constraintValidatorFactory(this).buildValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {

        @SuppressWarnings("rawtypes")
        Map beansByNames = applicationContext.getBeansOfType(key);
        if (beansByNames.isEmpty()) {
            try {
                return key.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Could not instantiate constraint validator class '" 
                        + key.getName() + "'", e);
            }
        }
        if (beansByNames.size() > 1) {
            throw new RuntimeException("Only one bean of type '" 
                    + key.getName() + "' is allowed in the application context");
        }

        Object next = beansByNames.values().iterator().next();

        return (T) next;
    }


    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
    }

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target);
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }
    }

    /**
     * Провалидировать target объект.
     *
     * @param target - объект который валидируем
     * @param errors - ошибки которое возникли во время валидации
     * @param groups - группы валидаторов по которым производиться валидация
     */
    public void validate(Object target, Errors errors, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target, groups);
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }
    }
}