package ru.croc.ctp.jxfw.core.reporting.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileFactory;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Реализация ищет шаблон отчета сначала во внешней директории, потом в класспасе.
 * Если шаблон с одинаковым именем есть и там, и там, то будет использован шаблон из внешней директории.
 *
 * @author OKrutova
 * @since 1.6
 */
@Service
public class XfwReportProfileManagerImpl implements XfwReportProfileManager {

    private static final String REPORTS_FOLDER = "reports/";
    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final ApplicationHome APPLICATION_HOME = new ApplicationHome();


    private final XfwReportProfileFactory reportProfileFactory;


    private final String reportsPath;


    /**
     * Конструктор.
     *
     * @param reportProfileFactory фабрика профилей отчетов , зависит от движка
     * @param reportsPath          путь к директории с шаблонами в файловой системе.
     */
    public XfwReportProfileManagerImpl(@Autowired(required = false) XfwReportProfileFactory reportProfileFactory,
                                       @Value("${jxfw.reporting.directory:#{null}}") String reportsPath) {
        this.reportProfileFactory = reportProfileFactory;
        this.reportsPath = reportsPath;
    }


    @Override
    public XfwReportProfile getReport(String reportName) {
        Iterable<XfwReportProfile> reports = getReports(reportName);
        if (reports.iterator().hasNext()) {
            return reports.iterator().next();
        }
        throw new IllegalArgumentException("Report profile not found " + reportName);
    }

    @Override
    public Resource getResource(String fileName) {
        if (fileName.startsWith("http:") || fileName.startsWith("https:")) {
            return resolver.getResource("url:" + fileName);
        } else {
            if (reportsPath != null) {
                Path path = getAppHomePath().resolve(Paths.get(reportsPath)).resolve(Paths.get(fileName));
                Resource resource = resolver.getResource("file:" + path);
                if (resource.exists()) {
                    return resource;
                }
            }
            return resolver.getResource("classpath:" + REPORTS_FOLDER + fileName);
        }

    }

    @Override
    public Iterable<XfwReportProfile> getReports() {
        return getReports(null);
    }

    private Iterable<XfwReportProfile> getReports(String reportName) {
        if (reportProfileFactory == null) {
            return Collections.EMPTY_LIST;
        }
        List<XfwReportProfile> reports = new ArrayList<>();
        try {
            // сначала поищем по директории
            if (reportsPath != null) {
                Path path = getAppHomePath().resolve(Paths.get(reportsPath));
                File reportsDir = path.toFile();

                if (reportsDir.exists()) {
                    FilenameFilter filter = (dir, name) ->
                            name.matches(reportProfileFactory.getProfileTemplateRegex(reportName));
                    for (File file : reportsDir.listFiles(filter)) {
                        reports.add(reportProfileFactory.getInstance(
                                resolver.getResource("file:" + file.getAbsolutePath())));
                    }
                }
            }

            // потом поищем по класспасу
            Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + REPORTS_FOLDER + reportProfileFactory.getProfileTemplate(reportName));
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].exists()) {
                    reports.add(reportProfileFactory.getInstance(resources[i]));
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return reports;
    }

    private final Path getAppHomePath() {
        return APPLICATION_HOME.getDir().toPath();
    }


}
