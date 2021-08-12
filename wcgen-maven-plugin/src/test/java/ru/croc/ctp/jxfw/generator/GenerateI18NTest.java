package ru.croc.ctp.jxfw.generator;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateI18NTest {

    private TemporaryFolder temporaryFolder;

    @Before
    public void init() throws IOException {
        temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
    }

    @After
    public void finish() {
        temporaryFolder.delete();
    }

    @Test
    public void testEscapeCharacter() throws Exception {
        File outFolder = temporaryFolder.newFolder("testEscapeCharacter");
        I18NGenerator processor = new I18NGenerator(
                new File("src/test/resources/models/normal-models/model/XFWModel3.ecore"),
                outFolder);
        File model = new File(outFolder, "ru/resources.js");

        processor.process();

        assertTrue(model.exists());

        String jsonModel = extractI18nJson(model);

        assertThat(jsonModel, isJson());
        assertThat(jsonModel, hasJsonPath("['model.EscapeTestClass']", equalTo("Объект\n\"EscapeTestClass\"")));
        assertThat(jsonModel, hasJsonPath("['model.EscapeTestClass.s1']", equalTo("Обычная строка")));
        assertThat(jsonModel, hasJsonPath("['model.EscapeTestClass.s2']", equalTo("Строка с \"кавычками\"")));
        assertThat(jsonModel, hasJsonPath("['model.EscapeTestClass.s3']", equalTo("Строка с\nпереносом")));
        assertThat(jsonModel, hasJsonPath("['model.EscapeTestClass.s4']", equalTo("Строка с \\ обратной косой чертой")));
    }

    private String extractI18nJson(File model) throws IOException {
        Pattern pattern = Pattern.compile("(?s)(\\{.+?})");
        Matcher matcher = pattern.matcher(Files.toString(model, Charsets.UTF_8));
        matcher.find();
        return matcher.group(1);
    }
}
