package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core

import org.w3c.dom.Node
import spock.lang.Specification

/**
 * @author PaNovikov
 * @since 10.05.2017.
 */
class CalculatorSpec extends Specification{
    def "outputFormats"() {
        given: "list formats"
            String[] formats = ["pdf", "docx"]
        when: "get output formats as node list"
            Node node = Calculator.outputFormats(formats)
        then: "check that node list is filling"
            node.getChildNodes().length == 2
            node.getFirstChild().getAttributes().getNamedItem("n").getNodeValue() == "pdf"
            node.getLastChild().getAttributes().getNamedItem("n").getNodeValue() == "docx"

    }
}
