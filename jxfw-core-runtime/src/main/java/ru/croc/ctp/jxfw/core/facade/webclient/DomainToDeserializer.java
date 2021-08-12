package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Десереиализатор для {@link DomainTo} объектов из JSON.
 * 
 * @since 1.0
 */
public class DomainToDeserializer extends JsonDeserializer<DomainTo> {

    @Override
    public DomainTo deserialize(JsonParser jp, DeserializationContext ctx)
            throws IOException {
        DomainTo vo = new DomainTo();

        jp.nextToken();
        while (!jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            String fn = jp.getText();
            switch (fn) {
                case "__metadata":
                    jp.nextToken();
                    decodeMetadata(jp, vo);
                    break;
                case "__original":
                    jp.nextToken();
                    decodeOriginal(jp, vo);
                    break;
                case "id":
                    vo.setId(jp.nextTextValue());
                    break;
                default:
                    jp.nextToken();
                    Object value = jp.readValueAs(Object.class);
                    vo.addProperty(fn, value);
                    break;
            }
            jp.nextToken();
        }

        return vo;
    }

    private void decodeMetadata(JsonParser jp, DomainTo vo)
            throws IOException {
        jp.nextToken();
        while (!jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            String fn = jp.getText();
            switch (fn) {
                case "type":
                    vo.setType(jp.nextTextValue());
                    break;
                case "isNew":
                    vo.setNew(jp.nextBooleanValue());
                    break;
                case "isRemoved":
                    vo.setRemoved(jp.nextBooleanValue());
                    break;
                case "ts":
                    vo.setTs(jp.nextLongValue(-1L));
                    break;
                default:
                    break;
            }
            jp.nextToken();
        }
    }

    private void decodeOriginal(JsonParser jp, DomainTo vo)
            throws IOException {
        jp.nextToken();
        while (!jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            String fn = jp.getText();
            JsonToken value = jp.nextValue();
            if (value.equals(JsonToken.START_ARRAY)) {
                final ArrayList<Object> list = Lists.newArrayList();
                while (!jp.nextToken().equals(JsonToken.END_ARRAY)) {
                    list.add(jp.getText());
                }
                vo.addOriginal(fn, list);
            } else if (value.equals(JsonToken.VALUE_STRING)) {
                vo.addOriginal(fn, jp.getText());
            } else if (value.equals(JsonToken.VALUE_NUMBER_INT)) {
                final int v = Integer.parseInt(jp.getText());
                vo.addOriginal(fn, v);
            } else if (value.equals(JsonToken.VALUE_NULL)) {
                vo.addOriginal(fn, null);
            }
            jp.nextToken();
        }
    }
}
