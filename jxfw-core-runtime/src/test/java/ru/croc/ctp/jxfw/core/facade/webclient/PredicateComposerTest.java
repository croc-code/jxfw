package ru.croc.ctp.jxfw.core.facade.webclient;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Assert;
import org.junit.Test;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.services.TestObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Проверяет корректность преобразования выражений фильтра в предикат QueryDSL.
 */
public class PredicateComposerTest {

    @SuppressWarnings("serial")
    private abstract class DomainObjectTest implements DomainObject<String> {
    }

    /**
     * Создаёт предикат по фильтру и сравнивает его текстовое представление с ожидаемым.
     *
     * @param filter       объект фильтра.
     * @param expected     ожидаемое текстовое представление.
     * @param isIgnoreCase флаг включающий регистро-независимые проверки для строк.
     */
    private void testOperators(ObjectFilter filter, String expected, boolean isIgnoreCase) {
        final PredicateComposer predicateComposer = new PredicateComposer();
        DomainToServicesResolverWebClient toServicesResolverWebClient = mock(DomainToServicesResolverWebClient.class);
        when(toServicesResolverWebClient.resolveToService(anyString())).thenReturn(
                getFilterHelper(isIgnoreCase));
        predicateComposer.setDomainToServicesResolver(toServicesResolverWebClient);
        final Predicate predicate = predicateComposer.createPredicate(filter, TestObject.class.getSimpleName());

        Assert.assertNotNull(predicate);
        Assert.assertEquals(expected, predicate.toString());
    }

    @Test
    public void testOp2() {
        System.out.println("test op");
    }

    @SuppressWarnings("serial")
    @Test
    public void testMultipleContainsAndMultipleNotContainsOnOneField() {
        final ObjectFilter filter = new ObjectFilter();

        final String expected = "(contains(test.field,contains_value1) "
                + "|| contains(test.field,contains_value2)) "
                + "&& !contains(test.field,not_contains_value1) "
                + "&& !contains(test.field,not_contains_value2)";
        filter.addRange("field", new LinkedHashMap<String, Object>() {{
            put(Filter.CONTAINS, Arrays.asList("contains_value1", "contains_value2"));
            put(Filter.NOT_CONTAINS, Arrays.asList("not_contains_value1", "not_contains_value2"));
        }});

        testOperators(filter, expected, false);
    }

    @SuppressWarnings("serial")
    @Test
    public void testContainsAndMultipleNotContainsOnOneField() {
        final ObjectFilter filter = new ObjectFilter();

        final String expected = "contains(test.field,contains_value1) "
                + "&& !contains(test.field,not_contains_value1) "
                + "&& !contains(test.field,not_contains_value2)";
        filter.addRange("field", new LinkedHashMap<String, Object>() {{
            put(Filter.CONTAINS, "contains_value1");
            put(Filter.NOT_CONTAINS, Arrays.asList("not_contains_value1", "not_contains_value2"));
        }});

        testOperators(filter, expected, false);
    }

    @SuppressWarnings("serial")
    @Test
    public void testMultipleContainsAndNotContainsOnOneField() {
        final ObjectFilter filter = new ObjectFilter();

        final String expected = "(contains(test.field,contains_value1) "
                + "|| contains(test.field,contains_value2)) "
                + "&& !contains(test.field,not_contains_value1)";

        filter.addRange("field", new LinkedHashMap<String, Object>() {{
            put(Filter.CONTAINS, Arrays.asList("contains_value1", "contains_value2"));
            put(Filter.NOT_CONTAINS, "not_contains_value1");
        }});

        testOperators(filter, expected, false);
    }

    /**
     * Проверка значения поля на null.
     */
    @Test
    public void testOperatorValueIsNull() {
        final String expected = "test.field1 is null && test.field2 is null";
        final ObjectFilter filter = new ObjectFilter();
        filter.addEquals("field1", (String) null);
        filter.addEquals("field2", (Integer) null);

        testOperators(filter, expected, false);
    }

    /**
     * Проверка вхождения значения в список.
     */
    @Test
    public void testOperatorAnyOfListWithNullOperator() {
        final String expected = "test.field1 = value1 || test.field1 = value2";
        final ObjectFilter filter = new ObjectFilter();
        filter.addSimple("field1", null, Arrays.asList("value1", "value2"));

        testOperators(filter, expected, false);
    }

    /**
     * Проверка выполнения списка операторов сравнения.
     */
    @Test
    public void testOperatorsOfComparing() {
        final String expected = "test.field1 = value1 " +
                "&& test.field1 != value2 " +
                "&& test.field1 <= value3 " +
                "&& test.field1 >= value4 " +
                "&& test.field1 < value5 " +
                "&& test.field1 > value6";
        final Map<String, String> value = new LinkedHashMap<>();
        value.put(Filter.EQ, "value1");
        value.put(Filter.NE, "value2");
        value.put(Filter.LE, "value3");
        value.put(Filter.GE, "value4");
        value.put(Filter.LT, "value5");
        value.put(Filter.GT, "value6");
        final ObjectFilter filter = new ObjectFilter();
        filter.addSimple("field1", null, value);

        testOperators(filter, expected, false);
    }

    /**
     * Проверка выполнения списка строковых операторов без учёта регистра.
     */
    @Test
    public void testMultiMapOrderedStringOperatorsWithoutIgnoreCase() {
        final String expected = "contains(test.field1,value1) " +
                "&& startsWith(test.field1,value2) " +
                "&& endsWith(test.field1,value3) " +
                "&& !contains(test.field1,value4) " +
                "&& !startsWith(test.field1,value5) " +
                "&& !endsWith(test.field1,value6)";
        final Map<String, Object> value = new LinkedHashMap<>();
        value.put(Filter.CONTAINS, "value1");
        value.put(Filter.STARTS, "value2");
        value.put(Filter.ENDS, "value3");
        value.put(Filter.NOT_CONTAINS, "value4");
        value.put(Filter.NOT_STARTS, "value5");
        value.put(Filter.NOT_ENDS, "value6");
        final ObjectFilter filter = new ObjectFilter();
        filter.addSimple("field1", null, value);

        testOperators(filter, expected, false);
    }

    /**
     * Проверка выполнения списка строковых операторов с учётом регистра.
     */
    @Test
    public void testMultiMapOrderedStringOperatorsWithIgnoreCase() {
        final String expected = "containsIc(test.field1,value1) " +
                "&& startsWithIgnoreCase(test.field1,value2) " +
                "&& endsWithIgnoreCase(test.field1,value3) " +
                "&& !containsIc(test.field1,value4) " +
                "&& !startsWithIgnoreCase(test.field1,value5) " +
                "&& !endsWithIgnoreCase(test.field1,value6)";
        final Map<String, String> value = new LinkedHashMap<>();
        value.put(Filter.CONTAINS, "value1");
        value.put(Filter.STARTS, "value2");
        value.put(Filter.ENDS, "value3");
        value.put(Filter.NOT_CONTAINS, "value4");
        value.put(Filter.NOT_STARTS, "value5");
        value.put(Filter.NOT_ENDS, "value6");
        final ObjectFilter filter = new ObjectFilter();
        filter.addSimple("field1", null, value);

        testOperators(filter, expected, true);
    }

    /**
     * Проверка выполнения списка флаговых операторов с учётом регистра.
     */
    @Test
    public void testOperatorsOfFlags() {
        final String expected = "bitwise_and(1, test.field1) = 1 " +
                "&& bitwise_and(6, test.field6) != 6 " +
                "&& bitwise_and(3, test.field3) != 0 " +
                "&& bitwise_and(2, test.field2) != 0 " +
                "&& bitwise_and(5, test.field5) = 0 " +
                "&& bitwise_and(4, test.field4) = 0";
        final ObjectFilter filter = new ObjectFilter();
        filter.addSimple("field1", Filter.ALL, "1");
        filter.addSimple("field2", Filter.SOME, "2");
        filter.addSimple("field3", Filter.ANY, "3");
        filter.addSimple("field4", Filter.NOT_SOME, "4");
        filter.addSimple("field5", Filter.NOT_ANY, "5");
        filter.addSimple("field6", Filter.NOT_ALL, "6");

        testOperators(filter, expected, false);
    }

    /**
     * JXFW-594 Поддержать WC 1.28
     * {"prop": {"not-eq": ["adc", "bcd"]}}
     */
    @Test
    public void testMassiveInequalities() {
        //TODO посмотреть как сделать через параметры метода (в тесте)
        HashMap<String, String> p = new HashMap<>();
        p.put(Filter.NE, "test.propName != asc && test.propName != dsc");
        p.put(Filter.NOT_STARTS, "!startsWith(test.propName,asc) && !startsWith(test.propName,dsc)");

        for (String opName : p.keySet()) {
            final ObjectFilter filter = new ObjectFilter();
            Map<String, Object> range = new LinkedHashMap<>();
            range.put(opName, Arrays.asList("asc", "dsc"));
            filter.addRange("propName", range);

            testOperators(filter, p.get(opName), false);
        }
    }

    /**
     * JXFW-594 Поддержать WC 1.28
     */
    @Test
    public void isNegatingOperator() { //TODO посмотреть как сделать через параметры метода (в тесте)

        HashMap<String, Boolean> p = new HashMap<>();
        p.put(Filter.NE, true);
        p.put(Filter.NOT_CONTAINS, true);
        p.put(Filter.NOT_STARTS, true);
        p.put(Filter.NOT_ENDS, true);
        p.put(Filter.NOT_SOME, true);
        p.put(Filter.NOT_ANY, true);
        p.put(Filter.NOT_ALL, true);
        p.put(Filter.EQ, false);

        for (Map.Entry<String, Boolean> entry : p.entrySet()) {
            String operator = entry.getKey();
            Boolean expectedResult = entry.getValue();

            Assert.assertEquals(expectedResult, PredicateComposer.isNegationOp(operator));
        }
    }


    /**
     * JXFW-774: Поддержать регистронезависимые операторы в фильтрах.
     * Для каждого оператора (contains, not-contains, starts, not-starts, ends, not-ends)
     * поддерживается еще два с суффиксами -cs  и -ci  для регистрозависимого и регистронезависимого
     * поиска соответственно.
     */
    @Test
    public void caseIgnoreAndCaseSensitiveOperators() {
        final Map<String, String> value = new LinkedHashMap<>();
        // регистра незавсимые
        value.put(Filter.CONTAINS_CI, "value1");
        value.put(Filter.STARTS_CI, "value2");
        value.put(Filter.ENDS_CI, "value3");
        value.put(Filter.NOT_CONTAINS_CI, "value4");
        value.put(Filter.NOT_STARTS_CI, "value5");
        value.put(Filter.NOT_ENDS_CI, "value6");
        // регистра зависимые
        value.put(Filter.CONTAINS_CS, "value1");
        value.put(Filter.STARTS_CS, "value2");
        value.put(Filter.ENDS_CS, "value3");
        value.put(Filter.NOT_CONTAINS_CS, "value4");
        value.put(Filter.NOT_STARTS_CS, "value5");
        value.put(Filter.NOT_ENDS_CS, "value6");

        final ObjectFilter filter = new ObjectFilter();
        filter.addSimple("field1", null, value);

        // проверяем
        final String expected = "containsIc(test.field1,value1) " +
                "&& startsWithIgnoreCase(test.field1,value2) " +
                "&& endsWithIgnoreCase(test.field1,value3) " +
                "&& !containsIc(test.field1,value4) " +
                "&& !startsWithIgnoreCase(test.field1,value5) " +
                "&& !endsWithIgnoreCase(test.field1,value6) " +
                "&& contains(test.field1,value1) " +
                "&& startsWith(test.field1,value2) " +
                "&& endsWith(test.field1,value3) " +
                "&& !contains(test.field1,value4) " +
                "&& !startsWith(test.field1,value5) " +
                "&& !endsWith(test.field1,value6)";
        testOperators(filter, expected, true);
        testOperators(filter, expected, false);
    }

    private DomainToService getFilterHelper(boolean isIgnoreCase) {
        class FH implements DomainToService, FilterHelper {
            @Override
            public Path<DomainObjectTest> createPath() {
                return Expressions.path(DomainObjectTest.class, "test");
            }

            @Override
            public Object parsePropValue(String propName, Object value) {
                return value;
            }

            @Override
            public boolean isIgnoreCaseForOperatorOfFiltering() {
                return isIgnoreCase;
            }

            @Override
            public String serializeKey(Serializable key) {
                return null;
            }

            @Override
            public Serializable parseKey(String key) {
                return null;
            }

            @Override
            public DomainObject createNewDomainObject(String key) {
                return null;
            }

            @Override
            public DomainObject getDomainObjectById(String key) {
                return null;
            }

            @Override
            public DomainObject getDomainObjectById(String key, LoadContext loadContext) {
                return null;
            }

            @Override
            public DomainTo toToPolymorphic(DomainObject domainObject, String... expand) {
                return null;
            }

            @Override
            public List<DomainTo> toToPolymorphic(List domainObjectList, String type, String... expand) {
                return null;
            }

            @Override
            public List<DomainTo> toTo(Iterable domainObject, String... expand) {
                return null;
            }

            @Override
            public DomainTo toTo(DomainObject domainObject, String... expand) {
                return null;
            }

            @Override
            public DomainObject fromTo(DomainTo vo, ConvertContext context) {
                return null;
            }

            @Override
            public DomainObject fromTo(DomainObject domainObject, DomainTo vo, ConvertContext context) {
                return null;
            }

        }

        return new FH();
    }

}
