package ru.croc.ctp.jxfw.core.facade.webclient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис разбора значений из транспортного формата в целевой тип доменного объекта.
 *
 * @author OKrutova
 * @since 1.6.2
 */
public interface ValueParser {

    /**
     * Разобрать значение в тип LocalDate.
     *
     * @param value значение
     * @return величина в LocalDate
     */
    @Nullable
    LocalDate parseLocalDate(@Nullable Object value);

    /**
     * Разобрать значение в тип LocalDateTime.
     *
     * @param value значение
     * @return величина в LocalDateTime
     */
    @Nullable
    LocalDateTime parseLocalDateTime(@Nullable Object value);


    /**
     * Разобрать значение в тип ZonedDateTime.
     *
     * @param value значение
     * @return величина в ZonedDateTime
     */
    @Nullable
    ZonedDateTime parseZonedDateTime(@Nullable Object value);

    /**
     * Разобрать значение в тип LocalTime.
     *
     * @param value значение
     * @return величина в LocalTime
     */
    @Nullable
    LocalTime parseLocalTime(@Nullable Object value);


    /**
     * Разобрать значение в заданный тип.
     *
     * @param value        значение
     * @param clazz        требуемый в результате тип
     * @param stringParser функция преобразования строки в требуемый тип
     * @param <T>          требуемый тип
     * @return величина, преобразованная в требуемый тип
     */
    @Nullable
    <T> T parse(@Nullable Object value, @Nonnull Class<T> clazz, @Nonnull Function<String, T> stringParser);
}
