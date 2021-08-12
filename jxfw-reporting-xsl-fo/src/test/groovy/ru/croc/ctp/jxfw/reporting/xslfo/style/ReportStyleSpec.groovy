package ru.croc.ctp.jxfw.reporting.xslfo.style

import spock.lang.Specification

class ReportStyleSpec extends Specification {

    def "constructor should parse valid style"() {
        given: "valid style string"
            def str = "border=\"1pt solid\" background-color=\"#FF7070\" color=\"#000000\" font-weight=\"bold\" text-align=\"center\""
        when: "Create reportStyle object"
            def rs = new ReportStyle("StyleName", str)
        then: "style is valid"
            rs.size() == 5
            rs.get("background-color") == "#FF7070"
    }
}