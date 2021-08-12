package ru.croc.ctp.jxfw.core.exception.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Базовая функциональность по сериализация потомков XExceptionTO.
 * @deprecated  since 1.6 ExceptionToSerializer used instead
 */
@Deprecated
public class XExceptionToSerializer extends StdSerializer<XExceptionTo> {

    private final static Logger logger = LoggerFactory.getLogger(XExceptionToSerializer.class);


    public XExceptionToSerializer() {
        super(XExceptionTo.class);
    }


    @Override
    public void serialize(XExceptionTo to, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.setCodec(new ObjectMapper());
        jgen.writeStartObject();
        jgen.writeBooleanField("$isException", true);
        jgen.writeStringField("$className", to.getClassName());

        Class<?> exClass = to.getClass();
        List<Field> fields = new ArrayList<>();

        getAllFields(fields, exClass).forEach(field -> {
                    try {
                        field.setAccessible(true);
                        jgen.writeObjectField(field.getName(), field.get(to));
                    } catch (IllegalAccessException | IOException ex) {
                        logger.error("{}", ex);
                    }
                }
        );

        jgen.writeEndObject();

    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null && type.getSuperclass() != Object.class) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }


}
