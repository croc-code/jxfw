package ru.croc.ctp.jxfw.security.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@inheritDoc}.
 */
public class Http401UnauthorizedEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final RequestMatcher REST_REQUEST_MATCHER =
            new ELRequestMatcher("hasHeader('X-Requested-With', 'XMLHttpRequest')");

    private final Logger log = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);
 
    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public Http401UnauthorizedEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    /**
     * {@inheritDoc}.
     */
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception)
            throws IOException, ServletException {
        if (isPreflight(request)) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else if (isRestRequest(request)) {
            log.debug("Pre-authenticated entry point called. Rejecting access by URI:" + request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
        } else {
            super.commence(request, response, exception);
        }
    }

    private boolean isPreflight(HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod());
    }

    private boolean isRestRequest(HttpServletRequest request) {
        return REST_REQUEST_MATCHER.matches(request);
    }
}
