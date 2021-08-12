package ru.croc.ctp.jxfw.core.load.impl;

import com.google.common.collect.Maps;
import java8.util.J8Arrays;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Утилитный класс для работы с пагинацией.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
public class PaginationUtil {

    /**
     * Создание объекта {@link Pageable}.
     *
     * @param skip  - кол-во пропускаемых записей.
     * @param top   - верхняя граница
     * @param props - дополнительные настройки
     * @return объект {@link Pageable}.
     */
    public static Pageable create(Integer skip, Integer top, Map<String, String> props) {
        final List<Order> orders = getOrders(props);
        final Sort sort = orders == null || orders.size() == 0 ? Sort.unsorted() : Sort.by(orders);
        return create(skip, top, sort);
    }

    /**
     * Создание объекта {@link Pageable}.
     *
     * @param skip  - кол-во пропускаемых записей.
     * @param top   - верхняя граница
     * @param sort - сортировка
     * @return объект {@link Pageable}.
     */
    public static Pageable create(Integer skip, Integer top, Sort sort) {
        if (top == null) {
            top = Integer.MAX_VALUE;
        }
        if (skip == null) {
            skip = 0;
        }
        return new OffsetBasedPageRequest(skip, top, sort);
    }

    /**
     * Список направлений сортировок {@link Order} для последующего построения {@link Sort}.
     *
     * @param props мапа поля-направления сортировки
     * @return cписок направлений сортировок.
     */
    public static List<Order> getOrders(final Map<String, String> props) {
        if (props != null) {
            final List<Order> orders = new ArrayList<>();
            java8.util.Maps.forEach(props, (propName, directionStr) -> {
                final Optional<Sort.Direction> direction = Sort.Direction.fromOptionalString(directionStr);
                final Order order = direction.isPresent() ? new Order(direction.get(), propName) : Sort.Order.by(propName);
                orders.add(order);
            });
            return orders;
        }
        return null;
    }


    /**
     * Парсинг дополнительных настроек настроек.
     *
     * @param orderByProp - настройка порядка
     * @return ключ-знаяение, настройки.
     */
    public static Map<String, String> parseOrderByProp(final String orderByProp) {
        final Map<String, String> map = Maps.newLinkedHashMap();

        if (orderByProp == null) {
            return map;
        }

        J8Arrays.stream(orderByProp.split(","))
                .forEach(propString -> {
                    final String[] s = propString.trim().split(" ");
                    final String prop = s[0];
                    String direction = null;
                    if (s.length > 1) {
                        direction = s[1];
                    }

                    map.put(prop, direction);
                });
        return map;
    }
}
