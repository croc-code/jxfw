package ru.croc.ctp.jxfw.core.reporting.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class XfwResourceLoaderTest {


    @Test
    @Ignore
    public void httpResource() throws Exception {
        XfwReportProfileManagerImpl reportProfileManager= new XfwReportProfileManagerImpl(null,null);
        byte[] bytes = IOUtils.toByteArray(reportProfileManager.getResource(
                "https://www.art-designs.ru/assets/images/Styles/Sredizemnomorsky/sredizemnomorsky-2.jpg").getInputStream());
        assertTrue(bytes.length > 0);
    }


    /*
    Если одинаковый ресурс есть в класспасе и в файле, то берем файл
     */
    @Test
    public void fsResource() throws Exception {
        XfwReportProfileManagerImpl reportProfileManager= new XfwReportProfileManagerImpl(null,"./");
        byte[] bytes = IOUtils.toByteArray(reportProfileManager.getResource(
                "dir/res.txt").getInputStream());
        assertEquals(3, bytes.length);
    }

    @Test
    public void classpathResource() throws Exception {
        XfwReportProfileManagerImpl reportProfileManager= new XfwReportProfileManagerImpl(null,null);
        byte[] bytes = IOUtils.toByteArray(reportProfileManager.getResource(
                "dir/res1.txt").getInputStream());
        assertEquals(2, bytes.length);
    }


}
