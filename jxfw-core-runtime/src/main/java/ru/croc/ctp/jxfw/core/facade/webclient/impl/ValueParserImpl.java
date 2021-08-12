package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.facade.webclient.ValueParser;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Базовая реализация сервиса разбора значений из транспортного формата в целевой тип доменного объекта.
 */
@Service
public class ValueParserImpl implements ValueParser {

    @Override
    public LocalDate parseLocalDate(Object value) {
        return parse(value, LocalDate.class, v -> LocalDateTime.parse(v).toLocalDate());

    }

    @Override
    public LocalDateTime parseLocalDateTime(Object value) {
        return parse(value, LocalDateTime.class, LocalDateTime::parse);
    }

    @Override
    public ZonedDateTime parseZonedDateTime(Object value) {
        return parse(value, ZonedDateTime.class, ZonedDateTime::parse);
    }

    @Nullable
    @Override
    public LocalTime parseLocalTime(@Nullable Object value) {
        return parse(value, LocalTime.class, v -> LocalDateTime.parse(v).toLocalTime());
    }

    @Override
    public <T> T parse(Object value, @Nonnull Class<T> clazz, @Nonnull Function<String, T> stringParser) {
        if (value == null || clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        }
        if (value instanceof String) {
            return stringParser.apply((String) value);
        }
        throw new IllegalArgumentException(MessageFormat.format("Value of type {0} cannot be converted to {1}",
                value.getClass().getName(), clazz.getName()));

    }
}
