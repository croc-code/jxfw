package ru.croc.ctp.jxfw.solr.facade.webclient;

import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ALL;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ANY;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.CONTAINS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.ENDS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.EQ;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.GE;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.GT;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.LE;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.LT;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NE;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_CONTAINS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_ENDS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.NOT_STARTS;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.SOME;
import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.STARTS;

import com.querydsl.core.types.Predicate;
import java8.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.domain.impl.EnumUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.facade.webclient.FilterHelper;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;
import ru.croc.ctp.jxfw.solr.predicate.SolrQueryContext;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Построитель запросов Solr.
 * <p/>
 *
 */
@SuppressWarnings("rawtypes")
@Service
@Order(0)
public class CriteriaComposer implements PredicateProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaComposer.class);

    //пропускать поля (используется для указания контроллеру базового хранилища что поиск будет полнотекстом)
    private static final String FULLTEXT = "fulltext";

    private DomainToServicesResolverWebClient domainToServicesResolver;

    /**
     * метод возвращает запрос понятный solrServer' у, из фильтров пришедших от клиента.
     *
     * @param filter     фильтр
     * @param domainType тип доменного объекта
     * @return Query - запрос с фильтрами (критериями)
     */
    @SuppressWarnings("unchecked")
    public Query createQuery(
            ObjectFilter filter,
            Class<? extends DomainObject> domainType
    ) {
        ArrayList<Criteria> listCriterias = new ArrayList<>();

        if (filter == null) {
            return addCriterias(listCriterias);
        }

        for (Entry<String, Object> e : filter.entrySet()) {

            String field = e.getKey();

            if (FULLTEXT.equals(field)) {
                continue;
            }

            String schemaFieldName = convertToSchemaFieldName(field, domainType);
            Object value = e.getValue();

            if (Objects.isNull(value)) {  // фильтрация на незаполненное поле
                listCriterias.add(new Criteria(schemaFieldName).isNull());
            } else if (value instanceof Map<?, ?>) {
                Map<String, Object> map = (Map<String, Object>) value;
                populateCriteriasFromMap(field, schemaFieldName, map, listCriterias,
                        getFilterHelper(domainType), domainType);
            } else if (value instanceof List) { //значит фильтрация по "ИЛИ"
                List list = ((List) value);
                populateCriteriasFromList(schemaFieldName, list, listCriterias);
            } else {
                listCriterias.add(new Criteria(schemaFieldName).is(value));
            }
        }

        return addCriterias(listCriterias);
    }

    /**
     * Если задан массив, то на сервере это будет преобразовано в композитное условие из множества подусловий,
     * объединенных через логическое ИЛИ (дока от WC).
     *
     * @param schemaFieldName Имя поля в схеме
     * @param list            Список значений для сравнения
     * @param listCriterias   Список критериев сравнения
     */
    private void populateCriteriasFromList(String schemaFieldName, List list, ArrayList<Criteria> listCriterias) {
        listCriterias.add(new Criteria(schemaFieldName).in(list));
    }

    /**
     * метод заполнит лист критериев, по логическим условиям, когда условия принадлежат одному полю fieldName.
     *
     * @param field           Имя поля
     * @param schemaFieldName Имя поля в схеме
     * @param conditions      Список условий сравнения
     * @param listCriterias   Список критериев
     */
    private void populateCriteriasFromMap(String field, String schemaFieldName, Map<String, Object> conditions,
                                          ArrayList<Criteria> listCriterias, FilterHelper helper,
                                          Class<? extends DomainObject> domainType) {

        for (Entry<String, Object> condition : conditions.entrySet()) {
            String cond = condition.getKey();
            Object value = condition.getValue();

            if (value instanceof List) {
                List list = ((List) value);
                addCriteriaList(schemaFieldName, listCriterias, cond, list);
            } else {
                addCriteria(field, schemaFieldName, listCriterias, cond, value, helper, domainType);
            }
        }
    }

    private void addCriteriaList(String schemaFieldName, ArrayList<Criteria> listCriterias, String cond, List list) {
        switch (cond.toLowerCase()) {
            case EQ: //значит фильтрация по "ИЛИ"
                populateCriteriasFromList(schemaFieldName, list, listCriterias);
                break;
            default:
                throw new UnsupportedOperationException(
                    MessageFormat.format("Comparer  = {0} with with values passed in list, not implemented", cond));
        }
    }

    private void addCriteria(
            String field,
            String schemaFieldName,
            ArrayList<Criteria> listCriterias,
            String cond,
            Object value,
            FilterHelper helper,
            Class<? extends DomainObject> domainType
    ) {
        if (Objects.isNull(value)) { // фильтрация на незаполненное поле
            listCriterias.add(new Criteria(schemaFieldName).isNull());
            return;
        }
        switch (cond.toLowerCase()) {
            case NOT_CONTAINS:
                listCriterias.add(notContains(schemaFieldName, (String) value));
                break;
            case NOT_STARTS:
                listCriterias.add(notStartsWith(schemaFieldName, (String) value));
                break;
            case NOT_ENDS:
                listCriterias.add(notEndsWith(schemaFieldName, (String) value));
                break;
            case CONTAINS:
                listCriterias.add(contains(schemaFieldName, (String) value));
                break;
            case STARTS:
                listCriterias.add(startsWith(schemaFieldName, (String) value));
                break;
            case ENDS:
                listCriterias.add(endsWith(schemaFieldName, (String) value));
                break;
            case NE: // !=
                listCriterias.add(new Criteria(schemaFieldName).is(helper.parsePropValue(field, value)).not());
                break;
            case LE: // <=
                listCriterias.add(new Criteria(schemaFieldName).lessThanEqual(helper.parsePropValue(field, value)));
                break;
            case GE: // >=
                listCriterias.add(new Criteria(schemaFieldName).greaterThanEqual(helper.parsePropValue(field, value)));
                break;
            case LT: // <
                listCriterias.add(new Criteria(schemaFieldName).lessThan(helper.parsePropValue(field, value)));
                break;
            case GT: // >
                listCriterias.add(new Criteria(schemaFieldName).greaterThan(helper.parsePropValue(field, value)));
                break;
            case SOME:
                Criteria cndsSome = fillCriteriasForSomeAndAny(field, schemaFieldName, value, helper, domainType);
                listCriterias.add(cndsSome);
                break;
            case ANY:
                Criteria cndsAny = fillCriteriasForSomeAndAny(field, schemaFieldName, value, helper, domainType);
                listCriterias.add(cndsAny);
                break;
            case ALL:
                Criteria cndsAll = fillCriteriasForAll(field, schemaFieldName, value, helper, domainType);
                listCriterias.add(cndsAll);
                break;
            default: //== (Режим по умолчанию)
                listCriterias.add(new Criteria(schemaFieldName).is(helper.parsePropValue(field, value)));
                break;
        }
    }

    /**
     * Конвертация названия поля модели, на ее наименования в схеме
     * бывает что при генерации кода мы меняем названия полей для схемы в snake_case.
     *
     * @param fieldName  Имя поля
     * @param fieldClass Класс, которому принадлежит поле
     * @return Имя поля в схеме
     */
    private static String convertToSchemaFieldName(String fieldName, Class fieldClass) {
        java.lang.reflect.Field[] fields = fieldClass.getDeclaredFields();
        for (java.lang.reflect.Field f : fields) {
            if (fieldName.equals(f.getName())) {
                Indexed fieldAnnotation = f.getAnnotation(Indexed.class);
                if (fieldAnnotation != null) {
                    String actualFieldName = fieldAnnotation.name();
                    if (StringUtils.isNotEmpty(actualFieldName)) {
                        return actualFieldName;
                    }
                }
            }
        }

        return fieldName;
    }

    /**
     * Метод получает критерии и возвращает готовый запрос.
     *
     * @param list Список критериев
     * @return Созданный запрос с учетом критериев
     */
    private Query addCriterias(ArrayList<Criteria> list) {
        if (list.isEmpty()) {
            //критерия - все подряд
            return new SimpleQuery(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD));
        }

        SimpleQuery query = new SimpleQuery();

        list.forEach(query::addCriteria);

        return query;
    }

    private Criteria fillCriteriasForSomeAndAny(
            String field,
            String schemaFieldName,
            Object value,
            FilterHelper helper,
            Class<? extends DomainObject> domainType
    ) {
        XfwClassifier classifier = getXfwClass(domainType).getEStructuralFeature(field).getEType();
        if (!(classifier instanceof XfwEnumeration)) {
            return null;
        }
        XfwEnumeration xfwEnumeration = (XfwEnumeration)classifier;
        EnumSet parseFlags
                = xfwEnumeration.convertToEnumSet(Integer.parseInt(value.toString()),
                (Class<Enum>)xfwEnumeration.getInstanceClass());
        List<Integer> collect = xfwEnumeration.getELiterals().stream()
                .map( xfwEnumLiteral -> xfwEnumLiteral.getValue())
                .collect(Collectors.toList());
        Set<Integer> cmbs = new HashSet<>();
        for (Object e : parseFlags) {
            cmbs.addAll(EnumUtil.getPossibleValuesContainingCombinatios(collect,
                    xfwEnumeration.convertToInt((Enum)e)));
        }
        Criteria cnds = null;
        for (Integer abc : cmbs) {
            if (cnds == null) {
                cnds = new Criteria(schemaFieldName).is(helper.parsePropValue(field, abc));
            } else {
                cnds = cnds.or(new Criteria(schemaFieldName).is(helper.parsePropValue(field, abc)));
            }
        }
        return cnds;
    }

    private Criteria fillCriteriasForAll(
            String field,
            String schemaFieldName,
            Object value,
            FilterHelper helper,
            Class<? extends DomainObject> domainType
    ) {
        XfwClassifier classifier = getXfwClass(domainType).getEStructuralFeature(field).getEType();
        if (!(classifier instanceof XfwEnumeration)) {
            return null;
        }
        XfwEnumeration xfwEnumeration = (XfwEnumeration)classifier;
        List<Integer> collect = xfwEnumeration.getELiterals().stream()
                .map( xfwEnumLiteral -> xfwEnumLiteral.getValue())
                .collect(Collectors.toList());
        Set<Integer> cmbs = EnumUtil.getPossibleValuesContainingCombinatios(collect,
            Integer.parseInt(value.toString()));
        Criteria cnds = null;
        for (Integer abc : cmbs) {
            if (cnds == null) {
                cnds = new Criteria(schemaFieldName).is(helper.parsePropValue(field, abc));
            } else {
                cnds = cnds.or(new Criteria(schemaFieldName).is(helper.parsePropValue(field, abc)));
            }
        }
        return cnds;
    }

    /**
     * Подмена имен колонок для пагинации.
     *
     * @param pageable    Информация для управления постраничным доступом к списку
     * @param domainClass Класс сущности элементов списка
     * @return {@link Pageable} с замененными именами колонок в условиях сортировки
     */
    public static Pageable modifyColumnNames(Pageable pageable, Class domainClass) {
        if (pageable.getSort() != null) {
            return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),
                modifyColumnNames(pageable.getSort(), domainClass));
        }
        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * Подмена имен колонок для сортировки.
     *
     * @param sort        Условия сортировки
     * @param domainClass Класс сущности элементов списка
     * @return Условия сортировки с замененными именами колонок
     */
    public static Sort modifyColumnNames(Sort sort, Class domainClass) {
        List<Sort.Order> orders = new ArrayList<>();
        sort.forEach(o -> orders.add(new Sort.Order(o.getDirection(),
            convertToSchemaFieldName(o.getProperty(), domainClass))));
        return new Sort(orders);
    }

    private static final String DOUBLEQUOTE = "\"";
    private static final String[] RESERVED_CHARS = {
        DOUBLEQUOTE, "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]",
        "^", "~", "*", "?", ":", "\\"};
    private static final String[] RESERVED_CHARS_REPLACEMENT = {
        "\\" + DOUBLEQUOTE, "\\+", "\\-", "\\&\\&", "\\|\\|", "\\!",
        "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\~", "\\*", "\\?", "\\:", "\\\\"};

    /**
     * По мотивам BasePredicateProcessor.
     */
    private static String prepareExpressionValue(String searchString, boolean leadingWildcard,
                                                 boolean trailingWildcard) {
        String exp = (leadingWildcard ? "*" : "")
            + StringUtils.replaceEach(searchString,
            RESERVED_CHARS, RESERVED_CHARS_REPLACEMENT).replaceAll(" ", "\\\\ ")
            + (trailingWildcard ? "*" : "");

        LOGGER.debug(MessageFormat.format("Prepared solrQuery expression value ==> {0} <==", exp));

        return exp;
    }

    private Criteria endsWith(String schemaFieldName, String searchValue) {
        // своя логика если искомое выражение - фраза (слова с пробелами)
        if (containsWhitespace(searchValue)) {
            return new Criteria(schemaFieldName).expression(prepareExpressionValue(searchValue, true, false));
        } else {
            return new Criteria(schemaFieldName).endsWith(searchValue);
        }
    }

    private Criteria notEndsWith(String schemaFieldName, String searchValue) {
        if (containsWhitespace(searchValue)) {
            return new Criteria(schemaFieldName).expression(prepareExpressionValue(searchValue, true, false)).not();
        } else {
            return new Criteria(schemaFieldName).endsWith(searchValue).not();
        }
    }

    private Criteria notStartsWith(String schemaFieldName, String searchValue) {
        if (containsWhitespace(searchValue)) {
            return (new Criteria(schemaFieldName).expression(prepareExpressionValue(searchValue, false, true))).not();
        } else {
            return (new Criteria(schemaFieldName).startsWith(searchValue)).not();
        }
    }

    private Criteria startsWith(String schemaFieldName, String searchValue) {
        if (containsWhitespace(searchValue)) {
            return new Criteria(schemaFieldName).expression(prepareExpressionValue(searchValue, false, true));
        } else {
            return new Criteria(schemaFieldName).startsWith(searchValue);
        }
    }

    private Criteria contains(String schemaFieldName, String searchValue) {
        if (containsWhitespace(searchValue)) {
            return new Criteria(schemaFieldName).expression(prepareExpressionValue(searchValue, true, true));
        } else {
            return new Criteria(schemaFieldName).contains(searchValue);
        }
    }

    private Criteria notContains(String schemaFieldName, String searchValue) {
        if (containsWhitespace(searchValue)) {
            return (new Criteria(schemaFieldName).expression(prepareExpressionValue(searchValue, true, true))).not();
        } else {
            return (new Criteria(schemaFieldName).contains(searchValue)).not();
        }
    }

    /**
     * Часто приходится строить свой запрос для фразы содержащей пробелы.
     */
    private boolean containsWhitespace(String text) {
        return StringUtils.contains(text, Criteria.CRITERIA_VALUE_SEPERATOR);
    }

    @Override
    public Predicate buildPredicate(
            @Nonnull String domainTypeName,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable
    ) {
       Predicate predicate = new SolrQueryContext(
                DomainObjectUtil.getDomainObjectType(domainTypeName.replaceFirst("solr.", "")),
                this,
                objectFilter,
                null, // будет передан в отдельным аргументом к сервису, иначе капризничает
                null
        );
        return predicate;
    }

    @Override
    public Predicate buildPredicateById(@Nonnull String domainTypeName,@Nonnull Serializable id) {
        //FIXME: predicateByID
        return buildPredicate(domainTypeName,null,null,null);
    }

    @Override
    public boolean accepts(String domainTypeName) {
        //т.к. при fulltext feature для fulltext комплекта не готовится ecore        
        if (domainTypeName.contains("solr.")) {
            return true;
        }

        Class<?> domainType = DomainObjectUtil.getDomainObjectType(domainTypeName);
        Annotation solrDocument = domainType.getAnnotation(SolrDocument.class);
        return solrDocument != null;
    }

    private FilterHelper getFilterHelper(Class<? extends DomainObject> type) {
        DomainToService toService = null;
        try {
            toService = domainToServicesResolver.resolveFulltextToService(type.getSimpleName());
        } catch (Exception e1) {
            try {
                toService = domainToServicesResolver.resolveToService(type.getTypeName());
            } catch (Exception e2) {
                throw new RuntimeException("ТО-сервис для " + type.getName() + " не определен");
            }
        }
        return (FilterHelper) toService;
    }

    private XfwClass getXfwClass(Class<? extends DomainObject> domainType) {
        return XfwModelFactory.getInstance().findThrowing(domainType.getSimpleName(), XfwClass.class);
    }

    @Autowired
    public void setDomainToServicesResolver(DomainToServicesResolverWebClient domainToServicesResolver) {
        this.domainToServicesResolver = domainToServicesResolver;
    }
}
