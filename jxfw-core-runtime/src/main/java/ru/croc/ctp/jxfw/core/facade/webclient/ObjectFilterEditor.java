package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.HashMap;

/**
 * Реализация {@link PropertyEditorSupport} c переопределнием метода {@code setAsText(String text)}.
 */
public class ObjectFilterEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        try {
            ObjectFilter of = new ObjectFilter(mapper.readValue(text, typeRef));
            setValue(of);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
