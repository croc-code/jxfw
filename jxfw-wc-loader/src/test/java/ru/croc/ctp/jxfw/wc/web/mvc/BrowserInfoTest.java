package ru.croc.ctp.jxfw.wc.web.mvc;

import eu.bitwalker.useragentutils.UserAgent;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Algolovin on 18.01.2017.
 */
public class BrowserInfoTest {

    /** Проверяет корректность обработки версии браузера. */
    @Test
    public void testVersionBrowser() {
        final UserAgent userAgent = new UserAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/55.0.2883.87 Mobile Safari/537.36");
        final BrowserInfo browserInfo = BrowserInfo.create(userAgent);

        Assert.assertEquals(Double.valueOf(55.0), browserInfo.getVersion());
    }

    /** Проверяет корректность обработки версии браузера равной null. */
    @Test
    public void testVersionBrowserIsNull() {
        final UserAgent userAgent = new UserAgent("");
        final BrowserInfo browserInfo = BrowserInfo.create(userAgent);

        Assert.assertEquals(null, browserInfo.getVersion());
    }

    /**
     * JXFW-705 Добавить определение PantomJS так, чтобы его можно было использовать в "if-match" в main.config.json
     */
    @Test
    public void detectPhantomJsBrowser() {
        String headerStr = "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                "AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1 Safari/538.1";
        final UserAgent userAgent = new UserAgent(headerStr);
        final BrowserInfo browserInfo = BrowserInfo.create(userAgent, headerStr);

        Assert.assertEquals("phantomjs", browserInfo.getBrowser().toLowerCase());
        Assert.assertEquals(Double.valueOf(2.1), browserInfo.getVersion());
    }
}
