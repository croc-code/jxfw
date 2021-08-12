package ru.croc.ctp.jxfw.ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel;

import com.aspose.cells.Workbook;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.XslFo2ExcelRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.ExcelRowHeightsSetter;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class XslFo2ExcelRendererTest {

    String source = "<?xml version='1.0' encoding='UTF-8'?><fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"><fo:layout-master-set><fo:simple-page-master master-name=\"default-page-master\" page-width=\"210mm\" page-height=\"297mm\" reference-orientation=\"\"><fo:region-body margin=\"20mm\" region-name=\"PageBody\" display-align=\"before\"/></fo:simple-page-master></fo:layout-master-set><fo:page-sequence master-reference=\"default-page-master\"><fo:flow flow-name=\"PageBody\"><fo:table><fo:table-column column-number=\"1\" column-width=\"100.0%\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#51729D\" border=\"1pt solid\" padding=\"5px\" color=\"#FFFFFF\" font-weight=\"bold\" font-size=\"20px\" border-color=\"#000000\" text-align=\"center\"><fo:block>Информационное обслуживание</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table><fo:table><fo:table-column column-number=\"1\" column-width=\"100.0%\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#E9E9E9\" border=\"1pt solid\" padding=\"5px\" font-weight=\"bold\" font-size=\"16px\" border-color=\"#000000\" text-align=\"center\"><fo:block>Некорректные дела</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table><fo:table><fo:table-column column-number=\"1\" column-width=\"100.0%\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#E9E9E9\" border=\"1pt solid\" padding=\"5px\" font-weight=\"bold\" font-size=\"16px\" border-color=\"#000000\" text-align=\"center\"><fo:block>Тип документа: Все</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table><fo:table><fo:table-column column-number=\"1\" column-width=\"100.0%\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#E9E9E9\" border=\"1pt solid\" padding=\"5px\" font-weight=\"bold\" font-size=\"16px\" border-color=\"#000000\" text-align=\"center\"><fo:block>Судья: Все</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table><fo:table><fo:table-column column-number=\"1\" column-width=\"15.0%\"/><fo:table-column column-number=\"2\" column-width=\"15.0%\"/><fo:table-column column-number=\"3\" column-width=\"35.0%\"/><fo:table-column column-number=\"4\" column-width=\"35.0%\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#E1EAF4\" border=\"1pt solid\" color=\"#000000\" font-weight=\"bold\" border-color=\"#000000\" text-align=\"center\"><fo:block>Дело/Документ</fo:block></fo:table-cell><fo:table-cell background-color=\"#E1EAF4\" border=\"1pt solid\" color=\"#000000\" font-weight=\"bold\" border-color=\"#000000\" text-align=\"center\"><fo:block>Судья</fo:block></fo:table-cell><fo:table-cell background-color=\"#E1EAF4\" border=\"1pt solid\" color=\"#000000\" font-weight=\"bold\" border-color=\"#000000\" text-align=\"center\"><fo:block>Ошибочная ситуация</fo:block></fo:table-cell><fo:table-cell background-color=\"#E1EAF4\" border=\"1pt solid\" color=\"#000000\" font-weight=\"bold\" border-color=\"#000000\" text-align=\"center\"><fo:block>Как исправить</fo:block></fo:table-cell></fo:table-row><fo:table-row><fo:table-cell background-color=\"#FFFFFF\" border=\"1pt solid\" color=\"#000000\" text-align=\"center\"><fo:block>Уголовное дело № <fo:basic-link external-destination=\"url('#bac94620-a1a8-11e7-954d-5364d2793077')\" title=\"перейти\" class=\"crimeCaseRef\">01-0002/2017</fo:basic-link></fo:block></fo:table-cell><fo:table-cell background-color=\"#FFFFFF\" border=\"1pt solid\" color=\"#000000\" padding-left=\"5px\" text-align=\"left\"><fo:block></fo:block></fo:table-cell><fo:table-cell background-color=\"#FFFFFF\" border=\"1pt solid\" color=\"#000000\" padding-left=\"5px\" text-align=\"left\"><fo:block>Дело окончено, но не введены сведения по томам дела (если дело находится в конечном состоянии, а список \"Тома дела\" пустой)</fo:block></fo:table-cell><fo:table-cell background-color=\"#FFFFFF\" border=\"1pt solid\" color=\"#000000\" padding-left=\"5px\" text-align=\"left\"><fo:block>Укажите сведения по томам дела</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table><fo:table><fo:table-column column-number=\"1\" column-width=\"proportional-column-width(1)\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#FFFFFF\" border-color=\"#FFFFFF\" color=\"#FFFFFF\"><fo:block>.</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table><fo:table><fo:table-column column-number=\"1\" column-width=\"proportional-column-width(1)\"/><fo:table-body><fo:table-row><fo:table-cell background-color=\"#E9E9E9\" border=\"1pt solid\" color=\"#000000\" padding-left=\"5px\" border-color=\"#000000\" text-align=\"right\"><fo:block>Отчёт составлен 17.10.2017 в 11:21</fo:block></fo:table-cell></fo:table-row></fo:table-body></fo:table></fo:flow></fo:page-sequence></fo:root>";

    @Test
    public void rowHeightStrategy() throws Exception {
        InputStream inputStream = IOUtils.toInputStream(source, "UTF-8");
        XslFo2ExcelRenderer xslFo2ExcelRenderer = new XslFo2ExcelRenderer();
        ExcelRowHeightsSetter spy = spy(ExcelRowHeightsSetter.class);
        xslFo2ExcelRenderer.setHeightsSetter(spy);
        xslFo2ExcelRenderer.render(inputStream, mock(OutputStream.class), StandardCharsets.UTF_8, new Locale("ru"), OutputFormat.EXCEL2010);
        ArgumentCaptor<Workbook> contextArgumentCaptor = ArgumentCaptor.forClass(Workbook.class);
        verify(spy, atLeastOnce()).setHeightsOfRows(contextArgumentCaptor.capture());
    }

    @Test
    public void rowHeightStrategyDefaultIsUsed() throws Exception {
        InputStream inputStream = IOUtils.toInputStream(source, "UTF-8");
        XslFo2ExcelRenderer xslFo2ExcelRenderer = new XslFo2ExcelRenderer();
        xslFo2ExcelRenderer.setHeightsSetter(null);
        xslFo2ExcelRenderer.render(inputStream, mock(OutputStream.class), StandardCharsets.UTF_8, new Locale("ru"), OutputFormat.EXCEL2010);
    }
}
