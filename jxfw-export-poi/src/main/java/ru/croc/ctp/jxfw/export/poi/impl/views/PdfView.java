package ru.croc.ctp.jxfw.export.poi.impl.views;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportRow;
import ru.croc.ctp.jxfw.core.export.impl.model.Column;
import ru.croc.ctp.jxfw.core.export.impl.model.XfwExportConfig;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Компонент отвечающий за генерацию PDF представления экспорта списка.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class PdfView extends AbstractPdfView {

    private static final String FONT = "fonts/FreeSans.ttf";
    private static final String PDF_EXT = ".pdf";

    @Override
    protected void buildPdfDocument(final Map<String, Object> model, final Document document,
                                    final PdfWriter pdfWriter, final HttpServletRequest request,
                                    final HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked") final ExportDataProvider dataProvider = (ExportDataProvider) model.get("data");
        final XfwExportConfig exportConfig = (XfwExportConfig) model.get("config");

        ControllerBase.addFileDownLoadCookieAndHeader(request, response,
                exportConfig.calculateFileNameFromConfig(PDF_EXT));

        document.add(createTable(dataProvider, exportConfig));
    }

    /**
     * Формирует таблицу со списком данных.
     */
    private static Table createTable(ExportDataProvider dataProvider, XfwExportConfig exportConfig)
            throws DocumentException, IOException {
        final ClassPathResource pathResource = new ClassPathResource(FONT);
        final BaseFont baseFont = BaseFont.createFont(pathResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        final Table table = new Table(exportConfig.getColumns().size());
        table.setPadding(3.0f);
        final Font fontOfHeader = new Font(baseFont);
        fontOfHeader.setColor(Color.blue);

        for (Column column : exportConfig.getColumns()) {
            final Chunk chunk = new Chunk(column.getTitle(), fontOfHeader);

            table.addCell(createCell(chunk, StyleTypeCell.HEADER));
        }

        fillRows(dataProvider, exportConfig, table, baseFont);

        return table;
    }

    /**
     * Заполняет строки таблицы данными.
     */
    private static void fillRows(ExportDataProvider dataProvider, XfwExportConfig exportConfig,
                                 Table table, BaseFont baseFont)
            throws BadElementException {
        final Font font = new Font(baseFont);
        font.setColor(Color.BLACK);

        Iterable<ExportRow> exportRows = dataProvider.getMoreRows();
        int rowNum = 0;
        while (exportRows.iterator().hasNext()) {
            for (ExportRow exportRow : exportRows) {
                rowNum++;
                for (int i = 0; i < exportConfig.getColumns().size(); i++) {
                    final Column column = exportConfig.getColumns().get(i);

                    final Chunk chunk;
                    if (column.getRole().equalsIgnoreCase("number")) {
                        chunk = new Chunk(Integer.toString(rowNum), font);
                    } else {  // default role - data
                        chunk = new Chunk(exportRow.getValueOfColumn(column).toString(), font);
                    }

                    table.addCell(createCell(chunk, StyleTypeCell.ROW));
                }

            }
            exportRows = dataProvider.getMoreRows();
        }
    }

    /**
     * Создаёт форматированную ячейку.
     */
    private static Cell createCell(Chunk chunk, StyleTypeCell style) throws BadElementException {
        final Cell cell = new Cell(chunk);

        cell.setBackgroundColor(style.color);
        cell.setHorizontalAlignment(style.alignmentHorizontal);
        cell.setVerticalAlignment(style.alignmentVertical);

        return cell;
    }

    /**
     * Стили оформления ячеек. (заголовок, данные)
     */
    private enum StyleTypeCell {
        HEADER(Color.orange, Element.ALIGN_CENTER, Element.ALIGN_CENTER),
        ROW(Color.WHITE, Element.ALIGN_CENTER, Element.ALIGN_CENTER);

        StyleTypeCell(Color color, int alignmentHorizontal, int alignmentVertical) {
            this.color = color;
            this.alignmentHorizontal = alignmentHorizontal;
            this.alignmentVertical = alignmentVertical;
        }

        Color color;
        int alignmentHorizontal;
        int alignmentVertical;
    }


}
