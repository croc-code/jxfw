package ru.croc.ctp.jxfw.core.facade.webclient;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.ValueParserImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ValueParserTest {


    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private ValueParser valueParser = new ValueParserImpl();

    @Test
    public void testLocalDate() {
        assertNull(valueParser.parseLocalDate(null));
        LocalDate localDate = LocalDate.now();
        assertEquals(valueParser.parseLocalDate(localDate), localDate);
        //разбор из строки заточен под формат, приходящий из вебклиента,
        //а это формат со времененем.
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(0, 0, 0));
        assertEquals(valueParser.parseLocalDate(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                localDate);
        exception.expect(IllegalArgumentException.class);
        valueParser.parseLocalDate(Integer.MAX_VALUE);
    }

    @Test
    public void testLocalDateTime() {
        assertNull(valueParser.parseLocalDateTime(null));
        LocalDateTime localDateTime = LocalDateTime.now();
        assertEquals(valueParser.parseLocalDateTime(localDateTime), localDateTime);
        assertEquals(valueParser.parseLocalDateTime(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                localDateTime);
        exception.expect(IllegalArgumentException.class);
        valueParser.parseLocalDateTime(Integer.MAX_VALUE);
    }
    
    @Test
    public void testZonedDateTime() {
        assertNull(valueParser.parseZonedDateTime(null));
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        assertEquals(valueParser.parseZonedDateTime(zonedDateTime), zonedDateTime);
        assertEquals(valueParser.parseZonedDateTime(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                zonedDateTime);
        exception.expect(IllegalArgumentException.class);
        valueParser.parseZonedDateTime(Integer.MAX_VALUE);
    }

    @Test
    public void testLocalTime() {
        assertNull(valueParser.parseLocalTime(null));
        LocalTime localTime = LocalTime.now();
        assertEquals(valueParser.parseLocalTime(localTime), localTime);
        //разбор из строки заточен под формат, приходящий из вебклиента,
        //а это формат со времененем.
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
        assertEquals(valueParser.parseLocalTime(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                localTime);
        exception.expect(IllegalArgumentException.class);
        valueParser.parseLocalTime(Integer.MAX_VALUE);
    }
}
