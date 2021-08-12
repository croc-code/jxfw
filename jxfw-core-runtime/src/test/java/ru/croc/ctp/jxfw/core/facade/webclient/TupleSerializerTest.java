package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.Visitor;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class TupleSerializerTest {

    public static void main(String[] args) throws Exception {
        new TupleSerializerTest().serializeTest();
    }

    @Test
    public void serializeTest() throws Exception {
        String  uuid = "4290e819-22b3-45cc-9eff-67fd82dd62ee";
        DomainTo domainTo = new DomainTo("User", uuid);
        Tuple tuple = new TupleForTest(
                new ExpressionForTest(String.class, "STRING"), new ExpressionForTest(Long.class, "LONG"),
                new ExpressionForTest(Integer.class, "INTEGER"), new ExpressionForTest(DomainTo.class, "DomainTo"))
                .newInstance("string", 10L, 18, domainTo);
        JsonFactory jf = new JsonFactory();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator generator = jf.createGenerator(baos);

        TupleSerializer serializer = new TupleSerializer();
        serializer.serialize(tuple, generator, null);
        generator.close();

        Assert.assertEquals("{\"STRING\":\"string\",\"LONG\":10,\"INTEGER\":18,"
                + "\"DomainTo\":{\"__metadata\":{\"type\":\"User\",\"ts\":-1},\"id\":\"4290e819-22b3-45cc-9eff-67fd82dd62ee\"}}",
                new String( baos.toByteArray(), StandardCharsets.UTF_8));
    }

    private class ExpressionForTest implements Expression {
        private final Class<?> clazz;
        private final String name;

        private ExpressionForTest(Class<?> clazz,
                final String name) {
            this.clazz = clazz;
            this.name = name;
        }

        public Object accept(final Visitor visitor, final Object c) {
            throw new UnsupportedOperationException();
        }

        public Class<?> getType() {
            return clazz;
        }

        @Override
        public String toString() {
            return name;
        }
    };

    private class TupleForTest extends QTuple {
        private TupleForTest(Expression... args) {
            super(args);
        }
    }
}
