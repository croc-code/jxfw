package ru.croc.ctp.jxfw.core.facade.webclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.load.LoadContext;

import java.util.Map;

public class DomainObjectFactoryTest {

    private DomainObjectFactory domainObjectFactory;
    private static final String TYPE_NAME = "User";
    private static final String OBJECT_ID = "4290e819-22b3-45cc-9eff-67fd82dd62ee";
    private static final DomainTo domainTo = new DomainTo(TYPE_NAME, OBJECT_ID);

    @Mock
    private DomainToServicesResolver domainToServicesResolver;
    @Mock
    private DomainToService<DomainObject<String>, String> domainToService;
    @Mock
    private DomainObject<String> domainObject;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Map<String, DomainObject<String>> objects = ImmutableMap.of(OBJECT_ID, domainObject);

        domainObjectFactory = new DomainObjectFactory(domainToServicesResolver);
        Mockito.when(domainToServicesResolver.resolveToService(TYPE_NAME)).thenReturn(domainToService);
        Mockito.doAnswer(inv -> inv.getArguments()[0]).when(domainToService).parseKey(Mockito.anyString());
        Mockito.doAnswer(inv -> objects.get(inv.getArguments()[0]))
                .when(domainToService).getDomainObjectById(Mockito.anyString(), Mockito.any(LoadContext.class));
    }

    @Test
    public void createByIdTest() {
        Assert.assertEquals(domainObjectFactory.create(OBJECT_ID, TYPE_NAME), domainObject);
    }

    @Test
    public void createTest() {
        Assert.assertEquals(domainObjectFactory.create(domainToService, domainTo), domainObject);
    }

    @Test
    public void createListTest() {
        Assert.assertArrayEquals(
                domainObjectFactory.createList(ImmutableList.of(OBJECT_ID), TYPE_NAME).toArray(),
                new DomainObject[] {domainObject});
    }

    @Test
    public void createSetTest() {
        Assert.assertArrayEquals(
                domainObjectFactory.createSet(ImmutableList.of(OBJECT_ID), TYPE_NAME).toArray(),
                new DomainObject[] {domainObject});
    }
}
