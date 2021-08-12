package ru.croc.ctp.jxfw.core.util;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId;
import ru.croc.ctp.jxfw.core.domain.impl.EnumUtil;

/**
 * Тест методов {@link EnumUtil}.
 * 
 * @author Dmitry Malenok
 */
public class TestCmfEnumUtil {

    /**
     * Тестовый enum 1.
     */
    private enum TestEnum1 {
        /**
         * Значение 0.
         */
        @XFWEnumId(0) VALUE_0,

        /**
         * Значение 2.
         */
        @XFWEnumId(2) VALUE_2,

        /**
         * Значение без ID.
         */
        VALUE_NO_ID
    }

    /**
     * Ожидаемое исключение.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test method for {@link EnumUtil#valueOf(Class, int)}.
     */
    @Test
    public void testValueOf() throws Exception {
        assertThat(EnumUtil.valueOf(TestEnum1.class, 0), equalTo(TestEnum1.VALUE_0));
        assertThat(EnumUtil.valueOf(TestEnum1.class, 2), equalTo(TestEnum1.VALUE_2));
    }

    /**
     * Test method for {@link EnumUtil#valueOf(Class, int)}.
     * <p/>
     * Тестирование ситуации, когда значения нет.
     */
    @Test
    public void testValueOfInvalidValue() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(both(containsString(TestEnum1.class.getName())).and(containsString("1")));
        EnumUtil.valueOf(TestEnum1.class, 1);
    }

    @Test
    public void testGetEnumValue() throws Exception {
        assertThat(EnumUtil.getEnumValue(TestEnum1.VALUE_0), equalTo(0));
    }

    @Test
    public void testGetEnumValueFail() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(containsString("Enum VALUE_NO_ID not contains annotation XFWEnumId"));
        EnumUtil.getEnumValue(TestEnum1.VALUE_NO_ID);
    }
}