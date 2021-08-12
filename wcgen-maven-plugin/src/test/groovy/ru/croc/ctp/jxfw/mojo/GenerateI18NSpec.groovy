package ru.croc.ctp.jxfw.mojo

import org.junit.rules.TemporaryFolder
import ru.croc.ctp.jxfw.generator.I18NGenerator
import spock.lang.Specification


import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import static java.io.File.separator
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsEqual.equalTo

class GenerateI18NSpec extends Specification {

    TemporaryFolder tempFolder;

    def setup() {
        tempFolder = new TemporaryFolder()
        tempFolder.create()
    }

    def cleanup() {
        tempFolder.delete()
    }

    def "generate i18n resources"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new I18NGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}normal-models"),
                outputFolder)
        def ruResources = new File(outputFolder, "ru${separator}resources.js")
        def enResources = new File(outputFolder, "en${separator}resources.js")

        when:
        processor.process()

        then:
        ruResources.exists()
 //       enResources.exists()
        def jsonRu = JsonUtils.extractI18nJson(ruResources)
        assertThat(jsonRu, isJson());
        assertThat(jsonRu, hasJsonPath(/$.['model.Group']/, equalTo("Группа")));
        assertThat(jsonRu, hasJsonPath(/$.['model.CMObject.comments']/, equalTo("Обсуждение")));
        assertThat(jsonRu, hasNoJsonPath(/$.['model.CMObject.id']/));
        assertThat(jsonRu, hasNoJsonPath(/$.['model.CMObject.ts']/));
    }

}
