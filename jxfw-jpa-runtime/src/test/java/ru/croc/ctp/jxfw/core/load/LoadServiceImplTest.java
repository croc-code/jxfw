package ru.croc.ctp.jxfw.core.load;

import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;
import ru.croc.ctp.jxfw.jpa.load.impl.JpaLoadServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class LoadServiceImplTest {

    @Test
    public void constructService() {
        JpaLoadServiceImpl service = new JpaLoadServiceImpl(mock(DomainServicesResolver.class),
                mock(ApplicationEventPublisher.class), mock(PredicateProvider.class), mock(JpaContext.class));

        Assert.isInstanceOf(LoadServiceImpl.class, service);

        Service annotation = service.getClass().getAnnotation(Service.class);
        assertEquals("jpaLoadService", annotation.value());

    }

}
