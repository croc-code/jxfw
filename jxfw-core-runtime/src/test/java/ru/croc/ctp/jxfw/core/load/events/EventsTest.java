package ru.croc.ctp.jxfw.core.load.events;

import com.querydsl.core.types.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilder;
import ru.croc.ctp.jxfw.core.services.TestObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EventsTest {

    @Test
    @Ignore // FIXME JXFW-1223
    public void toStringOfReadSessionEventTest() {
        String id = UUID.randomUUID().toString();
        TestObject testObject = new TestObject(id);
        List uow = new ArrayList();
        uow.add(testObject);
        Predicate predicate = mock(Predicate.class);
        Sort sort = mock(Sort.class);
        Pageable pageable = mock(Pageable.class);
        QueryParams queryParams = new QueryParamsBuilder("TestObject", mock(PredicateProvider.class), predicate)
                .withSort(sort).withPageable(pageable)
                .build();
        LoadContext loadContext = new LoadContext();
        LoadResult loadResult = new LoadResult();
        PreCheckSecurityEvent checkSecurityEvent = new PreCheckSecurityEvent( queryParams, loadContext);
        BeforeLoadEvent beforeLoadEvent = new BeforeLoadEvent( queryParams, loadContext);
        AfterLoadEvent afterLoadEvent = new AfterLoadEvent( loadResult, queryParams, loadContext);

        Arrays.asList(checkSecurityEvent, beforeLoadEvent, afterLoadEvent)
                .forEach(event -> {
                    //   System.out.println(event);

                    assertTrue(event.toString().contains(event.getClass().getSimpleName()));
                    assertTrue(event.toString().contains("objectId"));
                    assertTrue(event.toString().contains("predicate"));
                    assertTrue(event.toString().contains("sort"));
                    assertTrue(event.toString().contains("pageable"));
                    assertTrue(event.toString().contains("loadContext"));
                    assertTrue(event.toString().contains("source"));
                    assertTrue(event.toString().contains("timestamp"));
                    if (event instanceof AfterLoadEvent) {
                        assertTrue(event.toString().contains("uow"));
                    }
                });
    }
}
