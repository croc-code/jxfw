package ru.croc.ctp.jxfw.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Работа со структурой токенов.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public class TokenTest {

    @Test
    public void tokenToJson() {
        Token2F token2F = new Token2F();
        String code = "123";
        token2F.setSecret(code);
        Login2fResponse response = token2F.get2FResponse();
        String json = response.toJson();

        Map twoF = JsonPath.read(json, "$.2f");
        Assert.assertNotNull(twoF);

        int tOut = JsonPath.read(json, "$.2f.timeout");
        Assert.assertEquals(60_000, tOut);

        int length = JsonPath.read(json, "$.2f.length");
        Assert.assertEquals(code.length(), length);
    }

    @Test
    public void expirationTest() {
        LocalDateTime now = LocalDateTime.now();
        Token2F token2F = new Token2F();

        token2F.setIssued(now.minusSeconds(59)); //на секунду меньше чем время жизни
        Assert.assertFalse(token2F.hasExpired());

        token2F.setIssued(now.minusSeconds(61)); //на секунду болше чем время жизни
        Assert.assertTrue(token2F.hasExpired());


    }
}
