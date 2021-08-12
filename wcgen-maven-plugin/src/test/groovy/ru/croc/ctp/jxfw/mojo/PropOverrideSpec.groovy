package ru.croc.ctp.jxfw.mojo

import org.eclipse.emf.ecore.EStructuralFeature
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.croc.ctp.jxfw.generator.I18NGenerator
import ru.croc.ctp.jxfw.generator.JsModelGenerator
import ru.croc.ctp.jxfw.metamodel.XFWClass
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl
import spock.lang.Specification

import java.nio.file.Paths

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import static java.io.File.separator
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertEquals

class PropOverrideSpec extends Specification {
    private static final String JS_MODEL_META_FILE_NAME = "model.js";

    TemporaryFolder tempFolder;

    def setup() {
        tempFolder = new TemporaryFolder()
        tempFolder.create()
    }

    def cleanup() {
        tempFolder.delete()
    }




    /*

@XFWObject
@XFWElementLabel(value = "Название1" , propName= "title")
class Base{
    @XFWElementLabel("Имя")
    @XFWElementLabel(value = "Name", lang = "en")
    String name;


    @XFWElementLabel("Название")
    @XFWElementLabel(value = "Title", lang = "en")
    String title;

}



@XFWObject
@XFWElementLabel( value = "ИмяИмя" , propName= "name")
class Ext extends Base{
}



@XFWObject
@XFWElementLabel( value = "НазваниеНазвание" , propName= "title")
class ExtExt extends Ext{

    String prop;
}

     */

    def "override i18n resources"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new I18NGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}propOverride"),
                outputFolder)
        def ruResources = new File(outputFolder, "ru${separator}resources.js")
        def enResources = new File(outputFolder, "en${separator}resources.js")

        when:
        processor.process()

        then:
        ruResources.exists()
        enResources.exists()
        def jsonRu = JsonUtils.extractI18nJson(ruResources)
        assertThat(jsonRu, isJson());
        assertThat(jsonRu, hasJsonPath(/$.['model.Base.title']/, equalTo("Название1")));
        assertThat(jsonRu, hasJsonPath(/$.['model.Base.name']/, equalTo("Имя")));
        assertThat(jsonRu, hasJsonPath(/$.['model.Ext.name']/, equalTo("ИмяИмя")));
        assertThat(jsonRu, hasJsonPath(/$.['model.ExtExt.title']/, equalTo("НазваниеНазвание")));
    }



    def "override modeljs"() {
        given:
        def outputFolder = tempFolder.newFolder("wcgen-maven-plugin-tests")
        def processor = new JsModelGenerator(
                new File("src${separator}test${separator}resources${separator}models${separator}propOverride"),
                outputFolder, JS_MODEL_META_FILE_NAME)
        def modelResource = new File(outputFolder, JS_MODEL_META_FILE_NAME)

        when:
        processor.process()

        then:
        modelResource.exists()
        def jsonModel = JsonUtils.extractModelJson(modelResource)
        assertThat(jsonModel, isJson());
        assertThat(jsonModel, hasJsonPath(/$.entities.Base.props.title.descr/, equalTo("resources[\"model.Base.title\"]")))
        assertThat(jsonModel, hasJsonPath(/$.entities.Base.props.name.descr/, equalTo("resources[\"model.Base.name\"]")))
        assertThat(jsonModel, hasJsonPath(/$.entities.Ext.props.name.descr/, equalTo("resources[\"model.Ext.name\"]")))
        assertThat(jsonModel, hasJsonPath(/$.entities.ExtExt.props.title.descr/, equalTo("resources[\"model.ExtExt.title\"]")))
    }



     def "testGetOwnAndOverridenStructuralFeatures"() {
        given:
        def xfwModel = new XFWModelImpl(Paths.get("src${separator}test${separator}resources${separator}models${separator}propOverride"))
        Collection<EStructuralFeature> features;
        when:
        features = (Collection) xfwModel.find("Base", XFWClass.class).getOwnAndOverridenStructuralFeatures();
        then:
        assertEquals(features.size(), 3);

        assertEquals(features.stream().filter({feature ->
                feature.getName().equals("name")|| feature.getName().equals("title")|| feature.getName().equals("login")}
        ).count(), 3);
        when:

        // в классе Ext собственных св-в нет, переопределено св-во name
        features = (Collection) xfwModel.find("Ext", XFWClass.class).getOwnAndOverridenStructuralFeatures();
        then:
        assertEquals(features.size(), 1);

        assertEquals(features.stream().filter({feature ->
                feature.getName().equals("name")}
        ).count(), 1);

        when:


        // в классе ExtExt одно собственное св-во, переопределено св-во title
        features = (Collection) xfwModel.find("ExtExt", XFWClass.class).getOwnAndOverridenStructuralFeatures();
        then:
        assertEquals(features.size(), 2);

        assertEquals(features.stream().filter({feature ->
                feature.getName().equals("prop") || feature.getName().equals("title")}
        ).count(), 2);


    }

    // JXFW-1159
    def "props order do not change"(){
        given:
        def xfwModel = new XFWModelImpl(Paths.get("src${separator}test${separator}resources${separator}models${separator}demo-arms"))
        def features = xfwModel.find("Employee", XFWClass.class).getOwnAndOverridenStructuralFeatures();

        when:
        def iterator = features.iterator();
        for(int i=0; i<num; i++){
            iterator.next()
        }

        then:
        assertEquals(iterator.next().getName(),propName)

        where:
        num| propName
        0| "phoneHome"

        1| "signPublicKey"

        2| "lastPass"

        3| "info"

        4| "salary"

        5| "birthDay"

        6| "isManager"

        7| "skills"

        8| "photo"

        9| "passID"

        10| "resume"

        11| "factor"

        12| "login"

        13| "qualification"

        14| "mainQualification"

        15| "department"

        16| "tasks"

        17| "projects"

        18| "resource"

        19| "employeeAddress"



    }


}
