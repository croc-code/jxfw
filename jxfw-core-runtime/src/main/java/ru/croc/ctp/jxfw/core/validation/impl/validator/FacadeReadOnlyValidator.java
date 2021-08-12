package ru.croc.ctp.jxfw.core.validation.impl.validator;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWFacadeReadOnlyCheck;

/**
 * Валидатор read-only для фасадной части.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
@Component
public class FacadeReadOnlyValidator extends BaseReadOnlyValidator<XFWFacadeReadOnlyCheck> {

    @Override
    protected boolean isFacade() {
        return true;
    }
}
