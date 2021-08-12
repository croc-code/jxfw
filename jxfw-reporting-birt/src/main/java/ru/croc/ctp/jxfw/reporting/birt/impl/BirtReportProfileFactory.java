package ru.croc.ctp.jxfw.reporting.birt.impl;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;
import ru.croc.ctp.jxfw.core.reporting.impl.XfwReportProfileFactoryImpl;

import java.io.File;


/**
 * Имплементация для BIRT.
 *
 * @since 1.6
 * @author OKrutova
 */
@Service
public class BirtReportProfileFactory extends XfwReportProfileFactoryImpl {

    /**
     * Расширение шаблонов отчетов BIRT.
     */
    private static final String TEMPLATE_EXT = ".rptdesign";

    @Override
    public XfwReportProfile getInstance(Resource resource) {
        return new BirtReportProfile(resource);
    }

    @Override
    public String getReportProfileExtension() {
        return TEMPLATE_EXT;
    }


}
