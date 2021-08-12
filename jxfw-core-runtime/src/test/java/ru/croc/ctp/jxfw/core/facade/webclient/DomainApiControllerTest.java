package ru.croc.ctp.jxfw.core.facade.webclient;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.store.StoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DomainApiControllerTest {

    private DomainApiController domainApiController;

    @Mock
    private ResourceStore resourceStore;
    @Mock
    private DomainFacadeIgnoreService domainFacadeIgnoreService;
    @Mock
    private StoreService storeService;
    @Mock
    private StoreResultToService storeResultToService;

    @BeforeClass
    public static void initClass() {
        final TestLoggerAppender appender = new TestLoggerAppender();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        appender.setContext(lc);
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(DomainApiController.class);
        logger.addAppender(appender);
    }

    @Before
    public void init() {
        TestLoggerAppender.events.clear();

        MockitoAnnotations.initMocks(this);
        domainApiController = new DomainApiController(
            resourceStore,
            null,
            null,
            null
        );
        domainApiController.setDomainFacadeIgnoreService(domainFacadeIgnoreService);
        domainApiController.setStoreService(storeService);
        domainApiController.setStoreResultToService(storeResultToService);
    }

    @Test(expected = RuntimeException.class)
    public void storeTest() {
        DomainTo domainTo = new DomainTo("testType", "testId");
        List<DomainTo> uow = Collections.singletonList(domainTo);
        Mockito.when(storeService.store(uow, Collections.emptyList(), Locale.ENGLISH, ""))
                .thenThrow(new RuntimeException("testException"));

        try {
            domainApiController.store(uow, 0, Collections.emptyList(), Collections.emptyList(), "", Locale.ENGLISH, null, null);
        } finally {
            Assert.assertTrue(TestLoggerAppender.events.stream().
                    anyMatch(lev -> lev.getMessage().equals("Exception on store UoW by request ./api/_store uow: \n"
                            + "[DomainTo[type=testType, id=testId, isNew=false, isRemoved=false, {}]] \n"
                            + " $sync: 0"))
            );
        }
    }

    private static class TestLoggerAppender extends AppenderBase<ILoggingEvent> {

        public static List<ILoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            events.add(eventObject);
        }
    }
}
