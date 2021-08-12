package ru.croc.ctp.jxfw.mojo

import com.google.common.base.Charsets
import com.google.common.io.Files
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern


class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(GenerateI18NSpec.class)
    static String extractI18nJson(File file) {
        def pattern = Pattern.compile(/(?s)(\{.+?\})/)
        def matcher = pattern.matcher(Files.toString(file, Charsets.UTF_8))
        matcher.find();
        logger.debug("extractJson result: \n{}", matcher.group(1))
        matcher.group(1)
    }

    static String extractModelJson(File file) {
        def pattern = Pattern.compile(/(?s)return(.+?)\}\);/)
        def matcher = pattern.matcher(Files.toString(file, Charsets.UTF_8))
        matcher.find();
        logger.debug("extractJson result: \n{}", matcher.group(1))
        matcher.group(1)
    }
}
