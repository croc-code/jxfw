package ru.croc.ctp.mojo.solr

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.velocity.VelocityContext
import ru.croc.ctp.jxfw.mojo.GenerateSolrSchemaMojo
import ru.croc.ctp.jxfw.solr.OutTableHistory
import ru.croc.ctp.jxfw.solr.SchemaMetadata
import spock.lang.Specification

import static java.io.File.separator

/**
 * @author SMufazzalov
 * @since 1.4
 */
class SolrSchemaSpec extends Specification {

    def "file contents and generated identical"() {
        given:
        def ve = GenerateSolrSchemaMojo.getVelocityEngine()
        def meta = new SchemaMetadata(OutTableHistory)
        def context = new VelocityContext()
        context.put("meta", meta)
        def baos = new ByteArrayOutputStream()
        def writer = new OutputStreamWriter(baos)

        when: "смержим шаблон с данными"
        def vt = ve.getTemplate("solr-schema.vm")
        vt.merge(context, writer)
        writer.close()

        then: "получившиеся тексты сгенеренные и эталонного файла идентичны"
        //print(baos.toString())
        new File("src${separator}test${separator}resources${separator}OutTableHistory.xml").text == baos.toString()
    }
}
