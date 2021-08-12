package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DimensionTest {

    @Test
    public void testCaching() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getCacheMtd = Dimension.class.getDeclaredMethod("getCache");
        getCacheMtd.setAccessible(true);
        Method setCacheMtd = Dimension.class.getDeclaredMethod("setCache", Map.class);
        setCacheMtd.setAccessible(true);
        Dimension dimension = Dimension.getInstance("16");

        Object invoke = getCacheMtd.invoke(dimension);

        assertTrue(((Map)invoke).containsKey("16"));
        assertNull(((Map)invoke).get("17"));
    }
}
