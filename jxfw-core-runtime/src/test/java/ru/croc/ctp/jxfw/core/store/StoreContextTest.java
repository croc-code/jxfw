package ru.croc.ctp.jxfw.core.store;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainApiController;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.StoreResultToService;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;

import javax.management.remote.JMXPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StoreContextTest {

    @Test
    public void storeContextCreation() {
        final List<String> hints = new ArrayList<>();
        final TimeZone timeZone = TimeZone.getDefault();
        final Locale locale = Locale.getDefault();
        final Principal principal = new JMXPrincipal("");
        final String txId = "txId";

        StoreContext.StoreContextBuilder builder = new StoreContext.StoreContextBuilder();
        builder
                .withTxId(txId)
                .withLocale(locale)
                .withHints(hints)
                .withPrincipal(principal)
                .withTimeZone(timeZone);


        assertContextIsBuildCorrectly(hints, timeZone, locale, principal, txId, new StoreContext(builder));
        assertContextIsBuildCorrectly(hints, timeZone, locale, principal, txId, builder.build());
    }

    private void assertContextIsBuildCorrectly(
            List<String> hints,
            TimeZone timeZone,
            Locale locale,
            Principal principal,
            String txId,
            StoreContext storeContext
    ) {
        assertEquals(hints, storeContext.getHints());
        assertEquals(txId, storeContext.getTxId());
        assertEquals(locale, storeContext.getLocale());
        assertEquals(principal, storeContext.getPrincipal());
        assertEquals(timeZone, storeContext.getTimeZone());
    }

    @Test
    public void storeContextStoreResult() {
        StoreResult storeResult = new StoreResult();
        StoreContext storeContext = new StoreContext(null, null);
        storeContext.setStoreResult(storeResult);

        assertEquals(storeResult, storeContext.getStoreResult());
    }

    @Test
    public void storeContextCommonObjects() {
        StoreContext storeContext = new StoreContext(null, null);

        assertNotNull(storeContext.getCommonObjects());
    }

    @Test
    public void storeContextDefaultValues() {
        StoreContext storeContext = new StoreContext();
        assertEquals(Locale.getDefault(), storeContext.getLocale());
        assertEquals(TimeZone.getDefault(), storeContext.getTimeZone());
        assertEquals("empty", storeContext.getTxId());
        assertEquals(0, storeContext.getHints().size());
    }

    @Test
    public void buildStoreContext() {
        final List<String> hints = Collections.emptyList();
        final TimeZone timeZone = TimeZone.getDefault();
        final Locale locale = Locale.getDefault();
        final Principal principal = new JMXPrincipal("");
        final String txId = "txId";
        final List<DomainTo> uow = Collections.emptyList();
        final List<? extends DomainObject<?>> domainObjects = Collections.emptyList();

        StoreContext.StoreContextBuilder builder = new StoreContext.StoreContextBuilder();
        StoreContext storeContext = builder
                .withHints(hints)
                .withLocale(locale)
                .withPrincipal(principal)
                .withTimeZone(timeZone)
                .withTxId(txId)
                .withUow(uow)
                .withDomainObjects(domainObjects)
                .build();

        assertEquals(locale, storeContext.getLocale());
        assertEquals(timeZone, storeContext.getTimeZone());
        assertEquals(txId, storeContext.getTxId());
        assertEquals(hints, storeContext.getHints());
        assertEquals(principal, storeContext.getPrincipal());
        assertEquals(uow, storeContext.getOriginalsObjects());
        assertEquals(domainObjects, storeContext.getDomainObjects());
    }

    @Test
    public void buildStoreContextFromNullBuilder() {
        StoreContext storeContext = new StoreContext((StoreContext.StoreContextBuilder) null);
        assertNotNull(storeContext.getDomainObjects());
        assertNotNull(storeContext.getOriginalsObjects());
    }

    @Test
    public void buildStoreContextFromNullOriginalsObjects() {
        StoreContext storeContext = new StoreContext(new StoreContext.StoreContextBuilder());
        assertNotNull(storeContext.getOriginalsObjects());
    }

    @Test
    public void buildStoreContextFromNullDomainObjects() {
        StoreContext storeContext = new StoreContext(new StoreContext.StoreContextBuilder());
        assertNotNull(storeContext.getDomainObjects());
    }

    @Test
    public void correctlyPreparedStoreContextFromController() {
        final List<String> hints = Collections.emptyList();
        final TimeZone timeZone = TimeZone.getDefault();
        final Locale locale = Locale.getDefault();
        final Principal principal = new JMXPrincipal("");
        final String txId = "txId";
        final List<DomainTo> uow = Collections.emptyList();

        DomainApiController controller = new DomainApiController(
            mock(ResourceStore.class),
            null,
            null,
            null);
        StoreService storeService = spy(StoreService.class);
        StoreResult storeResult = mock(StoreResult.class);
        when(storeResult.getHttpStatus()).thenReturn(HttpStatus.OK);
        doReturn(storeResult).when(storeService).store(any(StoreContext.class));
        controller.setStoreService(storeService);
        controller.setStoreResultToService(mock(StoreResultToService.class));
        controller.store(uow, Integer.MAX_VALUE, hints, newArrayList(), txId, locale, timeZone, principal);

        ArgumentCaptor<StoreContext> contextArgumentCaptor = ArgumentCaptor.forClass(StoreContext.class);

        verify(storeService, atLeastOnce()).store(contextArgumentCaptor.capture());

        StoreContext storeContext = contextArgumentCaptor.getValue();
        assertEquals(locale, storeContext.getLocale());
        assertEquals(timeZone, storeContext.getTimeZone());
        assertEquals(txId, storeContext.getTxId());
        assertEquals(hints, storeContext.getHints());
        assertEquals(principal, storeContext.getPrincipal());
        assertEquals(uow, storeContext.getOriginalsObjects());
    }
}
