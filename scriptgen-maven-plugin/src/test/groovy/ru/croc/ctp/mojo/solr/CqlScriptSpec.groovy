package ru.croc.ctp.mojo.solr

import ru.croc.ctp.jxfw.mojo.GenerateCqlScriptsMojo
import ru.croc.ctp.jxfw.solr.Respondent
import spock.lang.Ignore
import spock.lang.Specification

import static java.io.File.separator

/**
 * @author SMufazzalov
 * @since 1.4
 */
class CqlScriptSpec extends Specification {

    def "file contents and generated identical"() {
        given:
        def originalText = new File("src${separator}test${separator}resources${separator}Respondent.cql").text

        when:
        def generatedText = GenerateCqlScriptsMojo.getScript(Respondent)

        then:
        //print(generatedText)
        originalText.contains(generatedText)
    }

    @Ignore("JXFW-852 jXFW Cass генерация скрипта с ZonedDateTime и LocalDateTime полями")
    def "file contents and generated identical 2"() {
        given:
        def originalText = "TODO"

        when:
        def generatedText = GenerateCqlScriptsMojo.getScript(CommentHistory)

        then:
        //print(generatedText)
        originalText.contains(generatedText)
    }
}
