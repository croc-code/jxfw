package ru.croc.ctp.jxfw.wc.web.mvc;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockServletContext;
import ru.croc.ctp.jxfw.wc.web.config.BaseConfigModule;
import ru.croc.ctp.jxfw.wc.web.config.ConfigSettings;
import ru.croc.ctp.jxfw.wc.web.config.XConfig;
import ru.croc.ctp.jxfw.wc.web.config.XSecurityConfig;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static util.Utils.getResource;

/**
 * Проверка резултатов мерджа конфигов.
 *
 * @author SMufazzalov
 * @since 1.4
 */
public class ConfigTest {

    @Test
    public void loadConfigs() {
        BaseConfigModule spy = spy(BaseConfigModule.class);

        String fileName = "issue658.main.config.json";

        InputStreamResource resource = getResource(fileName);

        doReturn(resource).when(spy).getResource(any());

        Map<String, Object> config = spy.loadJsonConfig("", fileName);

        Assert.assertNotNull(config);
    }

    /**
     * JXFW-658 мердж конфигов main.config.json и require.config.json
     */
    @Test
    public void mergeToRequireJson() {
        String mainConfigFile = "issue658.main.config.json";
        String requireConfigFile = "issue658.require.config.json";

        //подготовка тестовой среды
        BaseConfigModule spy = spy(BaseConfigModule.class);

        ConfigSettings settings = new ConfigSettings();
        settings.setMainConfigFile(mainConfigFile);
        settings.setRequireConfigFile(requireConfigFile);
        spy.setConfigSettings(settings);
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");
        spy.setServletContext(servletContext);

        InputStreamResource mResource = getResource(mainConfigFile);
        InputStreamResource rResource = getResource(requireConfigFile);

        doReturn(mResource).doReturn(rResource).when(spy).getResource(any());

        //вызов основной логики
        XConfig xConfig = spy.createClientConfig();

        Assert.assertNotNull(xConfig.getClientJson().get("require"));

        Map<String, Object> requireJson = xConfig.getRequireJson();
        //установилось значение waitSeconds
        Assert.assertEquals(658, requireJson.get("waitSeconds"));

        //смержились paths
        Assert.assertEquals(2, ((Map) requireJson.get("paths")).size());
        Assert.assertEquals("658", ((Map) requireJson.get("paths")).get("underscore"));

        //смержились shim
        Assert.assertEquals(2, ((Map) requireJson.get("shim")).size());
        Assert.assertEquals("658", ((List) ((Map) requireJson.get("shim")).get("bootstrap")).get(0));
    }

    /**
     * JXFW-813 WC 1.31 - Поддержка двухфакторной аутентификации
     */
    @Test
    public void twoFactorAuthEnabled() {
        String mainConfigFile = "issue813.main.config.json";
        String requireConfigFile = "issue658.require.config.json";
        ConfigSettings settings = new ConfigSettings();
        settings.setMainConfigFile(mainConfigFile);
        settings.setRequireConfigFile(requireConfigFile);

        //подготовка тестовой среды
        BaseConfigModule spy = spy(BaseConfigModule.class);
        spy.setConfigSettings(settings);

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");
        spy.setServletContext(servletContext);

        InputStreamResource mResource = getResource(mainConfigFile);
        InputStreamResource rResource = getResource(requireConfigFile);

        doReturn(mResource).doReturn(rResource).when(spy).getResource(any());

        XConfig xConfig = spy.createClientConfig();
        XSecurityConfig security = xConfig.getSecurity();

        Assert.assertTrue(security.isTwoFactorAuthEnabled());

        String url = security.getTwoFactorAuthUrl();
        Assert.assertEquals("right_url", url);
    }

    @Test
    public void setSecurityParamsFromCode() {
        XSecurityConfig config = new XSecurityConfig();

        String rightUrl = "rightUrl";
        //в таком порядке
        config.setTwoFactorAuthUrl(rightUrl);
        config.setTwoFactorAuthEnabled(true);

        Assert.assertTrue(config.isTwoFactorAuthEnabled());
        Assert.assertEquals(rightUrl, config.getTwoFactorAuthUrl());

        config = new XSecurityConfig();
        //меняем порядок
        config.setTwoFactorAuthEnabled(true);
        config.setTwoFactorAuthUrl(rightUrl);

        Assert.assertTrue(config.isTwoFactorAuthEnabled());
        Assert.assertEquals(rightUrl, config.getTwoFactorAuthUrl());
    }

    /** JXFW-968 Автоматическое определение и добавление contextPath в конфигурацию WC*/
    @Test
    public void setRootTest() {
        String correctClientRoot = "/correctClientRoot/";

        XConfig config = new XConfig();

        config.setRoot(null);
        assertNull(config.getRoot());

        config.setRoot("");
        assertNull(config.getRoot());

        config.setRoot("/");
        assertNull(config.getRoot());

        config.setRoot("/correctClientRoot");
        assertEquals(correctClientRoot, config.getRoot());

        config.setRoot(correctClientRoot);
        assertEquals(correctClientRoot, config.getRoot());
    }

    /** JXFW-968 Автоматическое определение и добавление contextPath в конфигурацию WC*/
    @Test
    public void setRootFromServletContext() {
        String correctClientRoot = "/correctClientRoot/";
        String correctContextPath = "/correctClientRoot";

        BaseConfigModule module = spy(BaseConfigModule.class);
        doReturn(new HashMap<>()).when(module).loadJsonConfig(any(), any());

        module.setApplicationContext(mock(ApplicationContext.class));
        module.setConfigSettings(mock(ConfigSettings.class));
        MockServletContext servletContext = new MockServletContext();
        servletContext.setContextPath(correctContextPath);
        module.setServletContext(servletContext);

        XConfig xConfig = module.createClientConfig();

        assertEquals(correctClientRoot, xConfig.getRoot());
    }
}
