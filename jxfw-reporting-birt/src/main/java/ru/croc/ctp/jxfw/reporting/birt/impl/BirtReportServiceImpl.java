package ru.croc.ctp.jxfw.reporting.birt.impl;

import static java.lang.Thread.currentThread;

import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.lowagie.text.FontFactory;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;
import ru.croc.ctp.jxfw.reporting.birt.BirtReportService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@inheritDoc}.
 */
@Service
public class BirtReportServiceImpl implements BirtReportService {

    private static final String PARAM_ISNULL = "__isnull";

    private static final String UTF_8_ENCODE = "UTF-8";

    private static final Logger log = LoggerFactory.getLogger(BirtReportServiceImpl.class);

    private final IReportEngine reportEngine;

    private final ResourceLoader resourceLoader;

    private final XfwReportProfileManager reportProfileManager;



    /**
     * Конструктор.
     * <p/>
     *  @param reportEngine   движок генератора отчетов
     * @param resourceLoader
     * @param reportProfileManager
     */
    @Autowired
    public BirtReportServiceImpl(IReportEngine reportEngine, ResourceLoader resourceLoader, XfwReportProfileManager reportProfileManager) {
        this.reportEngine = reportEngine;
        this.resourceLoader = resourceLoader;
        this.reportProfileManager = reportProfileManager;
    }

    /**
     * Инициализация сервиса генерации отчетов.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing Birt Report Service URLs");
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public void renderHtmlReport(HttpServletRequest request, HttpServletResponse response,
                                 OutputFormat format, Map<String, Object> params, String reportFileName) {

        XfwReportProfile reportProfile=reportProfileManager.getReport(reportFileName);

        IRunAndRenderTask task = null;
        try(InputStream stream = reportProfile.getStream()) {
            final IReportRunnable runnable = reportEngine.openReportDesign(stream);
            task = reportEngine.createRunAndRenderTask(runnable);

            //put the parameter values from request to the reportRunnable parameter
            final HashMap<String, Object> parameterValues = (HashMap<String, Object>)
                    discoverAndSetParameters(runnable, request);
            parameterValues.putAll(params);
            task.setParameterValues(parameterValues);

            //set the format 
            response.setContentType(format.getMime());

            final ServletOutputStream outputStream = response.getOutputStream();
            writeHack(outputStream);

            HTMLRenderOption htmlOptions = new HTMLRenderOption(new RenderOption());
            htmlOptions.setOutputFormat("html");
            htmlOptions.setOutputStream(outputStream);
            htmlOptions.setImageHandler(new HTMLServerImageHandler());
            htmlOptions.setEnableCompactMode(true);
            htmlOptions.setHtmlPagination(false);
            htmlOptions.setImageDirectory(request.getSession().getServletContext().getRealPath("/images"));
            htmlOptions.setSupportedImageFormats("PNG");
            htmlOptions.setWrapTemplateTable(false);
            htmlOptions.setEmbeddable(true);
            htmlOptions.setMasterPageContent(false);

            task.setRenderOption(htmlOptions);

            injectLocale(task);
            injectTimezone(task, getTimezoneFromRequest(request, "__timezone"));

            @SuppressWarnings("unchecked") final Map<Object, Object> taskAppContext = task.getAppContext();
            if (taskAppContext != null) {
                taskAppContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, currentThread().getContextClassLoader());
                taskAppContext.put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);
            }
            task.run();
        } catch (Exception e) {
            log.error("Exception while proceessing reportRunnable ", e);
            throw new RuntimeException(e);
        } finally {
            if (task != null) {
                task.close();
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void renderPdfReport(HttpServletRequest request, HttpServletResponse response,
                                OutputFormat format, Map<String, Object> params, String reportFileName) {
        XfwReportProfile reportProfile=reportProfileManager.getReport(reportFileName);
        IRunAndRenderTask task = null;
        try {
            String downloadFileName;

            FontFactory.register(resourceLoader.getResource("classpath:/fonts/Roboto.ttf").getURL().getPath());
            //give the download reportRunnable Name here.
            downloadFileName = (String) request.getAttribute("filename");
            if (downloadFileName == null) {
                downloadFileName = "-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            }

            ControllerBase.addFileDownLoadCookieAndHeader(request, response, downloadFileName + format.getExtension());

            final IReportRunnable runnable = reportEngine.openReportDesign(reportProfile.getStream());
            task = reportEngine.createRunAndRenderTask(runnable);


            //put the parameter values from request to the reportRunnable parameter
            final HashMap<String, Object> parameterValues = (HashMap<String, Object>)
                    discoverAndSetParameters(runnable, request);
            parameterValues.putAll(params);
            task.setParameterValues(parameterValues);

            //set the format 
            response.setContentType(format.getMime());

            PDFRenderOption pdfOptions = new PDFRenderOption(new RenderOption());
            pdfOptions.setSupportedImageFormats("PNG;GIF;JPG;BMP");
            pdfOptions.setImageHandler(new HTMLServerImageHandler());
            pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
            pdfOptions.setEmbededFont(true);

            pdfOptions.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
            pdfOptions.setOutputStream(response.getOutputStream());

            task.setRenderOption(pdfOptions);
            injectTimezone(task, getTimezoneFromRequest(request, "__timezone"));

            @SuppressWarnings("unchecked") final Map<Object, Object> taskAppContext = task.getAppContext();
            if (taskAppContext != null) {
                taskAppContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, currentThread().getContextClassLoader());
                taskAppContext.put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);
            }
            task.run();
        } catch (Exception e) {
            log.error("Exception while proceessing reportRunnable ", e);
            throw new RuntimeException(e);
        } finally {
            if (task != null) {
                task.close();
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    public void renderExcelReport(HttpServletRequest request,
                                  HttpServletResponse response,
                                  OutputFormat format,
                                  Map<String, Object> params,
                                  String reportFileName) {

        XfwReportProfile reportProfile=reportProfileManager.getReport(reportFileName);
        IRunAndRenderTask task = null;
        try {
            String downloadFileName;

            //give the download reportRunnable Name here.
            downloadFileName = (String) request.getAttribute("filename");
            if (downloadFileName == null) {
                downloadFileName = "-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            }

            //opend design document
            final IReportRunnable runnable = reportEngine.openReportDesign(reportProfile.getStream());
            task = reportEngine.createRunAndRenderTask(runnable);

            //put the parameter values from request to the reportRunnable parameter
            final HashMap<String, Object> parameterValues = (HashMap<String, Object>)
                    discoverAndSetParameters(runnable, request);
            parameterValues.putAll(params);
            task.setParameterValues(parameterValues);

            //set the format
            response.setContentType(format.getMime());
            injectTimezone(task, getTimezoneFromRequest(request, "__timezone"));

            response.setCharacterEncoding("UTF-8");

            EXCELRenderOption xlsOptions = new EXCELRenderOption(new RenderOption());
            xlsOptions.setOutputFormat("xls_spudsoft");
            ControllerBase.addFileDownLoadCookieAndHeader(request, response, downloadFileName + format.getExtension());
            xlsOptions.setImageHandler(new HTMLServerImageHandler());
            xlsOptions.setOutputStream(response.getOutputStream());
            xlsOptions.setOption(IRenderOption.EMITTER_ID, "uk.co.spudsoft.birt.emitters.excel.XlsEmitter");
            task.setRenderOption(xlsOptions);

            @SuppressWarnings("unchecked") final Map<Object, Object> taskAppContext = task.getAppContext();
            if (taskAppContext != null) {
                taskAppContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, currentThread().getContextClassLoader());
                taskAppContext.put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);
            }
            task.run();
        } catch (Exception e) {
            log.error("Exception while proceessing reportRunnable ", e);
            e.printStackTrace();
        } finally {
            if (task != null) {
                task.close();
            }
        }
    }

    @Override
    public void renderReport(HttpServletRequest request, HttpServletResponse response, OutputFormat outputFormat,
                             Map<String, Object> params, String reportFileName) {
        switch (outputFormat) {
            case PDF:
                renderPdfReport(request, response, outputFormat, params, reportFileName);
                break;
            case EXCEL:
                renderExcelReport(request, response, outputFormat, params, reportFileName);
                break;
            default:
                renderHtmlReport(request, response, outputFormat, params, reportFileName);
        }

    }

    /**
     * Найти параметры в запросе и обработать их для передачи движку.
     *
     * @param reportRunnable - задача отчета
     * @param request        - запрос
     * @return {@code Map} с именами-значениями параметров
     * @throws Exception - исключение
     */
    private Map<String, Object> discoverAndSetParameters(IReportRunnable reportRunnable, HttpServletRequest request)
            throws Exception {

        final Map<String, Object> params = new HashMap<>();
        IGetParameterDefinitionTask task = reportEngine.createGetParameterDefinitionTask(reportRunnable);

        @SuppressWarnings("unchecked")
        Collection<IParameterDefnBase> paramsDefs = task.getParameterDefns(true);
        for (IParameterDefnBase param : paramsDefs) {
            IScalarParameterDefn scalar = (IScalarParameterDefn) param;
            if (request.getParameter(param.getName()) != null
                    && !request.getParameter(param.getName()).trim().equals("")) {
                params.put(param.getName(), getParamValueObject(request, scalar));
            }
        }
        task.close();
        return params;
    }

    private void writeHack(ServletOutputStream outputStream) throws IOException {
        outputStream.write(("<script>\n"
                + "\troot = document.getElementById('__BIRT_ROOT');\n"
                + "\troot.removeAttribute(\"class\");\n"
                + "\n"
                + "</script>\n").getBytes());
    }

    private void injectLocale(IEngineTask task) {
        Locale locale = LocaleContextHolder.getLocale();

        log.debug("Setting Report Locale to " + locale);

        task.setLocale(locale);
        task.setLocale(ULocale.forLocale(locale));

        log.debug("Report Language is " + task.getULocale().getBaseName());
    }

    private void injectTimezone(IEngineTask task, String timeZoneId) {
        task.setTimeZone(TimeZone.getTimeZone(timeZoneId));
    }

    private String getTimezoneFromRequest(HttpServletRequest request, String timezone) {
        final String timeZone = request.getParameter(timezone);
        if (timeZone == null || StringUtils.isEmpty(timeZone)) {
            return TimeZone.getDefault().getID();
        }
        return timeZone;
    }

    /**
     * Get parameter value.
     *
     * @param request      - HTTP запрос
     * @param parameterObj - объект параметра
     * @return значение параметра
     * @throws Exception - исключение.
     */
    private Object getParamValueObject(HttpServletRequest request,
                                       IScalarParameterDefn parameterObj) throws Exception {
        String paramName = parameterObj.getName();
        String format = parameterObj.getDisplayFormat();
        if (doesReportParameterExist(request, paramName)) {
            ReportParameterConverter converter = new ReportParameterConverter(format, request.getLocale());
            // Get value from http request
            String paramValue = getReportParameter(request,
                    paramName, null);
            return converter.parse(paramValue, parameterObj.getDataType());
        }
        return null;
    }

    /**
     * Check if reportRunnable parameter exist.
     *
     * @param request - HTTP запрос
     * @param name    - имя параметра
     * @return флаг присутсвия или отсуствия параметра
     */
    private static boolean doesReportParameterExist(HttpServletRequest request, String name) {
        assert request != null && name != null;

        boolean isExist = false;

        Map paramMap = request.getParameterMap();
        if (paramMap != null) {
            isExist = (paramMap.containsKey(name));
        }
        Set nullParams = getParameterValues(request, PARAM_ISNULL);
        if (nullParams != null && nullParams.contains(name)) {
            isExist = true;
        }

        return isExist;
    }

    /**
     * Получить значение параметра отчета по имени.
     *
     * @param request      - HTTP запрос
     * @param name         - имя параметра
     * @param defaultValue - дефолтное значение
     * @return значение параметра
     */
    private static String getReportParameter(HttpServletRequest request,
                                             String name, String defaultValue) {
        assert request != null && name != null;

        String value = getParameter(request, name);
        if (value == null || value.length() <= 0) {
            value = ""; //$NON-NLS-1$
        }

        Map paramMap = request.getParameterMap();
        if (paramMap == null || !paramMap.containsKey(name)) {
            value = defaultValue;
        }

        Set nullParams = getParameterValues(request, PARAM_ISNULL);

        if (nullParams != null && nullParams.contains(name)) {
            value = null;
        }

        return value;
    }

    /**
     * Получить множественные значения параметра отчета по имени.
     *
     * @param request       - HTTP запрос
     * @param parameterName - имя параметра
     * @return множественные значения
     */
    private static Set getParameterValues(HttpServletRequest request,
                                          String parameterName) {
        Set<String> parameterValues = null;
        String[] parameterValuesArray = request.getParameterValues(parameterName);

        if (parameterValuesArray != null) {
            parameterValues = new LinkedHashSet<>();

            Collections.addAll(parameterValues, parameterValuesArray);
        }

        return parameterValues;
    }

    /**
     * Получить параметр из HTTP зарпоса.
     *
     * @param request       - HTTP запрос
     * @param parameterName - имя параметра
     * @return значение параметра
     */
    private static String getParameter(HttpServletRequest request,
                                       String parameterName) {

        if (request.getCharacterEncoding() == null) {
            try {
                request.setCharacterEncoding(UTF_8_ENCODE);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return request.getParameter(parameterName);
    }




    @Override
    public Iterable<OutputFormat> getFormats() {
        Set<OutputFormat> result = new HashSet<>();
        result.add(OutputFormat.HTML5);
        result.add(OutputFormat.EXCEL);
        result.add(OutputFormat.PDF);

        return result;
    }

}
