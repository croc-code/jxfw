package ru.croc.ctp.jxfw.core.reporting.facade.webclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Сериализует коллекцию в формат, нужный веб-клиенту.
 * @author OKrutova
 * @since 1.6
 */
public class WcSpecificSerializer extends StdSerializer<CollectionWithWcSpecificSerialization> {

    /**
     * 
     */
    private static final long serialVersionUID = 2946116085797537012L;

    /**
     * Конструктор.
     */
    public WcSpecificSerializer() {
        super(CollectionWithWcSpecificSerialization.class);
    }

    /*
    { "survey": { "name": "Survey", "title": "Голосование" },
    "surveys": { "name": "Surveys", "title": "Список голосований" } }

   { "html": { "name": "HTML", "format": "HTML", "mime": "text/html" },
   "pdf": { "name": "PDF", "format": "PDF", "mime": "application/pdf" },
   "word": { "name": "WORD", "format": "WORD", "mime": "application/msword" },
   "xps": { "name": "XPS", "format": "XPS", "mime": "application/vnd.ms-xpsdocument" },
   "excel": { "name": "EXCEL", "format": "EXCEL", "mime": "application/vnd.ms-excel" },
   "xsl-fo": { "name": "XSL-FO", "format": "XSL-FO", "mime": "text/xml" } }
     */

    @Override
    public void serialize(CollectionWithWcSpecificSerialization collection,
                          JsonGenerator gen,
                          SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        for (ItemWithWcSpecificSerialization item : collection.getCollection()) {
            gen.writeObjectField(item.getName().toLowerCase(), item);
        }

        gen.writeEndObject();
    }
}
