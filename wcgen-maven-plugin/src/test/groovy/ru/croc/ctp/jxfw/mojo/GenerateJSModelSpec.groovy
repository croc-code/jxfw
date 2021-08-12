package ru.croc.ctp.jxfw.mojo

import org.junit.rules.TemporaryFolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.croc.ctp.jxfw.generator.JsModelGenerator
import spock.lang.Specification


import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import static java.io.File.separator
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsEqual.equalTo

class GenerateJSModelSpec extends Specification {

    private static final String JS_MODEL_META_FILE_NAME = "model.js";

    TemporaryFolder tempFolder;

    def setup() {
        tempFolder = new TemporaryFolder()
        tempFolder.create()
    }

    def cleanup() {
        tempFolder.delete()
    }

    def "generate JS model"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}normal-models"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, JS_MODEL_META_FILE_NAME)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson());
        assertThat(jsonModel, hasJsonPath(/$.entities.Group.descr/, equalTo(/resources["model.Group"]/)));
        assertThat(jsonModel, hasNoJsonPath(/$.entities.CMObject.props.id.descr/));
        assertThat(jsonModel, hasNoJsonPath(/$.entities.CMObject.props.ts.descr/));
    }


    def "generate JS model with the same classes"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}duplicate-classes"),
                outputFolder, JS_MODEL_META_FILE_NAME)

        when:
        def modelClasses = processor.getEClasses()

        then:
        modelClasses
        modelClasses.size() == 1
    }


    def "should set nullable for boolean"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}nullable-model"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, /model.js/)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson());
        assertThat(jsonModel, hasJsonPath(/$.entities.NullableBoolean.props.isFlag.nullable/, equalTo(false)))
    }


    def "@XFWObject(persistence=TRANSIENT, temp=<facade_temp>) в модели соответствует temp=<facade_temp>"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}transient-models"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, /model.js/)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson())
        assertThat(jsonModel, hasJsonPath(/$.entities.CommandEntity.temp/, equalTo(true)))
        assertThat(jsonModel, hasNoJsonPath((/$.entities.CommandEntityTempFalse.temp/)))
    }

    def "генерация flags для полей-перечислений"(){
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}demo-arms"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, /model.js/)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson())
        assertThat(jsonModel, hasJsonPath(/$.entities.Employee.props.qualification.flags/, equalTo(true)))
        assertThat(jsonModel, hasJsonPath(/$.entities.Employee.props.mainQualification.flags/, equalTo(false)))


    }
    def "генерация init для полей-перечислений"(){
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}demo-arms"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, /model.js/)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson())
        assertThat(jsonModel, hasJsonPath(/$.entities.Employee.props.qualification.init/, equalTo(68)))
        assertThat(jsonModel, hasJsonPath(/$.entities.Employee.props.mainQualification.init/, equalTo(1)))


    }

    def "генерация temp для временных полей"(){
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}transient-models"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, /model.js/)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson())
        assertThat(jsonModel, hasJsonPath(/$.entities.SampleTransientField.props.a.temp/, equalTo(true)))
        assertThat(jsonModel, hasNoJsonPath(/$.entities.SampleTransientField.props.b.temp/))
        assertThat(jsonModel, hasJsonPath(/$.entities.SampleTransientField.props.c.temp/, equalTo(true)))
    }

}
