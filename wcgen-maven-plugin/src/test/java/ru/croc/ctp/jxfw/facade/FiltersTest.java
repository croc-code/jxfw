package ru.croc.ctp.jxfw.facade;

import static com.google.common.collect.Maps.newHashMap;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.EXCLUDE_FILTERS;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.INCLUDE_FILTERS;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.croc.ctp.jxfw.generator.FacadeGenerator;
import ru.croc.ctp.jxfw.metamodel.filter.impl.ClassifierFilterFactoryImpl;
import ru.croc.ctp.jxfw.metamodel.filter.impl.AllClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.impl.NoneClassifierFilter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class FiltersTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    Log log;
    Map<String, Object> options = newHashMap();

    @Before
    public void init(){
        log = mock(Log.class);


    }


    @Test
    public void testNoFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(3)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(1)).debug(startsWith("start  creating DataSource "));


    }

    @Test
    public void testIncludeModelNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{"XFWModel"},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(1)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(1)).debug(startsWith("start  creating DataSource "));


    }


    @Test
    public void testExcludeModelNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{"XFWModel"},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(2)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(0)).debug(startsWith("start  creating DataSource "));


    }

    @Test
    public void testIncludeAndExcludeModelNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{"XFWModel", "XFWModel2"},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{"XFWModel"},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(1)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(0)).debug(startsWith("start  creating DataSource "));


    }


    @Test
    public void testSeveralIncludesModelNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
        ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{"XFWModel", "XFWModel2"},
                new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(2)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(1)).debug(startsWith("start  creating DataSource "));


    }
    @Test
    public void testSeveralExcludesModelNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{"XFWModel", "XFWModel2"},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(1)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(0)).debug(startsWith("start  creating DataSource "));


    }

    @Test
    public void testFqClassNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{"ru.croc.ctp.audit.domain.ActionLog"},  new String[]{},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(1)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(0)).debug(startsWith("start  creating DataSource "));


    }
    @Test
    public void testSimpleClassNameFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{"LocalityNames"},  new String[]{},
                        new String[]{}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(0)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(1)).debug(startsWith("start  creating DataSource "));


    }

    /*
   Имена классов в тестовых моделях:
    ru.croc.ctp.audit.domain.ActionLog
ru.croc.ctp.integration.domain.MessageType
ru.croc.ctp.scheduler.model.XfwTask
ru.croc.ctp.survey.datasource.LocalityNames
     */

    @Test
    public void testRegexpFilter() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{"ru.croc.ctp.*"}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(3)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(1)).debug(startsWith("start  creating DataSource "));

    }
    /*
   Имена классов в тестовых моделях:
    ru.croc.ctp.audit.domain.ActionLog
ru.croc.ctp.integration.domain.MessageType
ru.croc.ctp.scheduler.model.XfwTask
ru.croc.ctp.survey.datasource.LocalityNames
     */

    @Test
    public void testRegexpFilter2() throws IOException {
        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{"ru.*.domain.*"}, new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters( new String[]{},  new String[]{},
                        new String[]{}, new NoneClassifierFilter()));
        FacadeGenerator facadeGenerator = new FacadeGenerator(
                Paths.get("src/test/resources/models/filter-models"),
                temporaryFolder.newFolder("wcgen-maven-plugin-tests").toPath(),
                options, log);
        facadeGenerator.generate();
        verify(log, times(2)).debug(startsWith("start  creating Controller for class "));
        verify(log, times(0)).debug(startsWith("start  creating DataSource "));


    }

}
