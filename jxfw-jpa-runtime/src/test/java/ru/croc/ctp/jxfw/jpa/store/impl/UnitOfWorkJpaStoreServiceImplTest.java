package ru.croc.ctp.jxfw.jpa.store.impl;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;

import java.util.List;

public class UnitOfWorkJpaStoreServiceImplTest {

    private UnitOfWorkJpaStoreServiceImpl service;

    @Mock
    private DomainObject<String> obj1;

    @Mock
    private DomainObject<String> obj2;

    @Before
    public void setUp() {
        service = new UnitOfWorkJpaStoreServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = XInvalidDataException.class)
    public void beforeStoreFailTest() {
        Mockito.when(obj1.getId()).thenReturn("1");
        Mockito.when(obj1.getTypeName()).thenReturn("typeName");
        Mockito.when(obj1.isNew()).thenReturn(false);
        Mockito.when(obj1.isRemoved()).thenReturn(true);

        Mockito.when(obj2.getId()).thenReturn("2");
        Mockito.when(obj2.getTypeName()).thenReturn("typeName");
        Mockito.when(obj2.isRemoved()).thenReturn(true);
        Mockito.when(obj2.isNew()).thenReturn(true);

        List<DomainObject<?>> uow = Lists.newArrayList(obj1, obj2);
        service.beforeStore(uow);
    }
}
