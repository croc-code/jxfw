package ru.croc.ctp.jxfw.security;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Структура для хранения сессии двухфакторной аутентификации.
 * Поле Secret (секрет) сравнивается на 2-м шаге аутентификации с клиентским значением.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public class Token2F {

    /**
     * 60 секунд по умолчанию, токен считается валидным.
     */
    public static final long DEFAULT_TIMEOUT = 60_000L;

    /**
     * Конструктор.
     */
    public Token2F() {
        setIssued(LocalDateTime.now());
        setTimeout(DEFAULT_TIMEOUT);
    }

    /**
     * Сгенерированный секрет и отправленный пользователю по другому каналу для подтверждения.
     */
    private String secret;
    /**
     * Время генерации.
     */
    private LocalDateTime issued;
    /**
     * Время жизни секрета.
     */
    private Long timeout;
    /**
     * Identity объекта, выполняющего аутентификацию.
     */
    private Principal principal;

    private Login2fResponse login2fResponse;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public LocalDateTime getIssued() {
        return issued;
    }

    public void setIssued(LocalDateTime issued) {
        this.issued = issued;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * Структура для ответа {@link Login2fResponse}, частично полученная из самого токена {@link Token2F}.
     *
     * @return {@link Login2fResponse}
     */
    public Login2fResponse get2FResponse() {
        if (login2fResponse == null) {
            login2fResponse = new Login2fResponse(getSecret(), getTimeout());
        }
        return login2fResponse;
    }

    /**
     * Вышло время жизни.
     * @return да/нет
     */
    public boolean hasExpired() {
        long lasted = Duration.between(issued, LocalDateTime.now()).toMillis();
        return lasted > getTimeout();
    }
}
