package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.stream.XMLStreamConstants;

import static org.junit.Assert.assertEquals;

public class XslFoTreeBuilderTest {

    /**
     * https://jira.croc.ru/browse/JXFW-1339
     *
     * тест не очень важный просто демонстрация что иследование велось
     */
    @Test
    @Ignore
    public void endElementThroughReaderApi() {
        //по информации из дебага
        //т.к. пакет не видно при запуске через мавен
        //https://stackoverflow.com/questions/10614852/com-sun-xml-internal-ws-client-does-not-exist
        //com.sun.xml.internal.stream.events.EndElementEvent endElementEvent = new com.sun.xml.internal.stream.events.EndElementEvent();

        //assertEquals(XMLStreamConstants.END_ELEMENT, endElementEvent.getEventType());
    }
}
