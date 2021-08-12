package ru.croc.ctp.jxfw.ru.croc.ctp.jxfw.reporting.xslfo.impl.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class ConverterTest {

    @Test
    public void checkTypeNamesAreCorrect() {
        Converter converter = new Converter();

        String intStr = converter.toString(new Integer(56));
        assertTrue("56".equals(intStr));

        String bigDec = converter.toString(new BigDecimal(11));
        assertTrue("11".equals(bigDec));

        String dbl = converter.toString(new Double(2.5));
        assertTrue("2.5".equals(dbl));

        String calendar = converter.toString(Calendar.getInstance());
        assertTrue(StringUtils.isNotEmpty(calendar)); //строковое представление объекта оч. развесистое

        String bool = converter.toString(Boolean.TRUE);
        assertTrue("true".equals(bool));

        UUID from = UUID.randomUUID();
        String uuid = converter.toString(from);
        assertTrue(from.toString().equals(uuid));
    }
}
