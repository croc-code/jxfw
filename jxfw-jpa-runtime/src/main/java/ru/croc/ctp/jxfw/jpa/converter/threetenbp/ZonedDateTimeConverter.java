package ru.croc.ctp.jxfw.jpa.converter.threetenbp;

import static org.threeten.bp.DateTimeUtils.toLocalDateTime;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.sql.Timestamp;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * ZonedDateTimeConverter для трансформации даты с таймзоной в БД и обратно.
 * Для threeten backport java 7
 *
 * @author Nosov Alexander
 * @since 1.2
 */
@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime entityValue) {
        return entityValue == null ? null : DateTimeUtils.toSqlTimestamp(entityValue.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp databaseValue) {
        return databaseValue == null ? null : toLocalDateTime(databaseValue).atZone(ZoneId.systemDefault());
    }
}
