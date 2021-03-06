package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;
import ru.croc.ctp.jxfw.reporting.xslfo.IReport;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.IReportLayout;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors.IReportParamProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.IRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.HtmlRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.MsExcelRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.XslFo2WordRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.style.IStyleInliner;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractDataSourceClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.LayoutsClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.stream.XMLStreamException;


/**
 * ?????????? ?????????????????????? ???????????? ???????????????????? ????????????.
 * Created by vsavenkov on 13.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
/*
  FIXME ?????????? ???????????? ??????????????????????, ??.??. ?????????????? ???????????????????? ???? ?????????? ?? ?????????? ???????? ?????????????? ??????????, ?? ?????????????? ???? ????????????????????
  ?????????????? ?????????????? ???????????? ?? ?????????????? ?????????????????? ????????????.
 */
public abstract class XfwXslfoReport implements IReport, BeanNameAware {
    /**
     * ????????????.
     */
    protected static final Logger logger = LoggerFactory.getLogger(XfwXslfoReport.class);
    /**
     * ????????????????.
     */
    public static final String REFRESH = "Refresh";
    /**
     * ???? ????????????????????.
     */
    public static final String DONT_CACHE_XSLFO = "DontCacheXslfo";
    /**
     * ???? ????????????????????.
     */
    public static final String PARAM_OUTPUT_FORMAT = "format";

    /**
     * ?????????????????????????????????? ?????????????? ????????????.
     */
    private ReportClass reportProfile;


    private String reportName;

    private MsExcelRenderer msExcelRenderer;

    private XslFo2WordRenderer xslFo2WordRenderer;

    /**
     * ?????????????????????????????????? ?????????????? ????????????.
     */
    private XfwReportProfile xfwReportProfile;

    /**
     * ??????????????????????.
     */
    protected static final Locale LOCALE = new Locale("ru");


    private final IStyleInliner inliner;

    private final XfwReportProfileManager profileManager;

    /**
     * ??????????????????????.
     *
     * @param inliner        {@link IStyleInliner}
     * @param profileManager {@link XfwReportProfileManager}
     */
    protected XfwXslfoReport(IStyleInliner inliner, XfwReportProfileManager profileManager) {
        this.inliner = inliner;
        this.profileManager = profileManager;
    }


    @Override
    public void setBeanName(String name) {
        this.reportName = name;
        this.xfwReportProfile = profileManager.getReport(reportName);
        this.reportProfile = ((XslfoReportProfile) xfwReportProfile).getReportProfile();
    }


    /**
     * {@inheritDoc}.
     */
    public String getTitle() {
        return reportProfile.getT();
    }

    /**
     * {@inheritDoc}.
     */
    public ReportClass getReportProfile() {
        return reportProfile;
    }

    @Override
    public void make(OutputStream outputStream,
                     MakeReportParams reportParams,
                     XslfoCache xslfoCache,
                     IReportDataProvider provider) {
        if (reportParams == null) {
            throw new ArgumentNullException("reportParams");
        }
        if (outputStream == null) {
            throw new ArgumentNullException("outputStream");
        }
        /* TODO: ?????????? ?????????????????
        if (!outputStream.CanWrite)
            throw new ArgumentException("???????????????? ?????????? ???????????????????? ?????? ????????????");
        */
        if (xslfoCache == null) {
            throw new ArgumentNullException("xslFoCache");
        }
        this.xfwReportProfile = profileManager.getReport(reportName);
        this.reportProfile = ((XslfoReportProfile) xfwReportProfile).getReportProfile();

        try {
            makeReport(outputStream, reportParams, xslfoCache, provider);
        } catch (Exception e) {
            throw new RuntimeException("Error by make report", e);
        }
    }

    /**
     * ???????????????? ?????????????????? ???????????????????? ????????????.
     *
     * @param reportParams ?????????????? ?? ???????????????? ?????????????????? ???????????????????????? ????????????
     * @return ?????????????????? ???????????????????? ????????????
     */
    public ReportParams createReportParams(MakeReportParams reportParams) {
        Map<String, String> paramsCollection = reportParams.getReportParamsCollection();
        ReportClass.ParamsClass paramsClass = reportProfile.getParams();
        List<ReportClass.ParamsClass.ParamClass> params = new ArrayList<>();
        if (paramsClass != null) {
            params = paramsClass.getParam();
        }
        return new ReportParams(params, paramsCollection);
    }

    /**
     * ?????????????? ?????????? ????????????:
     * 1. ???????????????? IParamProcessor.process (???????????????????? ????????????????????)
     * 2. ???????????????? ??????????  ILayout.make ?????????????????????????? ????????????
     *
     * @param outputStream ?????????? ?????? ???????????????????????? ????????????
     * @param reportParams ?????????????? ?? ???????????????? ?????????????????? ???????????????????????? ????????????
     * @param xslfoCache   ?????? ?????????????? ?? ?????????????? XSLFO
     * @param provider     ?????????????????? ????????????
     * @throws Exception ?????? ???????????? ???????????? ?? flush ????????????
     */
    /* FIXME
    ?????? ???????????? ???????? ???? XFW2, ???? ???? ?????? ?????????????????????????? ?????? ?????? ???? ?????????????????????? ?????????????????? ???????????? ???? ???????????????????? ????????????,
    ?????????? ???????????????????? ?? ?? ?????????????????????????? ?? ?????????? ????????( ????????????, ??????????????).
    ???????????????????????????????? ???????????????????? ?????????????? ???? ?????????????? ???????????? ???????????????????? ???????????????????????????? ??????????????????????  ?? ??????????????,
    ???????????????? ???? ????????????-???? ?? ?????????????????????? ??????????????????????????. ?????????????? ???????????? ???????? ?????????????? ?????????????????????????????? ?????????????? ????????????.
     */
    /*protected void makeReport(OutputStream outputStream,
                              MakeReportParams reportParams,// Data.XStorageConnection XmlStorage,
                              XslfoCache xslfoCache,
                              IReportDataProvider provider) throws Exception {
        ReportParams params;                    // ?????????????????? ???????????????????? ????????????

        boolean refresh;                       // ?????????????? ?????????????????????????? ???????????????????? FO-??????????????
        boolean dontCacheXslfo;                // ?????????????? ????????, ?????? ???????????????????? XSLFO ???? ???????? ????????????????????

        Map<String, String> paramsCollection = reportParams.getReportParamsCollection();
        // ?????????? ???? ?????????????????? ???????????????????? ????????????, ???? ???? ???????? ?????? ?????????????? ?? ?????????????????? xsl-fo
        String reportXslfoKey = getReportXslfoKey(paramsCollection);

        // ?????????????? ?????????????? ?????????????????????? ????????????
        refresh = Converter.toBoolean(Converter.toString(paramsCollection.get(REFRESH)), false);

        // ?????????????? ?????????????? ?????????????????????? ?????????????????????? XSLFO
        dontCacheXslfo = Converter.toBoolean(Converter.toString(paramsCollection.get(DONT_CACHE_XSLFO)), false);

        // ???????? ?? ???????? xslfo ?????????????????????????????? ?????????????????????? ???????????? ????????????????????
        ByteArrayOutputStream xslfoStream;     // ?????????? ?????? ???????????????? XSLFO ??????????????
        xslfoStream = xslfoCache.getXslfoStream(reportXslfoKey.toString(), refresh || dontCacheXslfo);
        if (xslfoStream == null) {
            xslfoStream = new ByteArrayOutputStream();
        }
        try {
            if (xslfoStream.size() == 0 || refresh) {
                // ???????????????? ???????????? ???? ????????????
                // ?????????? ??????????
               // xslfoStream.reset();
                // ???????????????????? ???????????? ????????????????????
                params = createReportParams(reportParams);

                // ???????????????? ???????????? ?????? ?????????????????? XSL FO ??????????????
                XslFoProfileWriter foWriter = createXlsFoProfilerWriter(xslfoStream,
                        params,
                        reportParams.getCustomData());

                ReportLayoutData data = new ReportLayoutData(foWriter,
                        params,
                        provider,
                        reportProfile,
                        reportParams.getCustomData());

                // ???????????????????? ?????????????????? ???????????????????? ????????????
                processParams(data);

                writeReport(data);
                // ???????????? ???????????? ?? ??????????
                foWriter.flush();
                // ?????????? ?????????????????? xsl-fo ?????????? ???????????????? ?????? ?? ??????????????????,
                // ???????? ???????????????????? ???? ??????????????, ?????? ???????????? ?????????? ???? ????????
                if (!dontCacheXslfo) {
                    xslfoCache.tryToAddXslfoToCache(xslfoStream, reportXslfoKey.toString());
                }
            }

            // ?????????????????? XSLFO ?? ???????????????? ????????????
            renderProfile(outputStream, new ByteArrayInputStream(xslfoStream.toByteArray()),
                    reportParams.getEncoding(),
                    reportParams.getOutputFormat());
            // TODO: ???????? ???????????? ????????????????????, ???? ???????? ?????????? ??????????????????????????????????
            // reportParams.setFormatExtension(format.getFormatExtension());
            reportParams.setReportTitle(getTitle());
        } finally {
            xslfoStream.close();
        }
    }*/
    private void makeReport(
            OutputStream outputStream, MakeReportParams reportParams,
            XslfoCache xslfoCache,
            IReportDataProvider provider
    ) throws Exception {
        ReportParams params = null;// ?????????????????? ???????????????????? ????????????
        Map<String, String> paramsCollection = reportParams.getReportParamsCollection();
        // ?????????????? ?????????????????????????? ???????????????????? FO-??????????????
        boolean refresh = Converter.toBoolean(Converter.toString(paramsCollection.get(REFRESH)), false);
        // ?????????????? ????????, ?????? ???????????????????? XSLFO ???? ???????? ????????????????????
        boolean dontCacheXslfo = Converter.toBoolean(Converter.toString(paramsCollection.get(DONT_CACHE_XSLFO)), false);
        ByteArrayOutputStream xslfoStream = null;// ?????????? ?????? ???????????????? XSLFO ??????????????
        // ?????????? ???? ?????????????????? ???????????????????? ????????????, ???? ???? ???????? ?????? ?????????????? ?? ?????????????????? xsl-fo
        String reportXslfoKey = getReportXslfoKey(paramsCollection);

        // ?????????????????????????? ?????????????? ?? ?????????????????????? ????????????(?????????????????? xsl-fo) ?? ??????????????????(??????????????????).
        // ?????????????????????????? ???? ???????????? ?????????????? ????????????. ??.??. ?????? ???????????? ???????????? ???????? ???????? ?? ?????????????? ??????????????????????
        // ???? ?????????? ???????????????????? ????????????????????????. ???????? ????????, ???????????? ????????????.
        synchronized (this) {
            // ???????? ?? ???????? xslfo ?????????????????????????????? ?????????????????????? ???????????? ????????????????????
            if (xslfoCache.contains(reportXslfoKey)) {
                // ???????? xsl-fo ??????????????, ?????????????????????????????? ???????????? ???????????? ???????????????????? ??????????????, ???? ?????????? ??????
                xslfoStream = xslfoCache.getXslfoStream(reportXslfoKey, true);
            }

            // ???????? ?????? ???? ????????????????????????????????, ?????????? ????????????????????????????????
            if (xslfoStream == null) {
                xslfoStream = new ByteArrayOutputStream();
            }

            if (xslfoStream.size() == 0 || refresh) {
                // ???????????????????? ???????????? ????????????????????
                List<ReportClass.ParamsClass.ParamClass> param = null;
                if (null != reportProfile.getParams()) {
                    param = reportProfile.getParams().getParam();
                }
                params = new ReportParams(param, paramsCollection);

                // ???????????????? ???????????? ?????? ?????????????????? XSL FO ??????????????
                XslFoProfileWriter foWriter = createXlsFoProfilerWriter(xslfoStream,
                        params,
                        reportParams.getCustomData());

                ReportLayoutData data = new ReportLayoutData(foWriter,
                        params,
                        provider,
                        reportProfile,
                        reportParams.getCustomData());
                // ???????????????????? ?????????????????? ???????????????????? ????????????
                processParams(data);

                // ?????????? ????????????-????????????
                if (reportProfile.getLayoutMaster() == null) {
                    foWriter.writeLayoutMaster();
                } else {
                    foWriter.writeLayoutMaster(reportProfile.getLayoutMaster());
                }

                foWriter.startPageSequence();
                foWriter.startPageBody();

                tryBuildReport(foWriter,
                        params,
                        provider,
                        reportParams.getCustomData());

                foWriter.endPageSequence();
                foWriter.endPageBody();

                // ???????????? ???????????? ?? ??????????
                foWriter.flush();

                // ???????? ?????????????? ??????????????????????:
                if (refresh) {
                    // ?????????????? ?????????? ???? ???????? XSLFO
                    xslfoCache.remove(reportXslfoKey);
                }

                // ?????????? ?????????????????? xsl-fo ?????????? ???????????????? ?????? ?? ??????????????????,
                // ???????? ???????????????????? ???? ??????????????, ?????? ???????????? ?????????? ???? ????????
                if (!dontCacheXslfo) {
                    xslfoCache.tryToAddXslfoToCache(xslfoStream, reportXslfoKey);
                }
            }

        } // ???????????? ????????????????????


        // ?????????????????? XSLFO ?? ???????????????? ????????????
        renderProfile(
                outputStream,
                new ByteArrayInputStream(xslfoStream.toByteArray()),
                reportParams.getEncoding(),
                reportParams.getOutputFormat()
        );
        reportParams.setReportTitle(getTitle());
    }

    private void tryBuildReport(XslFoProfileWriter foWriter, ReportParams params, IReportDataProvider provider,
                                Object customData) {
        try {
            buildReport(foWriter, params, provider, customData);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            foWriter.emptyBody(stacktrace);
            logger.error("Error by build report", e);
        }
    }

    /**
     * ???????????? ??????????.
     *
     * @param xslFoProfileWriter ???????????? ReportGenerator
     * @param params             ?????????????????? ???????????????????? ????????????
     * @param provider           ???????????? ?????? ?????????????? ?? ???????????? ????????????
     * @param customData         ???????????????????????????????? ????????????
     * @throws XMLStreamException ?????? ?????????????????????? ?????????????? ??????????????????
     */
    protected abstract void buildReport(XslFoProfileWriter xslFoProfileWriter,
                                        ReportParams params,
                                        IReportDataProvider provider,
                                        Object customData) throws XMLStreamException;


    /**
     * ????????.
     *
     * @param paramsCollection ??????????????????
     * @return ????????
     */
    public String getReportXslfoKey(Map<String, String> paramsCollection) {
        // ???????????? ?? ?????????????? ???????????????? ?????????? ????????????????????
        StringBuilder reportXslfoKey = new StringBuilder(xfwReportProfile.getName());
        // ?????????????????? ???????????? ???? ?????????????????? ??????????????????
        for (String key : paramsCollection.keySet()) {
            String formattedKey = key.toLowerCase();
            if (!formattedKey.equals("name")
                    && !formattedKey.equals(REFRESH)
                    && !formattedKey.equalsIgnoreCase(PARAM_OUTPUT_FORMAT)
                    && !formattedKey.equals(DONT_CACHE_XSLFO)
                    && !formattedKey.equals("execmode")
                    && !formattedKey.equals("tm")) {
                reportXslfoKey.append(',').append(key).append('=').append(paramsCollection.get(key));
            }
        }
        return reportXslfoKey.toString();
    }

    /**
     * ?????????? ?????? ?????????????????? ????????????????.
     *
     * @param layouts ???????????? ??????????????
     * @param data    ???????????? ????????????
     * @throws XMLStreamException - ?????????????????????????? ???? XMLStreamWriter
     */
    private static void writeLayoutsCollection(List<AbstractLayoutClass> layouts, ReportLayoutData data)
            throws XMLStreamException {
        for (AbstractLayoutClass layoutProfile : layouts) {
            if (layoutProfile.isMasterDetailPart()) {
                continue;
            }

            // ?????????????? ?????????????????? Layout
            IReportLayout layout = ReportLayoutFactory.getInstance(layoutProfile);
            // ?? ???????????????? ??????
            layout.make(layoutProfile, data);
        }
    }

    /**
     * ?????????????? ???????????? FOWriter.
     *
     * @param outputStream - ?????????? ?????? ???????????????????????? ????????????
     * @param params       - ?????????????????? ????????????
     * @param customData   ???????????????????????? ????????????
     * @return ???????????? FOWriter
     */
    protected XslFoProfileWriter createXlsFoProfilerWriter(OutputStream outputStream,
                                                           ReportParams params,
                                                           Object customData) {
        XslFoProfileWriter writer = new XslFoProfileWriter(outputStream, reportProfile);
        insertStyles(writer, params, customData);
        //insertScripts(writer, params, serviceConfig, customData);
        return writer;
    }

    /**
     * ?????????????????? ???????????????????????????????? ??????????????.
     *
     * @param rw         ?????????????????? ??????????????
     * @param params     ???????????????????? ???????????????????? ????????????
     * @param customData ???????????????????????? ????????????
     */
    protected void insertScripts(XslFoProfileWriter rw,
                                 ReportParams params,
                                 Object customData) {
        if (reportProfile.getCommon() == null || reportProfile.getCommon().getScript() == null) {
            return;
        }


        for (ReportClass.CommonClass.ScriptClass script : reportProfile.getCommon().getScript()) {
            // ???????? ??????????????
            String scriptBody = null;
            // ???????? ????????????????
            String scriptLanguage = script.getLanguage().toString();

            if (StringUtils.isBlank(script.getSrc())) {
                scriptBody = script.getValue();
            } else if (script.getSrc().startsWith("~/")) {
                // ???????? ???????????? ???????? ??????????????, ?? ???? ???????????????????? ?? "~/", ???? ?????? ???????????????? ??????????????
                rw.addScript(script.getSrc().substring(0, 2), scriptLanguage, script.getEncoding());
                continue;
            } else {
                // ???????? ???????????? ???????? ??????????????, ???? ?????????????????? ????????
                try {
                    Charset encoding = Charset.forName(script.getEncoding());

                    InputStream scriptFile = profileManager.getResource(script.getSrc()
                            /* TODO: ???????????????????? ??????????????????, encoding*/).getInputStream();
                    scriptFile.close();
                } catch (Exception e) {
                    switch (script.getLanguage()) {
                        case JAVASCRIPT:
                            scriptBody = "/*???????????? ?????? ???????????? ??????????*/";
                            break;
                        case VBSCRIPT:
                            scriptBody = "'???????????? ?????? ???????????? ??????????";
                            break;
                        default:
                            throw new IllegalStateException("language = " + script.getLanguage());
                    }
                }
            }

            // ?????????????????? ?????????????????? ?????? ?????????????????????? ???? ????????????????????
            MacroProcessor processor = new MacroProcessor(params);

            // ?????????????? ????????????
            rw.addScript(processor.process(scriptBody), scriptLanguage);
        }
    }

    /**
     * ?????????????????? ???????????????????????????????? ??????????.
     *
     * @param rw         - ?????????????????? ??????????????
     * @param params     - ?????????????????? ???????????????????? ????????????
     * @param customData - ???????????????????????? ????????????
     */
    protected void insertStyles(XslFoProfileWriter rw,
                                ReportParams params,
                                Object customData) {
        ReportClass.CommonClass common = reportProfile.getCommon();
        if (common == null) {
            return;
        }
        List<ReportClass.CommonClass.StyleClassClass> styleClassList = common.getStyleClass();
        styleClassList.stream().forEach(styleClassClass -> {
            rw.getStylesCollection().add(styleClassClass.getN(), inlined(styleClassClass));
        });
    }

    /**
     * ?????????????????? ??????????.
     *
     * @param styleClassClass ??????????.
     * @return ???????????????????????? ??????????
     */
    private String inlined(ReportClass.CommonClass.StyleClassClass styleClassClass) {
        if (inliner != null) {
            return inliner.inlined(styleClassClass.getValue());
        } else {
            return styleClassClass.getValue();
        }

    }

    /**
     * ???????????????????????????? ?????????????????? ????????????????????.
     *
     * @param layoutData ???????????? ??????????????
     */
    protected void processParams(ReportLayoutData layoutData) {
        IReportParamProcessor processor;    // ???????????????????? ???????????????????? ????????????

        // ???????????????????? ?????????????????? ?????????????????????? ???????????? ??????????????????????, ?????????????????? ??????????????????????????
        // ???????????????????????? ?????? ??????????????????????
        if (reportProfile.getParamProcessors() != null) {
            /* TODO: ???????? ??????-???? ????????????
            foreach (abstractparamprocessorClass ProcessorProfile in reportProfile.paramprocessors)
            {
                if (null == (processor = (ReportObjectFactory.getInstance(ProcessorProfile)
                                            as ParamProcessors.IReportParamProcessor)))
                throw new NotImplementedException();

                processor.process(ProcessorProfile, layoutData);
            }
            */
        }
    }

    /**
     * ?????????? ?????????????????? ????????????.
     *
     * @param data ???????? ???????????? ?????? ???????????????????? ??????????????????
     * @throws Exception ?????? ??????????????????
     */
    protected void writeReport(ReportLayoutData data) throws Exception {

        IReportDataProvider provider = data.getDataProvider();

        // ?????????? ????????????-????????????
        if (reportProfile.getLayoutMaster() == null) {
            data.getRepGen().writeLayoutMaster();
        } else {
            data.getRepGen().writeLayoutMaster(reportProfile.getLayoutMaster());
        }
        if (reportProfile.getLayouts() != null) {
            // ???????????????? ???? ???????? ??????-??????????.
            for (Object layoutType : reportProfile.getLayouts().getAbstractLayout()) {

                //?????????? page-sequence
                if (layoutType.getClass().getName() == ReportClass.PageSequenceClass.class.getName()) {
                    ReportClass.PageSequenceClass pageSequenceClass = (ReportClass.PageSequenceClass) layoutType;

                    //???????????????? ?????????????? page-sequence
                    data.getRepGen().startPageSequence(pageSequenceClass);

                    // ?????????????????? ?????????????? title
                    data.getRepGen().writeTitle(reportProfile.getT());

                    //region ?????????????????? ????????????????
                    if (pageSequenceClass.getPageHeader() != null) {
                        if (pageSequenceClass.getPageHeader().getLayouts() != null) {
                            // ?????????????? ?????????? ???????????????? ???????????????????????? layout-????(ArrayList ???? ????????????????)
                            List layouts = pageSequenceClass.getPageHeader().getLayouts().getAbstractLayout();
                            // ?????????????? static-content
                            data.getRepGen().startPageHeader();
                            // ???????????? ???????????????? ???????????????????? ???????? ?? ???????? XSL FO
                            writeLayoutsCollection(layouts, data);
                            data.getRepGen().endPageHeader();
                        }
                    }
                    if (pageSequenceClass.getPageBody() != null) {
                        if (pageSequenceClass.getPageBody().getLayouts() != null) {
                            // ?????????????? ?????????? ???????????????? ???????????????????????? layout-????(ArrayList ???? ????????????????)
                            List layouts = pageSequenceClass.getPageBody().getLayouts().getAbstractLayout();
                            //???????????????? ???????????? flow
                            data.getRepGen().startPageBody();
                            // ???????????? ???????????????? ???????????????????? ???????? ?? ???????? XSL FO
                            writeLayoutsCollection(layouts, data);
                            //???????????????? ???????????? flow
                            data.getRepGen().endPageBody();
                        }
                    }
                    if (pageSequenceClass.getPageFooter() != null) {
                        if (pageSequenceClass.getPageFooter().getLayouts() != null) {
                            // ?????????????? ?????????? ???????????????? ???????????????????????? layout-????(ArrayList ???? ????????????????)
                            List layouts = pageSequenceClass.getPageFooter().getLayouts().getAbstractLayout();
                            // ?????????????? static-content
                            data.getRepGen().startPageBottom();
                            // ???????????? ???????????????? ???????????????????? ???????? ?? ???????? XSL FO
                            writeLayoutsCollection(layouts, data);
                            data.getRepGen().endPageBottom();
                        }
                    }
                    //endregion

                    // ???????????????? ?????????????? page-sequence
                    data.getRepGen().endPageSequence();

                } else if (layoutType.getClass().getName() == LayoutsClass.class.getName()) {
                    //???????????????? ?????????????? page-sequence
                    data.getRepGen().startPageSequence();
                    // ?????????????????? ?????????????? title
                    data.getRepGen().writeTitle(reportProfile.getT());
                    //???????????????? ???????????? flow
                    data.getRepGen().startPageBody();

                    // ?????????????? ?????????? ???????????????? ???????????????????????? layout-????(ArrayList ???? ????????????????)
                    List layouts = ((LayoutsClass) layoutType).getAbstractLayout();
                    // ???????????? ???????????????? ???????????????????? ???????? ?? ???????? XSL FO
                    writeLayoutsCollection(layouts, data);
                    // ???????????????? ?????????????? flow
                    data.getRepGen().endPageBody();
                    // ???????????????? ?????????????? page-sequence
                    data.getRepGen().endPageSequence();
                }
            }
        }
    }

    /**
     * ??????????, ???????????????????????? ?????????????????? ????????????.
     *
     * @param outputStream  ???????????????? ??????????
     * @param profileStream ?????????? ?? XSL-FO
     * @param textEncoding  ?????????????????? ?????????????????? ????????????
     * @param outputFormat  ???????????????? ????????????
     * @throws Exception ?????? ?????????????????? ????????????
     */
    protected void renderProfile(OutputStream outputStream,
                                 InputStream profileStream,
                                 Charset textEncoding,
                                 OutputFormat outputFormat) throws Exception {


        // ???????????? ?? ???????????????????? ??????????????????
        String[] outputFormats = null; /*FIXME JXFW-1092 = serviceConfig.getOutputFormatNames();*/

        // ???????????????? ??????????
        /* TODO: ?????? ??????
        System.Reflection.Assembly Assembly = ReportObjectFactory.LoadAssembly(format.getAssembly());
        Type MyType = Assembly.GetType(format.Class, true, false);
        System.Reflection.ConstructorInfo MyConstructor = MyType.GetConstructor(Type.EmptyTypes);
        ?????????? ??????
        */

        IRenderer renderer = new HtmlRenderer(profileManager);
        switch (outputFormat) {
            case PDF:
            case WORD:
            case XPS:
                renderer = xslFo2WordRenderer;
                break;
            case EXCEL:
            case EXCEL2010:
                renderer = msExcelRenderer;
                break;
        }
        /*
        try {
            profileStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        renderer.render(profileStream, outputStream, textEncoding, LOCALE, outputFormat);
    }

    @Autowired
    public void setMsExcelRenderer(MsExcelRenderer msExcelRenderer) {
        this.msExcelRenderer = msExcelRenderer;
    }

    @Autowired
    public void setXslFo2WordRenderer(XslFo2WordRenderer xslFo2WordRenderer) {
        this.xslFo2WordRenderer = xslFo2WordRenderer;
    }

    /**
     * ???????????????????????????? ?????????????? ?? ????????????.
     *
     * @return String   - ???????????????????????? ????????????
     */
    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * ???????????? ???????????????????? ????????????.
     *
     * @return ???????????? ???????????????????? ????????????
     */
    public List<AbstractDataSourceClass> getListAbstactDataSourceClass() {
        ReportClass.DataSourcesClass dss = reportProfile.getDataSources();
        if (dss == null) {
            return new ArrayList<>();
        }
        return reportProfile.getDataSources().getAbstractDataSource();
    }
}
