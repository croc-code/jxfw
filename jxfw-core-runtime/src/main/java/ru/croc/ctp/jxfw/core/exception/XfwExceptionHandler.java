package ru.croc.ctp.jxfw.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.croc.ctp.jxfw.core.exception.dto.ExceptionTo;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.facade.webclient.StoreResultToService;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilderFactory;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;
import ru.croc.ctp.jxfw.core.store.StoreResult;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 * Перехватчик исключений фреймворка для оборачивания в ExceptionTO.
 *
 * @author Nosov Alexander on 08.06.15.
 * @see ExceptionTo
 */
@ControllerAdvice
@Order(0)
public class XfwExceptionHandler {
    /**
     * Логгер.
     */
    static Logger logger = LoggerFactory.getLogger(XfwExceptionHandler.class);

    @Autowired
    private StoreResultToService storeResultToService;

    private XfwMessageTemplateResolver messageTemplateResolver;

    private LoadService loadService;

    @Autowired
    private DomainToServicesResolverWebClient domainToServicesResolver;

    private QueryParamsBuilderFactory queryParamsBuilderFactory;

    public XfwMessageTemplateResolver getMessageTemplateResolver() {
        return messageTemplateResolver;
    }

    @Autowired
    public void setMessageTemplateResolver(XfwMessageTemplateResolver messageTemplateResolver) {
        this.messageTemplateResolver = messageTemplateResolver;
    }

    @Autowired
    public void setLoadService(LoadService loadService) {
        this.loadService = loadService;
    }

    /**
     * Обработчик ошибок фреймворка.
     *
     * @param ex          XException
     * @param locale      {@link Locale}
     * @param httpRequest httpRequest
     * @return XExceptionTo
     */
    @ExceptionHandler(XException.class)
    public ResponseEntity<?> exceptionHandler(final XException ex, Locale locale, HttpServletRequest httpRequest) {

        logger.error(ex.toString());

        HttpStatus httpCode = StoreResult.calcHttpStatus(ex);
        if (httpRequest.getRequestURI().endsWith("api/_store")) {
            /**
             * это прилетело из storeService. Исключение надо обернуть в StoreResult для веб-клиента.
             */
            StoreResult storeResult = null;
            try {
                storeResult = new StoreResult(ex, domainToServicesResolver, queryParamsBuilderFactory, loadService);
            } catch (Exception e) {
                return new ResponseEntity<>(new XException("Error creating StoreResult", e)
                        .toTo(messageTemplateResolver, locale), httpCode);
            }
            return new ResponseEntity<>(storeResultToService.toTo(storeResult, locale), storeResult.getHttpStatus());
        } else {
            return new ResponseEntity<>(ex.toTo(messageTemplateResolver, locale), httpCode);
        }


    }

    @Autowired
    public void setQueryParamsBuilderFactory(QueryParamsBuilderFactory queryParamsBuilderFactory) {
        this.queryParamsBuilderFactory = queryParamsBuilderFactory;
    }
}