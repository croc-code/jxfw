package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Сериализатор {@link DomainTo} в объект JSON.
 *
 * @since 1.0
 */
public class DomainToSerializer extends JsonSerializer<DomainTo> {

    private static final BigInteger MAX_SAFE_INTEGER = BigInteger.valueOf(2).pow(53).subtract(BigInteger.ONE);

    @Override
    public void serialize(DomainTo vo, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeFieldName("__metadata");
        jgen.writeStartObject();
        jgen.writeStringField("type", vo.getType());
        jgen.writeNumberField("ts", vo.getTs());
        jgen.writeEndObject();
        jgen.writeStringField("id", vo.getId());
        vo.forEachProperty((pn, pv) -> {
            try {
                Object value = pv;
                /*
                 * JXFW-661 Числа вне диапазона [Number.MIN_SAFE_INTEGER,Number.MAX_SAFE_INTEGER]
                 * [-(2^53 - 1) ,2^53 - 1] шлем на клиент строкой
                 */
                if (pv instanceof Long) {
                    pv = BigInteger.valueOf((Long) pv);
                }
                if (pv instanceof BigInteger) {
                    BigInteger bigInteger = ((BigInteger) pv).abs();
                    if (bigInteger.compareTo(MAX_SAFE_INTEGER) > 0) {
                        value = pv.toString();
                    }
                }
                /*
                 * JXFW-661 проверяем, теряется ли точность при переводе в double
                 * если теряется, то шлем строкой, если нет, то double
                 */
                if (pv instanceof BigDecimal) {
                    double doubleValue = ((BigDecimal) pv).doubleValue();
                    if (pv.equals(new BigDecimal(doubleValue))) {
                        value = doubleValue;
                    } else {
                        value = pv.toString();
                    }
                }
                jgen.writeObjectField(pn, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        jgen.writeEndObject();
    }
}
