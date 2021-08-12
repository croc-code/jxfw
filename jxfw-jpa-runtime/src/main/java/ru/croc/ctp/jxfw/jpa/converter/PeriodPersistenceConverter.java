package ru.croc.ctp.jxfw.jpa.converter;

import java8.util.Objects;

import java.time.Period;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 *
 * @author Nosov Alexander
 *         on 14.10.15.
 */
@Converter(autoApply = true)
public class PeriodPersistenceConverter implements AttributeConverter<Period, String> {

    /**
     * @return an ISO-8601 representation of this duration.
     * @see Period#toString()
     */
    @Override
    public String convertToDatabaseColumn(Period entityValue) {
        return Objects.toString(entityValue, null);
    }

    @Override
    public Period convertToEntityAttribute(String databaseValue) {
        return Period.parse(databaseValue);
    }
}
