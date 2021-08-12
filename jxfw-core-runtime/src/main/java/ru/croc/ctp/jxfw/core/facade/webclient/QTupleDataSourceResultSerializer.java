package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;

import ru.croc.ctp.jxfw.core.datasource.QTupleDataSourceResult;

import java.io.IOException;
import java.util.List;

/**
 * Сериализатор Jackson для {@link QTupleDataSourceResult} в JSON.
 * @deprecated since 1.6
 */
@Deprecated
public class QTupleDataSourceResultSerializer extends StdSerializer<QTupleDataSourceResult> {

    private static final long serialVersionUID = 3363728329560311903L;

    /**
     * Конструктор.
     */
    public QTupleDataSourceResultSerializer() {
        super(QTupleDataSourceResult.class);
    }

    @Override
    public void serialize(QTupleDataSourceResult value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        List<Expression<?>> metadata = value.getQTuple().getArgs();
        for (Tuple tuple : value.getData()) {
            jgen.writeStartObject();
            for (int i = 0; i < tuple.size(); i++) {
                String fieldName;
                Expression<?> exp = metadata.get(i);
                if (exp instanceof Operation
                        && ((Operation<?>) exp).getOperator() == Ops.ALIAS) {
                    exp = ((Operation<?>) exp).getArg(1);
                }
                if (exp instanceof Path) {
                    fieldName = ((Path<?>) exp).getMetadata().getName();
                } else {
                    fieldName = exp.toString();
                }
                jgen.writeFieldName(fieldName);
                Object tupleValue = tuple.get(i, Object.class);
                if (tupleValue instanceof Integer) {
                    jgen.writeNumber((Integer) tupleValue);
                } else if (tupleValue instanceof Long) {
                    jgen.writeNumber((Long) tupleValue);
                } else {
                    if (tupleValue != null) {
                        jgen.writeString(tupleValue.toString());
                    }
                }
            }
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
}
