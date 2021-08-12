package ru.croc.ctp.jxfw.export.poi.impl.views;

import static ru.croc.ctp.jxfw.core.export.impl.model.Layout.MARGINS_COUNT;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportRow;
import ru.croc.ctp.jxfw.core.export.impl.model.Column;
import ru.croc.ctp.jxfw.core.export.impl.model.XfwExportConfig;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Компонент отвечающий за генерацию Excel представления экспорта списка.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
public class ExcelView extends AbstractXlsView {

    private static final Logger log = LoggerFactory.getLogger(ExcelView.class);

    private static final String XLS_EXT = ".xlsx";

    private final ResourceLoader resourceLoader;

    public ExcelView(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {

        //Workbook с поддержкой стримминга, те будет писать на диск пачку по 500 строк
        return new SXSSFWorkbook(500);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        final ExportDataProvider dataProvider = (ExportDataProvider) model.get("data");
        final XfwExportConfig exportConfig = (XfwExportConfig) model.get("config");
        log.info("Preparing EXCEL document");

        ControllerBase.addFileDownLoadCookieAndHeader(request, response,
                exportConfig.calculateFileNameFromConfig(XLS_EXT));


        setupStyles(exportConfig, workbook);

        Sheet sheet = workbook.createSheet("sheet-1");
        fillHeaderAndRows(dataProvider, exportConfig, sheet);
        setupLayout(sheet, exportConfig);


    }

    private void setupLayout(Sheet sheet, XfwExportConfig exportConfig) {


        if (exportConfig.hasTemplate()) {
            sheet.setFitToPage(exportConfig.getLayout().isTemplateFitToPage());
            PrintSetup printSetup = sheet.getPrintSetup();
            PrintSetup template = (PrintSetup) exportConfig.getLayout().getTemplatePrintSetup();
            printSetup.setPaperSize(template.getPaperSize());
            printSetup.setLandscape(template.getLandscape());
            printSetup.setFitWidth(template.getFitWidth());
            printSetup.setFitHeight(template.getFitHeight());
            for (short m = 0; m < MARGINS_COUNT; m++) {
                sheet.setMargin(m, exportConfig.getLayout().getTemplateMargins()[m]);
            }

        } else {
            if (Arrays.asList("landscape", "90", "270").contains(exportConfig.getLayout().getPageOrientation())) {
                sheet.getPrintSetup().setLandscape(true);
            }
            switch (exportConfig.getLayout().getPageFormat()) {
                // UNSUPPORTED in POI  case "A2": sheet.getPrintSetup().setPaperSize(PrintSetup.); break;
                case "A3":
                    sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
                    break;
                case "A5":
                    sheet.getPrintSetup().setPaperSize(PrintSetup.A5_PAPERSIZE);
                    break;
                case "Letter":
                    sheet.getPrintSetup().setPaperSize(PrintSetup.LETTER_PAPERSIZE);
                    break;
                default:
                    sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            }



            /*
            автоподбор ширины убрала. Т.к. ценность сомнительна, все равно будут равнять вручную,
            а на большом экспорте это затратная операция.
            for (int i = 0; i < exportConfig.getColumns().size(); i++) {
                ((SXSSFSheet)sheet).trackColumnForAutoSizing(i);
                sheet.autoSizeColumn(i);
            }*/
        }

        int columnWidthSum = 0;
        boolean allWidthSet = true;
        for (int i = 0; i < exportConfig.getColumns().size(); i++) {
            final Column column = exportConfig.getColumns().get(i);
            allWidthSet = allWidthSet && (column.getWidth() > 0);
            columnWidthSum += column.getWidth();
        }

        // FIXME как лучше определить ширину страницы?
        int defaultPageWidth = 15 * sheet.getColumnWidth(0);
        // для всех колонок задана относительная ширина
        if (allWidthSet) {
            sheet.getPrintSetup().setFitWidth((short) 1);
            for (int i = 0; i < exportConfig.getColumns().size(); i++) {
                final Column column = exportConfig.getColumns().get(i);
                sheet.setColumnWidth(i, defaultPageWidth * column.getWidth() / columnWidthSum);
            }

        }

    }

    private void setupStyles(XfwExportConfig exportConfig, Workbook workbook) {

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle plainStyle = workbook.createCellStyle();

        // сначала заполним дефолтными стилями
        for (Column column : exportConfig.getColumns()) {
            column.setHeaderStyle(headerStyle);
            column.setStyle(plainStyle);
        }

        try {
            // если нашелся шаблон, то возьмем стили из него
            final Workbook styleTemplate = new XSSFWorkbook(resourceLoader.getResource(
                    "classpath:/export/" + exportConfig.getTemplateName(XLS_EXT)).getInputStream());


            if (styleTemplate.getNumberOfSheets() > 0) {

                exportConfig.setTemplate(true);

                exportConfig.getLayout().setTemplatePrintSetup(styleTemplate.getSheetAt(0).getPrintSetup());
                exportConfig.getLayout().setTemplateFitToPage(styleTemplate.getSheetAt(0).getFitToPage());

                for (short m = 0; m < MARGINS_COUNT; m++) {
                    exportConfig.getLayout().getTemplateMargins()[m] = styleTemplate.getSheetAt(0).getMargin(m);
                }


                final Row header = styleTemplate.getSheetAt(0).getRow(0);
                final Row plainRow = styleTemplate.getSheetAt(0).getRow(1);

                if (header == null) {
                    return;
                }
                for (int i = 0; i < exportConfig.getColumns().size(); i++) {
                    final Column column = exportConfig.getColumns().get(i);

                    // ищем столбец в шаблоне по его названию.
                    int templateColumnIndex = -1;

                    for (int j = 0; j < Math.min(header.getLastCellNum(), 100); j++) {
                        if (column.getPropName().equalsIgnoreCase(header.getCell(j).getStringCellValue())) {
                            templateColumnIndex = j;
                            break;
                        }
                    }

                    if (templateColumnIndex < 0) {
                        continue;
                    }


                    if (header != null && header.getCell(templateColumnIndex) != null) {
                        CellStyle cellStyle = workbook.createCellStyle();
                        cellStyle.cloneStyleFrom(header.getCell(templateColumnIndex).getCellStyle());
                        column.setHeaderStyle(cellStyle);
                    }
                    if (plainRow != null && plainRow.getCell(templateColumnIndex) != null) {
                        CellStyle cellStyle = workbook.createCellStyle();
                        cellStyle.cloneStyleFrom(plainRow.getCell(templateColumnIndex).getCellStyle());
                        column.setStyle(cellStyle);
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            // Если не найден шаблон для конкретной выгрузки, то
            // это штатная ситуация
            log.debug("{}", ex);
        } catch (Exception ex) {
            log.error("{}", ex);
        }

    }


    /**
     * Стиль для ячеек заголовка.
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();

        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    /**
     * Заполняет строки таблицы данными.
     */
    private void fillHeaderAndRows(ExportDataProvider dataProvider, XfwExportConfig exportConfig, Sheet sheet) {

        //шапка
        final Row header = sheet.createRow(0);

        for (int i = 0; i < exportConfig.getColumns().size(); i++) {
            final Column column = exportConfig.getColumns().get(i);
            final Cell cell = header.createCell(i);
            cell.setCellValue(column.getTitle());
            cell.setCellStyle((CellStyle) column.getHeaderStyle());
        }

        // данные
        Iterable<ExportRow> exportRows = dataProvider.getMoreRows();
        int rowNum = 0;
        while (exportRows.iterator().hasNext()) {
            for (ExportRow exportRow : exportRows) {
                rowNum++;
                final Row row = sheet.createRow(rowNum);

                for (int i = 0; i < exportConfig.getColumns().size(); i++) {
                    final Column column = exportConfig.getColumns().get(i);
                    final Cell cell = row.createCell(i);
                    cell.setCellStyle((CellStyle) column.getStyle());
                    if (column.getRole().equalsIgnoreCase("number")) {
                        cell.setCellValue(rowNum);
                    } else {  // default role - data
                        Object value = exportRow.getValueOfColumn(column);
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue(((Boolean) value));
                        } else if (value instanceof Date) {
                            cell.setCellValue(((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            exportRows = dataProvider.getMoreRows();
        }
    }

}
