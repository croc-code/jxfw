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
 * Класс реализующий логику построения отчета.
 * Created by vsavenkov on 13.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
/*
  FIXME класс сделан абстрактным, т.к. текущая реализация не умеет в общем виде строить отчет, а требует от конкретных
  классов отчетов знания о способе получения данных.
 */
public abstract class XfwXslfoReport implements IReport, BeanNameAware {
    /**
     * Логгер.
     */
    protected static final Logger logger = LoggerFactory.getLogger(XfwXslfoReport.class);
    /**
     * Обновить.
     */
    public static final String REFRESH = "Refresh";
    /**
     * Не кешировать.
     */
    public static final String DONT_CACHE_XSLFO = "DontCacheXslfo";
    /**
     * Не кешировать.
     */
    public static final String PARAM_OUTPUT_FORMAT = "format";

    /**
     * Десереализованный профиль отчета.
     */
    private ReportClass reportProfile;


    private String reportName;

    private MsExcelRenderer msExcelRenderer;

    private XslFo2WordRenderer xslFo2WordRenderer;

    /**
     * Десереализованный профиль отчета.
     */
    private XfwReportProfile xfwReportProfile;

    /**
     * Локализация.
     */
    protected static final Locale LOCALE = new Locale("ru");


    private final IStyleInliner inliner;

    private final XfwReportProfileManager profileManager;

    /**
     * Конструктор.
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
        /* TODO: такое возможно?
        if (!outputStream.CanWrite)
            throw new ArgumentException("Выходной поток недоступен для записи");
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
     * Сорздаёт коллекцию параметров отчёта.
     *
     * @param reportParams Входные и выходные параметры формируемого отчета
     * @return коллекцию параметров отчёта
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
     * Главный метод класса:
     * 1. Вызывает IParamProcessor.process (обработчик параметров)
     * 2. Вызывает метод  ILayout.make представления отчета
     *
     * @param outputStream Поток для формирования отчета
     * @param reportParams Входные и выходные параметры формируемого отчета
     * @param xslfoCache   Кеш отчетов в формате XSLFO
     * @param provider     Провайдер данных
     * @throws Exception При записи отчета и flush потока
     */
    /* FIXME
    это видимо порт из XFW2, но он был переопределен так как не реализованл получение данных из источников данных,
    через провайдера и с представление в общем виде( скаляр, таблица).
    Переопределенная реализация требует от каждого класса наследника самостоятельно разбираться  с данными,
    получать их откуда-то и формировать представление. Поэтому видимо было сделано переопределение данного метода.
     */
    /*protected void makeReport(OutputStream outputStream,
                              MakeReportParams reportParams,// Data.XStorageConnection XmlStorage,
                              XslfoCache xslfoCache,
                              IReportDataProvider provider) throws Exception {
        ReportParams params;                    // Коллекция параметров отчета

        boolean refresh;                       // Признак необходимости обновления FO-профиля
        boolean dontCacheXslfo;                // Признак того, что полученный XSLFO не надо кешировать

        Map<String, String> paramsCollection = reportParams.getReportParamsCollection();
        // Набор не системных параметров отчета, он же ключ для доступа к хранилищу xsl-fo
        String reportXslfoKey = getReportXslfoKey(paramsCollection);

        // Получим признак перерисовки отчета
        refresh = Converter.toBoolean(Converter.toString(paramsCollection.get(REFRESH)), false);

        // Получим признак кеширования полученного XSLFO
        dontCacheXslfo = Converter.toBoolean(Converter.toString(paramsCollection.get(DONT_CACHE_XSLFO)), false);

        // Ищем в кеше xslfo соответствующий переданному набору параметров
        ByteArrayOutputStream xslfoStream;     // Поток для хранения XSLFO профиля
        xslfoStream = xslfoCache.getXslfoStream(reportXslfoKey.toString(), refresh || dontCacheXslfo);
        if (xslfoStream == null) {
            xslfoStream = new ByteArrayOutputStream();
        }
        try {
            if (xslfoStream.size() == 0 || refresh) {
                // Поставлю курсор на начало
                // Очищу поток
               // xslfoStream.reset();
                // Произведем разбор параметров
                params = createReportParams(reportParams);

                // Создадим объект для рисования XSL FO профиля
                XslFoProfileWriter foWriter = createXlsFoProfilerWriter(xslfoStream,
                        params,
                        reportParams.getCustomData());

                ReportLayoutData data = new ReportLayoutData(foWriter,
                        params,
                        provider,
                        reportProfile,
                        reportParams.getCustomData());

                // Произведем обработку параметров отчета
                processParams(data);

                writeReport(data);
                // Сброшу данные в поток
                foWriter.flush();
                // После отрисовки xsl-fo нужно положить его в хранилище,
                // если специально не указали, что делать этого не надо
                if (!dontCacheXslfo) {
                    xslfoCache.tryToAddXslfoToCache(xslfoStream, reportXslfoKey.toString());
                }
            }

            // рендеринг XSLFO в выходной формат
            renderProfile(outputStream, new ByteArrayInputStream(xslfoStream.toByteArray()),
                    reportParams.getEncoding(),
                    reportParams.getOutputFormat());
            // TODO: если вернут расширение, то надо будет раскомментировать
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
        ReportParams params = null;// Коллекция параметров отчета
        Map<String, String> paramsCollection = reportParams.getReportParamsCollection();
        // Признак необходимости обновления FO-профиля
        boolean refresh = Converter.toBoolean(Converter.toString(paramsCollection.get(REFRESH)), false);
        // Признак того, что полученный XSLFO не надо кешировать
        boolean dontCacheXslfo = Converter.toBoolean(Converter.toString(paramsCollection.get(DONT_CACHE_XSLFO)), false);
        ByteArrayOutputStream xslfoStream = null;// Поток для хранения XSLFO профиля
        // Набор не системных параметров отчета, он же ключ для доступа к хранилищу xsl-fo
        String reportXslfoKey = getReportXslfoKey(paramsCollection);

        // Синхронизация доступа к разделяемым данным(хранилище xsl-fo) и процессам(отрисовка).
        // Синхронизирую на основе объекта отчета. Т.е. два отчета одного типа даже с разными параметарми
        // не могут рисоваться одноврменнно. Один ждет, другой рисует.
        synchronized (this) {
            // Ищем в кеше xslfo соответствующий переданному набору параметров
            if (xslfoCache.contains(reportXslfoKey)) {
                // Если xsl-fo профиль, соответствующий такому набору параметров имеется, то верну его
                xslfoStream = xslfoCache.getXslfoStream(reportXslfoKey, true);
            }

            // Если ещё не инициализировали, нужно инициализировать
            if (xslfoStream == null) {
                xslfoStream = new ByteArrayOutputStream();
            }

            if (xslfoStream.size() == 0 || refresh) {
                // Произведем разбор параметров
                List<ReportClass.ParamsClass.ParamClass> param = null;
                if (null != reportProfile.getParams()) {
                    param = reportProfile.getParams().getParam();
                }
                params = new ReportParams(param, paramsCollection);

                // Создадим объект для рисования XSL FO профиля
                XslFoProfileWriter foWriter = createXlsFoProfilerWriter(xslfoStream,
                        params,
                        reportParams.getCustomData());

                ReportLayoutData data = new ReportLayoutData(foWriter,
                        params,
                        provider,
                        reportProfile,
                        reportParams.getCustomData());
                // Произведем обработку параметров отчета
                processParams(data);

                // Рисую лэйаут-мастер
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

                // Сброшу данные в поток
                foWriter.flush();

                // Если выбрана перерисовка:
                if (refresh) {
                    // Удаляем отчет из кеша XSLFO
                    xslfoCache.remove(reportXslfoKey);
                }

                // После отрисовки xsl-fo нужно положить его в хранилище,
                // если специально не указали, что делать этого не надо
                if (!dontCacheXslfo) {
                    xslfoCache.tryToAddXslfoToCache(xslfoStream, reportXslfoKey);
                }
            }

        } // Снятие блокировки


        // рендеринг XSLFO в выходной формат
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
     * Строит отчёт.
     *
     * @param xslFoProfileWriter объект ReportGenerator
     * @param params             Коллекция параметров отчета
     * @param provider           Объект для доступа к данным отчета
     * @param customData         пользовательские данные
     * @throws XMLStreamException при неожиданных ошибках обработки
     */
    protected abstract void buildReport(XslFoProfileWriter xslFoProfileWriter,
                                        ReportParams params,
                                        IReportDataProvider provider,
                                        Object customData) throws XMLStreamException;


    /**
     * Ключ.
     *
     * @param paramsCollection параметры
     * @return ключ
     */
    public String getReportXslfoKey(Map<String, String> paramsCollection) {
        // Ключом к профилю является набор параметров
        StringBuilder reportXslfoKey = new StringBuilder(xfwReportProfile.getName());
        // Учитываем только не системные аргументы
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
     * Метод для отрисовки лейаутов.
     *
     * @param layouts список макетов
     * @param data    данные макета
     * @throws XMLStreamException - выбрасывается из XMLStreamWriter
     */
    private static void writeLayoutsCollection(List<AbstractLayoutClass> layouts, ReportLayoutData data)
            throws XMLStreamException {
        for (AbstractLayoutClass layoutProfile : layouts) {
            if (layoutProfile.isMasterDetailPart()) {
                continue;
            }

            // Получим очередной Layout
            IReportLayout layout = ReportLayoutFactory.getInstance(layoutProfile);
            // И отрисуем его
            layout.make(layoutProfile, data);
        }
    }

    /**
     * Создает объект FOWriter.
     *
     * @param outputStream - поток для формирования отчета
     * @param params       - параметры отчёта
     * @param customData   произвольные данные
     * @return объект FOWriter
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
     * Вставляет пользовательские скрипты.
     *
     * @param rw         генератор отчетов
     * @param params     кооллекция параметров отчета
     * @param customData произвольные данные
     */
    protected void insertScripts(XslFoProfileWriter rw,
                                 ReportParams params,
                                 Object customData) {
        if (reportProfile.getCommon() == null || reportProfile.getCommon().getScript() == null) {
            return;
        }


        for (ReportClass.CommonClass.ScriptClass script : reportProfile.getCommon().getScript()) {
            // тело скрипта
            String scriptBody = null;
            // язык сценария
            String scriptLanguage = script.getLanguage().toString();

            if (StringUtils.isBlank(script.getSrc())) {
                scriptBody = script.getValue();
            } else if (script.getSrc().startsWith("~/")) {
                // Если указан файл скрипта, и он начинается с "~/", то его включаем ссылкой
                rw.addScript(script.getSrc().substring(0, 2), scriptLanguage, script.getEncoding());
                continue;
            } else {
                // Если указан файл скрипта, то считываем файл
                try {
                    Charset encoding = Charset.forName(script.getEncoding());

                    InputStream scriptFile = profileManager.getResource(script.getSrc()
                            /* TODO: установить кодировку, encoding*/).getInputStream();
                    scriptFile.close();
                } catch (Exception e) {
                    switch (script.getLanguage()) {
                        case JAVASCRIPT:
                            scriptBody = "/*ошибка при чтении файла*/";
                            break;
                        case VBSCRIPT:
                            scriptBody = "'ошибка при чтении файла";
                            break;
                        default:
                            throw new IllegalStateException("language = " + script.getLanguage());
                    }
                }
            }

            // разборщик выражения для подстановки из параметров
            MacroProcessor processor = new MacroProcessor(params);

            // добавим скрипт
            rw.addScript(processor.process(scriptBody), scriptLanguage);
        }
    }

    /**
     * Вставляет пользовательские стили.
     *
     * @param rw         - Генератор отчетов
     * @param params     - Коллекция параметров отчета
     * @param customData - Произвольные данные
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
     * Инлайнить стиль.
     *
     * @param styleClassClass стиль.
     * @return заинлайненый стиль
     */
    private String inlined(ReportClass.CommonClass.StyleClassClass styleClassClass) {
        if (inliner != null) {
            return inliner.inlined(styleClassClass.getValue());
        } else {
            return styleClassClass.getValue();
        }

    }

    /**
     * Дополнительная обработка параметров.
     *
     * @param layoutData Данные лэйаута
     */
    protected void processParams(ReportLayoutData layoutData) {
        IReportParamProcessor processor;    // Обработчик параметров отчета

        // Обработаем параметры посредством вызова процессоров, указанных пользователем
        // Проитерируем все обработчики
        if (reportProfile.getParamProcessors() != null) {
            /* TODO: надо что-то делать
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
     * Метод отрисовки отчета.
     *
     * @param data абор данных для выполнения отрисовки
     * @throws Exception при отрисовке
     */
    protected void writeReport(ReportLayoutData data) throws Exception {

        IReportDataProvider provider = data.getDataProvider();

        // Рисую лэйаут-мастер
        if (reportProfile.getLayoutMaster() == null) {
            data.getRepGen().writeLayoutMaster();
        } else {
            data.getRepGen().writeLayoutMaster(reportProfile.getLayoutMaster());
        }
        if (reportProfile.getLayouts() != null) {
            // пройдусь по всем лей-аутам.
            for (Object layoutType : reportProfile.getLayouts().getAbstractLayout()) {

                //Рисую page-sequence
                if (layoutType.getClass().getName() == ReportClass.PageSequenceClass.class.getName()) {
                    ReportClass.PageSequenceClass pageSequenceClass = (ReportClass.PageSequenceClass) layoutType;

                    //открываю элемент page-sequence
                    data.getRepGen().startPageSequence(pageSequenceClass);

                    // Записываю элемент title
                    data.getRepGen().writeTitle(reportProfile.getT());

                    //region отрисовка регионов
                    if (pageSequenceClass.getPageHeader() != null) {
                        if (pageSequenceClass.getPageHeader().getLayouts() != null) {
                            // Получим набор профилей отображаемых layout-ов(ArrayList из лейаутов)
                            List layouts = pageSequenceClass.getPageHeader().getLayouts().getAbstractLayout();
                            // Добавлю static-content
                            data.getRepGen().startPageHeader();
                            // Скажем лейаутам нарисовать себя в виде XSL FO
                            writeLayoutsCollection(layouts, data);
                            data.getRepGen().endPageHeader();
                        }
                    }
                    if (pageSequenceClass.getPageBody() != null) {
                        if (pageSequenceClass.getPageBody().getLayouts() != null) {
                            // Получим набор профилей отображаемых layout-ов(ArrayList из лейаутов)
                            List layouts = pageSequenceClass.getPageBody().getLayouts().getAbstractLayout();
                            //открываю эемент flow
                            data.getRepGen().startPageBody();
                            // Скажем лейаутам нарисовать себя в виде XSL FO
                            writeLayoutsCollection(layouts, data);
                            //закрываю эемент flow
                            data.getRepGen().endPageBody();
                        }
                    }
                    if (pageSequenceClass.getPageFooter() != null) {
                        if (pageSequenceClass.getPageFooter().getLayouts() != null) {
                            // Получим набор профилей отображаемых layout-ов(ArrayList из лейаутов)
                            List layouts = pageSequenceClass.getPageFooter().getLayouts().getAbstractLayout();
                            // Добавлю static-content
                            data.getRepGen().startPageBottom();
                            // Скажем лейаутам нарисовать себя в виде XSL FO
                            writeLayoutsCollection(layouts, data);
                            data.getRepGen().endPageBottom();
                        }
                    }
                    //endregion

                    // закрываю элемент page-sequence
                    data.getRepGen().endPageSequence();

                } else if (layoutType.getClass().getName() == LayoutsClass.class.getName()) {
                    //открываю элемент page-sequence
                    data.getRepGen().startPageSequence();
                    // Записываю элемент title
                    data.getRepGen().writeTitle(reportProfile.getT());
                    //открываю эемент flow
                    data.getRepGen().startPageBody();

                    // Получим набор профилей отображаемых layout-ов(ArrayList из лейаутов)
                    List layouts = ((LayoutsClass) layoutType).getAbstractLayout();
                    // Скажем лейаутам нарисовать себя в виде XSL FO
                    writeLayoutsCollection(layouts, data);
                    // закрываю элемент flow
                    data.getRepGen().endPageBody();
                    // закрываю элемент page-sequence
                    data.getRepGen().endPageSequence();
                }
            }
        }
    }

    /**
     * Метод, производящий рендеринг отчета.
     *
     * @param outputStream  Выходной поток
     * @param profileStream Поток с XSL-FO
     * @param textEncoding  Кодировка выходного потока
     * @param outputFormat  Выходной формат
     * @throws Exception при отрисовке отчёта
     */
    protected void renderProfile(OutputStream outputStream,
                                 InputStream profileStream,
                                 Charset textEncoding,
                                 OutputFormat outputFormat) throws Exception {


        // Список с возможными форматами
        String[] outputFormats = null; /*FIXME JXFW-1092 = serviceConfig.getOutputFormatNames();*/

        // поднимаю класс
        /* TODO: был так
        System.Reflection.Assembly Assembly = ReportObjectFactory.LoadAssembly(format.getAssembly());
        Type MyType = Assembly.GetType(format.Class, true, false);
        System.Reflection.ConstructorInfo MyConstructor = MyType.GetConstructor(Type.EmptyTypes);
        стало так
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
     * Преобразование объекта к строке.
     *
     * @return String   - Наименование отчета
     */
    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * Список источников данных.
     *
     * @return список источников данных
     */
    public List<AbstractDataSourceClass> getListAbstactDataSourceClass() {
        ReportClass.DataSourcesClass dss = reportProfile.getDataSources();
        if (dss == null) {
            return new ArrayList<>();
        }
        return reportProfile.getDataSources().getAbstractDataSource();
    }
}
