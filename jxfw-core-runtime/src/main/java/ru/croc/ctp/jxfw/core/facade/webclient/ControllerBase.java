package ru.croc.ctp.jxfw.core.facade.webclient;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.StandardCharsets.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;
import ru.croc.ctp.jxfw.core.load.GeneralLoadResult;
import ru.croc.ctp.jxfw.core.load.LoadResult;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Базовый класс, от которого наследуют все контроллеры всех доменных типов.
 */
public class ControllerBase {

    private static final Logger log = LoggerFactory.getLogger(ControllerBase.class);

    private static final String FILE_DOWNLOAD_DEFAULT_COOKIE_NAME = "fileDownload";

    private static final String FILE_DOWNLOAD_HEADER = "X-FileDownload";

    /**
     * Добавляет cookie для плагина $.fileDownload
     *
     * @param request  http-запрос
     * @param response http-ответ
     * @param fileName Имя файла из параметров запроса
     */
    public static void addFileDownLoadCookieAndHeader(HttpServletRequest request,
                                                      HttpServletResponse response, String fileName) {
        String contentDispositionMode;
        // волшебная печенька для плагина $.fileDownload
        String fileDownloadCookieName = request.getHeader(FILE_DOWNLOAD_HEADER);
        if (fileDownloadCookieName != null) {
            contentDispositionMode = "attachment";
            if (fileDownloadCookieName.isEmpty() || fileDownloadCookieName.equals("true")) {
                fileDownloadCookieName = FILE_DOWNLOAD_DEFAULT_COOKIE_NAME;
            }
            Cookie cookie = new Cookie(fileDownloadCookieName, "true");
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            contentDispositionMode = "inline";
        }

        // set headers for the response
        String encodeFileName = UriUtils.encode(fileName, UTF_8);
        String contentDisposition = contentDispositionMode + "; filename*=UTF-8''" + encodeFileName;
        response.setHeader("Content-Disposition", contentDisposition);
    }

    /**
     * Формирует результат в формате API WebClient.
     *
     * @param resultList Список DTO доменных объектов, которые нужно включить в результат вызова контроллера
     * @return Структура данных, которая будет отправлена на клиент
     */
    protected ResponseEntity<DomainResult> buildResponseOk(Iterable<DomainTo> resultList) {
        final Map<String, Object> hints = new HashMap<>();
        hints.put("exportable", true);
        final DomainResult result = new DomainResult.Builder()
                .result(resultList)
                .more(newArrayList())
                .hints(hints)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Формирует результат в формате API WebClient.
     *
     * @param resultList Список DTO доменных объектов, которые нужно включить в результат вызова контроллера
     * @param more       Массив DTO дополнительно загруженных объектов (прелоадов)
     * @param hints      Словарь дополнительных значений
     * @return Структура данных, которая будет отправлена на клиент
     */
    protected ResponseEntity<DomainResult> buildResponseOk(
            Iterable<DomainTo> resultList, Iterable<DomainTo> more, Map<String, Object> hints) {
        if (!hints.containsKey("exportable")) {
            hints.put("exportable", true);
        }
        final DomainResult result = new DomainResult.Builder()
                .result(resultList)
                .more(more)
                .hints(hints)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Формирует результат в формате API WebClient.
     *
     * @param resultList Список DTO доменных объектов, которые нужно включить в результат вызова контроллера
     * @param hints      Словарь дополнительных значений
     * @return Структура данных, которая будет отправлена на клиент
     */
    protected ResponseEntity<DomainResult> buildResponseOk(
            Iterable<DomainTo> resultList, Map<String, Object> hints) {
        if (!hints.containsKey("exportable")) {
            hints.put("exportable", true);
        }
        final DomainResult result = new DomainResult.Builder()
                .result(resultList)
                .more(newArrayList())
                .hints(hints)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Формирует результат в формате API WebClient.
     *
     * @param resultList Список DTO доменных объектов, которые нужно включить в результат вызова контроллера
     * @param more       Массив DTO дополнительно загруженных объектов (прелоадов)
     * @return Структура данных, которая будет отправлена на клиент
     */
    protected ResponseEntity<DomainResult> buildResponseOk(
            Iterable<DomainTo> resultList, Iterable<DomainTo> more) {
        final Map<String, Object> hints = new HashMap<>();
        hints.put("exportable", true);
        final DomainResult result = new DomainResult.Builder()
                .result(resultList)
                .more(more)
                .hints(hints)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Устанавливает параметры ответа для контроллера запроса бинарного свойства объекта.
     *
     * @param request      http-запрос
     * @param response     http-ответ
     * @param binPropValue Значение бинарного свойства
     */
    protected void buildBinPropResponse(HttpServletRequest request, HttpServletResponse response,
                                        DomainToServiceImpl.BinPropValue binPropValue) {
        String fileName = "empty.bin";
        String contentType = "application/octet-stream";
        if (binPropValue != null) {
            fileName = binPropValue.getFileName();
            contentType = binPropValue.getContentType();
        }
        setFileDownloadResponseAttributes(request, response, fileName, contentType);
    }

    /**
     * Устанавливает параметры ответа для контроллера запроса ресурса.
     *
     * @param request            http-запрос
     * @param response           http-ответ
     * @param resourceProperties Значение бинарного свойства
     */
    protected void buildBinPropResponse(HttpServletRequest request, HttpServletResponse response,
                                        ResourceProperties resourceProperties) {
        Assert.notNull(resourceProperties, "Method parameter resourceProperties should not be null");
        setFileDownloadResponseAttributes(request, response, resourceProperties.getFileName(),
                resourceProperties.getContentType());
    }

    /**
     * Устанавливает параметры ответа.
     *
     * @param request     http-запрос.
     * @param response    http-ответ.
     * @param fileName    имя файла.
     * @param contentType тип контента.
     */
    protected void setFileDownloadResponseAttributes(HttpServletRequest request, HttpServletResponse response,
                                                     String fileName, String contentType) {
        // set content attributes for the response
        response.setContentType(contentType);
        addFileDownLoadCookieAndHeader(request, response, fileName);

        if (log.isDebugEnabled()) {
            log.debug("Response headers:");
            for (String headerName : response.getHeaderNames()) {
                log.debug(headerName + " : " + response.getHeaders(headerName));
            }
        }
    }

    /**
     * Формирует результат в формате API WebClient.
     *
     * @param domainToServicesResolver резолвер ТО-сервисов
     * @param loadResult результат загрузки
     * @return результат в формате WebClient
     */

    protected ResponseEntity<DomainResult> buildGeneralResponseOk(GeneralLoadResult<?, ?> loadResult,
                                                                  DomainToServicesResolverWebClient
                                                                          domainToServicesResolver, String expand) {

        final DomainResult response = new DomainResult.Builder()
                .result(transformToDto(loadResult.getData(), domainToServicesResolver,  getExpandFirstLevelProps(expand)))
                .more(transformToDto(loadResult.getMoreList(), domainToServicesResolver, getExpandInnerProps(expand)))
                .hints(loadResult.getHints())
                .build();
        if (loadResult instanceof LoadResult) {
            loadResult.getHints().put("exportable", true);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    private Object transformToDto(Object data, DomainToServicesResolverWebClient domainToServicesResolver, String... expand) {
        Object transformedData = data;

        if (data instanceof DomainObject) {
            transformedData = domainObjectToDto((DomainObject) data, domainToServicesResolver, expand);
        } else if (data instanceof Iterable) {
            Object[] objects = newArrayList((Iterable) data).toArray();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof DomainObject) {
                    objects[i] = domainObjectToDto((DomainObject) objects[i], domainToServicesResolver, expand);
                }
            }
            transformedData = objects;
        }
        return transformedData;

    }

    private DomainTo domainObjectToDto(DomainObject<?> domainObject,
                                       DomainToServicesResolverWebClient domainToServicesResolver, String... expand) {
        DomainToService domainToService = domainToServicesResolver.resolveToService(domainObject.getTypeName());
        return domainToService.toToPolymorphic(domainObject, expand);
    }
    
    protected String[] getExpandFirstLevelProps(String expand) {
        if (expand == null) {
            return new String[0];
        }
        Set<String> props = new HashSet<>();
        for (String str: expand.split(",")) {
            props.add(str.split("\\.")[0]);
        }
        return props.toArray(new String[0]);
    }
    
    protected String[] getExpandInnerProps(String expand) {
        if (expand == null) {
            return new String[0];
        }
        Set<String> innerProps = new HashSet<>();
        for (String str: expand.split(",")) {
            String[] inner = str.split("\\.");
            for (int i = 0; i < inner.length; i++) {
                if (i == 0) {
                    continue;
                }
                innerProps.add(inner[i]);
            }
        }
        return innerProps.toArray(new String[0]);
    }
}
