package ru.croc.ctp.jxfw.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public class Login2fResponseSerializer extends StdSerializer<Login2fResponse> {

    /**
     * Конструктор.
     */
    public Login2fResponseSerializer() {
        this(null);
    }

    /**
     * Конструктор.

     * @param clz класс
     */
    public Login2fResponseSerializer(Class<Login2fResponse> clz) {
        super(clz);
    }

    @Override
    public void serialize(Login2fResponse value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("2f");
        {
            gen.writeStartObject();
            gen.writeStringField(Login2fResponse.TOKEN, value.getToken());
            gen.writeNumberField(Login2fResponse.TIMEOUT, value.getTimeout());
            gen.writeNumberField(Login2fResponse.LENGTH, value.getLength());
            gen.writeStringField(Login2fResponse.TYPE, value.getType().getValue());
            gen.writeStringField(Login2fResponse.HINT, value.getHint());
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }
}
