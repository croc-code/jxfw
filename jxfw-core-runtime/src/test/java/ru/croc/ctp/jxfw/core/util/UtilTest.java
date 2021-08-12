package ru.croc.ctp.jxfw.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import ru.croc.ctp.jxfw.core.load.impl.SortUtil;

/**
 * Created by SMufazzalov on 09.06.2017.
 */
public class UtilTest {
    

    @Test //JXFW-792 Метод SortUtil.parse вырезает из названия поля нужные символы
    public void sortUtilParseOrderByStringTest() {
        Sort sort = SortUtil.parse("sub_description");
        assertNotNull(sort.getOrderFor("sub_description"));

        sort = SortUtil.parse("sub_description asc");
        Sort.Direction direction = sort.getOrderFor("sub_description").getDirection();
        assertEquals(direction, Sort.Direction.ASC);
    }

    @Test //JXFW-849 Не работает сортировка по нескольким полям
    public void manyFieldsSorting() {
        Sort sort = SortUtil.parse("code, parent.name");

        assertNotNull(sort.getOrderFor("code"));
        assertNotNull(sort.getOrderFor("parent.name"));
    }

    @Test //JXFW-854 SortUtil.parse возвращает дефолтное полe id
    public void emptyOrderBy() {
        Sort sort = SortUtil.parse("");

        assertNull(sort);
    }
}
