package ru.croc.ctp.jxfw.core.load.impl;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.util.StringUtils.isEmpty;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Утилитный класс для управлением параметром orderBy.
 * Поддерживаются некоторые спецсимволы "-", "+", "*"
 *
 * @author Nosov Alexander
 * @since 1.0
 */
public class SortUtil {

    /**
     * Regex сортировки.
     */
    public static final String DESC_REGEXP = "\\bdesc\\b";
    /**
     * Regex сортировки.
     */
    public static final String ASC_REGEXP = "\\basc\\b";

    /** Сортировки.
     *
     * @param orderBy - свойство и порядок сортировки.
     * @return Порядок сортировки
     */
    public static Sort parse(final String orderBy) {
        if (isEmpty(orderBy)) {
            return null;
        }

        final List<Sort.Order> orders = Lists.newArrayList();
        final String[] splitOrderBy = orderBy.split(",");

        for (String orderByItem : splitOrderBy) {
            final String field = orderByItem
                    .replaceAll("[-\\*]", "")
                    .replaceAll(DESC_REGEXP, "")
                    .replaceAll(ASC_REGEXP, "")
                    .trim();
            if (orderByItem.contains("-") || contains(orderByItem, DESC_REGEXP)) {
                orders.add(new Sort.Order(DESC, field));
            } else {
                orders.add(new Sort.Order(ASC, field));
            }
        }

        return new Sort(orders);
    }

    private static boolean contains(String source, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(source);
        return matcher.find();
    }

    /**
     * Преобразование условий сортировки из Sort spring-data
     * в OrderSpecifier queryDsl
     * @param pathBuilder PathBuilder для сущности, к полям которой применять сортировку.
     * @param sort сортировка spring-data
     * @return сортировка queryDsl
     */
    public static OrderSpecifier<?>[] orderSpecifiers(PathBuilder pathBuilder, Sort sort){
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if(sort!=null) {
            for (Sort.Order o : sort) {
                orderSpecifiers.add(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                        pathBuilder.get(o.getProperty())));

            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier<?>[]{});
    }

}
