package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.croc.ctp.jxfw.core.datasource.GeneralDataSourceResult;

import java.io.IOException;

/**
 * Сериализатор Jackson для {@link GeneralDataSourceResult} в JSON.
 *
 * @author SMufazzalov
 * @since 1.5
 * @deprecated since 1.6
 */
@Deprecated
public class GeneralDataSourceResultSerializer extends StdSerializer<GeneralDataSourceResult> {


    /**
     * Конструктор.
     */
    public GeneralDataSourceResultSerializer() {
        super(GeneralDataSourceResult.class);
    }

    @Override
    public void serialize(GeneralDataSourceResult value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        for (Object obj : value.getData()) {
            if (obj == null) {
                jgen.writeNull();
            } else {
                jgen.writeString(obj.toString());
            }
        }
        jgen.writeEndArray();
    }
}
