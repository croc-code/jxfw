package ru.croc.ctp.jxfw.security.facade;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.croc.ctp.jxfw.security.JxfwUserDetails;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Хендлер для события "удачный логин"
 * используется для отправки JSON обратно в клиент при AJAX запросе.
 *
 * @author Nosov Alexander
 *         on 27.01.15.
 */
public class AjaxLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    /**
     * @param defaultUrl - URL для успешной AJAX логина.
     */
    public AjaxLoginSuccessHandler(String defaultUrl) {
        setDefaultTargetUrl(defaultUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        final Boolean isAjaxDetected = RequestUtil.isAjaxRequest(request);
        if (isAjaxDetected) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof JxfwUserDetails) {
                RequestUtil.sendJsonResponse(response, ((JxfwUserDetails) principal).toJsonString());
            } else {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }

    /**
     * Утильный класс для формирования JSON ответа с пользователем.
     */
    private static class RequestUtil {

        static final String JSON_VALUE = "{\"result\": %s}";

        private static final RequestMatcher REQUEST_MATCHER =
                new ELRequestMatcher("hasHeader('X-Requested-With','XMLHttpRequest')");

        static Boolean isAjaxRequest(HttpServletRequest request) {
            return REQUEST_MATCHER.matches(request);
        }

        static void sendJsonResponse(HttpServletResponse response, String message) throws IOException {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write(String.format(JSON_VALUE, message));
        }
    }

}
