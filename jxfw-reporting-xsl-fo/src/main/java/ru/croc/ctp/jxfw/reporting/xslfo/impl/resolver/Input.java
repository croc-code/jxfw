package ru.croc.ctp.jxfw.reporting.xslfo.impl.resolver;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

/**
 * {@link LSInput}.
 *
 * @author SMufazzalov
 * @since 1.6
 */
public class Input implements LSInput {

    private static final Logger logger = LoggerFactory.getLogger(Input.class);

    private String publicId;

    private String systemId;

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getBaseURI() {
        return null;
    }

    public InputStream getByteStream() {
        return null;
    }

    public boolean getCertifiedText() {
        return false;
    }

    public Reader getCharacterStream() {
        return null;
    }


    public String getEncoding() {
        return null;
    }

    /**
     * Контент схемы.
     *
     * @return схема
     */
    public String getStringData() {
        synchronized (inputStream) {
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer);
                return writer.toString();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * baseURI.
     *
     * @param baseURI baseURI
     */
    public void setBaseURI(String baseURI) {
    }

    /**
     * byteStream.
     *
     * @param byteStream byteStream
     */
    public void setByteStream(InputStream byteStream) {
    }

    /**
     * certifiedText.
     *
     * @param certifiedText certifiedText
     */
    public void setCertifiedText(boolean certifiedText) {
    }

    /**
     * characterStream.
     *
     * @param characterStream characterStream
     */
    public void setCharacterStream(Reader characterStream) {
    }

    /**
     * encoding.
     *
     * @param encoding encoding
     */
    public void setEncoding(String encoding) {
    }

    /**
     * stringData.
     *
     * @param stringData stringData
     */
    public void setStringData(String stringData) {
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public BufferedInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private BufferedInputStream inputStream;

    /**
     * Конструктор.
     *
     * @param publicId publicId
     * @param sysId    sysId
     * @param input    input
     */
    public Input(String publicId, String sysId, InputStream input) {
        this.publicId = publicId;
        this.systemId = sysId;
        this.inputStream = new BufferedInputStream(input);
    }
}