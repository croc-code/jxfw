package ru.croc.ctp.jxfw.core.facade.webclient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QTuple;

/**
 * Сериализатор в Json для Tuple.
 * Сериализаует в виде "имя поля" : "значение поля".
 */
public class TupleSerializer extends StdSerializer<Tuple> {

    /**
     * Конструтор.
     */
    public TupleSerializer() {
        super(Tuple.class);
    }

    @Override
    public void serialize(Tuple tuple, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {

        jgen.setCodec(new ObjectMapper());
        jgen.writeStartObject();
        try {
            Field field = tuple.getClass().getDeclaredField("this$0");
            field.setAccessible(true);
            QTuple qtuple = (QTuple) field.get(tuple);
            List<Expression<?>> metadata = qtuple.getArgs();
            for (int i = 0; i < metadata.size(); i++) {
                jgen.writeFieldName(resolveFieldName(metadata.get(i)));
                jgen.writeObject(tuple.get(i, Object.class));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        jgen.writeEndObject();

    }

    private String resolveFieldName(Expression<?> exp) {
        Expression<?> expression = (exp instanceof Operation && ((Operation<?>) exp).getOperator() == Ops.ALIAS)
                ? ((Operation<?>) exp).getArg(1) : exp;

        return expression instanceof Path ? ((Path<?>) expression).getMetadata().getName() : expression.toString();
    }
}
