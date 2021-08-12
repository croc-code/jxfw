package ru.croc.ctp.jxfw.solr.load;

import org.junit.Test;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.solr.load.impl.SolrLoadServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SolrLoadServiceTest {

    @Test
    public void createService() {
        LoadService service = new SolrLoadServiceImpl(mock(DomainServicesResolver.class));
        Service annotation = service.getClass().getAnnotation(Service.class);
        assertEquals("solrLoadService", annotation.value());
    }
}
