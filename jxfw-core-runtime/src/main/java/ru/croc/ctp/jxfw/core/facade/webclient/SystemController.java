package ru.croc.ctp.jxfw.core.facade.webclient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

/**
 * REST контроллер для системных запросов к jxfw.
 */
@Controller
@RequestMapping("**/api/")
public class SystemController {

    /**
     * Запрос на проверку доступа сервера на jxfw.
     *
     * @param response - HTTP ответ.
     * @throws IOException исключение.
     */
    @RequestMapping(value = "ping", method = {RequestMethod.GET, RequestMethod.POST})
    public void ping(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "-1");
        response.setHeader("Pragma", "no-cache");

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss.SSS\"");
        response.getWriter().write(dateFormat.format(date));
    }

}
