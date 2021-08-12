package ru.croc.ctp.jxfw.security.facade.webclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainResult;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

/**
 * Контроллер для получения информации связанной с безопасностью приложения.
 * Например, получение информации отекущем пользователе.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
@RequestMapping("**/api/_security")
public abstract class XSecurityControllerBase {

    /**
     * @return получение информации отекущем пользователе..
     */
    public abstract ResponseEntity<DomainResult> getCurrentUser();

    /**
     * @param resultList - доменный объект подготовленный для траспортировки на клиент.
     * @return структура данных для возврата клиенту.
     */
    @SuppressWarnings("unchecked")
    protected ResponseEntity<DomainResult> buildResponseOk(DomainTo resultList) {
        final DomainResult result = new DomainResult.Builder().result(resultList).build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
