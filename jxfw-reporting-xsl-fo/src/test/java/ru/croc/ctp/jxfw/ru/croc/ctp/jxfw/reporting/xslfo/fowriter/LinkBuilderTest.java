package ru.croc.ctp.jxfw.ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.LinkBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class LinkBuilderTest {

    class LinkBuilderDescendant extends LinkBuilder {

        public Map properties;

        public LinkBuilderDescendant(Map properties) {
            super();
            this.properties = properties;
        }
    }

    /**
     * JXFW-1499 Класс LinkBuilder имеет приватный конструктор по умолчанию
     */
    @Test
    public void createLinkBuilderDescendant() {
        LinkBuilderDescendant descendant = new LinkBuilderDescendant(new HashMap());
        assertNotNull(descendant.properties);
    }
}
