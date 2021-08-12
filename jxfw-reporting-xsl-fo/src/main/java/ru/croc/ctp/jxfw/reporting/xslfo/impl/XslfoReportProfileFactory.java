package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;
import ru.croc.ctp.jxfw.core.reporting.impl.XfwReportProfileFactoryImpl;

/**
 * Имплементация для XSL_FO.
 * @since 1.6
 * @author OKrutova
 */
@Service
public class XslfoReportProfileFactory extends XfwReportProfileFactoryImpl {

    /**
     * Расширение шаблонов отчетов XSL-FO.
     */
    private static final String TEMPLATE_EXT = ".xml";

    @Override
    public XfwReportProfile getInstance(Resource resource) {
        return new XslfoReportProfile(resource);
    }

    @Override
    public String getReportProfileExtension() {
        return TEMPLATE_EXT;
    }
}
