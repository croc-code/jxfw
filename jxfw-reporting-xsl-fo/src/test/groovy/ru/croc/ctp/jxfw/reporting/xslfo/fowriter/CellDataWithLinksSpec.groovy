package ru.croc.ctp.jxfw.reporting.xslfo.fowriter

import spock.lang.Specification

import javax.xml.stream.XMLStreamWriter


/**
 * Тестовая спецификация для данных ячейки со ссылкой.
 *
 * @author PaNovikov
 * @since 27.04.2017
 */
class CellDataWithLinksSpec extends Specification{

    final String CONVERTED_LINK = "[link]"

    def "writeCellDataWithLinks method should write vlaid xml"() {
        given:
            def sb = new StringBuilder()
            def linkBuilder = Mock(LinkBuilder)
            linkBuilder.writeLink(_) >> { sb.append(CONVERTED_LINK)};

            def xmlStreamWriter = Mock(XMLStreamWriter)
            xmlStreamWriter.writeCharacters(_) >> { args -> sb.append(args)}

        when: "Run cellData"
            def cellData = CellDataWithLinks.create(inputStr)
                .addLink(linkBuilder)
            cellData.writeCellDataWithLinks(xmlStreamWriter)
        then: "Check that parameter is filling"
            sb.toString() == outputStr
        where:
            inputStr                      | outputStr
            "abcdef @link ghijklmn"   | "[abcdef ][link][ ghijklmn]"
            "abcd"                        | "[abcd]"
            "@link"                       | "[][link][]"
            "ab@link"                     | "[ab][link][]"

    }
}