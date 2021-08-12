package ru.croc.ctp.jxfw.generator;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsModelGeneratorTest {
    TemporaryFolder temporaryFolder;

    @Before
    public void init() throws IOException {
        temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
    }

    @After
    public void finish() {
        temporaryFolder.delete();
    }
    /**
     * JXFW-1279 Неверная генерация атрибутов типа контента и поддерживаемых файлов для binary поля
     */
    @Test
    public void acceptFileTypesIsGeneratedForBlobs() throws IOException {
        File outFolder = temporaryFolder.newFolder("acceptFileTypesIsGeneratedForBlobs");

        JsModelGenerator generator = new JsModelGenerator(new File("src/test/resources/models/content-type/Issue1279.ecore"), outFolder, "model.js");

        File model = new File(outFolder, "model.js");

        generator.process();

        assertTrue(model.exists());

        String jsonModel = getModelJson(model);
        //".jpg,.jpeg"
        assertThat(jsonModel, isJson());
        assertThat(jsonModel, hasJsonPath("$.entities.User.props.avatar.contentType", equalTo("image")));
        assertThat(jsonModel, hasJsonPath("$.entities.User.props.avatar.acceptFileTypes", equalTo(".jpg,.jpeg")));
    }

    private String getModelJson(File model) throws IOException {
        Pattern pattern = Pattern.compile("(?s)return(.+?)\\}\\);");
        Matcher matcher = pattern.matcher(Files.toString(model, Charsets.UTF_8));
        matcher.find();
        return matcher.group(1);
    }

    /**
     * JXFW-1476 Для Lob полей не генерируется ограничение на размер поля.
     */
    @Test
    public void lobLength() throws IOException {
        File outFolder = temporaryFolder.newFolder("lobLength");

        JsModelGenerator generator = new JsModelGenerator(new File("src/test/resources/models/normal-models/model/Issue1476.ecore"),
                outFolder, "model.js");

        File model = new File(outFolder, "model.js");

        generator.process();

        assertTrue(model.exists());
        String jsonModel = getModelJson(model);
        assertThat(jsonModel, hasJsonPath("$.entities.LobObject.props.lob.vt", equalTo("text")));
        assertThat(jsonModel, hasNoJsonPath("$.entities.LobObject.props.lob.maxLen"));

    }

    @Test
    public void serverOnly() throws IOException {
        File outFolder = temporaryFolder.newFolder("lobLength");

        JsModelGenerator generator = new JsModelGenerator(new File("src/test/resources/models/server-only/server-only.ecore"),
                outFolder, "model.js");

        File model = new File(outFolder, "model.js");

        generator.process();

        assertTrue(model.exists());
        String jsonModel = getModelJson(model);
        assertThat(jsonModel, hasJsonPath("$.entities.ServerOnly.props", new EmptyObject()));
        assertThat(jsonModel, hasNoJsonPath("$.entities.RelationManyServerOnly"));
    }

    private static class EmptyObject extends BaseMatcher<String> {

        @Override
        public boolean matches(Object o) {
            return o instanceof Map && ((Map)o).isEmpty();
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
