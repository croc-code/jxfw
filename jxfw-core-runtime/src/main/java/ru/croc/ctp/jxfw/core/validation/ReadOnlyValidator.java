package ru.croc.ctp.jxfw.core.validation;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWReadOnlyCheck;
import ru.croc.ctp.jxfw.core.validation.impl.validator.BaseReadOnlyValidator;

/**
 * Валидация для ReadOnly полей.
 * 
 * @see XFWReadOnlyCheck
 * @since 1.1
 */
@Component
public class ReadOnlyValidator extends BaseReadOnlyValidator<XFWReadOnlyCheck> {

    @Override
    protected boolean isFacade() {
        return false;
    }
}
