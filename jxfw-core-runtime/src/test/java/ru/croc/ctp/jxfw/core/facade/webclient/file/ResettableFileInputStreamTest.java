package ru.croc.ctp.jxfw.core.facade.webclient.file;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Модульные тесты для {@link ResettableFileInputStream}
 * Created by SPlaunov on 01.06.2016.
 */
public class ResettableFileInputStreamTest {
	
    @Test
    public void fileInputStreamReset() throws IOException {
        //given:
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("croclive.png");
        InputStream inputStream = new ResettableFileInputStream(fileUrl.getPath());
        //when:
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.reset();
        bytes = IOUtils.toByteArray(inputStream);
        //then:
        assertNotNull(bytes);
    }
}
