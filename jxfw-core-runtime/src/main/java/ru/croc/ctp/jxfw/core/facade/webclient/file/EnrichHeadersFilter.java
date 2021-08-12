package ru.croc.ctp.jxfw.core.facade.webclient.file;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Пара ключ-значение из GET-параметра $headers переносит в http-заголовки
 * текущего запроса. Может использоваться для задания http-заголовков через URL,
 * когда нет возможности контролировать http request полностью (в частности при
 * скачивания файлов через iframe в $.fileDownload).
 * 
 * @author SPlaunov
 *
 */
public class EnrichHeadersFilter extends OncePerRequestFilter {

    /**
     * Обертка для объекта запроса, для подстановки доп. заголовков в запрос.
     */
    static class RequestWrapper extends HttpServletRequestWrapper {

        /**
         * @param request - оборачиваемый объект запроса.
         */
        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private Map<String, String[]> additionalHeaders = null;

        private Map<String, String[]> getAdditionalHeadersMap() {
            if (additionalHeaders != null) {
                return additionalHeaders;
            }

            additionalHeaders = new HashMap<>();
            
            String headers = getParameter("$headers");
            if (headers != null) {
                String[] pairs = headers.split(";");
                for (String pair : pairs) {
                    String[] header = pair.split("=");
                    String[] value = {header[1]};
                    additionalHeaders.put(header[0], value);
                }
            }
            

            return additionalHeaders;
        }

        private String getAdditionalHeader(String name) {
            String[] header = getAdditionalHeadersMap().get(name);
            return header == null ? null : header[0];
        }

        private Enumeration<String> getAdditionalHeaders(String name) {
            return Collections.enumeration(Arrays
                    .asList(getAdditionalHeadersMap().get(name)));
        }

        private Set<String> getAdditionalHeaderNames() {
            return getAdditionalHeadersMap().keySet();
        }

        @Override
        public String getHeader(String name) {
            String header = super.getHeader(name);
            return (header != null) ? header : getAdditionalHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(getAdditionalHeaderNames());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            Enumeration<String> headers = super.getHeaders(name);
            return (headers != null) ? headers : getAdditionalHeaders(name);
        }

    }

    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(new RequestWrapper(request), response);
    }
}
