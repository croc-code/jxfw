package ru.croc.ctp.jxfw.security.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Хендлер для неудачной AJAX-аутентификации.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
@Component
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    XfwMessageTemplateResolver resolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String message = exception.getMessage();
        XSecurityException xSecurityException = new XSecurityException(exception, message);
        XExceptionTo to = new XExceptionTo(xSecurityException, resolver, Locale.getDefault());

        ObjectMapper mapper = new ObjectMapper();
        String exceptionAsJson = mapper.writeValueAsString(to);

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(exceptionAsJson);
    }
}
