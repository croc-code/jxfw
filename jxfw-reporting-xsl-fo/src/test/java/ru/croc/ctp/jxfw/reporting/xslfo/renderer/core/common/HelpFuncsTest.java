package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertTrue;

public class HelpFuncsTest {

    @Test
    public void numOfSpaces() {
        Font font = new Font("Tahoma", Font.PLAIN, 12);

        String spaces = HelpFuncs.convertPixelsToSpaces(20, font);
        //на глаз конечно, ну и функция такая - примерная))
        //наверное на 20 px поместится примерно столько пробелов
        boolean condition = spaces.length() > 4 && spaces.length() < 10;
        assertTrue("Not expected amount (4..10), actual: " + spaces.length(), condition);
    }
}
