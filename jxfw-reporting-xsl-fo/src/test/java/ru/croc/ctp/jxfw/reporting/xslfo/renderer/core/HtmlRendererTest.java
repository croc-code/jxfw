package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class HtmlRendererTest {

    @BeforeClass
    @AfterClass
    public static void clean() {
        TestLoggerAppender.events.clear();
    }

    @Test
    public void render() throws Exception {
        HtmlRenderer renderer = new HtmlRenderer(mock(XfwReportProfileManager.class));
        try {
            renderer.render(
                    mock(InputStream.class),
                    mock(OutputStream.class),
                    Charset.defaultCharset(),
                    Locale.getDefault(),
                    OutputFormat.HTML5
            );
        } catch (Exception e) {
            assertTrue(
                    TestLoggerAppender.
                            events.
                            stream().
                            anyMatch(
                                    lev ->
                                            lev.getMessage().contains("Произошла ошибка при попытке рендеринга xml в html")
                            )
            );
        }

    }
}
