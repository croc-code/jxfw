package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.tree;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc.CommonArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AreaTreeBuilderTest {

    @Test
    public void checkAreaType() throws XslFoException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AreaTreeBuilder areaTreeBuilder = new AreaTreeBuilder(mock(XmlTextReader.class), new RootArea());
        Method method = AreaTreeBuilder.class.getDeclaredMethod("checkAreaType", AreaType.class, IArea.class, AreaType[].class);
        method.setAccessible(true);
        try {
            IArea parentArea = mock(IArea.class);
            when(parentArea.getAreaType()).thenReturn(AreaType.EXTERNAL_GRAPHIC);
            method.invoke(
                    areaTreeBuilder,
                    AreaType.CHARACTER,
                    new CommonArea(parentArea, new HashMap<>()),
                    new AreaType[]{AreaType.TABLE}
            );
        } catch (InvocationTargetException | IllegalAccessException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof XslFoException) {
                String message = cause.getMessage();
                //System.out.println(message);
                assertTrue(message.contains("Строка:0"));
                assertTrue(message.contains("Позиция:0"));
                assertTrue(message.contains("fo:external-graphic"));
            } else {
                throw e;
            }
        }
    }
}
