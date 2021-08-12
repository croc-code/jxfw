package ru.croc.ctp.jxfw.core.validation.impl.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.croc.ctp.jxfw.core.domain.DomainObject;

import javax.validation.ConstraintValidatorContext;

public class NewRemoveValidatorTest {

    private NewRemoveValidator validator;

    @Mock
    private DomainObject obj;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintBuilder;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        validator = new NewRemoveValidator();
        Mockito.when(context.buildConstraintViolationWithTemplate("{ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.newremove.message}"))
                .thenReturn(constraintBuilder);
    }

    @Test
    public void isValidTest() {
        Mockito.when(obj.isNew()).thenReturn(false);
        Mockito.when(obj.isRemoved()).thenReturn(false);
        Assert.assertTrue(validator.isValid(obj, context));

        Mockito.when(obj.isNew()).thenReturn(true);
        Mockito.when(obj.isRemoved()).thenReturn(false);
        Assert.assertTrue(validator.isValid(obj, context));

        Mockito.when(obj.isNew()).thenReturn(false);
        Mockito.when(obj.isRemoved()).thenReturn(true);
        Assert.assertTrue(validator.isValid(obj, context));

        Mockito.when(obj.isNew()).thenReturn(true);
        Mockito.when(obj.isRemoved()).thenReturn(true);
        Assert.assertFalse(validator.isValid(obj, context));
    }
}
