package ru.croc.ctp.jxfw.wc;

import org.junit.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.lang.reflect.Method;

import static junit.framework.TestCase.assertTrue;

public class WebClientLoaderConfigTest {

    @Test
    public void resourceStoreCheckAnnotations() throws NoSuchMethodException {
        Method resourceStore = WebClientLoaderConfig.class.getMethod("resourceStore", String.class);
        boolean conditionalAnnotationPresent = resourceStore.isAnnotationPresent(ConditionalOnMissingBean.class);

        assertTrue(conditionalAnnotationPresent);
    }
}
