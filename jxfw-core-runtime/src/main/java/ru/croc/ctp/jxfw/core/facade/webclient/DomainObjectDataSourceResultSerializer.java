package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ru.croc.ctp.jxfw.core.datasource.DomainObjectDataSourceResult;

import java.io.IOException;

/**
 * Сериализатор Jackson для объектов {@link DomainObjectDataSourceResult}.
 * 
 * @author Nosov Alexnader
 * @deprecated since 1.6
 */
@Deprecated
public class DomainObjectDataSourceResultSerializer extends StdSerializer<DomainObjectDataSourceResult> {

    private static final long serialVersionUID = -3480833036194912495L;

    /**
     * Конструктор.
     */
    public DomainObjectDataSourceResultSerializer() {
        super(DomainObjectDataSourceResult.class);
    }

    @Override
    public void serialize(DomainObjectDataSourceResult value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.setCodec(new ObjectMapper());
        jgen.writeStartArray();
        jgen.writeEndArray();
    }
}
