package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.AggregateFunctionUnknownException;

import java.math.BigDecimal;

public class LayoutTableTest {

    @Test(expected = AggregateFunctionUnknownException.class)
    public void throwExceptionIfAggregateFunctionIsUnknown() {
        //given
        LayoutTable table = new LayoutTable();
        table.addRow();
        table.getTableRows().get(0).addCell(123, "", 0, 0);
        //when
        table.getAggregatedValue(0, "dumbAggrFunction", 0, 10);
    }
}
