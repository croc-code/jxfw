package ru.croc.ctp.jxfw.wc.web.mvc.controllers;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import ru.croc.ctp.jxfw.wc.util.HashUtils;
import ru.croc.ctp.jxfw.wc.web.mvc.MainPageModel;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bootloader-контроллер. Предназначен для начальной загрузки скриптов (shims и
 * require.js). Url на контроллер устанавливает скрипт bootloader.js как
 * значение атрибута src динамически создаемого тега SCRIPT. Контроллер
 * возвращает js-код.
 *
 * @see PageControllerBase
 * @since 1.0
 */
@RestController
public class BootloaderController extends PageControllerBase {

    /**
     * отображение: ключ - строка из упорядоченного списка наименований скриптов
     * на содержимое в utf8.
     */
    private static final ConcurrentHashMap<String, String> CACHED_SCRIPTS = new ConcurrentHashMap<>();

    /**
     * отображение: ключ - хеш, вычисленный из строки наименований скриптов на
     * максимальную дату модификации.
     */
    private static final ConcurrentHashMap<String, Date> CACHED_SCRIPTS_TIMESTAMPS = new ConcurrentHashMap<>();

    @Autowired
    private ServletContext servletContext;

    /**
     * Контроллер для <b>/bootloader</b>.
     *
     * @param request - объект HTTP запроса
     * @return ответ
     */
    @RequestMapping(value = "/bootloader", method = RequestMethod.GET, produces = "application/javascript")
    public ResponseEntity<String> index(HttpServletRequest request) {
        MainPageModel model = new MainPageModel();
        initializePageModel(model, request);

        // http caching
        if (canReuseCachedScripts(request)) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        /*
         STEP 1: initialize bundles
         взять все файлы из конфигурации main.config.json/loader/scripts, применить условия
         NOTE: scriptFiles содержит список имен файлов относительно каталога Config.ClientBase
         */
        List<String> scriptFiles = getBootloader().getScripts(model, request);

        // STEP 2: Combine scripts in bundles
        Date lastModified;
        // добавлять ли в конец возвращаемого скрипта код загрузки require.js
        boolean isAppendRequireJsBootloader = !getBootloader().getRequirejsConfig().loadInline;
        if (scriptFiles.size() == 0) {
            // нет скриптов для возврата
            if (!getBootloader().getRequirejsConfig().enabled) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            // возвращаем require.js как обычный скрипт,
            // надо воспрепятстваовать добавлению кода загрузчика в конец
            isAppendRequireJsBootloader = false;
        }
        if (!isAppendRequireJsBootloader
                && getBootloader().getRequirejsConfig().enabled
                && getBootloader().getRequirejsConfig().scriptPath != null) {
            scriptFiles.add(getBootloader().getRequirejsConfig().scriptPath);
        }

        // если данный набор скриптов уже возвращался кому-то вернем его
        String[] scriptFilesArr = scriptFiles.toArray(new String[scriptFiles.size()]);
        Arrays.sort(scriptFilesArr);
        String key = StringUtils.join(scriptFilesArr, ":");
        String etag = "\"" + HashUtils.computeHash(key.getBytes(Charset.forName("UTF-8"))) + "\"";

        String scripts = CACHED_SCRIPTS.get(key);
        if (scripts != null) {
            // содержимое скриптов закешировано
            lastModified = CACHED_SCRIPTS_TIMESTAMPS.get(etag);
        } else {
            ScriptsCombined scriptsCombined;

            try {

                scriptsCombined = getScriptsCombined(scriptFiles);

                lastModified = scriptsCombined.lastModified;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            if (isAppendRequireJsBootloader && getBootloader().getRequirejsConfig().enabled) {
                // в конец добавим код, динамически вставляющий тег script для загрузки require.js
                // NOTE: к сожалению нельзя объединить код require.js с остальными скриптами, 
                // т.к. require.js модифицирует глобальное пространство имен (определяя переменные define/require), 
                // что может изменить выполенение кода скриптов (если они поддерживаются AMD)
                appendRequireBootloader(scriptsCombined.scriptsContent);
            } else if (!getBootloader().getRequirejsConfig().enabled) {
                appendWebpackBootloader(scriptsCombined.scriptsContent);
            }

            scripts = scriptsCombined.scriptsContent.toString();
            CACHED_SCRIPTS.put(key, scripts);
            CACHED_SCRIPTS_TIMESTAMPS.put(etag, scriptsCombined.lastModified);

            // Если раз проверим скрипты на кешируемость, если переданы ETag/If-Modified-Since.
            // Т.к. то, что нет закешированного контента скриптов не означает, что клиент не может использовать 
            // закешированные у себя данные (если скрипты не менялись).
            if (!getConfig().isDebug()) {
                // Если переданы ETag/If-Modified-Since,
                // и ETag равен хешу списка скриптов, то мы может сказать клиенту 304
                // NOTE: мы не вернули 304 в начале, т.к. у нас нет закешированного скрипта из-за рестарта сервера
                if (canReuseCachedScripts(request)) {
                    return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                }
            }
        }


        // prevent client caching via Expires header. 
        HttpHeaders headers = new HttpHeaders();
        headers.setExpires(LocalDateTime.of(1999, 12, 31, 0, 0).toEpochSecond(ZoneOffset.UTC));
        // Set http caching headers
        headers.setETag(etag);
        headers.setLastModified(lastModified.getTime());
        // NOTE: если мы оставим HttpCacheability.Private, который по умолчанию, то MVC не добавит хидер ETag
        headers.setCacheControl("public");

        return new ResponseEntity<>(scripts, headers, HttpStatus.OK);
    }

    private static Boolean canReuseCachedScripts(HttpServletRequest request) {
        if (request.getHeader("If-None-Match") != null) {
            String etag = request.getHeader("If-None-Match");
            Date lastModified = CACHED_SCRIPTS_TIMESTAMPS.get(etag);
            if (lastModified != null) {
                Date timestamp = new Date(0);
                String modifiedSince = request.getHeader("If-Modified-Since");
                if (modifiedSince != null && !"".equals(modifiedSince.trim())) {
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                    try {
                        timestamp = format.parse(modifiedSince);
                    } catch (ParseException e) {
                        // Оставляем timestamp без изменений
                    }
                }
                if (timestamp == lastModified || timestamp.after(lastModified)) {
                    // переданное бразуером значение "If-Modified-Since" больше, 
                    // чем максимальная дата изменения скриптов 
                    // (фактически они должны быть равны с учетом округления, 
                    // но главное что дата не меньше lastModified), 
                    // следовательно с момент прошлого запроса они не изменились
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Читает и объединяет содержимое файлов из списка scriptFiles.
     *
     * @param scriptFiles - файлы скриптов.
     * @return скомбинированный объект скриптов.
     * @see ScriptsCombined
     */
    private ScriptsCombined getScriptsCombined(List<String> scriptFiles) throws IOException {
        String rootDir = "classpath:/static/" + getConfig().getClientBase() + "/";

        ScriptsCombined result = new ScriptsCombined();
        result.lastModified = new Date(0);
        for (String fileName : scriptFiles) {
            Date modified;

            Resource resource = getApplicationContext().getResource(rootDir + fileName);
            modified = new Date(resource.lastModified());

            if (modified.after(result.lastModified)) {
                result.lastModified = modified;
            }

            String content = readFileEnsure(resource);
            result.scriptsContent.append("/* ").append(fileName).append(" */");
            result.scriptsContent.append(content);
            result.scriptsContent.append(System.getProperty("line.separator"));
        }

        return result;
    }

    private void appendRequireBootloader(StringBuilder builder) {
        final String contextPath = servletContext.getContextPath();
        final String requireScriptServerPath =
                contextPath + "/" + getConfig().getClientBase() + "/" + getBootloader().getRequirejsConfig().scriptPath;
        String codeWriteIncludeRequire =
                "var s = document.createElement('script');s.setAttribute('src', '"
                        + requireScriptServerPath + "');s.setAttribute('type', 'text/javascript');"
                        + "document.body.insertBefore(s,null);";
        builder.append(codeWriteIncludeRequire);
    }

    /**
     * Загрузчик для webpacka
     *
     * @param builder
     */
    private void appendWebpackBootloader(StringBuilder builder) {
        final String codeWriteIncludeRequire =
                "(function() {\n" +
                        "var scripts = document.getElementsByTagName('script') || [], i, script, dataMain, s; \n" +
                        "for (i = scripts.length - 1; i > -1; i -= 1) { \n" +
                        "if (script = scripts[i]) { \n" +
                        "if (dataMain = script.getAttribute('data-main')) {\n" +
                        "s = document.createElement('script');\n" +
                        "s.setAttribute('src', dataMain);\n" +
                        "s.setAttribute('type', 'text/javascript');\n" +
                        "document.body.insertBefore(s,null); \n" +
                        "script.parentNode.removeChild(script); \n" +
                        "return;\n" +
                        "} \n" +
                        "} \n" +
                        "} \n" +
                        "})();\n";

        builder.append(codeWriteIncludeRequire);
    }

    private String readFileEnsure(Resource resource) {
        InputStream is = null;
        try {
            is = resource.getInputStream();
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка в конфигурации: не найден скрипт " + resource.getDescription(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private class ScriptsCombined {
        private final StringBuilder scriptsContent;

        private Date lastModified;

        private ScriptsCombined() {
            this.scriptsContent = new StringBuilder();
        }
    }
}
