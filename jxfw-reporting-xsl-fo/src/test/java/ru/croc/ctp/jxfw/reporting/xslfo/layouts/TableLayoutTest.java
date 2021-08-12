package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataReader;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataRow;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataTable;
import ru.croc.ctp.jxfw.reporting.xslfo.types.RowClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TableLayoutClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TableLayoutTest {

    /**
     * https://jira.croc.ru/browse/JXFW-1324 исправлена ошибка в xsd схеме
     */
    @Test
    public void layoutProfileGetRow() {
        TableLayout tableLayout = getTableLayout();
        LayoutTable layoutTable = mock(LayoutTable.class);
        when(tableLayout.getLayoutTable()).thenReturn(layoutTable);

        IDataTable dataTable = getDataTable();

        //нет стиля
        TableLayoutClass layoutProfile = getTableLayoutClass(false);

        tableLayout.writeRows(
                layoutProfile,
                mock(ReportLayoutData.class),
                mock(IDataReader.class),
                mock(TableLayout.LayoutColumns.class),
                new HashMap<>(),
                dataTable
        );

        verify(layoutTable, times(1)).addRow();
        verify(layoutTable, times(0)).addRow(anyString());


    }

    private TableLayout getTableLayout() {
        return spy(new TableLayout() {
            @Override
            protected void writeGroupBound(TableLayoutClass layoutProfile, ReportLayoutData layoutData, IDataRow previousRow, IDataRow currentRow, LayoutColumns columns, Map<String, Object> groupings, IDataTable dataTable, int[] rowspans, boolean isLastRow) {
            }
        });
    }

    @Test
    public void layoutProfileGetRowWithStyle() {
        TableLayout tableLayout = getTableLayout();
        LayoutTable layoutTable = mock(LayoutTable.class);
        when(tableLayout.getLayoutTable()).thenReturn(layoutTable);

        IDataTable dataTable = getDataTable();

        //есть стиль
        TableLayoutClass layoutProfile = getTableLayoutClass(true);

        tableLayout.writeRows(
                layoutProfile,
                mock(ReportLayoutData.class),
                mock(IDataReader.class),
                mock(TableLayout.LayoutColumns.class),
                new HashMap<>(),
                dataTable
        );

        verify(layoutTable, times(0)).addRow();
        verify(layoutTable, times(1)).addRow(anyString());
    }

    private TableLayoutClass getTableLayoutClass(boolean hasStyle) {
        TableLayoutClass tableLayoutClass = new TableLayoutClass();
        if (hasStyle) {
            RowClass rowClass = new RowClass();
            rowClass.setStyleClass("superStyle");
            rowClass.setHideIf("NO");
            tableLayoutClass.setRow(rowClass);
        }
        return tableLayoutClass;
    }

    private IDataTable getDataTable() {
        IDataTable dataTable = mock(IDataTable.class);
        ArrayList<IDataRow> dataRows = new ArrayList<>();
        IDataRow dataRow = mock(IDataRow.class);
        dataRows.add(dataRow);
        when(dataTable.getRows()).thenReturn(dataRows);
        return dataTable;
    }
}
