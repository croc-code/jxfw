package ru.croc.ctp.jxfw.ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.logging;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.logging.AreaLogHelper;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AreaLogHelperMessagesTest {

    @Test
    public void getAreaPropertiesForLog() {
        String result = AreaLogHelper.getAreaPropertiesForLog(getStubAreaNoProps(false));

        //System.out.println(result);
        assertTrue(result.contains("fo:external-graphic"));

        result = AreaLogHelper.getAreaPropertiesForLog(getStubAreaNoProps(true));
        //System.out.println(result);
        assertTrue(result.contains("BACKGROUND_COLOR"));
        assertTrue(result.contains("some-style"));
    }

    @Test
    public void getValueWithTimestampForLog() {
        String result = AreaLogHelper.getValueWithTimestampForLog("sample");
        //System.out.println(result);
        assertTrue(result.endsWith("sample"));
    }

    @Test
    public void getAreaCoordinatesForLog() {
        String result = AreaLogHelper.getAreaCoordinatesForLog(getStubRectangle(true));
        //System.out.println(result);
        assertTrue(result.contains("fo:external-graphic"));
        assertTrue(result.contains("10") && result.contains("20") && result.contains("100") && result.contains("230"));
        assertTrue(result.contains(SystemUtils.LINE_SEPARATOR));
    }

    @Test
    public void getAreaRangesForLog() {
        String result = AreaLogHelper.getAreaRangesForLog(getStubRectangle(true));
        //System.out.println(result);
        assertTrue(result.contains("1000"));
        assertTrue(result.contains(SystemUtils.LINE_SEPARATOR));
    }

    private IArea getStubRectangle(boolean withChild) {
        IArea area = mock(IArea.class);
        when(area.getAreaType()).thenReturn(AreaType.EXTERNAL_GRAPHIC);
        when(area.getBorderRectangle()).thenReturn(new AreaRectangle(10, 20, 100, 230));
        Range range = new Range();
        range.setHeight(1000);
        range.setWidth(1000);
        range.setX(1000);
        range.setY(1000);
        when(area.getBorderRange()).thenReturn(range);
        when(area.getProgressionDirection()).thenReturn(AreaProgressionDirection.ROW);

        if (withChild) {
            List<IArea> child = Arrays.asList(getStubRectangle(false));
            when(area.getChildrenList()).thenReturn(child);
        }

        return area;
    }

    private IArea getStubAreaNoProps(boolean hasProps) {
        IArea area = mock(IArea.class);
        when(area.getAreaType()).thenReturn(AreaType.EXTERNAL_GRAPHIC);

        if (hasProps) {
            EnumMap<FoPropertyType, Object> map = new EnumMap<>(FoPropertyType.class);
            map.put(FoPropertyType.BACKGROUND_COLOR, "red");
            map.put(FoPropertyType.BORDER_BEFORE_STYLE, "some-style");
            when(area.getProperties()).thenReturn(map);
        }

        return area;
    }
}
