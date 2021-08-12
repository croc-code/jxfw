package ru.croc.ctp.jxfw.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 * Результат попытки логина при включенной двухфакторной аутентификации.
 * Отправляется клиенту в поле "2f" результата.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
@JsonSerialize(using = Login2fResponseSerializer.class)
public class Login2fResponse {

    /**
     * Имя проперти.
     */
    public static final String TOKEN = "token";
    /**
     * Имя проперти.
     */
    public static final String TIMEOUT = "timeout";
    /**
     * Имя проперти.
     */
    public static final String LENGTH = "length";
    /**
     * Имя проперти.
     */
    public static final String TYPE = "type";
    /**
     * Имя проперти.
     */
    public static final String HINT = "hint";

    /**
     * Токен, который должен быть отправлен с клиента вместе с секретом.
     * Заполняется автоматически.
     */
    private String token;
    /**
     * Время жизни секрета.
     */
    private Long timeout;
    /**
     * Количество символов в секрете.
     */
    private int length;
    /**
     * Типа секрета ("string", "number" или "wait").
     */
    private SecretType type = SecretType.NUMBER;

    /**
     * Дополнительная подсказка, отображаемая в UI.
     */
    private String hint;

    /**
     * Коструктор.
     *
     * @param secret  код
     * @param timeout время жизни
     */
    public Login2fResponse(String secret, Long timeout) {
        Objects.requireNonNull(secret, "secret code must be set");
        setLength(secret.length());
        setTimeout(timeout);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public SecretType getType() {
        return type;
    }

    public void setType(SecretType type) {
        this.type = type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    /**
     * Вернуть объект в виде json.
     *
     * @return json
     */
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Тип секрета.
     */
    public enum SecretType {
        /**
         * Тип.
         */
        STRING("string"),
        /**
         * Тип.
         */
        NUMBER("number"),
        /**
         * Тип.
         */
        WAIT("number");

        private String val;

        /**
         * Конструктор.
         *
         * @param val тип
         */
        SecretType(String val) {
            this.val = val;
        }

        public String getValue() {
            return val;
        }
    }
}
