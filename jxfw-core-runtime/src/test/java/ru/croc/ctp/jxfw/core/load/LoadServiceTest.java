package ru.croc.ctp.jxfw.core.load;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;
import ru.croc.ctp.jxfw.core.services.TestObject;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class LoadServiceTest {

    @Test
    //TODO: подумать, как доделать тест, чтобы учитывал контекст
    public void loadResults() {
        LoadService service = mock(LoadService.class);
        QueryParams params = mock(QueryParams.class);

        LoadContext loadContext = new LoadContext();

        HashMap<String, Object> hints = new HashMap<>();
        int hint = 123;
        hints.put("1", hint);
        
        LoadResult loadResult = new LoadResult(asList(new TestObject(""), new TestObject(""), new TestObject("")),
                asList(new TestObject("")), hints);
        
        when(service.load(any(QueryParams.class), any())).thenReturn(asList(new TestObject(""), new TestObject(""), new TestObject("")));
        QueryParams<TestObject, String> mock = mock(QueryParams.class);
        List<?> data = service.load(mock, loadContext);

        assertEquals(3, data.size());
        //assertEquals(1, loadContext.getLoadResult().getMoreList().size());
        //assertEquals(hint, loadContext.getLoadResult().getHints().get("1"));
    }
}
