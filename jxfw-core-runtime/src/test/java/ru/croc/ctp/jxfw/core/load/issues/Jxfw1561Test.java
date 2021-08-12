package ru.croc.ctp.jxfw.core.load.issues;

import org.junit.Assert;
import org.junit.Test;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.context.LoadContextWithStoreContext;
import ru.croc.ctp.jxfw.core.store.StoreContext;

import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

public class Jxfw1561Test {

    @Test
    public void testLoadContextWithStoreContextBuilderWithoutStoreContext() {
        LoadContext<?> context = new LoadContext.Builder().build();
        LoadContextWithStoreContext contextWithStoreContext = LoadContextWithStoreContext.from(context);

        Assert.assertFalse(contextWithStoreContext.exists());
        Assert.assertNull(contextWithStoreContext.getStoreContext());
    }

    @Test
    public void testLoadContextWithStoreContextBuilderWithStoreContext() {
        StoreContext storeContext = new StoreContext.StoreContextBuilder()
                .withTimeZone(TimeZone.getDefault())
                .withHints(Collections.emptyList())
                .withPrincipal(() -> "name")
                .withLocale(Locale.ENGLISH)
                .build();
        LoadContextWithStoreContext<?> loadContext = new LoadContextWithStoreContext.Builder(storeContext).build();

        Assert.assertTrue(loadContext.exists());
        Assert.assertNotNull(loadContext.getStoreContext());
    }
}
