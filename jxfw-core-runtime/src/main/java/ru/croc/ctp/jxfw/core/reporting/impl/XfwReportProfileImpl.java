package ru.croc.ctp.jxfw.core.reporting.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Базовый класс описания отчета.
 *
 * @author OKrutova
 * @since 1.6
 */
public abstract class XfwReportProfileImpl implements XfwReportProfile {

    private Resource resource;

    /**
     * Конструктор.
     *
     * @param resource ресурс из класспаса
     */
    public XfwReportProfileImpl(Resource resource) {
        this.resource = resource;
    }

    @Override
    public InputStream getStream() throws IOException {
        if (resource != null) {
            return resource.getInputStream();
        }
        throw new IOException("No resource found");
    }


    public String getFileName() {
        return resource.getFilename();
    }

    @Override
    public String getName() {
       return FilenameUtils.getBaseName(getFileName());
    }


}
