package ru.croc.ctp.jxfw.core.facade.webclient;

import static com.querydsl.core.types.ExpressionUtils.isNull;
import static com.querydsl.core.types.dsl.Expressions.constant;
import static com.querydsl.core.types.dsl.Expressions.predicate;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ALL;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ANY;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.COL_IS_EMPTY;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.CONTAINS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.CONTAINS_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.CONTAINS_CS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ENDS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ENDS_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ENDS_CS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.EQ;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.EQ_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.EQ_CS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.GE;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.GT;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.IN;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.LE;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.LT;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NE;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_ALL;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_ANY;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_CONTAINS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_CONTAINS_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_CONTAINS_CS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_ENDS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_ENDS_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_ENDS_CS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_SOME;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_STARTS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_STARTS_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_STARTS_CS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.SOME;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.STARTS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.STARTS_CI;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.STARTS_CS;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Преобразует выражение фильтра, полученное от клиента, в предикат QueryDSL.
 *
 * @since 1.1
 */
@Service
@Order(0)
public class PredicateComposer implements PredicateProvider {
    
    /**
     * Префикс операторов отрицания.
     */
    private static final String PREFIX_NOT = "not-";
    
    /**
     * Флаг игнорировать ли в строковых операторах регистр.
     */
    private DomainToServicesResolverWebClient domainToServicesResolver;

    /**
     * Создать предикат по фильтру переданному из WC.
     *
     * @param filter   объекта фильтра (может быть {@code null})
     * @param typeName имя доменного объекта
     * @return предикат с условиями фильтрации.
     */
    @SuppressWarnings("unchecked")
    public Predicate createPredicate(ObjectFilter filter, String typeName) {
        FilterHelper<?> helper = getFilterHelper(typeName);
        BooleanBuilder builder = new BooleanBuilder();
        if (filter == null) {
            return builder;
        }
        for (Entry<String, Object> e : filter.entrySet()) {
            Path<String> attrPath = Expressions.path(
                    String.class,
                    helper.createPath(),
                    helper.createPathProperty(e.getKey())
            );

            if (e.getValue() instanceof Map<?, ?>) {
                Map<String, Object> map = (Map<String, Object>) e.getValue();
                for (Entry<String, Object> operator : map.entrySet()) {
                    final String opName = operator.getKey();
                    if (ALL.equalsIgnoreCase(opName) || NOT_ALL.equalsIgnoreCase(opName)) {
                        Expression<Object> constant = constant(helper
                                .parsePropValue(e.getKey(), operator.getValue()));
                        final Expression<?> arg1 = ConstantImpl.create(operator.getValue());
                        final Expression<?> arg2 = attrPath;
                        NumberExpression<Integer> expression
                                = Expressions.numberTemplate(Integer.class, "bitwise_and({0}, {1})", arg1, arg2);
                        Ops predicateOp = opName.startsWith(PREFIX_NOT) ? Ops.NE : Ops.EQ;
                        builder.and(predicate(predicateOp, expression, constant));
                    } else if (SOME.equalsIgnoreCase(opName) || NOT_SOME.equalsIgnoreCase(opName)
                            || ANY.equalsIgnoreCase(opName) || NOT_ANY.equalsIgnoreCase(opName)) {
                        final Expression<?> arg1 = ConstantImpl.create(operator.getValue());
                        final Expression<?> arg2 = attrPath;
                        NumberExpression<Integer> expression
                                = Expressions.numberTemplate(Integer.class, "bitwise_and({0}, {1})", arg1, arg2);
                        Ops predicateOp = opName.startsWith(PREFIX_NOT) ? Ops.EQ : Ops.NE;
                        builder.and(predicate(predicateOp, expression, constant(0)));

                    } else if (COL_IS_EMPTY.equalsIgnoreCase(opName)) {

                        if (!isMassivePropertyMetamodel(typeName, e.getKey())) {
                            throw new IllegalStateException("\"null\" operator can be applied to collections only.");
                        }
                        // оператору "пустая коллекция" нужен QueryDsl-путь к свойству без добавления ".id",
                        //как это делает helper.createPathProperty для навигируемых свойств
                        Path<String> collectionAttrPath = Expressions.path(
                                String.class,
                                helper.createPath(),
                                e.getKey()
                        );
                        builder.and(predicate(getOperator(opName, false, helper), collectionAttrPath));
                    } else {
                        if (operator.getValue() instanceof List) {
                            BooleanExpression[] exps;
                            if (e.getValue() instanceof LinkedHashMap) {
                                exps = createMultiplyExpressions(operator, attrPath, helper);
                            } else {
                                exps = createMultiplyEqExpressions(e, attrPath, helper);
                            }
                            if (isNegationOp(opName)) {
                                // значения массива для компареров с отрицанием должны объединяться AND
                                builder.and(Expressions.allOf(exps));
                            } else {
                                builder.and(Expressions.anyOf(exps)); //объединение через "ИЛИ"
                            }
                        } else {
                            Object value = helper.parsePropValue(e.getKey(), operator.getValue());
                            Expression<Object> constant = value != null ? constant(value) : NullExpression.DEFAULT;
                            if (opName.toLowerCase().startsWith(PREFIX_NOT)) { //not-contains, not-starts, not-ends
                                builder.andNot(predicate(
                                        getOperator(opName.replaceAll(PREFIX_NOT, ""),
                                                value instanceof String, helper), attrPath, constant));
                            } else {
                                builder.and(predicate(
                                        getOperator(opName, value instanceof String, helper), attrPath, constant));
                            }
                        }
                    }
                }
            } else {
                if (e.getValue() == null) {
                    builder.and(isNull(attrPath));
                } else if (e.getValue() instanceof List) {
                    BooleanExpression[] exps = createMultiplyEqExpressions(e, attrPath, helper);
                    builder.and(Expressions.anyOf(exps));
                } else {
                    // TODO выяснить в каких случаях используется
                    if (e.getKey().split("\\.").length >= 2
                            && isMassiveProperty(e.getKey(), helper)) {
                        throw new IllegalStateException("Filter doesn't work with massive property - not supported."
                                + "If you want filtering by massive property or using its, you need to do "
                                + "implementation via custom DataSource.");
                    }
                    Object value = helper.parsePropValue(e.getKey(), e.getValue());
                    Expression<Object> constant = constant(value);
                    builder.and(predicate((helper.isIgnoreCaseForOperatorOfFiltering() && value instanceof String)
                            ? Ops.EQ_IGNORE_CASE : Ops.EQ, attrPath, constant));

                }
            }
        }
        return builder;
    }

    /**
     * Создать предикат по Id.
     *
     * @param id       id
     * @param typeName имя доменного объекта
     * @return {@link Predicate}
     */
    public Predicate createPredicate(Serializable id, String typeName) {
        XfwClass xfwClass = XfwModelFactory.getInstance().findThrowing(typeName, XfwClass.class);
        BooleanOperation predicate = Expressions.predicate(
                Ops.EQ,
                Expressions.path(
                        String.class,
                        getFilterHelper(typeName).createPath(),
                        xfwClass.getIdField().getName()
                ),
                Expressions.constant(id)
        );
        return predicate;
    }

    /**
     *
     * <pre>
     *     Один из способов попасть в метод.
     *     {"prop": {"not-eq": ["adc", "bcd"]}}
     * </pre>
     *
     * @param entry    имя опреатора как из WC
     * @param attrPath represents a path expression. Paths refer to variables, properties and collection members access
     * @return массив выражений
     */
    private BooleanExpression[] createMultiplyExpressions(
            Entry<String, Object> entry,
            Path<String> attrPath,
            FilterHelper<?> helper
    ) {

        final List<BooleanExpression> exps = new ArrayList<>();

        final String opName = entry.getKey(); // eq, ne, etc...
        final Operator operator = getOperator(operatorNameWithoutPrefixNot(opName), false, helper);
        final Object entryValue = entry.getValue();
        
        if (entryValue instanceof List) {            
            final List<?> entryValues = ((List<?>) entryValue);

            for (Object value : entryValues) {
                Expression<Object> constant;
                if (value == null) {
                    constant = NullExpression.DEFAULT;
                } else {
                    constant = constant(helper.parsePropValue(opName, value));
                }
                if (isNegationOp(opName) && !opName.equals(NE)) {
                    exps.add(predicate(operator, attrPath, constant).not());
                } else {
                    exps.add(predicate(operator, attrPath, constant));
                }
            }
        } else {
            final Expression<Object> constant;

            if (entryValue == null) {
                constant = NullExpression.DEFAULT;
            } else {
                constant = constant(helper.parsePropValue(opName, entryValue));
            }
            exps.add(predicate(operator, attrPath, constant));
        }
        return exps.toArray(new BooleanExpression[exps.size()]);
    }

    private BooleanExpression[] createMultiplyEqExpressions(
            Entry<String,
            Object> entry,
            Path<String> attrPath,
            FilterHelper<?> helper
    ) {
        final List<?> values = (List<?>) entry.getValue();
        final BooleanExpression[] exps = new BooleanOperation[values.size()];

        for (int i = 0; i < values.size(); i++) {
            final Object value = values.get(i);
            final Expression<Object> constant;

            if (value == null) {
                constant = NullExpression.DEFAULT;
            } else {
                constant = constant(helper
                        .parsePropValue(entry.getKey(), value));
            }
            exps[i] = predicate(Ops.EQ, attrPath, constant);
        }
        return exps;
    }

    /**
     * Преобразует имя оператора WC в соответсвующий оператор QueryDSL.
     *
     * @param soperator строка с именем оператора WC.
     * @return QueryDSL оператор.
     */
    private Operator getOperator(String soperator, boolean isString, FilterHelper<?> helper) {
        switch (soperator) {
            // регистрозависимость зависит от глобального параметра isIgnoreCase
            case CONTAINS:
                return helper.isIgnoreCaseForOperatorOfFiltering() ? Ops.STRING_CONTAINS_IC : Ops.STRING_CONTAINS;
            case STARTS:
                return helper.isIgnoreCaseForOperatorOfFiltering() ? Ops.STARTS_WITH_IC : Ops.STARTS_WITH;
            case ENDS:
                return helper.isIgnoreCaseForOperatorOfFiltering() ? Ops.ENDS_WITH_IC : Ops.ENDS_WITH;
            case EQ:
                return (helper.isIgnoreCaseForOperatorOfFiltering() && isString) ? Ops.EQ_IGNORE_CASE : Ops.EQ;
            // регистронезависимые операторы
            case CONTAINS_CI:
                return Ops.STRING_CONTAINS_IC;
            case STARTS_CI:
                return Ops.STARTS_WITH_IC;
            case ENDS_CI:
                return Ops.ENDS_WITH_IC;
            case EQ_CI:
                return (isString) ? Ops.EQ_IGNORE_CASE : Ops.EQ;
            // регистрозависимые
            case CONTAINS_CS:
                return Ops.STRING_CONTAINS;
            case STARTS_CS:
                return Ops.STARTS_WITH;
            case ENDS_CS:
                return Ops.ENDS_WITH;
            case EQ_CS:
                return Ops.EQ;
            // операторы
            case NE:
                return Ops.NE;
            case LE:
                return Ops.LOE;
            case GE:
                return Ops.GOE;
            case LT:
                return Ops.LT;
            case GT:
                return Ops.GT;
            // для коллекций
            case COL_IS_EMPTY:
                return Ops.COL_IS_EMPTY;
            case IN:
                return Ops.IN;
            default:
                throw new IllegalStateException("Unsupported operation: " + soperator);
        }
    }

    /**
     * Возвращает имя оператора WC без префикса отрицания, если он был. Иначе оператор без изменений.
     *
     * @param opName имя опреатора WC.
     * @return имя оператора без префикса отрицания.
     */
    private static String operatorNameWithoutPrefixNot(String opName) {
        return isNegationOp(opName) ? opName.replaceAll(PREFIX_NOT, "") : opName;
    }

    private boolean isMassiveProperty(String key, FilterHelper<?> helper) {
        final String[] strings = key.split("\\.");
        if (strings.length > 2) {
            final Field firstLevelField = ReflectionUtils.findField(helper.createPath().getType(), strings[0]);
            final Field secondLevelField = ReflectionUtils.findField(firstLevelField.getType(), strings[1]);
            return Collection.class.isAssignableFrom(secondLevelField.getType());
        }
        return false;
    }


    /**
     * Метод определяет по метамодели, является ли данное свойство массивным.
     *
     * @param typeName имя класса
     * @param propName имя свойства
     * @return да\нет
     */
    private boolean isMassivePropertyMetamodel(String typeName, String propName) {
        return Optional.ofNullable(XfwModelFactory.getInstance()
                .find(typeName, XfwClass.class))
                .flatMap(xfwClass -> Optional.ofNullable(xfwClass.getEStructuralFeature(propName)))
                .filter(XfwStructuralFeature::isMany)
                .isPresent();


    }

    /**
     * Проверка опреатора WC на наличие отрицания.
     *
     * @param operator оператор
     * @return да/нет
     */
    public static boolean isNegationOp(String operator) {
        Objects.requireNonNull(operator);

        switch (operator) {
            case NE:
            case NOT_CONTAINS:
            case NOT_CONTAINS_CS:
            case NOT_CONTAINS_CI:
            case NOT_STARTS:
            case NOT_STARTS_CI:
            case NOT_STARTS_CS:
            case NOT_ENDS:
            case NOT_ENDS_CI:
            case NOT_ENDS_CS:
            case NOT_SOME:
            case NOT_ANY:
            case NOT_ALL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Predicate buildPredicate(
            @Nonnull String domainTypeName,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable) {

        return new BooleanBuilder(createPredicate(objectFilter, domainTypeName));
    }

    @Override
    public Predicate buildPredicateById(@Nonnull String domainTypeName, @Nonnull Serializable id) {
        //для того чтобы можно было в событиях конвеера обогощать запрос
        return new BooleanBuilder(createPredicate(id, domainTypeName));
    }

    @Override
    public boolean accepts(String domainTypeName) {
        XfwClass xfwClass = XfwModelFactory.getInstance().find(domainTypeName, XfwClass.class);
        return xfwClass != null && xfwClass.getPersistenceModule().contains("JPA");
    }

    private <T extends DomainObject<?>> FilterHelper<T> getFilterHelper(String domainTypeName) {
        return domainToServicesResolver.resolveToService(domainTypeName);
    }

    @Autowired
    public void setDomainToServicesResolver(DomainToServicesResolverWebClient domainToServicesResolver) {
        this.domainToServicesResolver = domainToServicesResolver;
    }
    
}
