package ru.croc.ctp.jxfw.cql;

import org.junit.Assert;
import org.junit.Test;
import ru.croc.ctp.jxfw.cass.Binary;
import ru.croc.ctp.jxfw.cass.CommentHistory;
import ru.croc.ctp.jxfw.mojo.GenerateCqlScriptsMojo;

/**
 * Created by SMufazzalov on 15.06.2017.
 */
public class CqlTests {

    @Test //JXFW-716 jXFW Cass + scriptgen, ошибка генерации скрипта для типов Blob + ZDT
    public void generateWithBlob() {

        String generatedText = GenerateCqlScriptsMojo.getScript(Binary.class);

        Assert.assertNotNull(generatedText);
        Assert.assertEquals(true, generatedText.contains("blob"));
    }

    @Test //JXFW-716 jXFW Cass + scriptgen, ошибка генерации скрипта для типов Blob + ZDT
    public void generateWithZonedDateTime() {

        String generatedText = GenerateCqlScriptsMojo.getScript(CommentHistory.class);

        Assert.assertNotNull(generatedText);
        Assert.assertEquals(true, generatedText.contains("comment_date_zoned timestamp"));
        Assert.assertEquals(true, generatedText.contains("comment_date_local timestamp"));
    }
}
